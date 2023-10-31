package ie.tcd.jmcauliffe;
import java.io.*;
import java.util.ArrayList;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

public class Indexer
{

    // static map that maps the cranwell tags to the lucen field types
    public static final Map<String, String> fieldMap = new HashMap<>();

    static {
        fieldMap.put(".I", "ID");
        fieldMap.put(".T", "Title");
        fieldMap.put(".A", "Author");
        fieldMap.put(".B", "Bibliography");
        fieldMap.put(".W", "Body");
    }

    public Indexer(Analyzer analyzer) {
        try {
            ParseFile(analyzer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void ParseFile(Analyzer analyzer) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(Main.INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        // read input file from the jar, the cranfield is bundled with the jar as resource
        InputStream fileStream = Indexer.class.getClassLoader().getResourceAsStream("cran.all.1400");
        assert fileStream != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
        
        String currentLine;

        boolean newTagSeen = true;
        boolean firstPass = true;

        String currTag = "ID";
        String prevTag = "";
        String tagBody = "";

        
		List<Document> luceneDocs = new ArrayList<Document>();
        Document doc = new Document();

        //step through file line by line
        while ((currentLine = br.readLine()) != null)   {
            if (currentLine.startsWith(".")) {
                prevTag = currTag;
                currTag = fieldMap.get(currentLine.substring(0,2));
                if(!"ID".equals(prevTag)) {
                    doc.add(new TextField(prevTag, tagBody, Field.Store.YES));
                }
                // hit a new id tag
                if (currentLine.startsWith(".I")) {
                    String documentId = currentLine.substring(3);
                    if (!firstPass) {
                        luceneDocs.add(doc);
                    }
                    firstPass = false;
                    doc = new Document();
                    doc.add(new StringField(currTag, documentId, Field.Store.YES));
                }
                newTagSeen = true;
            }
            else { // continuation of previous tag
                if (newTagSeen) {
                    tagBody = currentLine;
                    newTagSeen = false;
                }
                else {
                    tagBody = tagBody + "\n" + currentLine;
                }
            }
        }

        doc.add(new TextField(currTag, tagBody, Field.Store.YES));
        luceneDocs.add(doc);

        indexWriter.addDocuments(luceneDocs);

        // close input streams
        indexWriter.close();
        directory.close();

        fileStream.close();
    }
}

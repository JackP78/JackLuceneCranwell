package ie.tcd.jmcauliffe;
import java.io.*;
import java.util.ArrayList;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer
{

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
        
        IndexWriter iwriter = new IndexWriter(directory, config);
        
        // read input file from the jar, the cranfield is bundled with the jar as resource
        InputStream fstream = Indexer.class.getClassLoader().getResourceAsStream("cran.all.1400");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        
        String strLine;
        String currentTag = "ID"; // Assumption document starts with ID
        String previousTag = "";
        String contentOfTag = "";
        Boolean startNewContent = true;
        Boolean firstIteration = true;

        // ArrayList of documents in the corpus
		ArrayList<Document> documents = new ArrayList<Document>();
        Document doc = new Document();

        //step through file line by line
        while ((strLine = br.readLine()) != null)   {
            if (strLine.startsWith(".")) {
                previousTag = currentTag;
                currentTag = fieldMap.get(strLine.substring(0,2));
                if(!"ID".equals(previousTag)) {
                    doc.add(new TextField(previousTag, contentOfTag, Field.Store.YES));
                }
                // hit a new id tag
                if (strLine.startsWith(".I")) {
                    String docId = strLine.substring(3);
                    if (!firstIteration) {
                        documents.add(doc);
                    }
                    firstIteration = false;
                    doc = new Document();
                    doc.add(new StringField(currentTag, docId, Field.Store.YES));
                    System.out.println("Document " + docId + " parsed");
                }
                startNewContent = true;
            }
            else { // continuation of previous tag
                if (startNewContent) {
                    contentOfTag = strLine;
                    startNewContent = false; 
                }
                else {
                    contentOfTag = contentOfTag + "\n" + strLine;
                }
            }
        }

        doc.add(new TextField(currentTag, contentOfTag, Field.Store.YES));
        documents.add(doc);

        iwriter.addDocuments(documents);

        // close input streams
        iwriter.close();
        directory.close();

        fstream.close();
    }
}

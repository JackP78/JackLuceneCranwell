package ie.tcd.jmcauliffe;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

class QueryEngine {

    private String similarityStrategy;

    private ArrayList<QueryItem> queries = new ArrayList<QueryItem>();
    private ArrayList<QueryResult> results = new ArrayList<QueryResult>();

    public QueryEngine(Analyzer analyzer, Similarity similarity, String type) {
        try {
            ParseFile();

            this.similarityStrategy = type;
            ExecuteQueries(analyzer, similarity);
            SaveResultsFile();
        } catch (Exception e) {
            System.out.println("Issue with QueryEngine.");
            e.printStackTrace();
        }
    }

    public void ParseFile() throws IOException {
        // File opening Setup
        InputStream fstream = QueryEngine.class.getClassLoader().getResourceAsStream("cran.qry");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String currentLine;

        int queryNumber = 1;
        String queryBody = "";
        boolean firstPass = true;

        while ((currentLine = br.readLine()) != null) {
            if(currentLine.startsWith(".I")) {
                if(!firstPass) {
                    QueryItem query = new QueryItem(queryNumber++, queryBody);
                    this.queries.add(query);
                    queryBody = "";
                }
                firstPass = false;
            }
            else if(!currentLine.startsWith(".W")) {
                if (queryBody == "") {
                    queryBody = currentLine;
                } else {
                    queryBody = queryBody + "\n" + currentLine;
                }
            }
        }
        QueryItem query = new QueryItem(queryNumber, queryBody);
        this.queries.add(query);

        System.out.println("Num queries processed " + this.queries.size());
    }

    public void ExecuteQueries(Analyzer analyzer, Similarity similarity) throws IOException, ParseException {
        // Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(Main.INDEX_DIRECTORY));
		
		DirectoryReader reader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(reader);

        indexSearcher.setSimilarity(similarity);
        System.out.println(indexSearcher.getSimilarity());

		QueryParser parser = new QueryParser("Body", analyzer);

        for (QueryItem thisQuery: this.queries) {
            String searchTerm = stripPunctuation(thisQuery.getBody());
            Query query = parser.parse(searchTerm);
            ScoreDoc[] hits = indexSearcher.search(query, 50).scoreDocs;
            System.out.println("Query ID " + thisQuery.getId() + " parsed");
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = indexSearcher.doc(hits[i].doc);
                QueryResult searchResult = new QueryResult(thisQuery.getId(),
                    Integer.valueOf(hitDoc.get("ID")), i+1, hits[i].score, this.similarityStrategy);
                this.results.add(searchResult);
		    }
        }

		reader.close();
		directory.close();
    }

    public void SaveResultsFile() throws IOException {
        String filePath = Main.RESULTS_DIRECTORY + this.similarityStrategy + ".test";
        System.out.println("Creating file " + filePath);
        File file = new File(filePath);

        if (file.exists()) {
            file.delete(); 
        }
        else {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (QueryResult result: this.results) {
            printWriter.println(result.toString());
        }
        printWriter.close();
    }

    public String stripPunctuation(String query) {
        return query.replaceAll("\\?", "");
    }

}
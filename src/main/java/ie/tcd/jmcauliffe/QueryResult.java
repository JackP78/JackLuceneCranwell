package ie.tcd.jmcauliffe;

public class QueryResult {



    private int rank;

    private float score;

    private String runName= "Standard";

    private int queryId;

    private int documentId;

    public QueryResult(int queryId, int documentId, int rank, float score, String runName) {
        this.rank = rank;
        this.score = score;
        this.runName = runName;
        this.queryId = queryId;
        this.documentId = documentId;

    }

    @Override
    public String toString() {
        return Integer.toString(queryId) + "\t"
                + "Q0 \t"
                + Integer.toString(documentId) + "\t"
                + Integer.toString(rank) + "\t"
                + Float.toString(score) + "\t"
                + runName;
    }

}

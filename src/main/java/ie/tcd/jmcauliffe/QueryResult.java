package ie.tcd.jmcauliffe;

public class QueryResult {

    private int queryId;

    private int docId;

    private int rank;

    private float score;

    private String runName= "Standard";

    public QueryResult(int queryId, int docId, int rank, float score, String runName) {
        this.queryId = queryId;
        this.docId = docId;
        this.rank = rank;
        this.score = score;
        this.runName = runName;
    }

    @Override
    public String toString() {
        return Integer.toString(queryId) + "\t"
                + "Q0 \t"
                + Integer.toString(docId) + "\t"
                + Integer.toString(rank) + "\t"
                + Float.toString(score) + "\t"
                + runName;
    }

}

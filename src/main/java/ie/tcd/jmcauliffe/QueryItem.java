package ie.tcd.jmcauliffe;

public class QueryItem {

    public QueryItem(int id, String body) {
        this.id = id;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private int id;

    private String body;
}

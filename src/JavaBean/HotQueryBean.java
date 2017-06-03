package JavaBean;

import java.util.Vector;

/**
 * Created by THU73 on 17/6/2.
 */
public class HotQueryBean {
    Vector<String> queries;
    Vector<Integer> counts;

    public Vector<String> getQueries() {
        return queries;
    }

    public void setQueries(Vector<String> queries) {
        this.queries = queries;
    }

    public Vector<Integer> getCounts() {
        return counts;
    }

    public void setCounts(Vector<Integer> counts) {
        this.counts = counts;
    }
}

package serv;

/**
 * Created by THU73 on 17/5/24.
 */

public class RequestBean {

    public static final int NONE = 0;
    public static final int DAY = 1;
    public static final int MONTH = 2;
    public static final int YEAR = 3;

    private String key = "";
    private int pageNum = 0;
    private int timeFilter = NONE;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getTimeFilter() {
        return this.timeFilter;
    }

    public void setTimeFilter(int timeFilter) {
        this.timeFilter = timeFilter;
    }
}

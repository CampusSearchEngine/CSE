package bean;

/**
 * Created by THU73 on 17/5/24.
 */

public class RequestBean {

    private String key = "";
    private int pageNum = 0;

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
}

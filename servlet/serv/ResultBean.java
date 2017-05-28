package serv;

import java.util.Vector;

/**
 * Created by THU73 on 17/5/24.
 */
public class ResultBean {
    private int page_num;
    private Vector<EntryBean> vec;
    private int total_num;

    public int getPage_num() {
        return page_num;
    }

    public void setPage_num(int page_num) {
        this.page_num = page_num;
    }

    public Vector<EntryBean> getVec() {
        return vec;
    }

    public void setVec(Vector<EntryBean> vec) {
        this.vec = vec;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }
}

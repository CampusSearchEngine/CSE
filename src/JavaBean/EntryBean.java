package JavaBean;

import java.util.Date;

/**
 * Created by THU73 on 17/5/24.
 */
public class EntryBean {
    private String link;
    private String title;
    private String abst;
    private Date date;
    private String type;
    private String com_link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbst() {
        return abst;
    }

    public void setAbst(String abst) {
        this.abst = abst;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCom_link() {
        return com_link;
    }

    public void setCom_link(String com_link) {
        this.com_link = com_link;
    }


}

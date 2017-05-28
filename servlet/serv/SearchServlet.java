package serv;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.lucene.document.Document;
import org.json.JSONException;
import org.json.JSONObject;

import indexSearcher.CampusSearcher;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by THU73 on 17/5/21.
 */
public class SearchServlet extends HttpServlet {
    static CampusSearcher searcher;

    static final String INDEX_PATH = "/index";

    static void initSearcher(String workingPath) {

        //searcher = new CampusSearcher(workingPath + INDEX_PATH);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        //if(searcher == null)
        //	initSearcher(req.getSession().getServletContext().getRealPath("/"));

        HttpSession session = req.getSession();

        String keyStr = req.getParameter("key");
        String pageNumStr = req.getParameter("page_num");
        String timeFilterStr = req.getParameter("time_filter");
        //get request info from http packet

        int pageNum = 1;
        int timeFilter = RequestBean.NONE;
        String key = "";

        if (keyStr.equals("")) {
            //if request field was NULL

            res.sendRedirect("/");
            return;
        } else {
            key = keyStr;
            //not needed and invalid
            //key = new String(keyStr.getBytes("iso8859-1"),"UTF-8");
        }

        if (!pageNumStr.equals("")) {
            pageNum = Integer.parseInt(pageNumStr);
            if (pageNum < 1 || pageNum > 10) {
                res.sendRedirect("/");
                return;
            }
        }
        //if page_num is invalid

        if (!timeFilterStr.equals("")) {
            timeFilter = Integer.parseInt(timeFilterStr);
            if (timeFilter != RequestBean.NONE &&
                    timeFilter != RequestBean.YEAR &&
                    timeFilter != RequestBean.MONTH &&
                    timeFilter != RequestBean.YEAR) {
                res.sendRedirect("/");
                return;
            }
        }
        //if time_filter is invalid

        /* key: String
         * pageNum: [1, 10]
         * timeFilter: [NONE, YEAR, MONTH, YEAR]
         */

        System.out.println("key: " + key + " pageNum: " + pageNum + " timeFilter: " + timeFilter);

        //do searching...

        SearcherResult sRst = searcher.doQuery(key, pageNum);
        Vector<Document> documents = sRst.docs;
        int totNum = sRst.totNum;
        System.out.println("actually find " + documents.size() + " pages");

        RequestBean rb = new RequestBean();
        rb.setKey(key);
        rb.setPageNum(pageNum);
        rb.setTimeFilter(timeFilter);
        session.setAttribute("request", rb);

        ResultBean reb = new ResultBean();
        reb.setPage_num(pageNum);
        reb.setTotal_num(67);
        
        Vector<EntryBean> vec = new Vector<EntryBean>();
        for (Document document : documents) {
            EntryBean eb = new EntryBean();

            eb.setTitle(document.get("title"));
            eb.setAbst(document.get("content"));
            eb.setLink(document.get("URI"));
            eb.setType(document.get("type"));// html pdf doc

            // do what you want with these fields, e.g., store them in Json
            /*
            eb.setTitle(new String("这是一个超链接结果".getBytes("ISO-8859-1"),"utf-8"));
            eb.setAbst(new String("精密测试技术及仪器国家重点实验室是1990年经国家计委批准、利用世行贷款、由清华大学与天津大学联合组建的国家级重点实验室。1995年实验室建成并通过主管部门验收，同...".getBytes("ISO-8859-1"),"utf-8"));
            eb.setLink("pmti.pim.tsinghua.edu.cn/");
            eb.setType("html");
            */
            vec.add(eb);
        }
        reb.setVec(vec);
        //!!!
        session.setAttribute("result", reb);

        req.getRequestDispatcher("/result.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}

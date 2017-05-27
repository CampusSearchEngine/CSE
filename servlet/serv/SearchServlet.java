package serv;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by THU73 on 17/5/21.
 */
public class SearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession();

        String keyStr = req.getParameter("key");
        String pageNumStr = req.getParameter("page_num");
        String timeFilterStr = req.getParameter("time_filter");
        //get request info from http packet

        int pageNum = 1;
        int timeFilter = RequestBean.NONE;
        String key = "";

        if(keyStr.equals("")) {
            //if request field was NULL

            res.sendRedirect("/");
            return;
        } else {
            key = new String(keyStr.getBytes("iso8859-1"),"UTF-8");
        }

        if(!pageNumStr.equals("")) {
            pageNum = Integer.parseInt(pageNumStr);
            if(pageNum < 1 || pageNum > 10) {
                res.sendRedirect("/");
                return;
            }
        }
        //if page_num is invalid

        if(!timeFilterStr.equals("")) {
            timeFilter = Integer.parseInt(timeFilterStr);
            if(timeFilter != RequestBean.NONE &&
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

        RequestBean rb = new RequestBean();
        rb.setKey(key);
        rb.setPageNum(pageNum);
        rb.setTimeFilter(timeFilter);
        session.setAttribute("request", rb);
        req.getRequestDispatcher(req.getContextPath() + "/result.jsp").forward(req, res);
    }

    protected void doPost (HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}

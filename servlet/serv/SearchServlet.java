package serv;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.lucene.document.Document;
import org.bouncycastle.asn1.icao.CscaMasterList;
import org.bson.json.JsonMode;
import org.json.JSONException;
import org.json.JSONObject;

import bsh.This;
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
		searcher = new CampusSearcher(workingPath + INDEX_PATH);
	}
	
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    	if(searcher == null)
    		initSearcher(req.getSession().getServletContext().getRealPath("/"));
    	
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
        Vector<Document> documents = searcher.doQuery(key, pageNum);
        int actualRetNum = documents.size();
        System.out.println("actually find " + actualRetNum + " pages");
        
        Vector<JSONObject> jsons = new Vector<JSONObject>();
        for(Document document : documents){
        	String content = document.get("content");
        	String title = document.get("title");
        	String URI = document.get("URI");
        	String type = document.get("type"); // html pdf doc
        	// do what you want with these fields, e.g., store them in Json
        	System.out.println("title: " + title + " URI " + URI);
        	JSONObject jsonObject = new JSONObject();
        	try {
				jsonObject.put("content", content);
				jsonObject.put("title", title);
				jsonObject.put("URI", URI);
				jsonObject.put("type", type);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	jsons.add(jsonObject);
        }

        RequestBean rb = new RequestBean();
        rb.setKey(key);
        rb.setPageNum(pageNum);
        rb.setTimeFilter(timeFilter);
        session.setAttribute("request", rb);
        req.getRequestDispatcher("/result.jsp").forward(req, res);
    }

    protected void doPost (HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}

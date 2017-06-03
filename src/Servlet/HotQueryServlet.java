package Servlet;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;

import HotQuery.HotQuery;

public class HotQueryServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();

        String topNumStr = req.getParameter("top_num");
        int topNum = Integer.parseInt(topNumStr);
        
        Vector<Document> documents =  HotQuery.getTop(topNum);
        topNum = documents.size() > topNum ? topNum : documents.size();
        //get request info from http packet
        Vector<String> hotQueries = new Vector<String>();
        Vector<Integer> hotCounts = new Vector<Integer>();
        for(int i = 0; i < topNum; i++){
        	Document doc = documents.elementAt(i);
        	hotQueries.addElement(doc.getString("query"));
        	hotCounts.addElement(doc.getInteger("count"));
        }
        session.setAttribute("hotQueries", hotQueries);
        session.setAttribute("hotCounts", hotCounts);

        req.getRequestDispatcher("/HotQuery.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}

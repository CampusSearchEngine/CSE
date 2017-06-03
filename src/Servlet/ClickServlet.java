package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;

import HotQuery.HotQuery;
import Log.UserClickLog;

public class ClickServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();

        String URL = req.getParameter("URL");
        String query = req.getParameter("query");
       
        new UserClickLog("guest", URL, query).push();
        PrintWriter pWriter = res.getWriter();
        pWriter.println("acknowledged");
        pWriter.close();
        req.getRequestDispatcher("/HotQuery.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}

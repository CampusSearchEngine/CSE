package Servlet;

import org.json.JSONObject;
import sun.net.www.http.HttpClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.CharacterIterator;

/**
 * Created by THU73 on 17/6/3.
 */
public class MisscutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        System.out.println("into post");


        HttpSession session = req.getSession();
        String text = req.getParameter("text");
        text = URLEncoder.encode(text, "utf-8");
        try {
            //创建连接
            URL url = new URL("http://www.misscut.cn:8001/submit_text_zcy?text=" + text);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("connection", "keep-alive");
            connection.setUseCaches(false);//设置不要缓存
            connection.setInstanceFollowRedirects(true);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty
                    ("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            connection.connect();

            //读取响应
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String s = "", response = "";
            while ((s = in.readLine()) != null)
                response += s;

            PrintWriter writer = res.getWriter();
            writer.write(response);
            writer.flush();
            writer.close();

            // 断开连接
            connection.disconnect();
        } catch (IOException e) {
            System.out.println("error");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

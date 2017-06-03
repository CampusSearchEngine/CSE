package serv;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.*;
import org.apache.lucene.document.Document;

import indexSearcher.CampusSearcher;
import indexSearcher.SearcherResult;

import java.io.*;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by THU73 on 17/5/21.
 */
public class SearchServlet extends HttpServlet {
    static CampusSearcher searcher;

    static final String INDEX_PATH = "/index";
    private Vector<CosineUnit> cosineData;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        cosineData = new Vector<CosineUnit>();
        /*try {
            FileReader reader = new FileReader("cse_word2vec"); // 读取文本中内容
            BufferedReader br = new BufferedReader(reader);

            String str = br.readLine();
            String[] s = str.split(" ");
            int totalNum = Integer.parseInt(s[0]);
            int count = 0;
            while (true) {
                count++;
                if (count % 100 == 0) {
                    System.out.println(count);
                }
                str = br.readLine();
                if (str == null) break;

                s = str.split(" ");

                String name = s[0];
                Vector<Double> vec = new Vector<Double>();
                for (int i = 1; i < s.length; i++) {
                    vec.add(Double.parseDouble(s[i]));
                }

                CosineUnit cu = new CosineUnit();
                cu.setWord(name);
                cu.setValue(vec);
                cosineData.add(cu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    static void initSearcher(String workingPath) {

        searcher = new CampusSearcher(workingPath + INDEX_PATH);
        searcher.workingPath = workingPath;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (searcher == null)
            initSearcher(req.getSession().getServletContext().getRealPath("/"));

        HttpSession session = req.getSession();

        String keyStr = req.getParameter("key");
        String pageNumStr = req.getParameter("page_num");
        //get request info from http packet

        int pageNum = 1;
        String key = "";

        if (keyStr.equals("")) {
            //if request field was NULL

            res.sendRedirect("/");
            return;
        } else {
            key = keyStr;
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

        /* key: String
         * pageNum: [1, 10]
         */

        System.out.println("key: " + key + " pageNum: " + pageNum);


        SearcherResult sRst = searcher.doQuery(key, pageNum);
        Vector<Document> documents = sRst.docs;
        int totNum = sRst.totNum;
        System.out.println("actually find " + totNum + " pages");

        RequestBean rb = new RequestBean();
        rb.setKey(key);
        rb.setPageNum(pageNum);
        session.setAttribute("request", rb);

        ResultBean reb = new ResultBean();
        reb.setPage_num(pageNum);
        reb.setTotal_num(totNum);

        Vector<EntryBean> vec = new Vector<EntryBean>();
        for (Document document : documents) {
            EntryBean eb = new EntryBean();

            eb.setTitle(document.get("title"));
            eb.setAbst(document.get("content"));
            eb.setLink(document.get("URI"));
            eb.setType(document.get("type"));// html pdf doc
            vec.add(eb);
        }
        reb.setVec(vec);
        session.setAttribute("result", reb);

        HotQueryBean hqb = new HotQueryBean();
        //fill hot queries in hqb...
        session.setAttribute("hotquery", hqb);

        ImagineBean ib = new ImagineBean();
        boolean found = false;
        for (int i = 0; i < cosineData.size(); ++i) {
            if (key.equals(cosineData.elementAt(i).getWord())) {
                found = true;
                for (int j = 0; j < cosineData.size(); ++j) {
                    cosineData.elementAt(j).setDistance(cosineData.elementAt(i));
                }
                cosineData.sort(new Comparator<CosineUnit>() {
                    @Override
                    public int compare(CosineUnit o1, CosineUnit o2) {
                        double result = o1.getDistance() - o2.getDistance();
                        if (result < 0) {
                            return 1;
                        } else if (result > 0) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
                break;
            }
        }
        Vector<String> svec = new Vector<String>();
        if (found) {
            for (int i = 1; i < 10; ++i) {
                svec.add(cosineData.elementAt(i).getWord());
            }
        }
        for(int i = 0; i < svec.size(); ++i) {
            System.out.println(svec.elementAt(i));
        }
        ib.setImagineWords(svec);
        session.setAttribute("imagine", ib);

        req.getRequestDispatcher("/result.jsp").forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}

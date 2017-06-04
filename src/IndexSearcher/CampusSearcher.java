package IndexSearcher;

import java.io.*;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import HotQuery.HotQuery;
import LuceneExtends.CSSHighlighter;
import mongoDB.MongoDBs;
import PageRank.DocPRSorter;

public class CampusSearcher {
    private IndexReader reader;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private CSSHighlighter highlighter;
    private float avgLength = 1.0f;
    public String workingPath;

    static final int MAX_RESULT_NUM = 100;
    static final int RESULT_PER_PAGE = 10;
    static final int MAX_CONTENT_LEN = 200;

    public CampusSearcher(String indexdir) {
        MongoDBs.initDB();
        analyzer = new IKAnalyzer();
        try {
            reader = IndexReader.open(FSDirectory.open(new File(indexdir)));
            searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new BM25Similarity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadGlobals(indexdir + "/global.txt");
        this.highlighter = new CSSHighlighter(this.analyzer, this.avgLength);
        System.out.println("avg length = " + avgLength);
    }

    public TopDocs searchQuery(String queryString, String[] fields, int maxnum, float[] weights) {
        try {
            BooleanQuery combinedQuery = new BooleanQuery();
            // for every field and weight create a BM25 query and combine them using
            // BooleanQuery
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                QueryParser parser = new BM25QueryParser(Version.LUCENE_35, field, analyzer, avgLength);
                //QueryParser parser = new QueryParser(Version.LUCENE_35, field, analyzer);
                Query query = parser.parse(queryString);
                query.setBoost(weights[i]);

                combinedQuery.add(query, Occur.SHOULD);
            }
            System.out.println("final query : " + combinedQuery.toString());
            TopDocs results = searcher.search(combinedQuery, maxnum);
            System.out.println(results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Document getDoc(int docID) {
        try {
            return searcher.doc(docID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadGlobals(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line = reader.readLine();
            avgLength = Float.parseFloat(line);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float getAvg() {
        return avgLength;
    }

    ScoreDoc[] doQuery(String query) {
        TopDocs results = this.searchQuery(query, new String[]{"content", "title", "anchor", "URI"}, MAX_RESULT_NUM,
                new float[]{1.0f, 2.0f, 0.5f, 0.1f});

        ScoreDoc[] hits = results.scoreDocs;
        hits = DocPRSorter.sort(hits, searcher, workingPath);
        /*for (int i = 0; i < hits.length; i++) { // output raw format
			Document doc = this.getDoc(hits[i].doc);
			System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score + " title= " + doc.get("title"));
			// System.out.println(doc.get("content"));
			System.out.println(doc.get("URI"));
		}*/
        return hits;
    }

    public SearcherResult doQuery(String query, int pageNum) {
        Vector<Document> documents = new Vector<Document>();

        ScoreDoc[] sDocs = doQuery(query);
        int docSize = sDocs.length;

        int startIndex = (pageNum - 1)* RESULT_PER_PAGE;
        int endIndex = startIndex + RESULT_PER_PAGE;
        while (startIndex < docSize && startIndex < endIndex) {
            Document doc = getDoc(sDocs[startIndex++].doc);
            // do some highlight
            String content = doc.get("content");
            content = highlighter.Highlight(content, query, 100);
            if (content == null) {
                content = doc.get("content");
                content = content.substring(0, Math.min(MAX_CONTENT_LEN, content.length()));
            }
            doc.removeField("content");
            doc.add(new Field("content", content, Store.YES, Index.ANALYZED));

            String title = doc.get("title");
            title = highlighter.Highlight(title, query, 100);
            if (title == null) {
                title = doc.get("title");
                title = title.substring(0, Math.min(MAX_CONTENT_LEN, title.length()));
            }
            doc.removeField("title");
            doc.add(new Field("title", title, Store.YES, Index.ANALYZED));

            documents.add(doc);
        }

        HotQuery.updateHotQuery(query);
        return new SearcherResult(documents, docSize);
    }

    /*
     * usage: <indexPath> <query>
     */
    public static void main(String[] args) {
        CampusSearcher search = new CampusSearcher(args[0]);

        search.doQuery(args[1]);
    }
}

package indexSearcher;

import java.io.*;
import java.io.IOException;
import java.util.Vector;

import javax.print.Doc;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.lowagie.text.List;

import searcherDB.MongoDBs;
import searcherPR.DocPRSorter;

public class CampusSearcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	private float avgLength = 1.0f;
	
	static final int MAX_RESULT_NUM = 100;
	static final int RESULT_PER_PAGE = 10;

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

	ScoreDoc[] doQuery(String query){
		TopDocs results = this.searchQuery(query, new String[] { "content", "title", "anchor", "URI" }, MAX_RESULT_NUM,
				new float[] { 1.0f, 2.0f, 0.5f, 0.1f });
		
		ScoreDoc[] hits = results.scoreDocs;
		hits = DocPRSorter.sort(hits, searcher);
		/*for (int i = 0; i < hits.length; i++) { // output raw format
			Document doc = this.getDoc(hits[i].doc);
			System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score + " title= " + doc.get("title"));
			// System.out.println(doc.get("content"));
			System.out.println(doc.get("URI"));
		}*/
		return hits;
	}
	
	public Vector<Document> doQuery(String query, int pageNum) {
		Vector<Document> documents = new Vector<Document>();
		
		ScoreDoc[] sDocs = doQuery(query);
		int docSize = sDocs.length;
		
		int startIndex = pageNum * RESULT_PER_PAGE;
		int endIndex = startIndex + RESULT_PER_PAGE;
		while(startIndex < docSize && startIndex < endIndex)
			documents.add(getDoc(sDocs[startIndex++].doc));
		
		return documents;
	}
	
	/*
	 * usage: <indexPath> <query>
	 */
	public static void main(String[] args) {
		CampusSearcher search = new CampusSearcher(args[0]);
		
		search.doQuery(args[1]);
	}
}

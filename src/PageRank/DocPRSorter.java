package PageRank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import mongoDB.MongoDBs;


/*
 * use page rank to sort lucene TopDocs
 * */
public class DocPRSorter {
	
	static Vector<Page> pages;
	
	static final String PR_LIST_FILE = "/prList";
	static final float a = 0.95f, b = 0.05f;		// final score for a doc is a*luceneScore + b*PageRank£¨percentiled£©
	
	/*
	 * use linear combination of lucene score and page rank
	 * @param originDocs : the doc array to be sorted
	 * @Param searcher	 : the searcher that gave the docs, used to get page ID and PageRank 
	 * */
	public static ScoreDoc[] sort(ScoreDoc[] originDocs, IndexSearcher searcher, String workingPath){
		// compute percentile pageRank
		float[] pageRanks = new float[originDocs.length];
		float maxPR = 0.0f;
		for(int i = 0; i < pageRanks.length; i++){
			try {
				org.apache.lucene.document.Document document = searcher.doc(originDocs[i].doc);
				int id = Integer.parseInt(document.get("ID"));
				pageRanks[i] = MemGetPR(id,workingPath);
				maxPR = maxPR < pageRanks[i] ? pageRanks[i] : maxPR;
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(int i = 0; i < pageRanks.length; i++){
			pageRanks[i] = pageRanks[i] / maxPR;
		}
		
		//compute percentile lucene score and final score
		float maxScore = 0.0f;
		for(int i = 0; i < originDocs.length; i++){
			maxScore = maxScore < originDocs[i].score ? originDocs[i].score : maxScore;
		}
		for(int i = 0; i < originDocs.length; i++){
			originDocs[i].score = (originDocs[i].score / maxScore)*a + b*pageRanks[i]; 
		}
		
		// resort by final score
		Arrays.sort(originDocs, new Comparator<ScoreDoc>() {

			@Override
			public int compare(ScoreDoc o1, ScoreDoc o2) {
				return	o1.score > o2.score ? -1 : 1;
			}
		});
		
		return originDocs;
	}
	
	static float MemGetPR(int pageID, String workingPath){
		if(pages == null){
			readPages(workingPath + PR_LIST_FILE);
		}
		return ((Double)(pages.get(pageID).pageRank)).floatValue();
	}
	
	static void readPages(String filename) {
		int pageCnt = 0;
		System.out.println("reading pages...");
		pages = new Vector<Page>();
		try {
			File file = new File(filename);
			//System.out.println(file.getAbsolutePath());
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader iReader = new InputStreamReader(fileInputStream,"utf-8");
			BufferedReader bReader = new BufferedReader(iReader);
			String line;
			
			while((line = bReader.readLine()) != null){
				String[] parts = line.split("<sep>");
				int id = Integer.parseInt(parts[0]);
				String title = parts[1];
				double pageRank = Double.parseDouble(parts[2]);
				int outDegree = Integer.parseInt(parts[3]);
				int inDegree = Integer.parseInt(parts[4]);
				
				Page page = new Page(title, id);
				page.pageRank = pageRank;
				page.inDegree = inDegree;
				page.outDegree = outDegree;
				
				pages.addElement(page);
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pageCnt = pages.size();
		System.out.println(pageCnt + " pages read");
	}
	
	static float MongoGetPR(int pageID){
		FindIterable<Document> iter = MongoDBs.pages.find(Filter.eq("ID", pageID));
		MongoCursor<Document> cursor = iter.iterator();
		Document document = null;
		if(cursor.hasNext()) {
			document = cursor.next();
		} else {
			return 0;
		}
		return Float.valueOf(document.get("pageRank").toString());
	}
}

package searcherPR;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.print.Doc;

import org.apache.lucene.LucenePackage;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import searcherDB.MongoDBs;
import sun.print.resources.serviceui;


/*
 * use page rank to sort lucene TopDocs
 * */
public class DocPRSorter {
	
	static final float a = 0.95f, b = 0.05f;		// final score for a doc is a*luceneScore + b*PageRank£¨percentiled£©
	
	/*
	 * use linear combination of lucene score and page rank
	 * @param originDocs : the doc array to be sorted
	 * @Param searcher	 : the searcher that gave the docs, used to get page ID and PageRank 
	 * */
	public static ScoreDoc[] sort(ScoreDoc[] originDocs, IndexSearcher searcher){
		// compute percentile pageRank
		float[] pageRanks = new float[originDocs.length];
		float maxPR = 0.0f;
		for(int i = 0; i < pageRanks.length; i++){
			try {
				org.apache.lucene.document.Document document = searcher.doc(originDocs[i].doc);
				int id = Integer.parseInt(document.get("ID"));
				pageRanks[i] = MongoGetPR(id);
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
	
	static float MongoGetPR(int pageID){
		FindIterable<Document> iter = MongoDBs.pages.find(Filters.eq("ID", pageID));
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

package IndexSearcher;

import java.util.Vector;

import org.apache.lucene.document.Document;

public class SearcherResult {
	public Vector<Document> docs;
	public int totNum;
	
	public SearcherResult(Vector<Document> docs, int totNum) {
		this.docs = docs;
		this.totNum = totNum;
	}
}

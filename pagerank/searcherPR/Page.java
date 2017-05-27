package searcherPR;
import java.util.Vector;

public class Page {
	String title;
	int id;
	int outDegree;
	int inDegree;
	double pageRank;
	
	Vector<Integer> outPages;
	Vector<Integer> inPages;
	
	public Page(String title, int id) {
		this.title = title;
		this.id = id;
		
		this.outPages = new Vector<Integer>();
		this.inPages = new Vector<Integer>();
	}
}

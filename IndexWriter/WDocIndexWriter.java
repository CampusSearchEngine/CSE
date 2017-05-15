import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/*
 * write lucene index using extracted document folder
 * */
public class WDocIndexWriter {
	String docPath;
	String indexPath;
	
	private Analyzer analyzer;
	private IndexWriter indexWriter;
	
	private double averageLength = 1.0;
	
	public WDocIndexWriter(String docPath, String indexPath) {
		this.docPath = docPath;
		this.indexPath = indexPath;
		analyzer = new IKAnalyzer();
		try {
			IndexWriterConfig iConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
			Directory dir = FSDirectory.open(new File(indexPath));
    		indexWriter = new IndexWriter(dir, iConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doWrite(){
		DirIter mIter = new DirIter(docPath);
		try {
			while(mIter.hasNext()){
				String file = mIter.next();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

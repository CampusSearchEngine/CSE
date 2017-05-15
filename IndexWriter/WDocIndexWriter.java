import java.io.BufferedReader;
import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONObject;
import org.wltea.analyzer.lucene.IKAnalyzer;

import jdk.nashorn.internal.parser.JSONParser;

/*
 * write lucene index using extracted document folder
 * */
public class WDocIndexWriter {
	String docPath;
	String indexPath;
	
	private Analyzer analyzer;
	private IndexWriter indexWriter;
	
	private double averageLength = 0.0;
	
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
		DirIter dIter = new DirIter(docPath);
		try {
			while(dIter.hasNext()){
				String filename = dIter.next();
				if(FileValidator.valiWDOC(filename)){
					BufferedReader bReader = IO.getReader(filename);
					String line;
					String jsonStr = new String();
					while((line = bReader.readLine()) != null)
							jsonStr += line;
					bReader.close();
					
					JSONObject json = new JSONObject(jsonStr);
					Document document = AllDocWriter.Json2Doc(json);
					String contentString = document.get("content");
					averageLength += contentString.length();
					indexWriter.addDocument(document);
				}
			}
			averageLength /= indexWriter.numDocs();
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

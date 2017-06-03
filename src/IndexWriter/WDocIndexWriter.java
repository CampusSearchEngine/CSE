package IndexWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONObject;
import org.wltea.analyzer.lucene.IKAnalyzer;

import DocumentWriter.AllDocWriter;
import StructAnalyze.FileValidator;
import Utils.DirIter;
import Utils.IO;


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
			iConfig.setOpenMode(OpenMode.CREATE);
			Directory dir = FSDirectory.open(new File(indexPath));
    		indexWriter = new IndexWriter(dir, iConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * write lucene index using extracted document folder
	 * */
	public void doWrite(){
		DirIter dIter = new DirIter(docPath);
		int count = 0;
		String filename = new String();
		try {
			while(dIter.hasNext()){
				count++;
				if((count % 10000) == 0)
					System.out.println(count + "/" + dIter.getCount() + " wdocs are indexed");
				
				filename = dIter.next();
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
			
			BufferedWriter bWriter = IO.getWriter(indexPath + "/global.txt");
			bWriter.write("" + averageLength +"\n");
			bWriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("caused by " + filename);
		}
	}
	
	/*
	 * usage : <docPath> <indexPath>
	 * <docPath> : the path where .wdoc files are stored
	 * <indexPath> : the path where the lucene index should be placed
	 * */
	static public void main(String[] args){
		WDocIndexWriter wDocIndexWriter = new WDocIndexWriter(args[0], args[1]);
		wDocIndexWriter.doWrite();
	}
}


import java.awt.color.ICC_ColorSpace;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.InitialContext;

import org.bson.Document;
import org.omg.PortableInterceptor.AdapterManagerIdHelper;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import com.mongodb.client.model.Filters;
import com.sun.org.apache.bcel.internal.generic.NEW;
/*
 * use sequence:
 * 	PageRanker() --> buildPageMap() --> initPageRank() --> updateAll() --> writePageRank()
 */
public class PageRanker {
	HashMap<Integer, Page> pageMap;
	HashMap<Integer, Double> increments; // record every page's pagerank update
	
	static final String separator = "<sep>";

	int pageCnt; // number of pages
	int iterCnt; // times need to iter
	double alpha; // prob of random walk
	double sumNoOut; // sum of pageRanks with 0 outdegree
	double nextSumNoOut; // sumNoOut for next iter

	public PageRanker(double alpha, int iterCnt) {
		this.alpha = alpha;
		this.iterCnt = iterCnt;
		this.increments = new HashMap<Integer,Double>();
	}

	void initPageRank() {
		System.out.println("initint PageRanks...");
		Iterator iterator = pageMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Page page = (Page) entry.getValue();
			
			page.pageRank = 1 / pageCnt;
			increments.put(page.id, alpha / pageCnt);
			if (page.outDegree == 0)
				nextSumNoOut += page.pageRank;
		}
	}

	void buildPageMap(String pageListFile, String pageMapFile) {
		this.pageMap = PageMapBuilder.buildPageMap(pageListFile, pageMapFile);
		this.pageCnt = pageMap.size();
	}

	void update() {
		sumNoOut = nextSumNoOut;
		nextSumNoOut = 0;

		Iterator iterator = pageMap.entrySet().iterator();
		// update increments
		System.out.println("updating increments...");
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Page page = (Page) entry.getValue();
		
			for (Integer id : page.outPages) {
				Double inc = increments.get(id);
				if(inc == null)		// a page is pointed to but it is not in the page list, ignore it
					continue;
				inc = inc + (1 - alpha) * page.pageRank / page.outDegree;
				increments.put(id, inc);
			}
		}
		
		// use increments to update pagerank and reset increments
		System.out.println("updating pageranks...");
		iterator = pageMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Page page = (Page) entry.getValue();
			
			Double inc = increments.get(page.id);
			page.pageRank = inc + (1 - alpha) * sumNoOut / pageCnt;
			inc = alpha / pageCnt;
			increments.put(page.id, inc);
			
			if(page.outDegree == 0)
				nextSumNoOut += page.pageRank;
		}
	}
	
	public void updateAll(){
		for(int i = 0; i < iterCnt; i++){
			System.out.println("ROUND " + (i+1) + " ...");
			update();
		}
			
	}
	
	void writePageRank(String filename){
		System.out.println("writing PageRanks..");
		try {
			File file = new File(filename);
			if(file.getParentFile() != null && !file.getParentFile().exists())
				file.getParentFile().mkdirs();
			FileOutputStream fOutputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOutputStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(outputStreamWriter);
			
			Iterator iterator = pageMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				Page page = (Page) entry.getValue();
				
				bWriter.write(page.id + separator + page.title + separator + page.pageRank  + separator + page.outDegree + separator + page.inDegree + "\n");
			}
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void wrtiePageRank2DB(){
		MongoDBs.initDB();
		Iterator iterator = pageMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Page page = (Page) entry.getValue();
			if(page.id == 2811)
				System.out.println(page.title + " " + page.pageRank);
			MongoDBs.pages.findOneAndUpdate(Filters.eq("ID", page.id), new Document("$set",new Document("pageRank",page.pageRank)));
		}
	}
	
	void showPage(int id){
		Page page = pageMap.get(id);
		System.out.println("the page is " + page.title + " is pointed by");
		for(Integer cid : page.inPages){
			page = pageMap.get(cid);
			System.out.println(cid + " : " +page.title);
		}
	}
	
	public static void main(String[] args){
		if(args.length < 5){
			System.out.println("USAGE: <pageListFile> <pageMapFile> <rounds_of_iterations> <alpha> <outputFile>");
			return;
		}
		
		PageRanker pageRanker = new PageRanker(Double.parseDouble(args[3]), Integer.parseInt(args[2]));
		pageRanker.buildPageMap(args[0], args[1]);
		pageRanker.initPageRank();
		pageRanker.updateAll();
		pageRanker.wrtiePageRank2DB();
		pageRanker.writePageRank(args[4]);
		System.out.println("pageRanker task over");
		//pageRanker.showPage(25883781);
	}
}

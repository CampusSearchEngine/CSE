package searcherPR;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.Vector;

import javax.print.attribute.standard.PagesPerMinute;
import javax.xml.parsers.DocumentBuilder;

public class PRAnalyzer {
	Vector<Page> pages;
	
	int pageCnt;
	double minPageRank;
	double maxPageRank;
	
	public PRAnalyzer() {
		this.pages = new Vector<Page>();
	}
	
	void readPages(String filename) {
		System.out.println("reading pages...");
		try {
			File file = new File(filename);
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
	
	/*
	 * evenly divide the interval[minPageRank maxPageRank] into <intervalNum> intervals
	 * save non-empty intervals' bounds, pageNum, avaragePR into <outputFile>
	 */
	void analyzeDistribution(int intervalNum, String outputFile){
		assert(intervalNum > 0);
		System.out.println("analyzing distribution...");
		Vector<Page>[] pageIntervals = new Vector[intervalNum];
		double[] averages = new double[intervalNum];
		for(int i = 0; i < intervalNum; i++)
			pageIntervals[i] = new Vector<Page>();
		// ascend sort by pagerank
		pages.sort(new Comparator<Page>() {

			@Override
			public int compare(Page o1, Page o2) {
				if(o1.pageRank > o2.pageRank)
					return 1;
				else if(o1.pageRank < o2.pageRank)
					return -1;
				return 0;
			}
		});
		
		minPageRank = pages.firstElement().pageRank;
		maxPageRank = pages.lastElement().pageRank;
		double step = (maxPageRank - minPageRank) / intervalNum;
		
		//distribute every page into interval
		for(int i = 0; i < pageCnt; i++) {
			Page page = pages.elementAt(i);
			int pageTeam = ((int) Math.ceil(((page.pageRank - minPageRank)/step))) - 1; // the interval the page belongs to
			if(pageTeam == -1)
				pageTeam = 0;
			if(pageTeam == intervalNum)
				pageTeam --;
			pageIntervals[pageTeam].addElement(page);
			averages[pageTeam] += page.pageRank;
		}
		
		for(int i = 0; i < intervalNum; i++){
			averages[i] /= pageIntervals[i].size();
		}
		
		try {
			File file = new File(outputFile);
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter oWriter = new OutputStreamWriter(fStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(oWriter);
			int nonEmptyIntervalNum = 0;
			for(int i = 0; i < intervalNum; i++){
				if(pageIntervals[i].size() == 0)
					continue;
				double lowerBound = minPageRank + step*i;
				double upperBound = lowerBound + step;
				bWriter.write(lowerBound + "," + upperBound + "," + pageIntervals[i].size() + "," + averages[i] + "," + averages[i]*pageIntervals[i].size() + "\n");
				nonEmptyIntervalNum++;
			}
			System.out.println(nonEmptyIntervalNum + " intervals are non-empty");
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * save [indegree, averagePR, maxPR, minPR, pageCnt]  into <outputFile>
	 */
	void analyzeIndegPR(String outputFile){
		System.out.println("analyzing indegree");
		//sort ascend by indegree
		pages.sort(new Comparator<Page>() {

			@Override
			public int compare(Page o1, Page o2) {
				if(o1.inDegree > o2.inDegree)
					return 1;
				else if(o1.inDegree < o2.inDegree)
					return -1;
				return 0;
			}
		});
		
		int maxIndegree = pages.lastElement().inDegree;
		
		double[] averages = new double[maxIndegree + 1];
		double[] maxPRs = new double[maxIndegree + 1];
		double[] minPRs = new double[maxIndegree + 1];
		for(int i = 0; i <= maxIndegree; i++)
			minPRs[i] = Double.MAX_VALUE;
		int[] pageCnts = new int[maxIndegree + 1];
		
		for(int i = 0; i < pages.size(); i++){
			Page page = pages.elementAt(i);
			int indegree = page.inDegree;
			averages[indegree] += page.pageRank;
			maxPRs[indegree] = maxPRs[indegree] > page.pageRank ? maxPRs[indegree] : page.pageRank;
			minPRs[indegree] = minPRs[indegree] < page.pageRank ? minPRs[indegree] : page.pageRank;
			pageCnts[indegree] ++;
		}
		
		for(int i = 0; i < maxIndegree; i++){
			averages[i] /= pageCnts[i];
		}
		
		try {
			File file = new File(outputFile);
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter oWriter = new OutputStreamWriter(fStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(oWriter);
			for(int i = 0; i <= maxIndegree; i++){
				if(pageCnts[i] == 0)
					continue;
				bWriter.write(i + "," + pageCnts[i] + "," + averages[i] + "," + maxPRs[i] + "," + minPRs[i] + "\n");
			}
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * save [outdegree, averagePR, maxPR, minPR, pageCnt]  into <outputFile>
	 */
	void analyzeOutdegPR(String outputFile){
		System.out.println("analyzing outdegree");
		//sort ascend by outdegree
		pages.sort(new Comparator<Page>() {

			@Override
			public int compare(Page o1, Page o2) {
				if(o1.outDegree > o2.outDegree)
					return 1;
				else if(o1.outDegree < o2.outDegree)
					return -1;
				return 0;
			}
		});
		
		int maxOutdegree = pages.lastElement().outDegree;
		
		double[] averages = new double[maxOutdegree + 1];
		double[] maxPRs = new double[maxOutdegree + 1];
		double[] minPRs = new double[maxOutdegree + 1];
		for(int i = 0; i <= maxOutdegree; i++)
			minPRs[i] = Double.MAX_VALUE;
		int[] pageCnts = new int[maxOutdegree + 1];
		
		for(int i = 0; i < pages.size(); i++){
			Page page = pages.elementAt(i);
			int outdegree = page.outDegree;
			averages[outdegree] += page.pageRank;
			maxPRs[outdegree] = maxPRs[outdegree] > page.pageRank ? maxPRs[outdegree] : page.pageRank;
			minPRs[outdegree] = minPRs[outdegree] < page.pageRank ? minPRs[outdegree] : page.pageRank;
			pageCnts[outdegree] ++;
		}
		
		for(int i = 0; i < maxOutdegree; i++){
			averages[i] /= pageCnts[i];
		}
		
		try {
			File file = new File(outputFile);
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter oWriter = new OutputStreamWriter(fStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(oWriter);
			for(int i = 0; i <= maxOutdegree; i++){
				if(pageCnts[i] == 0)
					continue;
				bWriter.write(i + "," + pageCnts[i] + "," + averages[i] + "," + maxPRs[i] + "," + minPRs[i] + "\n");
			}
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * extract the <anlyzeNum> pages with min and max PR into <outputFile>
	 */
	void analyzeMinMax(int analyzeNum, String outputFile){
		assert(analyzeNum <= pages.size());
		System.out.println("analyzing MinMax");
		pages.sort(new Comparator<Page>() {

			@Override
			public int compare(Page o1, Page o2) {
				if(o1.pageRank > o2.pageRank)
					return 1;
				else if(o1.pageRank < o2.pageRank)
					return -1;
				return 0;
			}
		});
		
		try {
			File file = new File(outputFile);
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter oWriter = new OutputStreamWriter(fStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(oWriter);
			for(int i = 0; i < analyzeNum; i++){
				Page page = pages.elementAt(i);
				bWriter.write(page.id + "," + page.title + "," + page.pageRank  + "," + page.outDegree + "," + page.inDegree + "\n");
			}
			for(int i = pages.size() - analyzeNum; i < pages.size(); i++){
				Page page = pages.elementAt(i);
				bWriter.write(page.id + "," + page.title + "," + page.pageRank  + "," + page.outDegree + "," + page.inDegree + "\n");
			}
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		if(args.length < 1){
			System.out.println("USAGE: <pageRankFile>");
			return;
		}
		
		PRAnalyzer prAnalyzer = new PRAnalyzer();
		prAnalyzer.readPages(args[0]);
		prAnalyzer.analyzeDistribution(50000, "pr_distrib.csv");
		prAnalyzer.analyzeIndegPR("pr_indegree.csv");
		prAnalyzer.analyzeOutdegPR("pr_outdegree.csv");
		prAnalyzer.analyzeMinMax(20, "pr_minmax.txt");
	}
}

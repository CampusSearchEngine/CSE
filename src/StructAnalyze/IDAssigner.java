package StructAnalyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bson.Document;

import mongoDB.MongoDBs;
import Utils.DirIter;



public class IDAssigner {
	HashMap<String, Integer> IDMap;
	
	public IDAssigner() {
	}
	
	/*
	 * assign unique ID for every valid URI (html doc pdf ¡£¡£¡££©
	 * */
	public HashMap<String, Integer> assignIDForMirror(String MirrorPath) {
		IDMap = new HashMap<String, Integer>();
		int maxID = -1;
		
		DirIter iter = new DirIter(MirrorPath);
		while(iter.hasNext()){
			String URI = iter.next();
			URI = URI.substring(MirrorPath.length()+1);
			int type = FileValidator.validate(URI);
			if(type != FileValidator.INVALID && type != FileValidator.WDOC)
				IDMap.put(URI, ++maxID);
		}
		return IDMap;
	}
	
	/*
	 * write every line as "URI-->ID-->type" for each URI into specified path
	 * */
	public void writeIDMap(String path) {
		try {
			File file = new File(path);
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(outputStreamWriter);
			
			Iterator<Entry<String, Integer>> iterator = IDMap.entrySet().iterator();
			int count = 0;
			while (iterator.hasNext()) {
				if(count++ % 10000 == 0)
					System.out.println(count + "/" + IDMap.size() + " Entires Written");
				Entry<String, Integer> entry = iterator.next();
				int type = FileValidator.validate(entry.getKey());
				if(type != FileValidator.INVALID && type != FileValidator.WDOC)
					bWriter.write(entry.getKey() + "-->" + entry.getValue() + "-->" + type + "\n");
			}
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeIDMap2DB() {
		MongoDBs.initDB();
		List<Document> list = new ArrayList<Document>();
		try {
			
			Iterator<Entry<String, Integer>> iterator = IDMap.entrySet().iterator();
			int count = 0;
			while (iterator.hasNext()) {
				if(count++ % 10000 == 0)
					System.out.println(count + "/" + IDMap.size() + " Entires Written to DB");
				Entry<String, Integer> entry = iterator.next();
				int type = FileValidator.validate(entry.getKey());
				if(type != FileValidator.INVALID && type != FileValidator.WDOC){
					Document document = new Document();
					document.append("ID", entry.getValue()).append("URI", entry.getKey());
					document.append("click", 0).append("pageRank", 0.0f);
					list.add(document);
				}
			}
			
			MongoDBs.pages.insertMany(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * usage : <mirrorPath> <output>
	 * */
	public static void main(String[] args){
		IDAssigner idAssigner = new IDAssigner();
		idAssigner.assignIDForMirror(args[0]);
		idAssigner.writeIDMap(args[1]);
		idAssigner.writeIDMap2DB();
	}
}

package searcherPageExtract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.tools.ExtractText;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.json.JSONException;
import org.json.JSONObject;

import searcherUtils.IO;

public class DOCExtractor implements Extractor {

	@Override
	public void extract(String URI, String toPath, String encoding, int ID, String mirrorPath) {
		boolean isDocx = false;
		if(URI.endsWith(".docx")){
			isDocx = true;
		}
		 
		try {
			String text = new String();
			if(isDocx){
				OPCPackage opcPackage = POIXMLDocument.openPackage(URI);
				XWPFWordExtractor wExtractor = new XWPFWordExtractor(opcPackage);
				
				text = wExtractor.getText();
			}
			else{	
				InputStream is = new FileInputStream(new File(URI));  
				POIFSFileSystem pSystem = new POIFSFileSystem(is);
	            WordExtractor ex = new WordExtractor(pSystem);  
	            text = ex.getText();  
			}
			
			String[] lines = text.split("\n");
			String titleStr = lines[0];
			String conStr = new String();
			for(int i = 1; i < lines.length; i++)
				conStr += lines[i];
			
			URI = URI.substring(mirrorPath.length()+1);
			JSONObject json = new JSONObject();
			json.put("type", "doc");
			json.put("title", titleStr);
			json.put("URI", URI);
			json.put("content", conStr);
			json.put("ID", ID);
			
			
			BufferedWriter bWriter = IO.getWriter(toPath);
			bWriter.write(json.toString());
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-->: " + URI);
		}
	}
	
	static public void main(String[] args){
		DOCExtractor pExtractor = new DOCExtractor();
		pExtractor.extract(args[0], args[1], "utf-8", 0, "");
	}

}

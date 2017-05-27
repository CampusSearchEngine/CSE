package searcherPageExtract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.pdfbox.tools.ExtractText;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.json.JSONException;
import org.json.JSONObject;

import searcherUtils.IO;

/*
 *	extract the content in a pdf into a json
 *  current elements:
 *  <type>		: pdf
 *  <title> 	: pdf title (first line in txt file)
 *  <content> 	: pdf content (remaining lines)
 *  <URI>		: remote URI
 * */
public class PDFExtractor implements Extractor {

	@Override
	public void extract(String URI, String toPath, String encoding, int ID) {
		String txtDoc = URI.substring(0, URI.length()-3) + "txt";
		try {
			ExtractText.main(new String[]{URI, txtDoc});
			
			BufferedReader bReader = IO.getReader(txtDoc);
			String titleStr = bReader.readLine();
			String conStr = new String();
			String line;
			while((line = bReader.readLine()) != null){
				conStr += line;
			}
			bReader.close();
			
			JSONObject json = new JSONObject();
			json.put("type", "pdf");
			json.put("title", titleStr);
			json.put("URI", URI);
			json.put("content", conStr);
			json.put("ID", ID);
			
			
			BufferedWriter bWriter = IO.getWriter(toPath);
			bWriter.write(json.toString());
			bWriter.close();
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			System.out.println("-->: " + URI);
		}
	}
	
	static public void main(String[] args){
		PDFExtractor pExtractor = new PDFExtractor();
		pExtractor.extract(args[0], args[1], "utf-8", 0);
	}
}

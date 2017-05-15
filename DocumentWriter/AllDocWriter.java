import org.apache.lucene.document.Document;
import org.json.JSONException;
import org.json.JSONObject;
/*
 * find matching DocWriter according to type field
 * */
public class AllDocWriter {
	static HTMLDocWriter htmlDocWriter = new HTMLDocWriter();
	static PDFDocWriter pdfDocWriter = new PDFDocWriter();
	static WORDDocWriter wordDocWriter = new WORDDocWriter();
	
	static Document Json2Doc(JSONObject json){
		try {
			String type = json.getString("type");
			if(type.equals("html"))
				return htmlDocWriter.Json2Doc(json);
			if(type.equals("pdf"))
				return pdfDocWriter.Json2Doc(json);
			if(type.equals("doc"))
				return pdfDocWriter.Json2Doc(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

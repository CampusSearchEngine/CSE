
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.json.JSONObject;



/*
 * convert a extracted pdf to lucene document with fields :
 * <title> 		pdf title
 * <content>	pdf content with no format info
 * <URI> 		remote URI of the pdf
 * <type> 		pdf
 * */
public class PDFDocWriter implements DocumentWriter {

	@Override
	public Document Json2Doc(JSONObject json) {
		Document document = new Document();
		
		try {
			Field titleField = new Field("title", json.getString("title"), Store.YES, Index.NOT_ANALYZED);
			Field contentField = new Field("content", json.getString("content"),Store.YES,Index.ANALYZED);
			Field URIField = new Field("URI", json.getString("URI"), Store.YES, Index.NOT_ANALYZED);
			Field typeField = new Field("type", "pdf", Store.YES, Index.NOT_ANALYZED);
			
			document.add(titleField);
			document.add(contentField);
			document.add(URIField);
			document.add(typeField);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return document;
	}

}

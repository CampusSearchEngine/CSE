import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * convert a extracted html to lucene document with fields :
 * <title> 		html title
 * <content>	html content with no format info
 * <anchor> 	anchor text in the html
 * <URI> 		remote URI of the html
 * <type> 		html
 * */
public class HTMLDocWriter implements DocumentWriter {

	@Override
	public Document Json2Doc(JSONObject json) {
		Document document = new Document();
		
		try {
			Field titleField = new Field("title", json.getString("title"), Store.YES, Index.NOT_ANALYZED);
			Field contentField = new Field("content", json.getString("content"), Store.YES, Index.ANALYZED);
			Field anchorField = new Field("anchor", json.getString("anchor"), Store.YES, Index.ANALYZED);
			Field URIField = new Field("URI", json.getString("URI"), Store.YES, Index.NOT_ANALYZED);
			Field typeField = new Field("type", "html", Store.YES, Index.NOT_ANALYZED);
			Field IDField = new Field("ID", json.getString("ID"), Store.YES, Index.NOT_ANALYZED);
			
			document.add(titleField);
			document.add(contentField);
			document.add(anchorField);
			document.add(URIField);
			document.add(typeField);
			document.add(IDField);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return document;
	}

}

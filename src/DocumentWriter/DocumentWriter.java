package DocumentWriter;
import org.apache.lucene.document.Document;
import org.json.JSONObject;

/*
 * convert an object to lucene Document
 * */
public interface DocumentWriter {
	public Document Json2Doc(JSONObject json);
}

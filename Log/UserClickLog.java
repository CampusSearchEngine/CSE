import org.bson.Document;

public class UserClickLog implements Log {

	Document log;
	
	@Override
	public void push() {
		MongoDBs.logs.insertOne(log);
	}
	
	public UserClickLog(String userName, String clickURI, String query){
		log = new Document();
		log.put("action", "click");
		log.put("userName",userName);
		log.put("query", query);
		log.put("URI", clickURI);
	}

}

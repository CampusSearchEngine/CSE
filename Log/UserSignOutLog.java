import org.bson.Document;

public class UserSignOutLog implements Log {
Document log;
	
	@Override
	public void push() {
		MongoDBs.logs.insertOne(log);
	}
	
	public UserSignOutLog(String userName){
		log = new Document();
		log.put("action", "signOut");
		log.put("userName",userName);
		log.put("timeStamp", System.currentTimeMillis());
	}
}

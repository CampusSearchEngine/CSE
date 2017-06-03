package Log;

import org.bson.Document;

import mongoDB.MongoDBs;

public class UserSignInLog implements Log {
Document log;
	
	@Override
	public void push() {
		MongoDBs.logs.insertOne(log);
	}
	
	public UserSignInLog(String userName){
		log = new Document();
		log.put("action", "signIn");
		log.put("userName",userName);
		log.put("timeStamp", System.currentTimeMillis());
	}
}

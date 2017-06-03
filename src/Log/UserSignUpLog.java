package Log;

import org.bson.Document;

import mongoDB.MongoDBs;

public class UserSignUpLog implements Log {

Document log;
	
	@Override
	public void push() {
		MongoDBs.logs.insertOne(log);
	}
	
	public UserSignUpLog(String userName){
		log = new Document();
		log.put("action", "signUp");
		log.put("userName",userName);
		log.put("timeStamp", System.currentTimeMillis());
	}
}

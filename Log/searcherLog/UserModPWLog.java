package searcherLog;

import org.bson.Document;

import searcherDB.MongoDBs;

public class UserModPWLog implements Log {
Document log;
	
	@Override
	public void push() {
		MongoDBs.logs.insertOne(log);
	}
	
	public UserModPWLog(String userName){
		log = new Document();
		log.put("action", "ModifyPW");
		log.put("userName",userName);
		log.put("timeStamp", System.currentTimeMillis());
	}
}

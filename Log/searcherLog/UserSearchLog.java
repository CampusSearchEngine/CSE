package searcherLog;

import org.bson.Document;

import searcherDB.MongoDBs;

public class UserSearchLog implements Log{

	Document log;
	
	@Override
	public void push() {
		MongoDBs.logs.insertOne(log);
	}
	
	public  UserSearchLog(String userName, String query){
		log = new Document();
		log.put("action", "search");
		log.put("userName",userName);
		log.put("query", query);
	}
}

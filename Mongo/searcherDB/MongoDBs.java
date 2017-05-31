package searcherDB;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.org.apache.regexp.internal.recompile;

public class MongoDBs {
	static MongoClient client;
	
	public static MongoDatabase mainDB;
	public static MongoCollection<Document> pages;
	public static MongoCollection<Document> users;
	public static MongoCollection<Document> logs;
	public static MongoCollection<Document> queries;
	
	static final String HOST = "localhost";
	static final int PORT = 27017;
	
	public static void initDB(){
		if(client != null)
			return;
		try {
			client = new MongoClient(HOST, PORT);
			
			mainDB = client.getDatabase("mainDB");
			pages = mainDB.getCollection("pages");
			users = mainDB.getCollection("users");
			logs = mainDB.getCollection("logs");
			queries = mainDB.getCollection("quries");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

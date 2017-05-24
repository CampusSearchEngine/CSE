import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.org.apache.regexp.internal.recompile;

public class MongoDBs {
	static MongoClient client;
	
	static MongoDatabase mainDB;
	static MongoCollection<Document> pages;
	static MongoCollection<Document> users;
	
	static final String HOST = "localhost";
	static final int PORT = 27017;
	
	static void initDB(){
		if(client != null)
			return;
		try {
			client = new MongoClient(HOST, PORT);
			
			mainDB = client.getDatabase("mainDB");
			pages = mainDB.getCollection("pages");
			users = mainDB.getCollection("users");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

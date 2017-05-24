import java.util.Vector;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class UserOperations {
	
	static final int DATABASE_EXCEPTION = -1;
	static final int SUCCEEDED = 0;
	static final int NO_SUCH_USER = 1;
	static final int WRONG_PASSWORD = 2;
	static final int USER_ALREADY_EXIST = 3;
	
	static SessionManager sManager = new SessionManager(); 
	
	/*
	 * test if a userName exists
	 * @param userName : userName
	 * return USER_ALREADY_EXIST when user exist
	 * 		  NO_SUCH_USER¡¡when no users found
	 * 		  DATABASE_EXCEPTION otherwise
	 * */
	static int testUserName(String userName){
		try {
			FindIterable<Document> iter = MongoDBs.users.find(Filters.eq("userName", userName));
			MongoCursor<Document> cursor = iter.iterator();
			return cursor.hasNext() ? USER_ALREADY_EXIST : NO_SUCH_USER;
		} catch (Exception e) {
			e.printStackTrace();
			return DATABASE_EXCEPTION;
		}
	}
	
	/*
	 * create a user in database
	 * @param userName : userName
	 * @param passWord : passWord 
	 * @param userInfo : other information in BSON, can be null
	 * return SUCCEED when succeed to add user and generate a log
	 * 		  DATABASE_EXCEPTION otherwise
	 * */
	static int addUser(String userName, String passWord, Document userInfo){
		try {
			Document user = new Document();
			user.put("userName", userName);
			user.put("passWord", passWord);
			if(userInfo != null)
				user.put("userInfo", userInfo);
			MongoDBs.users.insertOne(user);
			new UserSignUpLog(userName).push();
			
			return SUCCEEDED;
		} catch (Exception e) {
			e.printStackTrace();
			return DATABASE_EXCEPTION;
		}
	}
	
	/*
	 * sign in for a user
	 * @param userName : userName
	 * @param passWord : passWord 
	 * return SUCCEED when user signed in and generate a log
	 * 		  NO_SUCH_USER when no user found
	 * 		  WRONG_PASSWORD when the passWord does not match
	 * 		  DATABASE_EXCEPTION otherwise
	 * */
	static int signInUser(String userName, String passWord){
		try {
			FindIterable<Document> iter = MongoDBs.users.find(Filters.eq("userName", userName));
			MongoCursor<Document> cursor = iter.iterator();
			if(!cursor.hasNext())
				return NO_SUCH_USER;
			Document user = cursor.next();
			boolean match = user.get("passWord").equals(passWord);
			if(match){
				new UserSignInLog(userName).push();
				sManager.signIn(userName);
			}	
			return  match ? SUCCEEDED : WRONG_PASSWORD;
		} catch (Exception e) {
			e.printStackTrace();
			return DATABASE_EXCEPTION;
		}
	}
	
	/*
	 * sign out for a user and generate a log
	 * @param userName : userName
	 * */
	static void signOutUser(String userName){
		sManager.signOut(userName);
		new UserSignOutLog(userName).push();
	}
	
	/*
	 * modify passWord for a user
	 * @param username
	 * @param oldPassWord
	 * @param newPassWord
	 * return NO_SUCH_USER when no user found
	 * 		  WRONG_PASSWORD when passWord does not match
	 * 		  SUCCEEDED when modification succeed
	 * */
	static int modifyPassWord(String userName, String oldPassWord, String newPassWord) {
		try {
			FindIterable<Document> iter = MongoDBs.users.find(Filters.eq("userName", userName));
			MongoCursor<Document> cursor = iter.iterator();
			if(!cursor.hasNext())
				return NO_SUCH_USER;
			Document user = cursor.next();
			boolean match = user.get("passWord").equals(oldPassWord);
			if(match){
				MongoDBs.users.findOneAndUpdate(Filters.eq("userName", userName), new Document("$set",new Document("passWord",newPassWord)));
				new UserModPWLog(userName).push();
			}	
			return  match ? SUCCEEDED : WRONG_PASSWORD;
		} catch (Exception e) {
			e.printStackTrace();
			return DATABASE_EXCEPTION;
		}
	}
	
	/*
	 * modify userInfo for a user
	 * @param username
	 * @param oldPassWord
	 * @param newInfo
	 * return NO_SUCH_USER when no user found
	 * 		  WRONG_PASSWORD when passWord does not match
	 * 		  SUCCEEDED when modification succeed
	 * */
	static int modifyUserInfo(String userName, String oldPassWord, Document newInfo) {
		try {
			FindIterable<Document> iter = MongoDBs.users.find(Filters.eq("userName", userName));
			MongoCursor<Document> cursor = iter.iterator();
			if(!cursor.hasNext())
				return NO_SUCH_USER;
			Document user = cursor.next();
			boolean match = user.get("passWord").equals(oldPassWord);
			if(match){
				MongoDBs.users.findOneAndUpdate(Filters.eq("userName", userName), new Document("$set",new Document("userInfo",newInfo)));
				new UserModPWLog(userName).push();
			}	
			return  match ? SUCCEEDED : WRONG_PASSWORD;
		} catch (Exception e) {
			e.printStackTrace();
			return DATABASE_EXCEPTION;
		}
	}

	/*
	 * find at most num search queries of a user between [startTime endTime]
	 * @param userName
	 * @param startTime : queries before this time will not be retrieved
	 * @param endTime : queries after this time wiil not be retrieved
	 * @param num : at most return num queries
	 * @param action : the action to be searched, e.g. "search" "click" 
	 * */
	static Vector<Document> searchHistory(String userName, long startTime, long endTime, int num, String action){
		BasicDBList list = new BasicDBList();
		BasicDBObject userNameCond= new BasicDBObject()
					, timeCond = new BasicDBObject()
					, timeRange = new BasicDBObject()
					, typeCond = new BasicDBObject();
		typeCond.put("action", action);
		userNameCond.put("userName", userName);
		timeRange.put("$gte", startTime);
		timeRange.put("$lse", endTime);
		timeCond.put("timeStamp", timeRange);
		list.add(typeCond);
		list.add(userNameCond);
		list.add(timeCond);
		
		BasicDBObject query = new BasicDBObject();
		query.put("$and", list);
		MongoCursor<Document> cursor = MongoDBs.logs.find(query).iterator();
		Vector<Document> vector = new Vector<Document>();
		while(cursor.hasNext())
			vector.add(cursor.next());
		return vector;
	}
}

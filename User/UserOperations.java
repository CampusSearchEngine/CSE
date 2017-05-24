import org.bson.Document;

import com.mongodb.MongoSecurityException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sun.org.apache.bcel.internal.generic.PUSH;

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
}

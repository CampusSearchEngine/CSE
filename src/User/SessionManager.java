package User;

import java.util.HashMap;

public class SessionManager {
	HashMap<String, UserSession> sessions;
	
	public SessionManager() {
		this.sessions = new HashMap<String,UserSession>();
	}
	
	public void signIn(String userName) {
		UserSession session = new UserSession(userName, System.currentTimeMillis());
		sessions.put(userName, session);
	}
	public void signOut(String userName) {
		sessions.remove(userName);
	}
}

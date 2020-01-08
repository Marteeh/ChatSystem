package application.server;

import database.Request;

public class ClearUserRequest implements Request {
	
	public static final int ALL_USERS = -1;

	private final int userId;
	
	public ClearUserRequest(int userId) {
		this.userId = userId;
	}
	
	@Override
	public String createSQLRequest() {
		String request;
		if(userId == ALL_USERS) {
			request = "DELETE FROM messages;";
		} else {
			request = String.format("DELETE FROM messages WHERE from_user_id = %d OR to_user_id = %d;", userId, userId);
		}
		return request;
	}
}

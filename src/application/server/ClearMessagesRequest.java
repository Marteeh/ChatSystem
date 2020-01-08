package application.server;

import database.Request;

public class ClearMessagesRequest implements Request {
	
	public static final int ALL_USERS = -1;

	private final int userId;
	
	public ClearMessagesRequest(int userId) {
		this.userId = userId;
	}
	
	@Override
	public String createSQLRequest() {
		String request;
		if(userId == ALL_USERS) {
			request = "DELETE FROM messages WHERE to_user_id != -1;";
		} else {
			request = String.format("DELETE FROM messages WHERE to_user_id != -1 AND from_user_id = %d;", userId, userId);
		}
		return request;
	}
}

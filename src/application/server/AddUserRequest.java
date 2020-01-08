package application.server;

import database.Request;

public class AddUserRequest implements Request {

	private final int userId;
	
	public AddUserRequest(int attribuedUserId) {
		this.userId = attribuedUserId;
	}
	
	@Override
	public String createSQLRequest() {
		return String.format("INSERT INTO messages (from_user_id, to_user_id, content, creation_timestamp) VALUES (%d, -1, '', -1);", userId);
	}
}

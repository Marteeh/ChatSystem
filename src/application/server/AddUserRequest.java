package application.server;

import database.Request;

public class AddUserRequest implements Request {

	public int attribuedUserId;
	
	public AddUserRequest(int attribuedUserId) {
		this.attribuedUserId = attribuedUserId;
	}
	
	@Override
	public String createSQLRequest() {
		return String.format("INSERT INTO messages (from_user_id, to_user_id, content, creation_timestamp) VALUES (%d, -1, '', -1);", attribuedUserId);
	}
}

package application.server;

import application.Message;
import database.Request;
import utils.Utils;

public class AddMessageRequest implements Request {

	private final Message message;
	
	public AddMessageRequest(Message message) {
		this.message = message;
	}
	
	@Override
	public String createSQLRequest() {
		String content = Utils.escape(message.content);
		return String.format("INSERT INTO messages (from_user_id, to_user_id, content, creation_timestamp) VALUES (%d, %d, \"%s\", %d);", message.from, message.to, content, message.timestamp);
	}
}

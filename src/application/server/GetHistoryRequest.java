package application.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.Message;
import database.QueryRequest;

public class GetHistoryRequest implements QueryRequest {

	private final int userId1;
	private final int userId2;
	
	public final List<Message> messages = new ArrayList<Message>();
	
	public GetHistoryRequest(int userId1, int userId2) {
		this.userId1 = userId1;
		this.userId2 = userId2;
	}
	
	@Override
	public String createSQLRequest() {
		return String.format("SELECT * FROM messages WHERE (from_user_id = %d AND to_user_id = %d) OR (from_user_id = %d AND to_user_id = %d);", userId1, userId2, userId2, userId1);
	}

	@Override
	public void readResult(ResultSet rs) {
		try {
			while(rs.next()) {
				int fromUserId = rs.getInt("from_user_id");
				int toUserId = rs.getInt("to_user_id");
				long timestamp = rs.getLong("creation_timestamp");
				String content = rs.getString("content");
				messages.add(new Message(fromUserId, toUserId, timestamp, content));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

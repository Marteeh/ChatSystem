package application.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.QueryRequest;

public class GetMessagesRequest implements QueryRequest {
	
	public static final int ALL_USERS = -1;

	private final int userId;
	
	public final List<String> messages = new ArrayList<String>();
	
	public GetMessagesRequest(int userId) {
		this.userId = userId;
	}
	
	@Override
	public String createSQLRequest() {
		String request;
		if(userId == ALL_USERS) {
			request = "SELECT * FROM messages WHERE to_user_id != -1;";
		} else {
			request = String.format("SELECT * FROM messages WHERE to_user_id != -1 AND from_user_id = %d;", userId);
		}
		return request;
	}

	@Override
	public void readResult(ResultSet rs) {
		try {
			while(rs.next()) {
				int fromUserId = rs.getInt("from_user_id");
				int toUserId = rs.getInt("to_user_id");
				long timestamp = rs.getLong("creation_timestamp");
				String content = rs.getString("content");
				messages.add(formatedMessage(fromUserId, toUserId, timestamp, content));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String formatedMessage(int fromUserId, int toUserId, long timestamp, String content) {
		Date date = new Date(timestamp);
    	DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String formatedDate = shortDateFormat.format(date);
    	return String.format("User %d said to user %d at %s : %s", fromUserId, toUserId, formatedDate, content);
	}

}

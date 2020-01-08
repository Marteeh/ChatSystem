package application.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.QueryRequest;

public class GetUsersRequest implements QueryRequest {
	
	public final List<String> users = new ArrayList<String>();
	
	@Override
	public String createSQLRequest() {
		return "SELECT from_user_id FROM messages WHERE to_user_id = -1;";
	}

	@Override
	public void readResult(ResultSet rs) {
		try {
			while(rs.next()) {
				int fromUserId = rs.getInt("from_user_id");
				users.add(formatedUser(fromUserId));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String formatedUser(int fromUserId) {
    	return String.format("User %d", fromUserId);
	}

}

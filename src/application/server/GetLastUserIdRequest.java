package application.server;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.QueryRequest;

public class GetLastUserIdRequest implements QueryRequest {

	public int lastUserId;
	
	@Override
	public String createSQLRequest() {
		return "SELECT MAX(from_user_id) FROM messages;";
	}

	@Override
	public void readResult(ResultSet rs) {
		try {
			rs.next();
			lastUserId = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

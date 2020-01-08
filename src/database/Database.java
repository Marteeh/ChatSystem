package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private final Connection coon;
	private final Statement stmt;

	public Database(String url, String driver, String username, String password) throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		coon = DriverManager.getConnection(url, username, password);
		stmt = coon.createStatement();
	}
	
	public void executeRequest(Request request) {
		ResultSet rs;
		try {
			if(request instanceof QueryRequest) {
				rs = stmt.executeQuery(request.createSQLRequest());
				((QueryRequest)request).readResult(rs);
			} else {
				System.out.println(request.createSQLRequest());
				stmt.execute(request.createSQLRequest());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

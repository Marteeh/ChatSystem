package application.server;

import java.sql.ResultSet;

import database.Request;

public class RequestUserLogin implements Request {

	public final String username;
	public final String password;
	
	public boolean success;
	public int userId;
	
	RequestUserLogin(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public String createSQLRequest() {
		return null;
	}

	@Override
	public void readResult(ResultSet rs) {
		
	}

}

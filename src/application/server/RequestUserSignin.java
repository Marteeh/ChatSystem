package application.server;

import java.sql.ResultSet;

import database.Request;

public class RequestUserSignin implements Request {

	public int attribuedUserId;
	
	@Override
	public String createSQLRequest() {
		return null;
	}

	@Override
	public void readResult(ResultSet rs) {
		
	}

}

package database;

import java.sql.ResultSet;

public interface Request {
	public String createSQLRequest();
	public void readResult(ResultSet rs);
}

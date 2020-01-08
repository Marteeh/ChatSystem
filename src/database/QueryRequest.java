package database;

import java.sql.ResultSet;

public interface QueryRequest extends Request{

	public void readResult(ResultSet rs);
}

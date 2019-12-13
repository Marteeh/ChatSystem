package application.server;

import java.io.IOException;
import java.sql.SQLException;

public class TestServer {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		new ServerController().start();
	}
}

package application.server;

import java.io.IOException;
import java.sql.SQLException;

public class RunServer {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		new ServerController().start();
	}
}

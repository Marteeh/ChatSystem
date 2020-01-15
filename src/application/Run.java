package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Properties;

import application.client.ClientController;
import application.server.ServerController;
import utils.Utils;

public class Run {

	public static void main(String[] args) throws NumberFormatException, ClassNotFoundException, InterruptedException, IOException, URISyntaxException, SQLException {
		if(args.length == 1 && args[0].equalsIgnoreCase("server")) {
			runServer();
		} else {
			runClient();
		}
	}
	
	private static void runClient() throws NumberFormatException, InterruptedException, IOException, URISyntaxException {
		Properties p = loadProperties(true);
		String localAddress = p.getProperty("local_ip");
		String broadcastAdrress = p.getProperty("broadcast_ip");
		String serverAddress = p.getProperty("server_ip", "localhost");
		int localServerPort = Integer.parseInt(p.getProperty("local_server_port"));
		int centralizedServerPort = Integer.parseInt(p.getProperty("centralized_server_port"));
		int udpPort = Integer.parseInt(p.getProperty("udp_port"));
		boolean useCentralizedServer = p.getProperty("centralized", "false").equalsIgnoreCase("true");
		new ClientController("", localAddress, broadcastAdrress, serverAddress, localServerPort, centralizedServerPort, udpPort, useCentralizedServer).start();
	}
	
	private static void runServer() throws NumberFormatException, InterruptedException, IOException, URISyntaxException, ClassNotFoundException, SQLException {
		Properties p = loadProperties(false);
		String dbUrl = p.getProperty("db_url");
		String dbUsername = p.getProperty("db_username");
		String dbPassword = p.getProperty("db_password", "localhost");
		int serverPort = Integer.parseInt(p.getProperty("server_port", "4321"));
		new ServerController(dbUrl, dbUsername, dbPassword, serverPort).start();
	}

	private static Properties loadProperties(boolean client) throws FileNotFoundException, IOException, URISyntaxException {
		Properties p = new Properties();
		p.load(new FileInputStream(new File(Utils.getRunningirectory() + "/" + (client ? "client" : "server") + ".properties")));
		return p;
	}
}

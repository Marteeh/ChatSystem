package application.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import utils.Utils;

public class RunClient {

	public static void main(String[] args) throws Exception {
		Properties p = loadProperties();
		String instanceName = p.getProperty("instance_name", "");
		String localAddress = p.getProperty("local_ip");
		String serverAddress = p.getProperty("server_ip", "localhost");
		boolean useCentralizedServer = p.getProperty("centralized", "false").equalsIgnoreCase("true");
		new ClientController(instanceName, localAddress, serverAddress, useCentralizedServer).start();
	}

	private static Properties loadProperties() throws FileNotFoundException, IOException, URISyntaxException {
		Properties p = new Properties();
		p.load(new FileInputStream(new File(Utils.getRunningirectory() + "/config.properties")));
		return p;
	}
}

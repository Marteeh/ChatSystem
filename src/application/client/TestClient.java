package application.client;

import utils.EventQueue;

public class TestClient {
	
	public static void main(String[] args) throws Exception {
		if(args.length >= 3) {
			ClientController controller;
			String instanceName = args[0];
			String localAddress = args[1];
			String serverAddress = args[2];
			boolean useCentralizedServer = args[3].equals("use-centralized-server");
			controller = new ClientController(instanceName, localAddress, serverAddress, useCentralizedServer);
			EventQueue eventQueue = controller.getEventQueue();
			controller.start();
		} else {
			System.out.println("Invalid syntax");
		}
	}
}

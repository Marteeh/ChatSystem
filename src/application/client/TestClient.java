package application.client;

public class TestClient {
	
	public static void main(String[] args) throws Exception {
		if(args.length >= 3) {
			ClientController controller;
			String instanceName = args[0];
			String localAddress = args[1];
			String serverAddress = args[2];
			boolean useCentralizedServer = args[3].equals("use-centralized-server");
			controller = new ClientController(instanceName, localAddress, "10.1.255.255", serverAddress, 1234, 4321, 8454, useCentralizedServer);
			controller.start();
		} else {
			System.out.println("Invalid syntax");
		}
	}
}

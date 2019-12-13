package application.client;

import utils.EventQueue;

public class TestClient {
	
	public static void main(String[] args) throws Exception {
		ClientController controller;
		if(args.length == 2) {
			String instanceName = args[0];
			String serverAddress = args[1];
			controller = new ClientController(instanceName, serverAddress);
		} else {
			controller = new ClientController();
		}
		EventQueue eventQueue = controller.getEventQueue();
		controller.start();
		Thread.sleep(4000);
		eventQueue.addEventToQueue(new LoginEvent("flo", false));
		Thread.sleep(4000);
		eventQueue.addEventToQueue(new RenamePseudoEvent("floflo"));
	}
}

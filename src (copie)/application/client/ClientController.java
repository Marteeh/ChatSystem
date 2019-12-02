package application.client;

import java.io.IOException;
import java.net.UnknownHostException;

import application.PacketLogin;
import network.Client;
import network.NetworkEvent;
import network.PacketFactory;
import utils.Event;
import utils.EventListener;
import utils.EventQueue;

public class ClientController implements EventListener {

	private static final String SERVER_HOSTNAME = "localhost";
	private static final int SERVER_PORT = 1234;
	
	private final EventQueue eventQueue = new EventQueue(this);
	private final PacketFactory packetFactory = new PacketFactory();
	private Client client;
	private State state = State.NOT_LOGGED;
	
	ClientController() {
		registerPackets();
	}
	
	private void login(LoginEvent e)  {
		try {
			state = State.LOGGING;
			client = new Client(eventQueue, packetFactory, SERVER_HOSTNAME, SERVER_PORT);
			client.sendPacket(new PacketLogin(e.username, e.password, e.isExternal));
		} catch (IOException | InterruptedException e1) {
			state = State.NOT_LOGGED;
			e1.printStackTrace();
		}
	}
	
	private void registerPackets() {
		
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof GUIEvent) {
			onGUIEvent((GUIEvent) event);
		} else if(event instanceof NetworkEvent) {
			onNetworkEvent((NetworkEvent) event);
		}
	}
	
	private void onGUIEvent(GUIEvent event) {
		if(event instanceof LoginEvent) {
			LoginEvent e = (LoginEvent) event;
			if(state.equals(State.NOT_LOGGED)) {
				
			}
		}
	}
	
	private void onNetworkEvent(NetworkEvent event) {
		
	}
	
	private static enum State {
		NOT_LOGGED,
		LOGGING;
	}

}

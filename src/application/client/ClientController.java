package application.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import application.PacketLogin;
import application.PacketSuccess;
import application.PacketUserConnected;
import application.User;
import network.Client;
import network.NetworkEvent;
import network.PacketEvent;
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
	private final Map<Integer, User> connectedUsers = new HashMap<Integer, User>();
	
	ClientController() {
		registerPackets();
	}
	
	private void login(LoginEvent e)  {
		try {
			state = State.LOGGING;
			client = new Client(eventQueue, packetFactory, SERVER_HOSTNAME, SERVER_PORT);
			client.start();
			client.sendPacket(new PacketLogin(e.username, e.password, e.isExternal));
		} catch (IOException | InterruptedException e1) {
			state = State.NOT_LOGGED;
			e1.printStackTrace();
		}
	}
	
	private void registerPackets() {
		packetFactory.registerPacket(PacketLogin.class);
		packetFactory.registerPacket(PacketSuccess.class);
		packetFactory.registerPacket(PacketUserConnected.class);
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
				login(e);
			}
		}
	}
	
	private void onNetworkEvent(NetworkEvent event) {
		if(event instanceof PacketEvent) {
			PacketEvent e = (PacketEvent) event;
			if(e.packet instanceof PacketSuccess) {
				if(state.equals(State.LOGGING)) {
					state = State.LOGGED;
				}
			} else if(e.packet instanceof PacketUserConnected) {
				PacketUserConnected p = (PacketUserConnected) e.packet;
				if(!connectedUsers.containsKey(p.user.id)) {
					connectedUsers.put(p.user.id, p.user);
				}
			}
		}
	}
	
	private static enum State {
		NOT_LOGGED,
		LOGGING,
		LOGGED;
	}

}

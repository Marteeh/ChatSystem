package application.server;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import application.PacketError;
import application.PacketLogin;
import application.PacketSuccess;
import application.PacketUserConnected;
import application.User;
import application.client.GUIEvent;
import database.Database;
import network.NetworkEvent;
import network.PacketEvent;
import network.PacketFactory;
import utils.Console;
import utils.ConsoleEvent;
import utils.Event;
import utils.EventListener;
import utils.EventQueue;

public class ServerController implements EventListener {

	private static final int SERVER_PORT = 1234;
	private static final String DATABASE_URL = "com.mysql.jdbc.Driver";
	private static final String DATABASE_DRIVER = "jdbc:mysql://localhost:3306/sonoo";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "root";

	private final EventQueue eventQueue = new EventQueue(this);
	private final PacketFactory packetFactory = new PacketFactory();
	private State state;
	private final Map<Integer, User> connectedUsers = new HashMap<Integer, User>();
	private final Console console = new Console(eventQueue);
	private final Database database;

	ServerController() throws ClassNotFoundException, SQLException {
		registerPackets();
		database = new Database(DATABASE_URL, DATABASE_DRIVER, DATABASE_USERNAME, DATABASE_PASSWORD);
	}

	private void registerPackets() {
		packetFactory.registerPacket(PacketLogin.class);
		packetFactory.registerPacket(PacketSuccess.class);
		packetFactory.registerPacket(PacketUserConnected.class);
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof ConsoleEvent) {
			onConsoleEvent((ConsoleEvent) event);
		} else if(event instanceof NetworkEvent) {
			onNetworkEvent((NetworkEvent) event);
		}
	}

	private void onConsoleEvent(ConsoleEvent event) {
		if(event.action.equals("exit")) {
			System.exit(0);
		}
	}

	private void onNetworkEvent(NetworkEvent event) {
		if(event instanceof PacketEvent) {
			PacketEvent e = (PacketEvent) event;
			if(e.packet instanceof PacketLogin) {
				PacketLogin p = (PacketLogin) e.packet;
				RequestUserLogin r = new RequestUserLogin(p.username, p.password);
				database.executeRequest(r);
				if(r.success) {
					event.client.sendPacket(new PacketSuccess());
					
				} else {
					event.client.sendPacket(new PacketError("Bad username or password"));
				}
			}
		}
	}

	private static enum State {

	}

}

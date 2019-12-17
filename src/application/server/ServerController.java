package application.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import application.PacketLogin;
import application.PacketMessage;
import application.PacketPseudoAvailabilityCheck;
import application.PacketSignin;
import application.PacketStartSession;
import application.PacketUser;
import application.User;
import database.Database;
import network.NetworkEvent;
import network.TCPPacketEvent;
import network.PacketFactory;
import network.TCPServer;
import utils.Console;
import utils.ConsoleEvent;
import utils.Event;
import utils.EventListener;
import utils.EventQueue;

public class ServerController implements EventListener {

	private static final int SERVER_PORT = 4321;
	private static final String DATABASE_URL = "com.mysql.jdbc.Driver";
	private static final String DATABASE_DRIVER = "jdbc:mysql://localhost:3306/sonoo";
	private static final String DATABASE_USERNAME = "root";
	private static final String DATABASE_PASSWORD = "root";

	private final EventQueue eventQueue = new EventQueue(this);
	private final PacketFactory packetFactory = new PacketFactory();
	private State state;
	private final Map<Integer, User> connectedUsers = new HashMap<Integer, User>();
	private final Console console = new Console(eventQueue);
	private Database database;
	private TCPServer tcpServer;
	private int nextAttribuedUserId = 0;

	ServerController() throws ClassNotFoundException, SQLException {
		registerPackets();
		//database = new Database(DATABASE_URL, DATABASE_DRIVER, DATABASE_USERNAME, DATABASE_PASSWORD);
	}

	public void start() throws IOException {
		eventQueue.start();
		tcpServer = new TCPServer(eventQueue, packetFactory, SERVER_PORT);
		tcpServer.start();
	}

	private void registerPackets() {
		packetFactory.registerPacket(PacketLogin.class);
		packetFactory.registerPacket(PacketSignin.class);
		packetFactory.registerPacket(PacketPseudoAvailabilityCheck.class);
		packetFactory.registerPacket(PacketUser.class);
		packetFactory.registerPacket(PacketMessage.class);
		packetFactory.registerPacket(PacketStartSession.class);
	}

	@Override
	public void onEvent(Event event) {
		System.out.println("server event");
		if (event instanceof ConsoleEvent) {
			onConsoleEvent((ConsoleEvent) event);
		} else if (event instanceof NetworkEvent) {
			onNetworkEvent((NetworkEvent) event);
		}
	}

	private void onConsoleEvent(ConsoleEvent event) {
		if (event.action.equals("exit")) {
			System.exit(0);
		}
	}

	private void onNetworkEvent(NetworkEvent event) {
		if (event instanceof TCPPacketEvent) {
			TCPPacketEvent e = (TCPPacketEvent) event;
			if (e.packet instanceof PacketSignin) {
				RequestUserSignin r = new RequestUserSignin();
				//database.executeRequest(r);
				e.client.sendPacket(new PacketSignin(nextAttribuedUserId++));
			}
		}
	}

	private static enum State {

	}

}

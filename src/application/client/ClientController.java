package application.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.omg.CORBA.UserException;

import application.ControllerEvent;
import application.DisconnectUserEvent;
import application.LoginPhaseFinishedEvent;
import application.PacketLogin;
import application.PacketPseudoAvailabilityCheck;
import application.PacketSignin;
import application.PacketUser;
import application.PeriodicLoginEvent;
import application.StartEvent;
import application.User;
import network.NetworkEvent;
import network.Packet;
import network.PacketFactory;
import network.TCPClient;
import network.TCPPacketEvent;
import network.TCPServer;
import network.UDPPacketEvent;
import network.UDPSocket;
import utils.Event;
import utils.EventListener;
import utils.EventQueue;
import utils.Utils;

public class ClientController implements EventListener {

	private static final int SERVER_PORT = 1234;
	private static final int CENTRALIZED_SERVER_PORT = 4321;
	private static final int UDP_PORT = 2222;

	private final String serverAddress;
	private final Random random = new SecureRandom();
	private final EventQueue eventQueue = new EventQueue(this);
	private final PacketFactory packetFactory = new PacketFactory();
	private TCPServer tcpServer;
	private TCPClient tcpClient;
	private final UDPSocket udpSocket;
	private State state = State.STARTING;
	private final Map<Integer, User> connectedUsers = new HashMap<Integer, User>();
	private final List<PacketPseudoAvailabilityCheck> conflictingLoginRequests = new ArrayList<PacketPseudoAvailabilityCheck>();
	private int attribuedUserId = -1;
	private final File userIdFile;
	private User currentUser;
	private int sessionId = 0;
	
	ClientController() throws URISyntaxException, NumberFormatException, IOException {
		this("", "localhost");
	}

	//instanceName est utile pour pouvoir tester sur la même machine afin de diférencier le fichier qui
	//est cencé être unique par utilisateur
	ClientController(String instanceName, String serverAddress) throws URISyntaxException, NumberFormatException, IOException {
		this.serverAddress = serverAddress;
		registerPackets();
		userIdFile = new File(Utils.getRunningirectory() + "/userId" + instanceName + ".txt");
		udpSocket = new UDPSocket(eventQueue, packetFactory);
	}

	// Utile pour les tests seulement
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	public void start() throws InterruptedException, NumberFormatException, IOException {
		eventQueue.start();
		udpSocket.listen(UDP_PORT);
		// Delai d'écoute nécessaire pour s'assurer de pouvoir détecter les conflits
		eventQueue.addEventToQueue(new StartEvent(), 1000);
		loadUserId();
	}

	private void loadUserId() throws NumberFormatException, IOException {
		if (userIdFile.exists()) {
			String content = new String(Files.readAllBytes(userIdFile.toPath()));
			content = content.replaceAll("\n", "").replaceAll("\r", "").trim();
			attribuedUserId = Integer.parseInt(content);
		} else {
			getTCPClient().sendPacket(new PacketSignin());
		}
	}

	private void saveUserId() {
		try {
			Files.write(userIdFile.toPath(), Integer.toString(attribuedUserId).getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void login(LoginEvent e) {
		print("Try login with pseudo " + e.pseudo);
		boolean alreadyUsed = false;
		for (User u : connectedUsers.values()) {
			if (u.pseudo.equals(e.pseudo)) {
				alreadyUsed = true;
				break;
			}
		}
		if (!alreadyUsed) {
			state = State.LOGGING;
			long discriminant = random.nextLong();
			Packet packet = new PacketPseudoAvailabilityCheck(attribuedUserId, e.pseudo, discriminant);
			udpSocket.sendPacket(packet, UDPSocket.BROADCAST_ADDRESS, UDP_PORT);
			try {
				eventQueue.addEventToQueue(new LoginPhaseFinishedEvent(e.pseudo, e.isExternal, discriminant), 1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} else {
			print("Pseudo " + e.pseudo + " already used");
		}
	}

	private void validateLogin(LoginPhaseFinishedEvent e) {
		print("Checking for conflicting login with pseudo " + e.pseudo);
		for (PacketPseudoAvailabilityCheck p : conflictingLoginRequests) {
			if (p.pseudo.equals(e.pseudo) && p.discriminant < e.discriminant) {
				state = State.STARTED;
				// un utilisateur qui s'est connecté au même moment avec le même pseudo a eu
				// plus de chance
				print("Pseudo " + e.pseudo + " already used");
				return;
			}
		}
		conflictingLoginRequests.clear();
		currentUser = new User(attribuedUserId, e.pseudo, e.isExternal, udpSocket.getIpAddress());
		state = State.LOGGED;
		print("Succesfully connected");
		try {
			eventQueue.addEventToQueue(new PeriodicLoginEvent(sessionId), 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private void disconnect() {
		print("Disconnect");
		state = State.STARTED;
		sessionId++;
	}
	
	private void userConnected(PacketUser p) {
		if (p.user.id != attribuedUserId) {
			if (!connectedUsers.containsKey(p.user.id)) {
				connectedUsers.put(p.user.id, p.user);
			} else {
				connectedUsers.get(p.user.id).loggedDuration++;
			}
			try {
				eventQueue.addEventToQueue(new DisconnectUserEvent(p.user.id, connectedUsers.get(p.user.id).loggedDuration), 2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Maintient l'utilisateur actif pour qu'il soit considéré comme connecté par
	// les autres utilisateurs
	private void periodicLogin() {
		Packet packet = new PacketUser(currentUser);
		udpSocket.sendPacket(packet, UDPSocket.BROADCAST_ADDRESS, UDP_PORT);
		try {
			eventQueue.addEventToQueue(new PeriodicLoginEvent(sessionId), 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	private void removeUser(User user) {
		connectedUsers.remove(user.id);
		print("User " + user.pseudo + " disconnected");
	}

	private void registerPackets() {
		packetFactory.registerPacket(PacketLogin.class);
		packetFactory.registerPacket(PacketSignin.class);
		packetFactory.registerPacket(PacketPseudoAvailabilityCheck.class);
		packetFactory.registerPacket(PacketUser.class);
	}

	@Override
	public void onEvent(Event event) {
		//System.out.println("client event " + event);
		if (event instanceof GUIEvent) {
			onGUIEvent((GUIEvent) event);
		} else if (event instanceof ControllerEvent) {
			onControllerEvent((ControllerEvent) event);
		} else if (event instanceof NetworkEvent) {
			onNetworkEvent((NetworkEvent) event);
		}
	}

	private void onGUIEvent(GUIEvent event) {
		if (event instanceof LoginEvent) {
			LoginEvent e = (LoginEvent) event;
			if (state.equals(State.STARTED)) {
				login(e);
			}
		} else if (event instanceof DisconnectEvent) {
			if (state.equals(State.LOGGED)) {
				disconnect();
			}
		} else if (event instanceof RenamePseudoEvent) {
			RenamePseudoEvent e = (RenamePseudoEvent) event;
			if (state.equals(State.LOGGED)) {
				disconnect();
				try {
					eventQueue.addEventToQueue(new LoginEvent(e.pseudo, currentUser.isExternal));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void onControllerEvent(ControllerEvent event) {
		if (event instanceof StartEvent) {
			if (state.equals(State.STARTING)) {
				if (attribuedUserId != -1) {
					state = State.STARTED;
				} else {
					// server not responding
				}
			}
		} else if (event instanceof LoginPhaseFinishedEvent) {
			LoginPhaseFinishedEvent e = (LoginPhaseFinishedEvent) event;
			if (state.equals(State.LOGGING)) {
				validateLogin(e);
			}
		} else if (event instanceof PeriodicLoginEvent) {
			// A chaque déconnexion le sessionId change pour éviter de traiter les
			// PeriodicLoginEvent d'une autre session
			// dans le cas ou on se reconnecte rapidement
			if (state.equals(State.LOGGED) && ((PeriodicLoginEvent) event).sessionId == sessionId) {
				periodicLogin();
			}
		} else if (event instanceof DisconnectUserEvent) {
			// A chaque déconnexion le sessionId change pour éviter de traiter les
			// PeriodicLoginEvent d'une autre session
			// dans le cas ou on se reconnecte rapidement
			if (state.equals(State.LOGGED)) {
				DisconnectUserEvent e = (DisconnectUserEvent)event;
				User u = connectedUsers.get(e.userId);
				if(u != null && u.loggedDuration == e.loggedDuration) {
					//Cet utilisateur ne s'est pas manifesté depuis trop longtemps donc on le considère déconnecté
					removeUser(u);
				}
			}
		}
	}

	private void onNetworkEvent(NetworkEvent event) {
		if (event instanceof TCPPacketEvent) {
			TCPPacketEvent e = (TCPPacketEvent) event;
			if (e.packet instanceof PacketSignin) {
				attribuedUserId = ((PacketSignin) e.packet).attribuedUserId;
				saveUserId();
			}
		} else if (event instanceof UDPPacketEvent) {
			UDPPacketEvent e = (UDPPacketEvent) event;
			if (e.packet instanceof PacketUser) {
				PacketUser p = (PacketUser) e.packet;
				userConnected(p);
			} else if (e.packet instanceof PacketPseudoAvailabilityCheck) {
				PacketPseudoAvailabilityCheck p = (PacketPseudoAvailabilityCheck) e.packet;
				if (p.userId != attribuedUserId) {
					conflictingLoginRequests.add(p);
				}
			}
		}
	}

	private TCPClient getTCPClient() {
		if (tcpClient == null) {
			try {
				tcpClient = new TCPClient(eventQueue, packetFactory, serverAddress, CENTRALIZED_SERVER_PORT);
				tcpClient.start();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return tcpClient;
	}

	private TCPServer getTCPServer() {
		if (tcpServer == null) {
			try {
				tcpServer = new TCPServer(eventQueue, packetFactory, SERVER_PORT);
				tcpServer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tcpServer;
	}

	private static enum State {
		STARTING, STARTED, LOGGING, LOGGED;
	}
	
	private void print(String msg) {
		String str = "";
		if(currentUser != null) {
			str = String.format("User %s (id %d) : %s", currentUser.pseudo, currentUser.id, msg);
		} else {
			str = msg;
		}
		System.out.println(str);
	}

}

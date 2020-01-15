package application.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import application.ControllerEvent;
import application.DisconnectUserEvent;
import application.LoginPhaseFinishedEvent;
import application.Message;
import application.PacketConnectedUsers;
import application.PacketDisconnect;
import application.PacketGetConnectedUsers;
import application.PacketGetHistory;
import application.PacketHistory;
import application.PacketKeepMessage;
import application.PacketLogin;
import application.PacketLoginResult;
import application.PacketMessage;
import application.PacketPopup;
import application.PacketPseudoAvailabilityCheck;
import application.PacketSignin;
import application.PacketStartSession;
import application.PacketTunnel;
import application.PacketUser;
import application.PeriodicLoginEvent;
import application.Session;
import application.StartEvent;
import application.User;
import network.NetworkEvent;
import network.Packet;
import network.PacketFactory;
import network.TCPClient;
import network.TCPConnectionEvent;
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
	private static final int UDP_PORT = 8454;
	private static final String BROADCAST_ADDRESS = "10.1.255.255";

	private final String localAddress;
	private final String serverAddress;
	private final Random random = new SecureRandom();
	private final EventQueue eventQueue = new EventQueue(this);
	private final PacketFactory packetFactory = new PacketFactory();
	private TCPServer tcpServer;
	private TCPClient tcpClient;
	private final UDPSocket udpSocket;
	private State state = State.STARTING;
	private final Map<Integer, User> connectedUsers = new HashMap<Integer, User>();
	private final Map<Integer, Session> opennedSessions = new HashMap<Integer, Session>();
	private final List<PacketPseudoAvailabilityCheck> conflictingLoginRequests = new ArrayList<PacketPseudoAvailabilityCheck>();
	private int attribuedUserId = -1;
	private final File userIdFile;
	private User currentUser;
	private boolean externalUser;
	private int sessionId = 0;
	private LoginWindow loginWindow;
	private MainWindow mainWindow;
	private LoginEvent oldLogin;
	private LoginEvent askedLogin;
	private final boolean useCentralizedServer;

	ClientController() throws URISyntaxException, NumberFormatException, IOException {
		this("", "localhost", "localhost", false);
	}

	// instanceName est utile pour pouvoir tester sur la même machine afin de
	// diférencier le fichier qui
	// est cencé être unique par utilisateur
	ClientController(String instanceName, String localAddress, String serverAddress, boolean useCentralizedServer)
			throws URISyntaxException, NumberFormatException, IOException {
		this.localAddress = localAddress;
		this.serverAddress = serverAddress;
		registerPackets();
		userIdFile = new File(Utils.getRunningirectory() + "/userId" + instanceName + ".txt");
		udpSocket = new UDPSocket(eventQueue, packetFactory);
		this.useCentralizedServer = useCentralizedServer;
	}

	// Utile pour les tests seulement
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	public void start() throws InterruptedException, NumberFormatException, IOException {
		loginWindow = new LoginWindow(eventQueue, false, useCentralizedServer);
		loginWindow.disableLoginButton();
		eventQueue.start();
		udpSocket.listen(UDP_PORT);
		// Delai d'écoute nécessaire pour s'assurer de pouvoir détecter les conflits
		eventQueue.addEventToQueue(new StartEvent(), 1000);
		loadUserId();
		getTCPServer();
		if(useCentralizedServer) {
			getTCPClientToCentralizedServer().sendPacket(new PacketGetConnectedUsers());
		}
	}

	private void loadUserId() throws NumberFormatException, IOException {
		if (userIdFile.exists()) {
			String content = new String(Files.readAllBytes(userIdFile.toPath()));
			content = content.replaceAll("\n", "").replaceAll("\r", "").trim();
			attribuedUserId = Integer.parseInt(content);
		} else {
			getTCPClientToCentralizedServer().sendPacket(new PacketSignin());
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
		externalUser = e.isExternal;
		loginWindow.disableExternalUserCheckbox();
		askedLogin = e;
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
			if(!e.isExternal) {				
				if(useCentralizedServer) {					
					getTCPClientToCentralizedServer().sendPacket(new PacketLogin(new User(attribuedUserId, e.pseudo, e.isExternal, localAddress)));
				} else {
					long discriminant = random.nextLong();
					Packet packet = new PacketPseudoAvailabilityCheck(attribuedUserId, e.pseudo, discriminant);
					udpSocket.sendPacket(packet, BROADCAST_ADDRESS, UDP_PORT);
					try {
						eventQueue.addEventToQueue(new LoginPhaseFinishedEvent(e.pseudo, e.isExternal, discriminant), 1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				if(useCentralizedServer) {
					getTCPClientToCentralizedServer().sendPacket(new PacketLogin(new User(attribuedUserId, e.pseudo, e.isExternal, localAddress)));
				} else {
					loginWindow.showMessage("Le serveur centralisé est injoiniable", "Erreur");
					state = State.STARTED;
				}
			}
		} else {
			print("Pseudo " + e.pseudo + " already used");
			loginWindow.showMessage("Le pseudo que vous avez choisi est déja pris, veuillez en choisir un autre.",
					"Erreur");
			loginWindow.enableLoginButton();
			state = State.STARTED;
			reconnectToLastSessionIfNeeded();
		}
	}
	
	private void reconnectToLastSessionIfNeeded() {
		if(oldLogin != null) {
			try {
				eventQueue.addEventToQueue(oldLogin);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			oldLogin = null;
		}
	}

	private void validateLogin(LoginPhaseFinishedEvent e) {
		print("Checking for conflicting login with pseudo " + e.pseudo);
		for (PacketPseudoAvailabilityCheck p : conflictingLoginRequests) {
			if (p.pseudo.equals(e.pseudo)) {
				if (p.discriminant < e.discriminant) {
					state = State.STARTED;
					print("Pseudo " + e.pseudo + " already used");
					loginWindow.showMessage(
							"Le pseudo que vous avez choisi est déja pris, veuillez en choisir un autre.", "Erreur");
					loginWindow.enableLoginButton();
					state = State.STARTED;
					reconnectToLastSessionIfNeeded();
					return;
				}
			}
		}
		conflictingLoginRequests.clear();
		currentUser = new User(attribuedUserId, e.pseudo, e.isExternal, localAddress);
		state = State.LOGGED;
		mainWindow = new MainWindow(currentUser, eventQueue);
		for (User u : connectedUsers.values()) {
			mainWindow.addConnectedUser(u);
		}
		loginWindow.dispose();
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
		if(mainWindow != null) {
			for(Session s : opennedSessions.values()) {
				s.hide();
			}
			mainWindow.dispose();
			mainWindow = null;
		}
		loginWindow.enableLoginButton();
		if(useCentralizedServer) {
			getTCPClientToCentralizedServer().sendPacket(new PacketDisconnect());
		}
	}

	private void userConnected(PacketUser p) {
		if (p.user.id != attribuedUserId) {
			if (!connectedUsers.containsKey(p.user.id)) {
				connectedUsers.put(p.user.id, p.user);
				if (mainWindow != null) {
					mainWindow.unselectUser();
					mainWindow.addConnectedUser(p.user);
				}
			} else {
				connectedUsers.get(p.user.id).loggedDuration++;
			}
			try {
				eventQueue.addEventToQueue(
						new DisconnectUserEvent(p.user.id, connectedUsers.get(p.user.id).loggedDuration), 2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		updatePseudoInSessions(p.user);
	}
	
	private void updatePseudoInSessions(User user) {
		for(Session s : opennedSessions.values()) {
			s.updatePseudos(user);
		}
	}

	// Maintient l'utilisateur actif pour qu'il soit considéré comme connecté par
	// les autres utilisateurs
	private void periodicLogin() {
		Packet packet = new PacketUser(currentUser);
		udpSocket.sendPacket(packet, BROADCAST_ADDRESS, UDP_PORT);
		try {
			eventQueue.addEventToQueue(new PeriodicLoginEvent(sessionId), 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private void removeUser(User user) {
		connectedUsers.remove(user.id);
		print("User " + user.pseudo + " disconnected");
		if (mainWindow != null) {
			mainWindow.unselectUser();
			mainWindow.removeConnectedUser(user);
		}
	}

	private void registerPackets() {
		packetFactory.registerPacket(PacketLogin.class);
		packetFactory.registerPacket(PacketSignin.class);
		packetFactory.registerPacket(PacketPseudoAvailabilityCheck.class);
		packetFactory.registerPacket(PacketUser.class);
		packetFactory.registerPacket(PacketMessage.class);
		packetFactory.registerPacket(PacketStartSession.class);
		packetFactory.registerPacket(PacketKeepMessage.class);
		packetFactory.registerPacket(PacketGetHistory.class);
		packetFactory.registerPacket(PacketHistory.class);
		packetFactory.registerPacket(PacketConnectedUsers.class);
		packetFactory.registerPacket(PacketDisconnect.class);
		packetFactory.registerPacket(PacketLoginResult.class);
		packetFactory.registerPacket(PacketTunnel.class);
		packetFactory.registerPacket(PacketGetConnectedUsers.class);
		packetFactory.registerPacket(PacketPopup.class);
	}

	@Override
	public void onEvent(Event event) {
		// System.out.println("client event " + event);
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
				loginWindow = new LoginWindow(eventQueue, externalUser, false);
			}
		} else if (event instanceof RenamePseudoEvent) {
			RenamePseudoEvent e = (RenamePseudoEvent) event;
			if (state.equals(State.LOGGED)) {
				disconnect();
				oldLogin = new LoginEvent(currentUser.pseudo, currentUser.isExternal);
				try {
					eventQueue.addEventToQueue(new LoginEvent(e.pseudo, currentUser.isExternal));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} else if (event instanceof SessionEvent) {
			SessionEvent e = (SessionEvent) event;
			if (state.equals(State.LOGGED)) {
				try {
					Session s = opennedSessions.get(e.user.id);
					if (s == null) {
						boolean useTunnel = currentUser.isExternal || e.user.isExternal;
						TCPClient client;
						if(useTunnel) {
							client = getTCPClientToCentralizedServer();
						} else {
							client = new TCPClient(eventQueue, packetFactory, e.user.ipAddress, SERVER_PORT);
							client.start();
						}
						s = new Session(client, currentUser, e.user, eventQueue, packetFactory);
						opennedSessions.put(e.user.id, s);
						s.sendPacket(new PacketStartSession(currentUser.id));
						TCPClient clientToCentralizedServer = getTCPClientToCentralizedServer();
						if(clientToCentralizedServer != null) {
							clientToCentralizedServer.sendPacket(new PacketGetHistory(currentUser.id, e.user.id));							
						}
					}
					s.show();
					mainWindow.unselectUser();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} else if (event instanceof MessageEvent) {
			MessageEvent e = (MessageEvent) event;
			if (state.equals(State.LOGGED)) {
				Session s = opennedSessions.get(e.userTo.id);
				long timestamp = System.currentTimeMillis();
				Message m = new Message(e.userFrom.id, e.userTo.id, timestamp, e.content);
				s.addMessage(m);
				s.sendPacket(new PacketMessage(m));
				TCPClient clientToCentralizedServer = getTCPClientToCentralizedServer();
				if(clientToCentralizedServer != null) {
					clientToCentralizedServer.sendPacket(new PacketKeepMessage(m));				
				}
			}
		} else if (event instanceof SessionCloseEvent) {
			SessionCloseEvent e = (SessionCloseEvent) event;
			Session s = opennedSessions.get(e.user.id);
			s.hide();
			mainWindow.unselectUser();
		} else if (event instanceof UserSelectionChangedEvent) {
			UserSelectionChangedEvent e = (UserSelectionChangedEvent) event;
			if (e.user == null) {
				mainWindow.setOpenChatEnabled(false);
			} else {
				Session s = opennedSessions.get(e.user.id);
				mainWindow.setOpenChatEnabled(s == null || !s.isVisible());
			}
		}
	}

	private void onControllerEvent(ControllerEvent event) {
		if (event instanceof StartEvent) {
			if (state.equals(State.STARTING)) {
				if (attribuedUserId != -1) {
					state = State.STARTED;
					loginWindow.enableLoginButton();
				} else {
					// server not responding
					loginWindow.showMessage(
							"Vous n'avez pas encore d'UID et le serveur ne réponds pas pour vous en donner un",
							"Erreur");
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
			DisconnectUserEvent e = (DisconnectUserEvent) event;
			User u = connectedUsers.get(e.userId);
			if (u != null && u.loggedDuration == e.loggedDuration) {
				// Cet utilisateur ne s'est pas manifesté depuis trop longtemps donc on le
				// considère déconnecté
				removeUser(u);
			}
		}
	}

	private void onNetworkEvent(NetworkEvent event) {
		if (event instanceof TCPConnectionEvent) {
			TCPConnectionEvent e = (TCPConnectionEvent) event;
			if (e.client != tcpClient) {

			}
		} else if (event instanceof TCPPacketEvent) {
			TCPPacketEvent e = (TCPPacketEvent) event;
			if (e.packet instanceof PacketSignin) {
				attribuedUserId = ((PacketSignin) e.packet).attribuedUserId;
				saveUserId();
			} else if (e.packet instanceof PacketStartSession) {
				if (state.equals(State.LOGGED)) {
					User distantUser = connectedUsers.get(((PacketStartSession) e.packet).userId);
					Session s = new Session(e.client, currentUser, distantUser, eventQueue, packetFactory);
					TCPClient clientToCentralizedServer = getTCPClientToCentralizedServer();
					if(clientToCentralizedServer != null) {
						clientToCentralizedServer.sendPacket(new PacketGetHistory(currentUser.id, distantUser.id));						
					}
					opennedSessions.put(distantUser.id, s);
				}
			} else if (e.packet instanceof PacketMessage) {
				if (state.equals(State.LOGGED)) {
					PacketMessage p = (PacketMessage) e.packet;
					Session s = opennedSessions.get(p.message.from);
					if (!s.isVisible()) {
						mainWindow.unselectUser();
						s.show();
					}
					s.addMessage(p.message);
				}
			} else if (e.packet instanceof PacketHistory) {
				PacketHistory p = (PacketHistory) e.packet;
				Session s = opennedSessions.get(p.userId2);
				if(s != null) {
					for(Message m : p.messages) {
						packetFactory.registerPacket(PacketGetConnectedUsers.class);
						packetFactory.registerPacket(PacketGetHistory.class);
						packetFactory.registerPacket(PacketHistory.class);
						s.addMessage(m);
					}
				}
			} else if (e.packet instanceof PacketTunnel) {
				PacketTunnel p = (PacketTunnel) e.packet;
				try {
					Packet extractedPacket = p.extractPacket(packetFactory);
					TCPPacketEvent newEvent = new TCPPacketEvent(e.client, extractedPacket);
					onNetworkEvent(newEvent);
				} catch (InstantiationException | IllegalAccessException | IOException e1) {
					e1.printStackTrace();
				}
			} else if (e.packet instanceof PacketConnectedUsers) {
				PacketConnectedUsers p = (PacketConnectedUsers)e.packet;
				for(User u : p.users) {
					if(connectedUsers.containsKey(u.id)) {
						User u2 = this.connectedUsers.get(u.id);
						if(!u.pseudo.equals(u2.pseudo)) {
							connectedUsers.put(u.id, u);
							if(mainWindow != null) {
								mainWindow.setConnectedUser(u);
							}
						}
					} else if(u.id != attribuedUserId){
						connectedUsers.put(u.id, u);
						if(mainWindow != null) {
							mainWindow.addConnectedUser(u);
						}
					}
					Session s = opennedSessions.get(u.id);
					if(s != null) {
						s.updatePseudos(u);
					}
					if(u.id == currentUser.id) {
						for(Session s2 : opennedSessions.values()) {
							s2.updatePseudos(u);
						}
					}
				}
				Set<Integer> connectedUsers = p.users.stream().map((User u) -> {return u.id;}).collect(Collectors.toSet());
				Set<Integer> disconnectedUsers = new HashSet<Integer>();
				for(int userId : this.connectedUsers.keySet()) {
					if(!connectedUsers.contains(userId)) {
						disconnectedUsers.add(userId);
					}
				}
				for(int userId : disconnectedUsers) {
					if(mainWindow != null) {
						mainWindow.removeConnectedUser(this.connectedUsers.get(userId));
					}
					this.connectedUsers.remove(userId);
				}
			} else if (e.packet instanceof PacketLoginResult) {
				if(((PacketLoginResult)e.packet).success) {
					conflictingLoginRequests.clear();
					currentUser = new User(attribuedUserId, askedLogin.pseudo, askedLogin.isExternal, localAddress);
					state = State.LOGGED;
					mainWindow = new MainWindow(currentUser, eventQueue);
					for (User u : connectedUsers.values()) {
						mainWindow.addConnectedUser(u);
					}
					loginWindow.dispose();
					print("Succesfully connected");
				} else {
					loginWindow.showMessage("Le pseudo que vous avez choisi est déja pris, veuillez en choisir un autre.", "Erreur");
					loginWindow.enableLoginButton();
					state = State.STARTED;
					reconnectToLastSessionIfNeeded();
				}
			} else if(e.packet instanceof PacketPopup) {
				new Popup();
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

	private TCPClient getTCPClientToCentralizedServer() {
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
		if (currentUser != null) {
			str = String.format("User %s (id %d) : %s", currentUser.pseudo, currentUser.id, msg);
		} else {
			str = msg;
		}
		System.out.println(str);
	}

}

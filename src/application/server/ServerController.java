package application.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import application.User;
import database.Database;
import network.NetworkEvent;
import network.Packet;
import network.PacketFactory;
import network.TCPClient;
import network.TCPPacketEvent;
import network.TCPServer;
import utils.Console;
import utils.ConsoleEvent;
import utils.Event;
import utils.EventListener;
import utils.EventQueue;

public class ServerController implements EventListener {

	private static final int SERVER_PORT = 4321;
	private static final String DATABASE_URL = "jdbc:mysql://srv-bdens.insa-toulouse.fr/tpservlet_07";
	private static final String DATABASE_USERNAME = "tpservlet_07";
	private static final String DATABASE_PASSWORD = "ODei0ce1";

	private final EventQueue eventQueue = new EventQueue(this);
	private final PacketFactory packetFactory = new PacketFactory();
	private final Map<Integer, User> connectedUsers = new HashMap<Integer, User>();
	private final Map<TCPClient, Integer> clientsToUserId = new HashMap<TCPClient, Integer>();
	private final Map<Integer, TCPClient> usersIdToClients = new HashMap<Integer, TCPClient>();
	private final Console console = new Console(eventQueue);
	private Database database;
	private TCPServer tcpServer;
	private String constructingCmd = null;

	ServerController() throws ClassNotFoundException, SQLException {
		registerPackets();
		database = new Database(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	}

	public void start() throws IOException {
		eventQueue.start();
		tcpServer = new TCPServer(eventQueue, packetFactory, SERVER_PORT);
		tcpServer.start();
		console.start();
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

	// Utile pour les tests seulement
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof ConsoleEvent) {
			onConsoleEvent((ConsoleEvent) event);
		} else if (event instanceof NetworkEvent) {
			onNetworkEvent((NetworkEvent) event);
		}
	}

	private void onConsoleEvent(ConsoleEvent event) {
		String fullCmd = event.getFullCmd().trim();
		String cmd = constructingCmd != null ? constructingCmd + " " : "";
		cmd += fullCmd;
		if (cmd.equals("exit")) {
			System.exit(0);
		} else if (cmd.startsWith("db ")) {
			cmd = cmd.substring("db".length()).trim();
			if (cmd.startsWith("clear ")) {
				cmd = cmd.substring("clear".length()).trim();
				if (cmd.equals("messages") || cmd.equals("user")) {
					System.out.println("For wich user id ? (type \"all\" for all users)");
					constructingCmd = fullCmd;
				} else if (cmd.startsWith("messages ")) {
					cmd = cmd.substring("messages".length()).trim();
					if (cmd.equals("all")) {
						constructingCmd = null;
						database.executeRequest(new ClearMessagesRequest(ClearMessagesRequest.ALL_USERS));
					} else {
						constructingCmd = null;
						try {
							int userId = Integer.parseInt(cmd);
							database.executeRequest(new ClearMessagesRequest(userId));
						} catch(NumberFormatException e) {
							System.out.println("Invalid syntax");
						}
					}
				} else if (cmd.startsWith("user ")) {
					cmd = cmd.substring("user".length()).trim();
					if (cmd.equals("all")) {
						constructingCmd = null;
						database.executeRequest(new ClearUserRequest(ClearMessagesRequest.ALL_USERS));
					} else {
						constructingCmd = null;
						try {
							int userId = Integer.parseInt(cmd);
							database.executeRequest(new ClearUserRequest(userId));
						} catch(NumberFormatException e) {
							System.out.println("Invalid syntax");
						}
					}
				} else {
					System.out.println("Invalid syntax");
				}
			}
			else if (cmd.startsWith("show ")) {
				cmd = cmd.substring("show".length()).trim();
				if (cmd.equals("messages")) {
					System.out.println("For wich user id ? (type \"all\" for all users)");
					constructingCmd = fullCmd;
				} else if (cmd.startsWith("messages ")) {
					cmd = cmd.substring("messages".length()).trim();
					if (cmd.equals("all")) {
						constructingCmd = null;
						GetMessagesRequest request = new GetMessagesRequest(GetMessagesRequest.ALL_USERS);
						database.executeRequest(request);
						for(String message : request.messages) {
							System.out.println(message);
						}
					} else {
						constructingCmd = null;
						try {
							int userId = Integer.parseInt(cmd);
							GetMessagesRequest request = new GetMessagesRequest(userId);
							database.executeRequest(request);
							for(String message : request.messages) {
								System.out.println(message);
							}
						} catch(NumberFormatException e) {
							System.out.println("Invalid syntax");
						}
					}
				} else if (cmd.equals("users")) {
					GetUsersRequest request = new GetUsersRequest();
					database.executeRequest(request);
					for(String user : request.users) {
						System.out.println(user);
					}
				} else {
					System.out.println("Invalid syntax");
				}
			} else {
				System.out.println("Invalid syntax");
			}
		} else if(cmd.equals("popup")) {
			System.out.println("Popup");
			for(TCPClient client : usersIdToClients.values()) {
				client.sendPacket(new PacketPopup());
			}			
		} else {
			System.out.println("Invalid syntax");
		}
	}

	private void onNetworkEvent(NetworkEvent event) {
		if (event instanceof TCPPacketEvent) {
			TCPPacketEvent e = (TCPPacketEvent) event;
			if (e.packet instanceof PacketSignin) {
				GetLastUserIdRequest r = new GetLastUserIdRequest();
				database.executeRequest(r);
				int attribuedUserId = r.lastUserId + 1;
				database.executeRequest(new AddUserRequest(attribuedUserId));
				e.client.sendPacket(new PacketSignin(attribuedUserId));
				System.out.println("New user is registered");
			} else if(e.packet instanceof PacketKeepMessage) {
				PacketKeepMessage p = (PacketKeepMessage) e.packet;
				database.executeRequest(new AddMessageRequest(p.message));
			} else if(e.packet instanceof PacketGetHistory) {
				PacketGetHistory p = (PacketGetHistory) e.packet;
				GetHistoryRequest request = new GetHistoryRequest(p.userId1, p.userId2);
				database.executeRequest(request);
				e.client.sendPacket(new PacketHistory(p.userId1, p.userId2, request.messages));
				System.out.println("Send history");
			}  else if(e.packet instanceof PacketLogin) {
				PacketLogin p = (PacketLogin) e.packet;
				boolean pseudoAlreadyUsed = false;
				for(User u : connectedUsers.values()) {
					if(u.pseudo.equals(p.user.pseudo)) {
						pseudoAlreadyUsed = true;
						break;
					}
				}
				if(pseudoAlreadyUsed) {
					e.client.sendPacket(new PacketLoginResult(false));
					System.out.println("User attempted to log but with a already used pseudo");
				} else {
					System.out.println("User successfully logged");
					addUser(p.user, e.client);
					e.client.sendPacket(new PacketLoginResult(true));
					notifyAllUsers();
				}
			}  else if(e.packet instanceof PacketDisconnect) {
				System.out.println("User disconnect");
				removeUser(clientsToUserId.get(e.client));
				notifyAllUsers();
			} else if(e.packet instanceof PacketTunnel) {
				PacketTunnel p = (PacketTunnel)e.packet;
				Packet extractedPacket;
				try {
					extractedPacket = p.extractPacket(packetFactory);
					System.out.println("Repeating packet " + extractedPacket.getClass().getName().substring(extractedPacket.getClass().getName().lastIndexOf('.') + 1));
					TCPClient client = usersIdToClients.get(p.destUserId);
					if(client != null) {
						client.sendPacket(new PacketTunnel(extractedPacket, p.destUserId, packetFactory));
					}
				} catch (InstantiationException | IllegalAccessException | IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	private void notifyAllUsers() {
		System.out.println("Notify all users");
		List<User> connectedUsers = new ArrayList<User>();
		for(User u : this.connectedUsers.values()) {
			connectedUsers.add(u);
		}
		for(TCPClient client : usersIdToClients.values()) {
			client.sendPacket(new PacketConnectedUsers(connectedUsers));
		}
	}
	
	private void addUser(User u, TCPClient client) {
		if(!connectedUsers.containsKey(u.id)) {
			connectedUsers.put(u.id, u);
			clientsToUserId.put(client, u.id);
			usersIdToClients.put(u.id, client);
		}
	}
	
	private void removeUser(int userId) {
		if(connectedUsers.containsKey(userId)) {
			connectedUsers.remove(userId);
			TCPClient client = usersIdToClients.get(userId);
			if(client != null) {
				usersIdToClients.remove(userId);
				clientsToUserId.remove(client);
			}
		}
	}

}

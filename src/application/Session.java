package application;

import application.client.ChatWindow;
import network.TCPClient;

public class Session {
	
	public final TCPClient tcpClient;
	public final User userFrom;
	public final User userTo;
	
	private boolean visible = false;
	private ChatWindow chatWindow;
	
	public Session(TCPClient client, User userFrom, User userTo) {
		tcpClient = client;
		this.userFrom = userFrom;
		this.userTo = userTo;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void show() {
		chatWindow = new ChatWindow(userFrom, userTo);
		visible = true;
	}
	
	public void sendMessage(Message m) {
		tcpClient.sendPacket(new PacketMessage(m));
	}
	
	public void addMessage(Message m) {
		String pseudo = m.from == userFrom.id ? userFrom.pseudo : userTo.pseudo;
		chatWindow.addMessageReceived(m.timestamp, pseudo, m.content);
	}
	
}

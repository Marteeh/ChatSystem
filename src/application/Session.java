package application;

import java.util.ArrayList;
import java.util.List;

import application.client.ChatWindow;
import network.TCPClient;
import utils.EventQueue;

public class Session {

	public final TCPClient tcpClient;
	public User userFrom;
	public User userTo;
	private final EventQueue eventQueue;

	private boolean visible = false;
	private ChatWindow chatWindow;
	private final List<Message> messages = new ArrayList<Message>();

	public Session(TCPClient client, User userFrom, User userTo, EventQueue eventQueue) {
		tcpClient = client;
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.eventQueue = eventQueue;
	}

	public boolean isVisible() {
		return visible;
	}

	public void show() {
		if (!isVisible()) {
			chatWindow = new ChatWindow(userFrom, userTo, eventQueue);
			visible = true;
			for (Message m : messages) {
				addMessage(m, false);
			}
		}
	}

	public void hide() {
		if (isVisible()) {
			chatWindow.dispose();
			chatWindow = null;
			visible = false;
		}
	}

	public void sendMessage(Message m) {
		tcpClient.sendPacket(new PacketMessage(m));
	}

	public void addMessage(Message m) {
		addMessage(m, true);
	}

	private void addMessage(Message m, boolean storeMessage) {
		if (storeMessage) {
			messages.add(m);
		}
		String pseudo = m.from == userFrom.id ? userFrom.pseudo : userTo.pseudo;
		chatWindow.addMessage(m.timestamp, pseudo, m.content);
	}

	public void updatePseudos(User user) {
		if (user.id == userFrom.id && !user.pseudo.equals(userFrom.pseudo)) {
			userFrom = user;
			hide();
			show();
		} else if (user.id == userTo.id && !user.pseudo.equals(userTo.pseudo)) {
			userTo = user;
			hide();
			show();
		}
	}
}

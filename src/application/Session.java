package application;

import network.TCPClient;

public class Session {
	
	public final TCPClient tcpClient;
	
	private boolean visible = false;
	
	public Session(TCPClient client) {
		tcpClient = client;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void show() {
		visible = true;
	}
	
	public void sendMessage(Message m) {
		tcpClient.sendPacket(new PacketMessage(m));
	}
	
}

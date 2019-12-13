package network;

public class ConnectionEvent implements NetworkEvent {
	public final TCPClient client;
	
	ConnectionEvent(TCPClient client) {
		this.client = client;
	}
}

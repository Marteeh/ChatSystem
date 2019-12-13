package network;

public class DisconnectionEvent implements NetworkEvent {
	public final TCPClient client;
	
	DisconnectionEvent(TCPClient client) {
		this.client = client;
	}
}

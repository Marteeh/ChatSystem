package network;

public class TCPConnectionEvent implements NetworkEvent {
	public final TCPClient client;
	
	TCPConnectionEvent(TCPClient client) {
		this.client = client;
	}
}

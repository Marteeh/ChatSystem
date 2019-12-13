package network;

public class TCPPacketEvent implements NetworkEvent {
	
	public final TCPClient client;
	public final Packet packet;
	
	TCPPacketEvent(TCPClient client, Packet packet) {
		this.client = client;
		this.packet = packet;
	}
}

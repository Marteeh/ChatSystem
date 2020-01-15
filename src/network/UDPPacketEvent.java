package network;

public class UDPPacketEvent implements NetworkEvent {
	
	public final UDPSocket receiverUDPSocket;
	public final Packet packet;
	
	UDPPacketEvent(UDPSocket receiverUDPSocket, Packet packet) {
		this.receiverUDPSocket = receiverUDPSocket;
		this.packet = packet;
	}
}

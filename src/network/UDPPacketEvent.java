package network;

public class UDPPacketEvent implements NetworkEvent {
	
	public final UDPSocket receiverUDPSocket;
	private final String senderIpAddress;
	public final Packet packet;
	
	UDPPacketEvent(UDPSocket receiverUDPSocket, String senderIpAddress, Packet packet) {
		this.receiverUDPSocket = receiverUDPSocket;
		this.senderIpAddress = senderIpAddress;
		this.packet = packet;
	}
}

package network;

public class PacketEvent extends NetworkEvent {
	public final Packet packet;
	
	PacketEvent(Client client, Packet packet) {
		super(client);
		this.packet = packet;
	}
}

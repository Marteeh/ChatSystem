package application;

import network.Packet;
import network.PacketFactory;
import network.TCPClient;

public class TCPTunnel {
	
	private final int destUserId;
	private final TCPClient client;
	private final PacketFactory packetFactory;
	
	public TCPTunnel(int destUserId, TCPClient client, PacketFactory packetFactory) {
		this.destUserId = destUserId;
		this.client = client;
		this.packetFactory = packetFactory;
	}
	
	public void sendPacket(Packet packet) {
		client.sendPacket(new PacketTunnel(packet, destUserId, packetFactory));
	}
}

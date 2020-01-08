package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;
import network.PacketFactory;

public class PacketTunnel implements Packet {

	public Packet packet;
	public int destUserId;
	
	int packetType;
	public byte[] data;
	
	private PacketFactory packetFactory;
	
	public PacketTunnel() {
		
	}
	
	public PacketTunnel(Packet packet, int destUserId, PacketFactory packetFactory) {
		this.packet = packet;
		this.destUserId = destUserId;
		this.packetFactory = packetFactory;
	}
	
	@Override
	public void read(DataInputStream input) throws IOException {
		destUserId = input.readInt();
		packetType = input.readInt();
		data = new byte[input.readInt()];
		input.readFully(data);
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(destUserId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		packet.write(new DataOutputStream(baos));
		baos.flush();
		byte[] data = baos.toByteArray();
		baos.close();
		output.writeInt(packetFactory.getPacketType(packet));
		output.writeInt(data.length);
		output.write(data);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Packet> T extractPacket(PacketFactory packetFactory) throws IOException, InstantiationException, IllegalAccessException {
		Packet p = packetFactory.createPacket(packetType);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		p.read(new DataInputStream(bais));
		bais.close();
		return (T)p;
	}
}

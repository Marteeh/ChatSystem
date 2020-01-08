package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketGetHistory implements Packet {

	public int userId1;
	public int userId2;
	
	public PacketGetHistory() {

	}
	
	public PacketGetHistory(int userId1, int userId2) {
		this.userId1 = userId1;
		this.userId2 = userId2;
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		userId1 = input.readInt();
		userId2 = input.readInt();
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(userId1);
		output.writeInt(userId2);
	}

}

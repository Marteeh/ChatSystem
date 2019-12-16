package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketStartSession implements Packet {
	
	public int userId;
	
	public PacketStartSession() {
		
	}
	
	public PacketStartSession(int userId) {
		
		this.userId = userId;
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		userId = input.readInt();
		
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(userId);;
		
	}

}

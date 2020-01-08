package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketSignin implements Packet {
	
	public int attribuedUserId;
	
	public PacketSignin() {

	}
	
	public PacketSignin(int attribuedUserId) {
		this.attribuedUserId = attribuedUserId;
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		attribuedUserId = input.readInt();
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(attribuedUserId);
	}

}

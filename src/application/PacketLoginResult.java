package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketLoginResult implements Packet {

	public boolean success;
	
	public PacketLoginResult() {
		
	}
	
	public PacketLoginResult(boolean success) {
		this.success = success;
	}
	
	@Override
	public void read(DataInputStream input) throws IOException {
		success = input.readBoolean();
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeBoolean(success);
	}
}

package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketLogin implements Packet {

	public String pseudo;
	public boolean isExternal;
	
	public PacketLogin() {
		
	}
	
	public PacketLogin(String pseudo, boolean isExternal) {
		this.pseudo = pseudo;
		this.isExternal = isExternal;
	}
	
	@Override
	public void read(DataInputStream input) throws IOException {
		pseudo = input.readUTF();
		isExternal = input.readBoolean();
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeUTF(pseudo);
		output.writeBoolean(isExternal);
	}
}

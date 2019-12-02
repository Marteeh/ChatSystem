package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketLogin implements Packet {

	public String username;
	public String password;
	public boolean isExternal;
	
	public PacketLogin(String username, String password, boolean isExternal) {
		this.username = username;
		this.password = password;
		this.isExternal = isExternal;
	}
	
	@Override
	public void read(DataInputStream input) throws IOException {
		username = input.readUTF();
		password = input.readUTF();
		isExternal = input.readBoolean();
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeUTF(username);
		output.writeUTF(password);
		output.writeBoolean(isExternal);
	}
}

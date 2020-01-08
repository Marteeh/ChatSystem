package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketLogin implements Packet {

	public User user;
	
	public PacketLogin() {
		
	}
	
	public PacketLogin(User user) {
		this.user = user;
	}
	
	@Override
	public void read(DataInputStream input) throws IOException {
		user = new User(input.readInt(), input.readUTF(), input.readBoolean(), input.readUTF());
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(user.id);
		output.writeUTF(user.pseudo);
		output.writeBoolean(user.isExternal);
		output.writeUTF(user.ipAddress);
	}
}

package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.Packet;

public class PacketConnectedUsers implements Packet {

	public final List<User> users = new ArrayList<User>();
	
	public PacketConnectedUsers() {

	}
	
	public PacketConnectedUsers(List<User> users) {
		this.users.addAll(users);
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		int count = input.readInt();
		for(int i = 0; i < count; i++) {
			users.add(new User(input.readInt(), input.readUTF(), input.readBoolean(), input.readUTF()));
		}
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(users.size());
		for(User u : users) {
			output.writeInt(u.id);
			output.writeUTF(u.pseudo);
			output.writeBoolean(u.isExternal);
			output.writeUTF(u.ipAddress);
		}
	}

}

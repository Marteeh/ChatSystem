package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import network.Packet;

public class PacketHistory implements Packet {

	public int userId1;
	public int userId2;
	public final List<Message> messages = new ArrayList<Message>();
	
	public PacketHistory() {

	}
	
	public PacketHistory(int userId1, int userId2, List<Message> messages) {
		this.userId1 = userId1;
		this.userId2 = userId2;
		this.messages.addAll(messages);
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		userId1 = input.readInt();
		userId2 = input.readInt();
		int count = input.readInt();
		for(int i = 0; i < count; i++) {
			messages.add(new Message(input.readInt(), input.readInt(), input.readLong(), input.readUTF()));
		}
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(userId1);
		output.writeInt(userId2);
		output.writeInt(messages.size());
		for(Message m : messages) {
			output.writeInt(m.from);
			output.writeInt(m.to);
			output.writeLong(m.timestamp);
			output.writeUTF(m.content);
		}
	}

}

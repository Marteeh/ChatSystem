package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketKeepMessage implements Packet {
	
	public Message message;
	
	public PacketKeepMessage() {

	}
	
	public PacketKeepMessage(Message message) {
		this.message = message;
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		message = new Message(input.readInt(), input.readInt(), input.readLong(), input.readUTF());
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(message.from);
		output.writeInt(message.to);
		output.writeLong(message.timestamp);
		output.writeUTF(message.content);
	}

}

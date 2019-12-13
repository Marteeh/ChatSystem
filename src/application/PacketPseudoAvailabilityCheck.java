package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import network.Packet;

public class PacketPseudoAvailabilityCheck implements Packet {
	
	public int userId;
	public String pseudo;
	public long discriminant;
	
	public PacketPseudoAvailabilityCheck() {
		
	}
	
	public PacketPseudoAvailabilityCheck(int userId, String pseudo, long discriminant) {
		this.userId = userId;
		this.pseudo = pseudo;
		this.discriminant = discriminant;
	}

	@Override
	public void read(DataInputStream input) throws IOException {
		userId = input.readInt();
		pseudo = input.readUTF();
		discriminant = input.readLong();
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(userId);
		output.writeUTF(pseudo);
		output.writeLong(discriminant);
	}

		
}

package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketError implements PacketResponse {
	
	public String errorMessage;
	
	public PacketError() {
		
	}
	
	public PacketError(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public void read(DataInputStream input) throws IOException {
		if(input.readBoolean()) {
			errorMessage = input.readUTF();
		}
	}

	@Override
	public void write(DataOutputStream output) throws IOException {
		output.writeBoolean(errorMessage != null);
		if(errorMessage != null) {
			output.writeUTF(errorMessage);
		}
	}
}

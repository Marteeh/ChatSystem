package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {
	public void read(DataInputStream input) throws IOException;
	
	public void write(DataOutputStream output) throws IOException;
}

package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import utils.EventQueue;

public class TCPClient extends Thread {
	private final EventQueue eventQueue;
	private final PacketFactory packetFactory;
	private final Socket socket;
	private final DataOutputStream output;

	public TCPClient(EventQueue eventQueue, PacketFactory packetFactory, String hostname, int port) throws UnknownHostException, IOException, InterruptedException {
		this(eventQueue, packetFactory, new Socket(hostname, port));
	}

	TCPClient(EventQueue eventQueue, PacketFactory packetFactory, Socket socket) throws UnknownHostException, IOException, InterruptedException {
		this.eventQueue = eventQueue;
		this.packetFactory = packetFactory;
		this.socket = socket;
		output = new DataOutputStream(socket.getOutputStream());
		eventQueue.addEventToQueue(new TCPConnectionEvent(this));
	}

	@Override
	public void run() {
		try {
			DataInputStream input = new DataInputStream(socket.getInputStream());
			while(!Thread.currentThread().isInterrupted()) {
				Packet packet = packetFactory.createPacket(input.readInt());
				packet.read(input);
				eventQueue.addEventToQueue(new TCPPacketEvent(this, packet));
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	public final void sendPacket(Packet packet) {
		try {
			output.writeInt(packetFactory.getPacketType(packet));
			packet.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package network;

import java.io.IOException;
import java.net.ServerSocket;

import utils.EventQueue;

public class TCPServer extends Thread {
	private final EventQueue eventQueue;
	private final PacketFactory packetFactory;
	private final ServerSocket serverSocket;

	public TCPServer(EventQueue eventQueue, PacketFactory packetFactory, int port) throws IOException {
		this.eventQueue = eventQueue;
		this.packetFactory = packetFactory;
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				new TCPClient(eventQueue, packetFactory, serverSocket.accept()).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

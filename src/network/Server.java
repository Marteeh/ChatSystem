package network;

import java.io.IOException;
import java.net.ServerSocket;

import utils.EventQueue;
import utils.EventListener;

public class Server extends Thread {
	private final EventQueue eventQueue;
	private final PacketFactory packetFactory;
	private final ServerSocket serverSocket;

	public Server(EventListener listener, PacketFactory packetFactory, int port) throws IOException {
		this.eventQueue = new EventQueue(listener);
		this.packetFactory = packetFactory;
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				new Client(eventQueue, packetFactory, serverSocket.accept()).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

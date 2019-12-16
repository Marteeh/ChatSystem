package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import utils.EventQueue;

public class UDPSocket extends Thread {

	private DatagramSocket receiverSocket;
	private final DatagramSocket senderSocket;
	private final byte[] buffer = new byte[65535];
	private final EventQueue eventQueue;
	private final PacketFactory packetFactory;
	
	public UDPSocket(EventQueue eventQueue, PacketFactory packetFactory) throws SocketException {
		senderSocket = new DatagramSocket();
		this.eventQueue = eventQueue;
		this.packetFactory = packetFactory;
	}
	
	public void listen(int port) throws SocketException {
		receiverSocket = new DatagramSocket(port);
		this.start();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				DatagramPacket p = new DatagramPacket(buffer, buffer.length);
				receiverSocket.receive(p);
				DataInputStream input = new DataInputStream(new ByteArrayInputStream(buffer));
				Packet packet = packetFactory.createPacket(input.readInt());
				packet.read(input);
				eventQueue.addEventToQueue(new UDPPacketEvent(this, p.getAddress().getHostAddress(), packet));
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
	}
	
	public void sendPacket(Packet packet, String ipAddress, int port) {		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(baos);
			output.writeInt(packetFactory.getPacketType(packet));
			packet.write(output);
			output.close();
			byte[] buffer = baos.toByteArray();
			if(buffer.length > 65535) {
				throw new RuntimeException("Packet length is too large");
			}
			senderSocket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ipAddress), port));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getIpAddress() {
		return receiverSocket.getLocalAddress().getHostAddress();
	}
}

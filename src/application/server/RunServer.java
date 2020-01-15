package application.server;

import java.io.IOException;
import java.sql.SQLException;

import application.PacketSignin;
import network.TCPPacketEvent;
import utils.EventQueue;

public class TestServer {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		ServerController serverController = new ServerController();
		serverController.start();
		EventQueue eventQueue = serverController.getEventQueue();
	}
}

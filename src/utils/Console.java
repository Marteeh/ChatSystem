package utils;

import java.util.Scanner;

public class Console extends Thread {
	
	private final EventQueue eventQueue;
	private final Scanner sc;
	
	public Console(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
		sc = new Scanner(System.in);
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			String cmd = sc.nextLine();
			String[] array = cmd.split(" ");
			if(array.length == 0) {
				System.out.print("Erreur : la commande n'est pas bien formée");
			} else {
				String action = array[0];
				String[] args = new String[array.length - 1];
				for(int i = 0; i < args.length; i++) {
					args[i] = array[i + 1];
				}
				try {
					eventQueue.addEventToQueue(new ConsoleEvent(action, args));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		sc.close();
	}
}

package utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventQueue extends Thread {
	private final EventListener listener;
	private final BlockingQueue<Event> queue;

	public EventQueue(EventListener listener) {
		this.listener = listener;
		queue = new LinkedBlockingQueue<Event>();
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Event event = queue.take();
				listener.onEvent(event);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addEventToQueue(Event event) throws InterruptedException {
		queue.put(event);
	}

}

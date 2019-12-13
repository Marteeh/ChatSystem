package utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventQueue extends Thread {
	private final EventListener listener;
	private final BlockingQueue<Event> queue;
	private final EventDelayer eventDelayer = new EventDelayer(this);

	public EventQueue(EventListener listener) {
		this.listener = listener;
		queue = new LinkedBlockingQueue<Event>();
	}

	@Override
	public void run() {
		eventDelayer.start();
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
	
	public void addEventToQueue(Event event, int delay) throws InterruptedException {
		eventDelayer.addEventToQueue(new DelayedEvent(delay, event));
	}
}

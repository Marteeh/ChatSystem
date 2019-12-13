package utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

class EventDelayer extends Thread {

	private final BlockingQueue<DelayedEvent> queue = new PriorityBlockingQueue<DelayedEvent>();
	private final EventQueue eventQueue;
	private volatile boolean sleeping = false;

	EventDelayer(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				DelayedEvent event = queue.take();
				int delay = (int) (event.fireTime - System.currentTimeMillis());
				if (delay > 0) {
					sleeping = true;
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						sleeping = false;
						Thread.interrupted();
						queue.put(event);
						continue;
					}
					sleeping = false;
				}
				eventQueue.addEventToQueue(event.event);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addEventToQueue(DelayedEvent event) throws InterruptedException {
		if (sleeping) {
			this.interrupt();
		}
		queue.put(event);
	}
}

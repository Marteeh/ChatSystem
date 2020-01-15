package utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

class EventDelayer extends Thread {

	private final BlockingQueue<DelayedEvent> queue = new PriorityBlockingQueue<DelayedEvent>();
	private final EventQueue eventQueue;

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
					try {
						Thread.sleep(delay);
						synchronized (this) {
							eventQueue.addEventToQueue(event.event);
						}
					} catch (InterruptedException e) {
						Thread.interrupted();
						queue.put(event);
						continue;
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void addEventToQueue(DelayedEvent event) throws InterruptedException {
		queue.put(event);
		if (this.getState().equals(Thread.State.TIMED_WAITING)) {
			this.interrupt();
		}
	}
}

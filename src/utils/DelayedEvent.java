package utils;

class DelayedEvent implements Comparable<DelayedEvent> {
	
	final long fireTime;
	final Event event;
	
	DelayedEvent(int delay, Event event) {
		fireTime = System.currentTimeMillis() + delay;
		this.event = event;
	}

	@Override
	public int compareTo(DelayedEvent o) {
		return Long.compare(fireTime, o.fireTime);
	}
}
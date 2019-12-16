package application;

public class Message {

	public final int from;
	public final int to;
	public final long timestamp;
	public final String content;
	
	public Message(int from, int to, long timestamp, String content) {
		this.from = from;
		this.to = to;
		this.timestamp = timestamp;
		this.content = content;
	}
}

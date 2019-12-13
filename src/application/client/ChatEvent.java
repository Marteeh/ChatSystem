package application.client;

public class ChatEvent extends GUIEvent {
	
	public final String from;
	public final String to;
	
	ChatEvent(String from, String to){
		this.from = from;
		this.to = to;
	}
}

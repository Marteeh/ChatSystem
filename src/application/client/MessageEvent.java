package application.client;

import java.util.Date;

public class MessageEvent extends GUIEvent {
	
	public final String from;
	public final String to;
	public final String message;
	public final Date sendDate;
	
	MessageEvent(String from, String to, String message, Date sendDate){
		this.from = from;
		this.to = to;
		this.message = message;
		this.sendDate = sendDate;
	}
}

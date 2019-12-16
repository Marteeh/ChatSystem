package application.client;

import java.util.Date;

import application.User;

public class MessageEvent extends GUIEvent {
	
	public final User userFrom;
	public final User userTo;
	public final Date sendDate;
	public final String content;
    
	MessageEvent(User userFrom, User userTo, Date sendDate, String content) {
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.sendDate = sendDate;
		this.content = content;
	}
}

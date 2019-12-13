package application.client;

public class MessageEvent extends GUIEvent {
	
	public final User userFrom;
	public final User userTo;
	public final Date sendDate;
	public final String content;
    
	MessageEvent(User userFrom, User userTo, Date sendDate, String content) {
		this.userFrom = username;
		this.userTo = password;
		this.sendDate = isExternal;
		this.content = content;
	}
}

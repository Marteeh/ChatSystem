package application;

public class DisconnectUserEvent implements ControllerEvent {

	public final int userId;
	public final int loggedDuration;
	
	public DisconnectUserEvent(int userId, int loggedDuration) {
		this.userId = userId;
		this.loggedDuration = loggedDuration;
	}
}

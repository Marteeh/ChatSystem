package application;

public class PeriodicLoginEvent implements ControllerEvent {
	
	public int sessionId;
	
	public PeriodicLoginEvent(int sessionId) {
		this.sessionId = sessionId;
	}
}

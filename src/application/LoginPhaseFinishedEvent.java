package application;

public class LoginPhaseFinishedEvent implements ControllerEvent {
	
	public final String pseudo;
	public final boolean isExternal;
	public final long discriminant;
	
	public LoginPhaseFinishedEvent(String pseudo, boolean isExternal, long discriminant) {
		this.pseudo = pseudo;
		this.isExternal = isExternal;
		this.discriminant = discriminant;
	}
}

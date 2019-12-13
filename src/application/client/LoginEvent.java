package application.client;

public class LoginEvent extends GUIEvent {
	
	public final String pseudo;
	public final boolean isExternal;
	
	LoginEvent(String pseudo, boolean isExternal) {
		this.pseudo = pseudo;
		this.isExternal = isExternal;
	}
}

package application.client;

public class LoginEvent extends GUIEvent {
	
	public final String username;
	public final String password;
	public final boolean isExternal;
	
	LoginEvent(String username, String password, boolean isExternal) {
		this.username = username;
		this.password = password;
		this.isExternal = isExternal;
	}
}

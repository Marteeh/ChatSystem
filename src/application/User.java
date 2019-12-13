package application;

public class User {
	
	public final int id;
	public final String pseudo;
	public final boolean isExternal;
	public final String ipAddress;
	
	public int loggedDuration = 0;
	
	public User(int id, String pseudo, boolean isExternal, String ipAddress) {
		this.id = id;
		this.pseudo = pseudo;
		this.isExternal = isExternal;
		this.ipAddress = ipAddress;
	}
}

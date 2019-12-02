package application;

public class User {
	
	public final int id;
	public final String username;
	public final boolean isExternal;
	public final String ipAddress;
	
	public User(int id, String username, boolean isExternal, String ipAddress) {
		this.id = id;
		this.username = username;
		this.isExternal = isExternal;
		this.ipAddress = ipAddress;
	}
}

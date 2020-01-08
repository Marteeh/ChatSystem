package utils;

public class ConsoleEvent implements Event {
	
	public final String action;
	public final String[] args;
	
	ConsoleEvent(String action, String[] args) {
		this.action = action;
		this.args = args;
	}
	
	public String getFullCmd() {
		String cmd = action;
		for(String arg : args) {
			cmd += " " + arg;
		}
		return cmd;
	}
}

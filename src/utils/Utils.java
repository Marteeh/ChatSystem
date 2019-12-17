package utils;

import java.io.File;
import java.net.URISyntaxException;

public class Utils {

	public static final String getRunningirectory() throws URISyntaxException {
		return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
	}
	
	public static final String addressToString(byte[] ipAddress) {
		String str = "";
		str += ipAddress[0];
		str += "." + ipAddress[1];
		str += "." + ipAddress[2];
		str += "." + ipAddress[3];
		return str;
	}
}

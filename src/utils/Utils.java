package utils;

import java.io.File;
import java.net.URISyntaxException;

public class Utils {

	public static final String getRunningirectory() throws URISyntaxException {
		return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
	}
}

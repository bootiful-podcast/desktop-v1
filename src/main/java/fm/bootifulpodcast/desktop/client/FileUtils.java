package fm.bootifulpodcast.desktop.client;

import java.io.File;

public abstract class FileUtils {

	public static String extensionFor(File file) {
		var name = file.getName();
		var lastIndexOf = name.lastIndexOf(".");
		var trim = name.substring(lastIndexOf).toLowerCase().trim();
		if (trim.startsWith(".")) {
			return trim.substring(1);
		}
		return trim;
	}

}

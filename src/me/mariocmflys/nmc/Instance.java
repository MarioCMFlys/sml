package me.mariocmflys.nmc;

import java.io.File;

public class Instance {
	/**
	 * Replaces / with the appropriate directory separator 
	 * @param url Input URL
	 * @return New URL matching OS standards
	 */
	public static String replacePathSeparators(String url) {
		return url.replace("/", File.separator);
	}
	
	/**
	 * Gets NMC data directory
	 * @return Path to NMC data directory
	 */
	public static String getDataDir() {
		return System.getProperty("user.home") + File.separator + ".newmc";
	}
	
	/**
	 * Get a blanket operating system description
	 * @return "windows", "osx", "linux", or "other"
	 */
	public static String getOSType() {
		String os = System.getProperty("os.name");
		if(os.indexOf("win") >= 0) {
			return "windows";
		}
		else if(os.indexOf("mac") >= 0) {
			return "osx";
		}
		else if(os.indexOf("nix") >= 0
				|| os.indexOf("nux") >= 0
				|| os.indexOf("aix") >= 0) {
			return "linux";
		}
		else {
			return "other";
		}
		
	}
	
	/**
	 * Get path to Java executable
	 * @return String path to Java binary
	 */
	public static String getJavaBinary() {
		if (System.getProperty("os.name").startsWith("Win")) {
		    return System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
		} else {
		    return System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		}
	}
	
	/**
	 * Recursively remove directory and its contents
	 * @param dir Directory to remove
	 */
	public static void rmdir(File dir) {
		File[] contents = dir.listFiles();
		if(contents != null) {
			for(File i: contents) {
				if(i.isDirectory()) {
					rmdir(i);
					continue;
				}
				i.delete();
			}
		}
		dir.delete();
	}
}

package me.mariocmflys.nmc;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.mariocmflys.nmc.auth.Player;

public class Instance {
	/**
	 * Main launcher configuration object
	 */
	public static Config config;
	
	/**
	 * User account information
	 */
	public static Player player;
	
	/**
	 * Replaces / with the appropriate directory separator 
	 * @param url Input URL
	 * @return New URL matching OS standards
	 */
	public static String replacePathSeparators(String url) {
		return url.replace("/", File.separator);
	}
	
	/**
	 * Get Bootstrap version
	 * @return Version information sent at runtime by bootstrap
	 */
	public static String getBootstrapVersion() {
		return System.getProperty("bootstrap_version");
	}
	
	/**
	 * Get String with system information
	 * @return System information
	 */
	public static String getSystemSpecs() {
		String specs = "NMC Launcher v" + C.VERSION + "\n"
				+ "bootstrap_version: " + System.getProperty("bootstrap_version") + "\n"
				+ "nmc.mode: " + System.getProperty("nmc.mode") + "\n" 
				+ "os.name: " + System.getProperty("os.name") + "\n"
				+ "os.version: " + System.getProperty("os.version") + "\n"
				+ "os.arch: " + System.getProperty("os.arch") + "\n"
				+ "java.version: " + System.getProperty("java.version") + "\n"
				+ "java.vendor: " + System.getProperty("java.vendor") + "\n"
				+ "java.vm.name: " + System.getProperty("java.vm.name") + "\n"
				+ "sun.arch.data.model: " + System.getProperty("sun.arch.data.model") + "\n"
				+ "File.pathSeparator: " + File.pathSeparator + "\n"
				+ "File.separator: " + File.separator;
		return specs;
	}
	
	/**
	 * Gets NMC data directory
	 * @return Path to NMC data directory
	 */
	public static String getDataDir() {
		return System.getProperty("user.home") + File.separator + ".sml";
	}
	
	/**
	 * Get a blanket operating system description
	 * @return "windows", "osx", "linux", or "other"
	 */
	public static String getOSType() {
		String os = System.getProperty("os.name").toLowerCase();
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
	 * Test if running in a 64-bit server VM
	 * @return True if running in a server VM
	 */
	public static boolean isServerVM() {
		return System.getProperty("java.vm.name").toLowerCase().contains("server");
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
	
	public static int[] parseJavaVersion(String ver) {
	    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\_(\\d*)?").matcher(ver);
	    if (!m.matches())
	        throw new IllegalArgumentException("Malformed Java version");

	    return new int[] { Integer.parseInt(m.group(1)),
	            Integer.parseInt(m.group(2)),
	            Integer.parseInt(m.group(3)),
	            Integer.parseInt(m.group(4))
	    };
	}
	
	/**
	 * Check if Java version is equal to or newer than the base Java version
	 * @param test Version to test
	 * @param base Base to test against
	 * @return True if newer or equal to base version
	 */
	public static boolean checkJavaVersion(String test, String base) {

	    int[] testVer = parseJavaVersion(test);
	    int[] baseVer = parseJavaVersion(base);

	    for (int i = 0; i < testVer.length; i++)
	        if (testVer[i] != baseVer[i])
	            return testVer[i] > baseVer[i];

	    return true;
	}
	
	/**
	 * Check if the client can access the internet. This function checks each 
	 * of the listed URLs for 204 response code and if none are successful 
	 * returns false.
	 * @return true if any test is successful.
	 */
	public static boolean checkInternet() {
		String[] testServers = {
				"http://connectivitycheck.gstatic.com/generate_204",
				"http://httpstat.us/204",
				"http://ozuma.sakura.ne.jp/httpstatus/200"
		};
		HttpURLConnection con;
		for(String i: testServers) {
			try {
				con = (HttpURLConnection) new URL(i).openConnection();
				con.setConnectTimeout(1500);
				con.setReadTimeout(1500);
				con.connect();
				
				if(con.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Remove references to user data
	 */
	public static void signOut() {
		config.remove("access_token");
		config.remove("client_token");
		config.remove("username");
		config.remove("uuid");
		config.remove("user_type");
		config.remove("user_properties");
		config.save();
		
		player = null;
	}
}

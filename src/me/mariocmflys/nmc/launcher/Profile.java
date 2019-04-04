package me.mariocmflys.nmc.launcher;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Object representing a profile. Data is at the discretion of the pack developer
 */
public class Profile {
	/**
	 * Unique modpack ID
	 */
	private String id;
	
	/**
	 * Human readable display name
	 */
	private String displayName;
	
	/**
	 * Profile author / maintainer
	 */
	private String author;
	
	/**
	 * Modpack version
	 */
	private String version;
	
	/**
	 * Minecraft Version
	 */
	private String mcVersion;
	
	/**
	 * List of tweaks
	 */
	private String[] tweaks;
	
	/**
	 * Java path to main class within classpath (eg. net.minecraft.launch.Launch) 
	 */
	private String mainClass;
	
	/**
	 * Command-line arguments as array
	 */
	private JSONArray argList;
	
	/**
	 * JSONObject of AssetIndex information
	 */
	private JSONObject assets;
	
	/**
	 * Downloadable primary assets
	 */
	private JSONObject downloads;
	
	/**
	 * Working directory files
	 */
	private JSONArray files;
	
	/**
	 * Java libraries to be included in classpath and native libraries to be extracted 
	 */
	private JSONArray libraries;
	
	/**
	 * Object representing a profile. Data is at the discretion of the pack developer
	 * @param data JSON representation of profile
	 */
	public Profile(String data) {
		JSONObject j = new JSONObject(data);
		id = j.getString("id");
		displayName = j.getString("display_name");
		author = j.getString("author");
		version = j.getString("version");
		mcVersion = j.getString("mc_version");
		tweaks = (String[]) j.getJSONArray("tweaks").toList().toArray(new String[j.getJSONArray("tweaks").toList().size()]);
		mainClass = j.getString("mainClass");
		argList = j.getJSONArray("args");
		assets = j.getJSONObject("assets");
		downloads = j.getJSONObject("downloads");
		files = j.getJSONArray("files");
		libraries = j.getJSONArray("libraries");
	}
	
	/**
	 * Gets the profile-unique ID
	 * @return Profile unique ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets human readable display name
	 * @return Display name
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Gets author or maintainer of profile
	 * @return Author / Maintainer name
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Gets current version
	 * @return Current version
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Gets required Minecraft version
	 * @return Dependent Minecraft version
	 */
	public String getMinecraftVersion() {
		return mcVersion;
	}
	
	/**
	 * Gets list of major codebase tweaks
	 * @return Array of tweaks
	 */
	public String[] getTweaks() {
		return tweaks;
	}
	
	/**
	 * Gets Launch class for game
	 * @return Java-formatted class
	 */
	public String getMainClass() {
		return mainClass;
	}
	
	/**
	 * Gets command-line parameter template as JSONArray
	 * @return JSONArray argument template
	 */
	public JSONArray getArguments() {
		return argList;
	}
	
	/**
	 * Gets AssetIndex information
	 * @return JSONObject of AssetIndex
	 */
	public JSONObject getAssetIndex() {
		return assets;
	}
	
	/**
	 * Gets downloadable primary assets
	 * @return JSONObject of downloads
	 */
	public JSONObject getDownloads() {
		return downloads;
	}
	
	/**
	 * Gets map of working directory files
	 * @return JSONArray of files map 
	 */
	public JSONArray getFiles() {
		return files;
	}
	
	/**
	 * Gets list of classpath and native libraries
	 * @return List of libraries
	 */
	public JSONArray getLibraries() {
		return libraries;
	}
}

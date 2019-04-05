package me.mariocmflys.nmc.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONObject;

import me.mariocmflys.nmc.Instance;

public class TunedProfile {
	private String id;
	private int memory;
	private Profile profile;
	private String dist_url;
	private String version;
	
	/**
	 * Create a customized Profile object
	 * @param confObj JSON object from config
	 */
	public TunedProfile(JSONObject confObj) {
		id = confObj.getString("id");
		memory = confObj.getInt("memory");
		dist_url = confObj.getString("dist_url");
		version = confObj.getString("version");
	}
	
	/**
	 * Attaches the corresponding profile object
	 * @throws FileNotFoundException
	 */
	public void attachProfile() throws FileNotFoundException {
		Scanner s = new Scanner(new File(Instance.getDataDir() + File.separator + "profile" + File.separator + id + File.separator + "manifest.json"));
		profile = new Profile(s.useDelimiter("\\Z").next());
		s.close();
	}
	
	/**
	 * Get corresponding ID
	 * @return String of ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Get the maximum memory set by the end user in megabytes
	 * @return
	 */
	public int getAllocatedMemory() {
		return memory;
	}
	
	/**
	 * Get URL of installation source
	 * @return Installation source or empty for locally installed
	 */
	public String getSource() {
		return dist_url;
	}
	
	/**
	 * Get last version upgraded to
	 * @return Installed version
	 */
	public String getInstalledVersion() {
		return version;
	}
	
	/**
	 * Get the corresponding curated profile object
	 * @return Profile object
	 */
	public Profile getProfile() {
		return profile;
	}
}

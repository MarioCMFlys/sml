package me.mariocmflys.nmc;

import java.awt.Component;
import java.util.HashMap;

/**
 * Adds functionality for addons which do not modify the SML codebase.
 */
public class Tweaker {
	public static HashMap<String, Component> tabs = new HashMap<String, Component>();
	
	/**
	 * Register a new user interface tab. 
	 * @param label Text to display on tab
	 * @param comp Component to add to JTabbedPane
	 */
	public static void addTab(String label, Component comp) {
		tabs.put(label, comp);
	}
	
	/**
	 * Future-proof method for triggering launcher start
	 * @param args
	 */
	public static void run(String[] args) {
		Launch.main(args);
	}
}

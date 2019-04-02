package me.mariocmflys.nmc.launcher;

/**
 * Player object for authentication and launching
 */
public class Player {
	private String username;
	private String uuid;
	private String accessToken;
	private String type;
	private String properties;
	
	/**
	 * Player object for authentication and launching
	 * @param username Player username
	 * @param uuid Mojang-assigned UUID of player
	 * @param accessToken Authentication token
	 * @param type Account type
	 * @param properties User properties
	 */
	public Player(String username, String uuid, String accessToken, String type, String properties) {
		this.username = username;
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.type = type;
		this.properties = properties;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getType() {
		return type;
	}
	
	public String getProperties() {
		return properties;
	}
}

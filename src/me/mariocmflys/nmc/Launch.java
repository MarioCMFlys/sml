package me.mariocmflys.nmc;

import java.io.File;

import javax.swing.UIManager;

import me.mariocmflys.nmc.launcher.Mojang;
import me.mariocmflys.nmc.launcher.Player;
import me.mariocmflys.nmc.ui.LoginWindow;
import me.mariocmflys.nmc.ui.MainWindow;

public class Launch {
	public static void main(String[] args) {
		System.out.println("NMC Launcher v1.0.0");
		System.out.println("bootstrap_version=" + System.getProperty("bootstrap_version"));
		System.out.println("os.name: " + System.getProperty("os.name"));
		System.out.println("os.version: " + System.getProperty("os.version"));
		System.out.println("os.arch: " + System.getProperty("os.arch"));
		System.out.println("java.version: " + System.getProperty("java.version"));
		System.out.println("java.vendor: " + System.getProperty("java.vendor"));
		System.out.println("sun.arch.data.model: " + System.getProperty("sun.arch.data.model"));
		
		Instance.config = new Config(Instance.getDataDir() + File.separator + "launcher.json");
		Instance.config.create();
		
		try { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
	    } 
		catch(Exception e){
			System.out.println("Failed to set look and feel");
			e.printStackTrace();
		}
		
		
		if(Instance.config.has("client_token") && Instance.config.has("access_token") &&
				Instance.config.has("username") && Instance.config.has("uuid") &&
				Instance.config.has("user_type") && Instance.config.has("user_properties")) {
			String accessToken = Instance.config.getString("access_token");
			String clientToken = Instance.config.getString("client_token");
			String username = Instance.config.getString("username");
			String userUUID = Instance.config.getString("uuid");
			String userType = Instance.config.getString("user_type");
			String userProperties = Instance.config.getString("user_properties");
			System.out.println("Validating login "+userUUID);
			
			if(Mojang.validateToken(accessToken, clientToken)) {
				System.out.println("Token valid, logged in as " + username);
				Instance.player = new Player(username, userUUID, accessToken, userType, userProperties);
				MainWindow window = new MainWindow();
				window.setVisible(true);
			}
			else {
				System.out.println("Access token invalid, attempting to revalidate");
				accessToken = Mojang.refreshToken(accessToken, clientToken);
				if(accessToken != null) {
					System.out.println("Successfully revalidated token, logged in as " + username);
					Instance.player = new Player(username, userUUID, accessToken, userType, userProperties);
					MainWindow window = new MainWindow();
					window.setVisible(true);
				}
				else {
					System.out.println("Client identified invalid");
					LoginWindow l = new LoginWindow();
					l.setVisible(true);
				}
			}
		}
		else {
			System.out.println("No login data saved");
			LoginWindow l = new LoginWindow();
			l.setVisible(true);
		}
		/**
		
		JSONObject j = Mojang.generateToken(args[1], args[2]);
		
		if(j == null) {
			System.out.println("Invalid authentication");
			System.exit(1);
		}
		
		String username = j.getJSONObject("selectedProfile").getString("name");
		String userUUID = j.getJSONObject("selectedProfile").getString("id");
		//String clientToken = j.getString("clientToken");
		String accessToken = j.getString("accessToken");
		System.out.println(j);
		
		try {
			Scanner s = new Scanner(new File(args[0]));
			String content = s.useDelimiter("\\Z").next();
			s.close();
			
			Profile profile = new Profile(content);
			File libDir = new File(Instance.getDataDir() + File.separator + "lib");
			File clientDir = new File(Instance.getDataDir() + File.separator + "clients");
			File workDir = new File(Instance.getDataDir() + File.separator + "profile" + File.separator + profile.getID());
			File assetDir = new File(Instance.getDataDir() + File.separator + "assets");
			libDir.mkdirs();
			clientDir.mkdirs();
			workDir.mkdirs();
			assetDir.mkdirs();
			File indexDir = new File(assetDir.getAbsolutePath() + File.separator + "indexes");
			indexDir.mkdirs();
			
			Player player = new Player(username, userUUID, accessToken, "legacy", "{}");
			
			MinecraftLauncher.launch(profile, player, libDir, clientDir, workDir, assetDir, indexDir);
			
		} catch (Exception e) {
			System.err.println("Fatal error occured while launching the game");
			e.printStackTrace();
		}
		
		**/
	}

}

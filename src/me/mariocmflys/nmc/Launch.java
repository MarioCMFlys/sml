package me.mariocmflys.nmc;

import java.io.File;
import java.util.Scanner;

import org.json.JSONObject;

import me.mariocmflys.nmc.launcher.MinecraftLauncher;
import me.mariocmflys.nmc.launcher.Mojang;
import me.mariocmflys.nmc.launcher.Player;
import me.mariocmflys.nmc.launcher.Profile;

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
		
		/**
		try { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
	    } 
		catch(Exception e){
			System.out.println("Failed to set look and feel");
			e.printStackTrace();
		}
		
		MainWindow window = new MainWindow();
		window.setVisible(true);**/
		
		
		
		
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
	}

}

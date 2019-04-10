package me.mariocmflys.nmc;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import me.mariocmflys.nmc.launcher.Mojang;
import me.mariocmflys.nmc.launcher.Player;
import me.mariocmflys.nmc.ui.LoginWindow;
import me.mariocmflys.nmc.ui.MainWindow;
import me.mariocmflys.nmc.ui.ProgressDialog;

public class Launch {
	public static void main(String[] args) {
		System.out.println("NMC Launcher v" + C.VERSION);
		System.out.println("bootstrap_version=" + System.getProperty("bootstrap_version"));
		System.out.println("os.name: " + System.getProperty("os.name"));
		System.out.println("os.version: " + System.getProperty("os.version"));
		System.out.println("os.arch: " + System.getProperty("os.arch"));
		System.out.println("java.version: " + System.getProperty("java.version"));
		System.out.println("java.vendor: " + System.getProperty("java.vendor"));
		System.out.println("java.vm.name: " + System.getProperty("java.vm.name"));
		System.out.println("sun.arch.data.model: " + System.getProperty("sun.arch.data.model"));
		System.out.println("File.pathSeparator: " + File.pathSeparator);
		System.out.println("File.separator: " + File.separator);
		
		System.setProperty("http.agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:66.0) Gecko/20100101 Firefox/66.0");
		
		Instance.config = new Config(Instance.getDataDir() + File.separator + "launcher.json");
		Instance.config.create();
		
		try { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
	    } 
		catch(Exception e){
			System.out.println("Failed to set look and feel");
			e.printStackTrace();
		}
		
		if(Instance.config.has("installed_profiles")) {
			JSONArray profiles = Instance.config.getArray("installed_profiles");
			if(profiles.toList().size() > 0) {
				int size = profiles.toList().size();
				ProgressDialog p = new ProgressDialog("Simplified Minecraft Launcher", "Updating profiles", 0, size, 0);
				try {
					p.setIconImage(ImageIO.read(MainWindow.class.getResource("/icon.png")));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				p.setVisible(true);
				for(int i = 0; i < size; i++) {
					JSONObject j = profiles.getJSONObject(i);
					p.updateLabel("Updating profiles (" + (i + 1) + " out of " + size + ")");
					String id = j.getString("id");
					String url = j.getString("dist_url");
					if(!url.equals("")) {
						System.out.println("Updating profile " + id);
						File file = new File(Instance.getDataDir() + File.separator + "profile" + File.separator + id + File.separator + "manifest.json");
						try {
							file.delete();
							FileUtils.copyURLToFile(new URL(url), file);
						} catch (IOException e) {
							System.err.println("Failed to update profile " + id);
							e.printStackTrace();
						}
					}
					p.updateValue(i+1);
				}
				p.close();
			}
		}
		else {
			Instance.config.set("installed_profiles", new JSONArray());
			Instance.config.save();
		}
		
		if(!Instance.config.has("communicate")) {
			Instance.config.set("communicate", true);
			Instance.config.save();
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
					Instance.config.set("access_token", accessToken);
					Instance.config.save();
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
	}

}

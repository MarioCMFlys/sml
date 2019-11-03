package me.mariocmflys.nmc;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.io.FileUtils;

import me.mariocmflys.jsoncompat.JSONArray;
import me.mariocmflys.jsoncompat.JSONObject;
import me.mariocmflys.nmc.auth.Mojang;
import me.mariocmflys.nmc.auth.Player;
import me.mariocmflys.nmc.ui.Appearance;
import me.mariocmflys.nmc.ui.LoginWindow;
import me.mariocmflys.nmc.ui.MainWindow;
import me.mariocmflys.nmc.ui.ProgressDialog;

public class Launch {
	public static void main(String[] args) {
		try {
			init(args);
		}
		catch(Exception e) {
			handleError(e);
		}
	}
	
	public static void handleError(Exception e) {
		StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		String err = stackTrace.toString();
		
		System.err.println(err);
		
		String[] opt = {"Exit"};
		JOptionPane.showOptionDialog(null,
				"An unhandled error has occured\n\n" + err + "\n" + Instance.getSystemSpecs(),
				"Simplified Minecraft Launcher",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				opt, opt[0]);
		System.exit(1);
	}
	
	private static Options makeOptions() {
		Options o = new Options();
		o.addOption("offline", false, "Force offline mode");
		o.addOption("v", "version", false, "Print version and exit");
		return o;
	}
	
	public static void init(String[] args) throws Exception {
		CommandLineParser cmdparse = new DefaultParser();
		Options cmdopts = makeOptions();
		CommandLine cmd = null;
		try {
			cmd = cmdparse.parse(cmdopts, args);
		}
		catch(UnrecognizedOptionException oe) {
			System.out.println(oe.getMessage() + "\n\nUsage: ");
			for(Option i: cmdopts.getOptions()) {
				System.out.println("-" + i.getOpt() 
						+ (i.hasArgName() ? " " + i.getArgName() : "")
						+ ": " + i.getDescription());
			}
			System.exit(-1);
		}
		
		if(cmd.hasOption("offline")) System.setProperty("nmc.mode", "offline");
		else System.setProperty("nmc.mode", "default");
		
		System.out.println(Instance.getSystemSpecs());
		
		if(cmd.hasOption("v")) System.exit(0); // Exit after printing version
		
		System.setProperty("http.agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:66.0) Gecko/20100101 Firefox/66.0");
		
		Instance.config = new Config(Instance.getDataDir() + File.separator + "launcher.json");
		Instance.config.create();
		
		if(Instance.config.has("lv")) {
			int lv = Instance.config.getVersion();
			System.out.println("Config LV: " + lv);
			if(lv > C.LOGICAL_VERSION) {
				JOptionPane.showMessageDialog(null, 
					"You have loaded an older version of SML (" 
						+ C.LOGICAL_VERSION 
						+ ") than what your data files were last used with (" 
						+ lv 
						+ ").\nExpect problems!", 
					"Simplified Minecraft Launcher", 
					JOptionPane.WARNING_MESSAGE);
			}
		}
		else {
			System.out.println("Warning: config does not contain LV");
			Instance.config.save();
		}
		
		System.out.println("Initializing fonts");
		InputStream is = Launch.class.getResourceAsStream("/OpenSans-Regular.ttf");
		Appearance.font_regular = Font.createFont(Font.TRUETYPE_FONT, is);
		
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value instanceof javax.swing.plaf.FontUIResource) {
	        UIManager.put (key, Appearance.font_regular.deriveFont(14f));
	      }
	    }
		
		try {
	        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

	    } 
		catch(Exception e){
			System.out.println("Failed to set look and feel");
			e.printStackTrace();
		}
		
		if(Instance.config.has("installed_profiles")) {
			JSONArray profiles = Instance.config.getArray("installed_profiles");
			if(profiles.toList().size() > 0 && !cmd.hasOption("offline")) {
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
		
		if(cmd.hasOption("offline")) {
			String accessToken = "";
			String username = "Steve";
			String userUUID = "8667ba71b85a4004af54457a9734eed7";
			String userType = "";
			String userProperties = "";
			if(Instance.config.has("client_token") && Instance.config.has("access_token") &&
					Instance.config.has("username") && Instance.config.has("uuid") &&
					Instance.config.has("user_type") && Instance.config.has("user_properties")) {
				accessToken = Instance.config.getString("access_token");
				username = Instance.config.getString("username");
				userUUID = Instance.config.getString("uuid");
				userType = Instance.config.getString("user_type");
				userProperties = Instance.config.getString("user_properties");
			}
			Instance.player = new Player(username, userUUID, accessToken, userType, userProperties);
			MainWindow window = new MainWindow();
			window.setVisible(true);
			return;
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

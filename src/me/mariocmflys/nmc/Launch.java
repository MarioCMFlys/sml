package me.mariocmflys.nmc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import me.mariocmflys.nmc.launcher.Mojang;
import me.mariocmflys.nmc.launcher.Profile;
import me.mariocmflys.nmc.launcher.StreamFeed;
import me.mariocmflys.nmc.launcher.Zipper;

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
			
			JSONArray libs = profile.getLibraries();
			String nativeDir = null;
			File nativeFile = null;
			try {
				nativeFile = Files.createTempDirectory("simplemc_natives").toFile();
				nativeDir = nativeFile.getAbsolutePath();
			} catch (IOException e) {
				System.err.println("Failed to create temporary directory for natives");
				e.printStackTrace();
				System.exit(1);
			}
			
			String os = Instance.getOSType();
			
			ArrayList<String> classpath = new ArrayList<String>();
			
			for(int i = 0; i < libs.length(); i++) {
				JSONObject l = libs.getJSONObject(i);
				
				String fileToPull = "";
				
				if(l.getBoolean("native")) {
					JSONArray supportedPlatforms = l.getJSONObject("rules").getJSONArray("os");
					boolean supported = false;
					for(int z = 0; z < supportedPlatforms.length(); z++) {
						if(supportedPlatforms.getString(z).equals(os)) {
							supported = true;
							break;
						}
					}
					if(!supported) {
						continue;						
					}
					String title = os;
					if(os.equals("windows")) {
						String arch = System.getProperty("os.arch");
						if(arch.equals("ia64") || arch.equals("amd64")) {
							title = "win64";
						}
						else {
							title = "win32";
						}
					}
					fileToPull = l.getString("native-" + title);
				}
				else {
					fileToPull = l.getString("universal");
				}
				
				String filename = FilenameUtils.getName(fileToPull);
				
				File ftp = new File(libDir + File.separator + filename);
				if(!ftp.exists()) {
					try {
						System.out.println("Pulling "+filename);
						FileUtils.copyURLToFile(new URL(fileToPull), ftp);
					} catch (IOException e) {
						System.err.println("Failed to download library " + filename + " from " + fileToPull);
						e.printStackTrace();
						continue;
					}
				}
				
				if(l.getBoolean("native")) {
					// extract native binaries to temp directory
					System.out.println("Unzipping native package " + ftp.getAbsolutePath());
					try {
						Zipper.extract(ftp.getAbsolutePath(), nativeDir);
					} catch (IOException e) {
						System.err.println("Failed to extract native library " + filename);
						e.printStackTrace();
						System.exit(1);
					}
				}
				else {
					System.out.println("Added to classpath: " + ftp.getAbsolutePath());
					classpath.add(ftp.getAbsolutePath());
				}
				
			}
			
			// Setup client
			String client = profile.getDownloads().getString("client");
			String filename = profile.getDownloads().getString("version") + ".jar";
			File ctp = new File(clientDir + File.separator + filename);
			if(!ctp.exists()) {
				try {
					System.out.println("Downloading client " + filename);
					FileUtils.copyURLToFile(new URL(client), ctp);
				} catch (IOException e) {
					System.err.println("Failed to download client " + filename + " from " + client);
					e.printStackTrace();
					System.exit(1);
				}
			}
			classpath.add(ctp.getAbsolutePath());
			
			// Load asset index
			String assetIndex = profile.getAssetIndex().getString("url");
			filename = profile.getAssetIndex().getString("id") + ".json";
			File atp = new File(indexDir + File.separator + filename);
			if(!atp.exists()) {
				try {
					System.out.println("Downloading AssetIndex " + filename);
					FileUtils.copyURLToFile(new URL(assetIndex), atp);
				} catch (IOException e) {
					System.err.println("Failed to download AssetIndex " + filename + " from " + assetIndex);
					e.printStackTrace();
					System.exit(1);
				}
			}
			
			// Download assets
			s = new Scanner(atp);
			String assets = s.useDelimiter("\\Z").next();
			s.close();
			
			JSONObject assetObj = new JSONObject(assets).getJSONObject("objects");
			Iterator<String> assetKeys = assetObj.keys();
			while (assetKeys.hasNext()) {
				String loc = assetKeys.next();
				String hash = assetObj.getJSONObject(loc).getString("hash");
				String hashStart = hash.substring(0, 2);
				
				String place = "https://resources.download.minecraft.net/" + hashStart + "/" + hash;
				File fileToMake = new File(assetDir + File.separator + "objects" + File.separator + hashStart + File.separator + hash);
				
				if(!fileToMake.exists()) {
					fileToMake.getParentFile().mkdirs();
					try {
						System.out.println("Downloading asset " + loc);
						FileUtils.copyURLToFile(new URL(place), fileToMake);
					} catch (IOException e) {
						System.out.println("Failed to download asset " + loc + " from " + place);
						e.printStackTrace();
					}					
				}
			}
			
			// TODO download working directory files
			
			JSONArray files = profile.getFiles();
			
			for(int i = 0; i < files.toList().size(); i++) {
				JSONObject f = files.getJSONObject(i);
				String place = f.getString("download");
				File fileToMake = new File(workDir.getAbsolutePath() + File.separator + Instance.replacePathSeparators(f.getString("path")));
				if(!fileToMake.exists()) {
					fileToMake.getParentFile().mkdirs();
					try {
						System.out.println("Downloading tree file " + f.getString("path"));
						FileUtils.copyURLToFile(new URL(place), fileToMake);
					} catch (IOException e) {
						System.out.println("Failed to download tree file " + f.getString("path") + " from " + place);
						e.printStackTrace();
					}
				}
			}
			
			// LAUNCH TIME
			
			// Convert classpath into launch arguments
			String cp = classpath.get(0);
			for(int i = 1; i < classpath.size(); i++) {
				String c = classpath.get(i);
				cp = cp + ":" + c;
			}
			
			ArrayList<String> launchArgs = new ArrayList<String>();
			
			// JRE binary
			launchArgs.add(Instance.getJavaBinary());
			
			// Runtime arguments
			launchArgs.add("-Xmx1G");
			launchArgs.add("-XX:+UseConcMarkSweepGC");
			launchArgs.add("-XX:+CMSIncrementalMode");
			launchArgs.add("-XX:-UseAdaptiveSizePolicy");
			launchArgs.add("-Xmn128M");
			
			// Definitions
			launchArgs.add("-Djava.library.path=" + nativeDir);
			launchArgs.add("-Dminecraft.launcher.brand=java-minecraft-launcher");
			launchArgs.add("-Dminecraft.launcher.version=1.6.89-j");
			launchArgs.add("-Dminecraft.client.jar="+ctp.getAbsolutePath());
			
			// Classpath
			launchArgs.add("-cp");
			launchArgs.add(cp);
			launchArgs.add(profile.getMainClass());
			
			// Game arguments
			JSONArray argTemplate = profile.getArguments();
			for(int i = 0; i < argTemplate.toList().size(); i++) {
				String a = argTemplate.getString(i)
						.replace("$USERNAME", username)
						.replace("$VERSION", profile.getMinecraftVersion())
						.replace("$GAMEDIR", workDir.getAbsolutePath())
						.replace("$ASSETSDIR", assetDir.getAbsolutePath())
						.replace("$ASSETINDEX", profile.getAssetIndex().getString("id"))
						.replace("$USER_UUID", userUUID)
						.replace("$USER_TOKEN", accessToken)
						.replace("$USER_TYPE", "legacy") // purpose TBD
						.replace("$USER_PROP", "{}"); // purpose TBD
				
				launchArgs.add(a);
			}
			
			String[] cmd = (String[]) launchArgs.toArray(new String[launchArgs.size()]);
			
			System.out.println("Starting game with cmdline: "+String.join(" ", cmd));
			
			try {
				Process p = Runtime.getRuntime().exec(cmd, null, workDir);
				
				StreamFeed feedError = new StreamFeed(p.getErrorStream(), System.err);
				StreamFeed feedOut = new StreamFeed(p.getInputStream(), System.out);
				
				feedError.start();
				feedOut.start();
				
			    try {
					p.waitFor();
				} catch (InterruptedException e) {
					p.destroy();
					System.out.println("Terminating guest");
					e.printStackTrace();
				}
			    Instance.rmdir(nativeFile);
			    System.out.println("Launcher successful exit");
			} catch (IOException e) {
				System.err.println("Failed to launch");
				e.printStackTrace();
				System.exit(1);
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

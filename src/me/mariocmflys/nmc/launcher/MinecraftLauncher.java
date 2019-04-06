package me.mariocmflys.nmc.launcher;

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

import me.mariocmflys.nmc.C;
import me.mariocmflys.nmc.Instance;
import me.mariocmflys.nmc.launcher.OutputConsole.Type;

/**
 * Static definitions for launch related functions
 */
public class MinecraftLauncher {
	/**
	 * Launch Minecraft from a profile configuration
	 * @param profile Profile object to launch 
	 * @param player Player's account and authentication data
	 * @param libDir Libraries directory
	 * @param clientDir Clients directory
	 * @param workDir Working directory
	 * @param assetDir Assets directory
	 * @param indexDir Directory with indexes
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void launch(TunedProfile tprofile, Player player, 
			File libDir, File clientDir, File workDir, File assetDir, 
			File indexDir, OutputConsole console) throws FileNotFoundException, IOException {
		console.write("Simplified Minecraft Launcher " + C.VERSION, Type.INIT);
		
		Profile profile = tprofile.getProfile();
		
		File nativeFile = null;
		nativeFile = Files.createTempDirectory("simplemc_natives").toFile();
		String nativeDir = nativeFile.getAbsolutePath();

		String os = Instance.getOSType();
		
		ArrayList<String> classpath = new ArrayList<String>();
		
		JSONArray libs = profile.getLibraries();
		
		// Libraries
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
					console.write("Pulling "+filename, Type.INIT);
					FileUtils.copyURLToFile(new URL(fileToPull), ftp);
				} catch (IOException e) {
					console.write("Failed to download library " + filename + " from " + fileToPull, Type.ERROR);
					e.printStackTrace();
					continue;
				}
			}
			
			if(l.getBoolean("native")) {
				// extract native binaries to temp directory
				console.write("Unzipping native package " + ftp.getAbsolutePath(), Type.INIT);
				try {
					Zipper.extract(ftp.getAbsolutePath(), nativeDir);
				} catch (IOException e) {
					console.write("Failed to extract native library " + filename, Type.ERROR);
					e.printStackTrace();
					return;
				}
			}
			else {
				console.write("Added to classpath: " + ftp.getAbsolutePath(), Type.INIT);
				
				classpath.add(ftp.getAbsolutePath());
			}
			
		}
		
		// Setup client
		String client = profile.getDownloads().getString("client");
		String filename = profile.getDownloads().getString("version") + ".jar";
		File ctp = new File(clientDir + File.separator + filename);
		if(!ctp.exists()) {
			try {
				console.write("Downloading client " + filename, Type.INIT);
				FileUtils.copyURLToFile(new URL(client), ctp);
			} catch (IOException e) {
				console.write("Failed to download client " + filename + " from " + client, Type.ERROR);
				e.printStackTrace();
				return;
			}
		}
		classpath.add(ctp.getAbsolutePath());
		
		// Load asset index
		String assetIndex = profile.getAssetIndex().getString("url");
		filename = profile.getAssetIndex().getString("id") + ".json";
		File atp = new File(indexDir + File.separator + filename);
		if(!atp.exists()) {
			try {
				console.write("Downloading AssetIndex " + filename, Type.INIT);
				FileUtils.copyURLToFile(new URL(assetIndex), atp);
			} catch (IOException e) {
				console.write("Failed to download AssetIndex " + filename + " from " + assetIndex, Type.ERROR);
				e.printStackTrace();
				return;
			}
		}
		
		// Download assets
		Scanner s = new Scanner(atp);
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
					console.write("Downloading asset " + loc, Type.INIT);
					FileUtils.copyURLToFile(new URL(place), fileToMake);
				} catch (IOException e) {
					console.write("Failed to download asset " + loc + " from " + place, Type.ERROR);
					e.printStackTrace();
				}					
			}
		}
		
		// Apply working directory upgrade instructions
		if(!tprofile.getInstalledVersion().equals(tprofile.getProfile().getVersion())) {
			JSONArray upgrades = profile.getUpgradeInstructions();
			
			for(int i = 0; i < upgrades.toList().size(); i++) {
				JSONObject u = upgrades.getJSONObject(i);
				if(u.getString("action").equalsIgnoreCase("delete")) {
					console.write("Upgrade removing file " + u.getString("path"), Type.INIT);
					new File(workDir + File.separator + Instance.replacePathSeparators(u.getString("path"))).delete();
				}
			}
			
			JSONArray prof = Instance.config.getArray("installed_profiles");
			for(int i = 0; i < prof.toList().size(); i++) {
				JSONObject j = prof.getJSONObject(i);
				if(j.getString("id").equals(tprofile.getID())) {
					j.remove("version");
					j.put("version", profile.getVersion());
					break;
				}
			}
			Instance.config.remove("installed_profiles");
			Instance.config.set("installed_profiles", prof);
			Instance.config.save();
		}
		
		// Working directory files
		JSONArray files = profile.getFiles();
		
		for(int i = 0; i < files.toList().size(); i++) {
			JSONObject f = files.getJSONObject(i);
			String place = f.getString("download");
			File fileToMake = new File(workDir.getAbsolutePath() + File.separator + Instance.replacePathSeparators(f.getString("path")));
			if(!fileToMake.exists()) {
				fileToMake.getParentFile().mkdirs();
				try {
					console.write("Downloading tree file " + f.getString("path"), Type.INIT);
					FileUtils.copyURLToFile(new URL(place), fileToMake);
				} catch (IOException e) {
					console.write("Failed to download tree file " + f.getString("path") + " from " + place, Type.ERROR);
					e.printStackTrace();
				}
			}
		}
		
		// LAUNCH TIME
		
		// Convert classpath into launch arguments
		String cp = classpath.get(0);
		for(int i = 1; i < classpath.size(); i++) {
			String c = classpath.get(i);
			cp = cp + File.pathSeparator + c;
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
					.replace("$USERNAME", player.getUsername())
					.replace("$VERSION", profile.getMinecraftVersion())
					.replace("$GAMEDIR", workDir.getAbsolutePath())
					.replace("$ASSETSDIR", assetDir.getAbsolutePath())
					.replace("$ASSETINDEX", profile.getAssetIndex().getString("id"))
					.replace("$USER_UUID", player.getUUID())
					.replace("$USER_TOKEN", player.getAccessToken())
					.replace("$USER_TYPE", player.getType()) // purpose TBD
					.replace("$USER_PROP", player.getProperties()); // purpose TBD
			
			launchArgs.add(a);
		}
		
		String[] cmd = (String[]) launchArgs.toArray(new String[launchArgs.size()]);
		
		System.out.println("[DEBUG] Starting game with cmdline: "+String.join(" ", cmd));
		
		try {
			Process p = Runtime.getRuntime().exec(cmd, null, workDir);
			
			StreamFeed feedError = new StreamFeed(p.getErrorStream(), console, OutputConsole.Type.ERROR);
			StreamFeed feedOut = new StreamFeed(p.getInputStream(), console, OutputConsole.Type.NORMAL);
			
			feedError.start();
			feedOut.start();
			
		    try {
				p.waitFor();
			} catch (InterruptedException e) {
				p.destroy();
				console.write("Terminating guest", Type.INIT);
				e.printStackTrace();
			}
		    Instance.rmdir(nativeFile);
		    console.write("Successful exit", Type.INIT);
		} catch (IOException e) {
			System.err.println("Failed to launch");
			e.printStackTrace();
		}
	}
}

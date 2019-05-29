package me.mariocmflys.nmc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import me.mariocmflys.jsoncompat.JSONArray;
import me.mariocmflys.jsoncompat.JSONObject;

public class Config{
	private String location = "";
	private File file;
	private JSONObject json;
	private boolean readonly;
	
	public Config(String location) {
		this.location = location;
		this.file = new File(this.location);
		this.readonly = false;
		
	}
	public Config(String location, boolean ro) {
		this.location = location;
		this.file = new File(this.location);
		this.readonly = ro;
	}
	
	public boolean exists() {
		return file.exists();
	}
	
	public void create() {
		if(!this.exists()) {
			this.json = new JSONObject("{}");
		}
		else {
			try {
				// TODO change to use this.file
				Scanner s = new Scanner(new File(location));
				String content = s.useDelimiter("\\Z").next();
				s.close();
				this.json = new JSONObject(content);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				this.json = new JSONObject("{}");
			}
		}
	}
	
	public void create(BufferedReader br) {
		Scanner s = new Scanner(br);
		String content = s.useDelimiter("\\Z").next();
		s.close();
		this.json = new JSONObject(content);
		
	}
	
	public String getString(String key) {
		return this.json.getString(key);
	}
	public int getInt(String key) {
		return this.json.getInt(key);
	}
	public float getFloat(String key) {
		return this.json.getFloat(key);
	}
	public long getLong(String key) {
		return this.json.getLong(key);
	}
	public double getDouble(String key) {
		return this.json.getDouble(key);
	}
	public boolean getBoolean(String key) {
		return this.json.getBoolean(key);
	}
	public JSONObject getObject(String key) {
		return this.json.getJSONObject(key);
	}
	public JSONArray getArray(String key) {
		return this.json.getJSONArray(key);
	}
	
	public boolean has(String key) {
		return this.json.has(key);
	}
	
	public void set(String key, Object value) {
		this.json = this.json.put(key, value);
	}
	public void set(String key, boolean value) {
		this.json = this.json.put(key, value);
	}
	public void set(String key, int value) {
		this.json = this.json.put(key, value);
	}
	public void set(String key, float value) {
		this.json = this.json.put(key, value);
	}
	public void set(String key, short value) {
		this.json = this.json.put(key, value);
	}
	public void set(String key, long value) {
		this.json = this.json.put(key, value);
	}
	public void set(String key, double value) {
		this.json = this.json.put(key, value);
	}
	
	public void remove(String key) {
		json.remove(key);
	}
	
	public void save() {
		if(!readonly) {
			String content = this.toString();
			
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				fw = new FileWriter(this.location);
				bw = new BufferedWriter(fw);
				bw.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						bw.close();
					}
					if (fw != null) {
						fw.close();
					}
				} 
				catch (IOException ex) {
					ex.printStackTrace();
				}
	
			}
		}
	}
	
	public JSONObject getOwnJSON() {
		return this.json;
	}
	
	public String toString() {
		return this.json.toString();
	}
}

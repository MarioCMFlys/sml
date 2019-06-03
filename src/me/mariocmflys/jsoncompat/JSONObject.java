package me.mariocmflys.jsoncompat;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * JSONObject
 * Part of a compatibility layer for JSON conversion from org.json to json-simple
 */
@SuppressWarnings("serial")
public class JSONObject extends org.json.simple.JSONObject {
	
	/**
	 * Create an empty JSONObject
	 */
	public JSONObject() {
		super();
	}
	
	/**
	 * Create a JSONObject from json-simple JSONObject
	 * @param obj json-simple object
	 */
	public JSONObject(org.json.simple.JSONObject obj) {
		super();
		
		@SuppressWarnings("unchecked")
		Set<Map.Entry<String, Object>> set = obj.entrySet();
		for(Map.Entry<String, Object> i: set) {
			this.put(i.getKey(), i.getValue());
		}
	}
	
	
	public JSONObject(String json) {
		super();
		
		JSONParser parser = new JSONParser();
		try {
			org.json.simple.JSONObject j = ((org.json.simple.JSONObject) parser.parse(json));
			
			@SuppressWarnings("unchecked")
			Set<Map.Entry<String, Object>> set = j.entrySet();
			for(Map.Entry<String, Object> i: set) {
				this.put(i.getKey(), i.getValue());
			}
		} catch (ParseException e) {}
		
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject put(String key, Object value) {
		((Map<String, Object>) this).put(key, value);
		return this;
	}
	
	public boolean has(String key) {
		return this.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<String> keys(){
		return this.keySet().iterator();
	}
	
	public boolean getBoolean(String key) {
		return (boolean) this.get(key);
	}
	
	public int getInt(String key) {
		try {
			return (int) Math.toIntExact((long) this.get(key));
		} catch(ClassCastException e) {
			return (int) this.get(key);
		}
		
	}
	
	public float getFloat(String key) {
		return (float) this.get(key);
	}
	
	public double getDouble(String key) {
		return (double) this.get(key);
	}
	
	public long getLong(String key) {
		return (long) this.get(key);
	}
	
	public String getString(String key) {
		return (String) this.get(key);
	}
	
	public JSONObject getJSONObject(String key) {
		return new JSONObject((org.json.simple.JSONObject) this.get(key));
	}
	
	public JSONArray getJSONArray(String key) {
		return new JSONArray((org.json.simple.JSONArray) this.get(key));
	}
}

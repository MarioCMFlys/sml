package me.mariocmflys.jsoncompat;

import java.util.List;

/**
 * JSONArray
 * Part of a compatibility layer for JSON conversion from org.json to json-simple
 */
@SuppressWarnings("serial")
public class JSONArray extends org.json.simple.JSONArray {
	
	/**
	 * Create an empty JSONArray
	 */
	public JSONArray() {
		super();
	}
	
	/**
	 * Create a new JSONArray from json-simple JSONArray
	 * @param array json-simple array
	 */
	public JSONArray(org.json.simple.JSONArray array) {
		super();
		
		for(Object i: array) {
			this.put(i);
		}
	}
	
	public int length() {
		return this.size();
	}
	
	public List<?> toList() {
		return (List<?>) this;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray put(Object value) {
		this.add(value);
		return this;
	}
	
	public int getInt(int index) {
		return (int) Math.toIntExact((long) this.get(index));
	}
	
	public float getFloat(int index) {
		return (float) this.get(index);
	}
	
	public double getDouble(int index) {
		return (double) this.get(index);
	}
	
	public long getLong(int index) {
		return (long) this.get(index);
	}
	
	public String getString(int index) {
		return (String) this.get(index);
	}
	
	public JSONObject getJSONObject(int index) {
		return new JSONObject((org.json.simple.JSONObject) this.get(index));
	}
	
	public JSONArray getJSONArray(int index) {
		return new JSONArray((org.json.simple.JSONArray) this.get(index));
	}
}

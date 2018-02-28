package com.moinapp.wuliao.commons.preference;


public interface IPreference {
	
	public String getString(String key);

	public String getString(String key, String def);
	
	public void setString(String key, String value);
	
	public boolean getBoolean(String key);

	public boolean getBoolean(String key, boolean def);
	
	public void setBoolean(String key, boolean value);
	
	public long getLong(String key);

	public long getLong(String key, long def);

	public void setLong(String key, long value);
	
	public int getInt(String key);

	public int getInt(String key, int def);

	public void setInt(String key, int value);

	public float getFloat(String key);

	public float getFloat(String key, float def);

	public void setFloat(String key, float value);
}

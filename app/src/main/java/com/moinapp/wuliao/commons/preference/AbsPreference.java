package com.moinapp.wuliao.commons.preference;


/**
 *  配置管理基类
 *
 */
public abstract class AbsPreference {
	private IPreference mPreference;

    public AbsPreference(String name){
        mPreference = PreferenceServiceFactory.getService(name);
	}

	public String getString(String key){
		return getString(key, "");
	}

	public String getString(String key, String def){
		synchronized (mPreference) {
			return mPreference.getString(key, def);
		}
	}
	public void setString(String key, String value){
		synchronized (mPreference) {
			mPreference.setString(key, value);
		}
	}

	public boolean getBoolean(String key){
		return mPreference.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean def){
		synchronized (mPreference) {
			return mPreference.getBoolean(key, def);
		}
	}

	public void setBoolean(String key, boolean value){
		synchronized (mPreference) {
			mPreference.setBoolean(key, value);
		}
	}

	public long getLong(String key){
		return mPreference.getLong(key, 0);
	}

	public long getLong(String key, long def){
		synchronized (mPreference) {
			return mPreference.getLong(key, def);
		}
	}

	public void setLong(String key, long value){
		synchronized (mPreference) {
			mPreference.setLong(key, value);
		}
	}

	public int getInt(String key){
		return mPreference.getInt(key, 0);
	}

	public int getInt(String key, int def){
		synchronized (mPreference) {
			return mPreference.getInt(key, def);
		}
	}

	public void setInt(String key, int value){
		synchronized (mPreference) {
			mPreference.setInt(key, value);
		}
	}

	public float getFloat(String key){
		return mPreference.getFloat(key, 0f);
	}

	public float getFloat(String key, float def){
		synchronized (mPreference) {
			return mPreference.getFloat(key, def);
		}
	}

	public void setFloat(String key, float value){
		synchronized (mPreference) {
			mPreference.setFloat(key, value);
		}
	}
}

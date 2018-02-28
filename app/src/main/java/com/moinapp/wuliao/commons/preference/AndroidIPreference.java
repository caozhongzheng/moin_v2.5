package com.moinapp.wuliao.commons.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *  配置管理类
 *
 */
public class AndroidIPreference implements IPreference {

	private static Map<String, AndroidIPreference> sInstanceMap = new HashMap<String, AndroidIPreference>();
    private Context mContext;
	private String mName;
	
	public synchronized static AndroidIPreference getInstance(String name) {
		AndroidIPreference ins = sInstanceMap.get(name);
		if (ins == null) {
			sInstanceMap.put(name, ins = new AndroidIPreference(name));
		}
		return ins;
	}
    
    private AndroidIPreference(String name){
    	mContext = BaseApplication.context();
    	mName = name;
    }

    @Override
	public String getString(String key){
		return getString(key, "");
	}

	@Override
	public String getString(String key, String def) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(key, def);
		return value;
	}

	@Override
	public void setString(String key, String value){
		if(StringUtils.isNullOrEmpty(key))
			return;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
    
    @Override
	public boolean getBoolean(String key){
		return getBoolean(key, false);
	}

	@Override
	public boolean getBoolean(String key, boolean def) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		boolean value = sharedPreferences.getBoolean(key, def);
		return value;
	}

	@Override
	public void setBoolean(String key, boolean value){
		if(StringUtils.isNullOrEmpty(key))
			return;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
    @Override
	public long getLong(String key){
		return getLong(key, 0);
	}

	@Override
	public long getLong(String key, long def) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		long value = sharedPreferences.getLong(key, def);
		return value;
	}

	@Override
	public void setLong(String key, long value) {
		if (StringUtils.isNullOrEmpty(key))
			return;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
    @Override
	public int getInt(String key){
		return getInt(key, 0);
	}

	@Override
	public int getInt(String key, int def) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		int value = sharedPreferences.getInt(key, def);
		return value;
	}

	@Override
	public void setInt(String key, int value){
		if(StringUtils.isNullOrEmpty(key))
			return;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	@Override
	public float getFloat(String key){
		return getFloat(key, 0);
	}

	@Override
	public float getFloat(String key, float def) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		float value = sharedPreferences.getFloat(key, def);
		return value;
	}

	@Override
	public void setFloat(String key, float value){
		if(StringUtils.isNullOrEmpty(key))
			return;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
}

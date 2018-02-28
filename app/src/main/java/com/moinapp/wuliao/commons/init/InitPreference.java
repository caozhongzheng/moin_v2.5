package com.moinapp.wuliao.commons.init;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class InitPreference extends MyPreference {
	// ===========================================================
	// Constants
	// ===========================================================
	/* 应用首次初始化是否完成 */
	public static final String KEY_APP_INIT_DONE = "app_init_done";
	public static final String KEY_INIT_FINISH = "init_finish";//兼容之前的KEY
	public static final String KEY_CURRENT_VERSION = "sdk_current_version";
	public static final String KEY_LAST_VERSION = "sdk_last_version";
	public static final String KEY_LAST_INSTALL_TIME = "app_last_install_time";
	public static final String KEY_BOOT_IMAGE_URL = "boot_image_url";
	public static final String KEY_BOOT_IMAGE_TIME = "boot_image_time";
	public static final String KEY_BOOT_IMAGE_MSG = "boot_image_message";
	// ===========================================================
	// Fields
	// ===========================================================
	private static InitPreference sInstance = new InitPreference();
	// ===========================================================
	// Constructors
	// ===========================================================
	private InitPreference() {
	}
	
	public static InitPreference getInstance(){
		return sInstance;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public boolean getAppInitDone(){
	    return getBoolean(KEY_APP_INIT_DONE) || getBoolean(KEY_INIT_FINISH);//兼容之前的KEY
	}
	
	public void setAppInitDone(boolean appInitDone){
        setBoolean(KEY_APP_INIT_DONE, appInitDone);
        setBoolean(KEY_INIT_FINISH, appInitDone);//兼容之前的KEY
    }
	
	public int getCurrentVersion(){
	    return getInt(KEY_CURRENT_VERSION);
	}
	
	public void setCurrentVersion(int version){
        setInt(KEY_CURRENT_VERSION, version);
    }
	
	public int getLastVersion(){
	    return getInt(KEY_LAST_VERSION);
	}
	
	public void setLastVersion(int version){
        setInt(KEY_LAST_VERSION, version);
    }
	
	public long getLastInstallTime(){
	    return getLong(KEY_LAST_INSTALL_TIME);
	}
	
	public void setLastInstallTime(long time){
        setLong(KEY_LAST_INSTALL_TIME, time);
    }

	public String getBootImageUrl() {
		return getString(KEY_BOOT_IMAGE_URL);
	}

	public void setBootImageUrl(String bootImageUrl) {
		setString(KEY_BOOT_IMAGE_URL, bootImageUrl);
	}

	public long getBootImageUpdateAt() {
		return getLong(KEY_BOOT_IMAGE_TIME);
	}

	public void setBootImageUpdateAt(long bootImageUpdateAt) {
		setLong(KEY_BOOT_IMAGE_TIME, bootImageUpdateAt);
	}

	public boolean isHasBootImageMsg() {
		return getBoolean(KEY_BOOT_IMAGE_MSG);
	}

	public void setHasBootImageMsg(boolean hasBootImageMsg) {
		setBoolean(KEY_BOOT_IMAGE_MSG, hasBootImageMsg);
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

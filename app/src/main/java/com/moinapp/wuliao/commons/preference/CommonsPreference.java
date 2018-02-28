package com.moinapp.wuliao.commons.preference;

public class CommonsPreference extends MyPreference {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_CHANEL_ID = "channel_id";
    private static final String KEY_NEED_FORE_ACTIVE = "need_fore_active";
    private static final String KEY_FORE_ACTIVE_SUCC = "fore_active_succ";
	private static final String KEY_UID = "moin_uid";
	private static final String KEY_PASSPORT = "moin_passport";
	private static final String KEY_USERNAME = "moin_username";
	private static final String KEY_VIRTUAL_KEYBOARD_HEIGHT = "virtual_keyboard_height";

	// ===========================================================
	// 百度推送需要用到的key
	// ===========================================================
	private static final String BAIDU_PUSH_CHANNEL = "baidu_push_channel_id";
	private static final String BAIDU_PUSH_APPID = "baidu_push__app_id";
	private static final String BAIDU_PUSH_USERID = "baidu_push_user_id";



	// ===========================================================
	// Fields
	// ===========================================================
    private static CommonsPreference sInstance = new CommonsPreference();

	// ===========================================================
	// Constructors
	// ===========================================================
	private CommonsPreference() {
	}
	
	public static CommonsPreference getInstance(){
		return sInstance;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
    public String getChannelId() {
        return getString(KEY_CHANEL_ID);
    }

    public void setChannelId(String channelId) {
        setString(KEY_CHANEL_ID, channelId);
    }
    
	public boolean isNeedForegroundActive(){
	    return getBoolean(KEY_NEED_FORE_ACTIVE);
	}	
	public void setNeedForegroundActive(boolean need){
        setBoolean(KEY_NEED_FORE_ACTIVE, need);
    }
	
	public boolean isForegroundActiveSuccess(){
	    return getBoolean(KEY_FORE_ACTIVE_SUCC);
	}	
	public void setForegroundActiveSuccess(boolean success){
        setBoolean(KEY_FORE_ACTIVE_SUCC, success);
    }

	public void setUID(String uid) {
		setString(KEY_UID, uid);
	}

	public String getUID() {
		return getString(KEY_UID);
	}

	public void setPassport(String passport) {
		setString(KEY_PASSPORT, passport);
	}

	public String getPassport() {
		return getString(KEY_PASSPORT);
	}

	public void setUsername(String username) {
		setString(KEY_USERNAME, username);
	}

	public String getUserName() {
		return getString(KEY_USERNAME);
	}

	public void setVirtualKeyboardHeight(int height) {
		setInt(KEY_VIRTUAL_KEYBOARD_HEIGHT, height);
	}

	public int getVirtualKeyboardHeight() {
		return getInt(KEY_VIRTUAL_KEYBOARD_HEIGHT);
	}

	public String getBaiduPushChannel() {
		return getString(BAIDU_PUSH_CHANNEL);
	}

	public void setBaiduPushChannel(String channel) {
		setString(BAIDU_PUSH_CHANNEL, channel);
	}

	public String getBaiduPushAppid() {
		return getString(BAIDU_PUSH_APPID);
	}

	public void setBaiduPushAppid(String channel) {
		setString(BAIDU_PUSH_APPID, channel);
	}

	public String getBaiduPushUserid() {
		return getString(BAIDU_PUSH_USERID);
	}

	public void setBaiduPushUserid(String channel) {
		setString(BAIDU_PUSH_USERID, channel);
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

package com.moinapp.wuliao.modules.update;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class UpdatePreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final String KEY_LAST_CHECK_UPDATE ="last_check_update";
    public static final String KEY_UPDATE_FILE_NAME = "update_file_name";
    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final String KEY_DOWNLOAD_FINISH = "download_finish";
    public static final String KEY_DOWNLOAD_REFER = "download_refer";

    // ===========================================================
    // Fields
    // ===========================================================

    private static UpdatePreference sInstance;

    // ===========================================================
    // Constructors
    // ===========================================================

    private UpdatePreference() {
    }

    public static UpdatePreference getInstance(){
        if(sInstance == null){
            sInstance = new UpdatePreference();
        }

        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
	public long getLastCheckUpdate(){
	    return getLong(KEY_LAST_CHECK_UPDATE);
	}
	public void setLastCheckUpdate(long time){
        setLong(KEY_LAST_CHECK_UPDATE, time);
    }

    public String getUpdateFileName() {
        return getString(KEY_UPDATE_FILE_NAME);
    }

    public void setUpdateFileName(String fileName) {
        setString(KEY_UPDATE_FILE_NAME, fileName);
    }

    public String getDownloadUrl() {
        return getString(KEY_DOWNLOAD_URL);
    }

    public void setDownloadUrl(String url) {
        setString(KEY_DOWNLOAD_URL, url);
    }

    public boolean getDownloadFinish() {
        return getBoolean(KEY_DOWNLOAD_FINISH);
    }

    public void setDownloadFinish(boolean result) {
        setBoolean(KEY_DOWNLOAD_FINISH, result);
    }

    public long getDownloadRefer() {
        return getLong(KEY_DOWNLOAD_REFER);
    }

    public void setDownloadRefer(long refer) {
        setLong(KEY_DOWNLOAD_REFER, refer);
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

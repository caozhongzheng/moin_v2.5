package com.moinapp.wuliao.modules.sticker;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by moin on 15/7/2.
 */
public class StickPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
//    public static final String KEY_RELOAD_STICKER = "reload_sticker";
    public static final String KEY_DEFAULT_USE_STICKER = "default_use_sticker";//3.2.6版本以前的在商城通过使用某个贴纸包发图时记录的贴纸包ID
    public static final String KEY_USE_SINGLE_STICKER = "use_single_sticker";//使用单张贴纸时记录下贴纸ID
    public static final String KEY_LAST_IMAGE_FOLDER = "last_image_folder";
    public static final String KEY_LAST_STICKERMALL_FOLDER = "last_stickermall_folder";

    public static final String KEY_LAST_GET_UPLOAD_SERVER_TIME = "last_get_upload_server_time";
    public static final String KEY_BEST_SERVER_DOMAIN = "best_server_domain";
    public static final String KEY_BEST_SERVER_BUCKET = "best_server_bucket";

    /**
     * 纪录照片编辑页面是否已经启动的标记
     */
    public static final String KEY_PHOTO_PROCESS_RUNNING = "photo_process_running";
    /**
     * 话题详情页面我要参与的话题名称
     */
    public static final String KEY_JOIN_TOPIC_NAME = "join_topic_name";
    /**
     * 话题详情页面我要参与的话题ID
     */
    public static final String KEY_JOIN_TOPIC_ID = "join_topic_id";
    /**
     * 话题详情页面我要参与的话题对应的贴纸包ID
     */
    public static final String KEY_JOIN_TOPIC_STICKERPACKAGE_ID = "join_topic_stickerpackage_id";
    /**
     * 使用商城中单张贴纸时保存其对应的贴纸包ID
     */
    public static final String KEY_USE_MALL_STICKERPACKAGE_ID = "use_mall_stickerpackage_id";
    /**
     * 点赞数上传service的上次检查时间
     */
    public static final String KEY_LAST_LIKE_PERIOD_CHECK = "last_period_check";
    public static final String KEY_LAST_COLOR_TEXT_POS = "last_color_text_pos";
    // ===========================================================
    // Fields
    // ===========================================================
    private static StickPreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private StickPreference() {
        super("sticker");
    }

    public static StickPreference getInstance() {
        if (sInstance == null) {
            sInstance = new StickPreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
//    public boolean getReloadStickerFlag() {
//        return getBoolean(KEY_RELOAD_STICKER, false);
//    }

//    public void setReloadStickerFlag(boolean value) {
//        setBoolean(KEY_RELOAD_STICKER, value);
//    }

    public String getDefaultUseSticker() {
        return getString(KEY_DEFAULT_USE_STICKER);
    }

    public void setDefaultUseSticker(String value) {
        setString(KEY_DEFAULT_USE_STICKER, value);
    }

    public String getUseSingleSticker() {
        return getString(KEY_USE_SINGLE_STICKER);
    }

    public void setUseSingleSticker(String value) {
        setString(KEY_USE_SINGLE_STICKER, value);
    }

    public String getLastImageFolder() {
        return getString(KEY_LAST_IMAGE_FOLDER);
    }

    public void setLastImageFolder(String value) {
        setString(KEY_LAST_IMAGE_FOLDER, value);
    }

    public boolean getPhotoProcessRunning() {
        return getBoolean(KEY_PHOTO_PROCESS_RUNNING, false);
    }

    public void setPhotoProcessRunning(boolean value) {
        setBoolean(KEY_PHOTO_PROCESS_RUNNING, value);
    }

    public long getLastStickerMallFolders() {
        return getLong(KEY_LAST_STICKERMALL_FOLDER);
    }

    public void setLastStickerMallFolders(long lastStickerMallFolders) {
        setLong(KEY_LAST_STICKERMALL_FOLDER, lastStickerMallFolders);
    }

    public long getLastGetUploadServer() {
        return getLong(KEY_LAST_GET_UPLOAD_SERVER_TIME);
    }

    public void setLastGetUploadServer(long last) {
        setLong(KEY_LAST_GET_UPLOAD_SERVER_TIME, last);
    }

    public String getBestServerDomain() {
        return getString(KEY_BEST_SERVER_DOMAIN);
    }

    public void setBestServerDomain(String value) {
        setString(KEY_BEST_SERVER_DOMAIN, value);
    }

    public String getBestServerBucket() {
        return getString(KEY_BEST_SERVER_BUCKET);
    }

    public void setBestServerBucket(String value) {
        setString(KEY_BEST_SERVER_BUCKET, value);
    }

    public String getJoinTopicName() {
        return getString(KEY_JOIN_TOPIC_NAME);
    }

    public String getJoinTopicID() {
        return getString(KEY_JOIN_TOPIC_ID);
    }

    public String getJoinTopicStickerpackageId() {
        return getString(KEY_JOIN_TOPIC_STICKERPACKAGE_ID);
    }

    public void setJoinTopicName(String joinTopicName) {
        setString(KEY_JOIN_TOPIC_NAME, joinTopicName);
    }

    public void setJoinTopicID(String joinTopicID) {
        setString(KEY_JOIN_TOPIC_ID, joinTopicID);
    }

    public void setJoinTopicStickerpackageId(String joinTopicStickPackageID) {
        setString(KEY_JOIN_TOPIC_STICKERPACKAGE_ID, joinTopicStickPackageID);
    }

    public void deleteJoinTopicInfo(){
        setJoinTopicName("");
        setJoinTopicID("");
        setJoinTopicStickerpackageId("");
    }

    public String getStickerMallPackageId() {
        return getString(KEY_USE_MALL_STICKERPACKAGE_ID);
    }

    public void setStickerMallPackageId(String stickerMallPackageId) {
        setString(KEY_USE_MALL_STICKERPACKAGE_ID, stickerMallPackageId);
    }

    public long getLastPeriodCheck() {
        return getLong(KEY_LAST_LIKE_PERIOD_CHECK);
    }

    public void setLastPeriodCheck(long lastPeriodCheck) {
        setLong(KEY_LAST_LIKE_PERIOD_CHECK, lastPeriodCheck);
    }

    public int getLastColorTextColorPos() {
        return getInt(KEY_LAST_COLOR_TEXT_POS);
    }

    public void setLastColorTextColorPos(int lastColorTextPos) {
        setInt(KEY_LAST_COLOR_TEXT_POS, lastColorTextPos);
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
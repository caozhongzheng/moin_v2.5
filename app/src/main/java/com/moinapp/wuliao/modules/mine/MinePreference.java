package com.moinapp.wuliao.modules.mine;

import android.text.TextUtils;

import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by moin on 15/7/2.
 */
public class MinePreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_NEED_FETCH_NICKNAME = "need_fetch_nickname";
    public static final String KEY_EMJ_DOWNLOAD_COUNT = "emj_download_count";
    public static final String KEY_EMJ_DOWNLOAD_ID = "emj_download_id";
    public static final String KEY_TAB_ID = "cos_tab_id";
    //最新的未读消息通知是点击进入消息后才更新的,比KEY_MSG_LAST_READ_TIME要滞后一些
    public static final String KEY_MSG_LAST_READ_TIME = "message_last_read_time_";
    //最新的未读消息通知是pop一次就更新的
    public static final String KEY_MSG_LAST_POP_TIME = "message_last_pop_time_";
    //刷新图片编辑[大咖秀制作,转改]的贴纸素材区域
    public static final String KEY_NEED_REFRESH_PHOTO_EDIT = "need_refresh_photo_edit";
    /**是否第一次打开应用*/
    public static final String KEY_FIRST_ENTER = "first_enter";
    /**预制贴纸更新时间间隔*/
    public static final String KEY_DEFAULT_STICKER = "default_sticker_";
    /**查看过详情的最新的贴纸包*/
    public static final String KEY_VIEWED_NEWEST_STICKER = "viewed_newest_sticker";
    public static final String KEY_SHARE_PLATFORM = "sticker_share_platform";
    /**个人空间里面的新消息标记,显示小红点用的 */
    public static final String KEY_USER_SPACE_NEW_MESSAGE = "user_space_new_messsage";
    /**个人空间里面的设置是否图片保存水印 */
    public static final String KEY_SAVE_WATERMARK = "save_watermark";
    /**个人空间里面的设置是否提醒新消息 */
    public static final String KEY_NOTIFY_NEW_MSG = "notify_new_message";
    /**个人空间里面的设置新消息提醒方式 */
    public static final String KEY_NOTIFICATION_STYLE = "notification_style";

    // ===========================================================
    // Fields
    // ===========================================================
    private static MinePreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private MinePreference() {
        super("mine");
    }

    public static MinePreference getInstance() {
        if (sInstance == null) {
            sInstance = new MinePreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    /**之所以加上UID前缀是为了防止别的用户登录后用的也是这个
     * ClientInfo.getUID()+"_" + KEY*/
    public String getNickname() {
        return getString(ClientInfo.getUID().hashCode() + "_" + KEY_NICKNAME);
    }

    public void setNickname(String nickname) {
        setString(ClientInfo.getUID().hashCode() + "_" + KEY_NICKNAME, nickname);
    }
    /**之所以加上UID前缀是为了防止别的用户登录后用的也是这个
     * ClientInfo.getUID()+"_" + KEY*/
    public String getUsername() {
        return getString(ClientInfo.getUID().hashCode() + "_" + KEY_USERNAME);
    }

    public void setUsername(String username) {
        setString(ClientInfo.getUID().hashCode() + "_" + KEY_USERNAME, username);
    }

    public boolean isNeedFetchNickname() {
        return getBoolean(KEY_NEED_FETCH_NICKNAME, true);
    }

    public void setNeedFetchNickname(boolean needFetchNickname) {
        setBoolean(KEY_NEED_FETCH_NICKNAME, needFetchNickname);
    }

    public int getEmjDownloadCount() {
        return getInt(ClientInfo.getUID().hashCode() + "_" + KEY_EMJ_DOWNLOAD_COUNT);
    }

    public void setEmjDownloadCount(int count) {
        setInt(ClientInfo.getUID().hashCode() + "_" + KEY_EMJ_DOWNLOAD_COUNT, count);
    }

    public int getCosCount() {
        return getInt(ClientInfo.getUID().hashCode()+"_" + KEY_TAB_ID);
    }

    public void setCosCount(int count) {
        setInt(ClientInfo.getUID().hashCode()+"_" + KEY_TAB_ID, count);
    }

    public String getEmjDownloadID() {
        return getString(ClientInfo.getUID().hashCode() + "_" + KEY_EMJ_DOWNLOAD_ID);
    }

    public void setEmjDownloadID(String id) {
        setString(ClientInfo.getUID().hashCode() + "_" + KEY_EMJ_DOWNLOAD_ID, id);
    }
    public void addEmjDownloadID(String id) {
        String oldID = getEmjDownloadID();
        if(TextUtils.isEmpty(oldID)) {
            setString(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_ID, id);
        } else {
            setString(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_ID, oldID + ";" + id);
        }
    }

    public boolean isEmjDownloaded(String id) {
        if (id == null) {
            return true;
        }
        String oldID = getEmjDownloadID();
        if(TextUtils.isEmpty(oldID))
            return false;
        return oldID.contains(id);
        // 更保险
//        String[] arr = oldID.split(";");
//        for (int i = 0; i < arr.length; i++) {
//            String s = arr[i];
//            if(s.equals(id))
//                return true;
//        }
//        return false;
    }

    public int getUserLoginCount(String uid) {
        return getInt(uid, 0);
    }

    public void setUserLoginCount(String uid, int count) {
        setInt(uid, count);
    }

    public long getLastReadTime() {
        return getLong(KEY_MSG_LAST_READ_TIME + ClientInfo.getUID());
    }

    public void setLastReadTime(long lastReadTime) {
        setLong(KEY_MSG_LAST_READ_TIME + ClientInfo.getUID(), lastReadTime);
    }

    public long getLastPopTime() {
        return getLong(KEY_MSG_LAST_POP_TIME + ClientInfo.getUID());
    }

    public void setLastPopTime(long lastReadTime) {
        setLong(KEY_MSG_LAST_POP_TIME + ClientInfo.getUID(), lastReadTime);
    }

    public boolean isNeedRefreshPhotoEdit() {
        return getBoolean(KEY_NEED_REFRESH_PHOTO_EDIT, true);
    }

    public void setNeedRefreshPhotoEdit(boolean isNeedRefresh) {
        setBoolean(KEY_NEED_REFRESH_PHOTO_EDIT, isNeedRefresh);
    }

    public boolean isFirstEnter() {
        return getBoolean(KEY_FIRST_ENTER, true);
    }

    public void setFirstEnter(boolean firstEnter) {
        setBoolean(KEY_FIRST_ENTER, firstEnter);
    }

    public long getLastRefreshTime(int type) {
        return getLong(KEY_DEFAULT_STICKER + type);
    }

    public void setLastRefreshTime(int type, long lastRefreshTime) {
        setLong(KEY_DEFAULT_STICKER + type, lastRefreshTime);
    }

    public String getViewedNewestSticker() {
        return getString(KEY_VIEWED_NEWEST_STICKER);
    }

    public void setViewedNewestSticker(String viewedNewestSticker) {
        setString(KEY_VIEWED_NEWEST_STICKER, viewedNewestSticker);
    }

    public String getSharePlatform() {
        return getString(KEY_SHARE_PLATFORM);
    }

    public void setSharePlatform(String sharePlatform) {
        setString(KEY_SHARE_PLATFORM, sharePlatform);
    }

    public boolean hasUserSpaceMessage() {
        return getBoolean(KEY_USER_SPACE_NEW_MESSAGE, false);
    }

    public void setUserSpaceMessage(boolean value) {
        setBoolean(KEY_USER_SPACE_NEW_MESSAGE, value);
    }

    public boolean isSaveWatermark() {
        return getBoolean(KEY_SAVE_WATERMARK, true);
    }

    public void setIsSaveWatermark(boolean isSaveWatermark) {
        setBoolean(KEY_SAVE_WATERMARK, isSaveWatermark);
    }

    public boolean isNotifyNewMsg() {
        return getBoolean(KEY_NOTIFY_NEW_MSG, true);
    }

    public void setIsNotifyNewMsg(boolean isNotifyNewMsg) {
        setBoolean(KEY_NOTIFY_NEW_MSG, isNotifyNewMsg);
    }

    public int getNotificationStyle() {
        return getInt(KEY_NOTIFICATION_STYLE, NotificationStyleFragment.NOTIFY_STYLE_SOUND);
    }

    public void setNotificationStyle(int notificationStyle) {
        setInt(KEY_NOTIFICATION_STYLE, notificationStyle);
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
package com.moinapp.wuliao.modules.mission;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by liujiancheng on 16/7/26.
 */
public class MissionPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    /**
     * 是否已经弹过发图的提示
     */
    public static final String KEY_MISSION_MAKE_COSPLAY_GUIDE = "mission_make_cosplay_guide";

    /**
     * 是否已经弹过分享的提示
     */
    public static final String KEY_MISSION_SHARE_GUIDE = "mission_share_guide";

    /**
     * 是否已经弹过点赞的提示
     */
    public static final String KEY_MISSION_LIKE_GUIDE = "mission_like_guide";

    /**
     * 是否已经弹过评论的提示
     */
    public static final String KEY_MISSION_COMMENT_GUIDE = "mission_comment_guide";

    // ===========================================================
    // Fields
    // ===========================================================
    private static MissionPreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private MissionPreference() {
        super("mine");
    }

    public static MissionPreference getInstance() {
        if (sInstance == null) {
            sInstance = new MissionPreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    public int getCosplayGuide() {
        return getInt(KEY_MISSION_MAKE_COSPLAY_GUIDE, 0);
    }

    public void setCosplayuide(int val) {
        setInt(KEY_MISSION_MAKE_COSPLAY_GUIDE, val);
    }

    public int getShareGuide() {
        return getInt(KEY_MISSION_SHARE_GUIDE, 0);
    }

    public void setShareGuide(int val) {
        setInt(KEY_MISSION_SHARE_GUIDE, val);
    }

    public boolean isFirstLikeGuide() {
        return getBoolean(KEY_MISSION_LIKE_GUIDE, true);
    }

    public void setFirstLikeGuide(boolean firstLikeGuide) {
        setBoolean(KEY_MISSION_LIKE_GUIDE, firstLikeGuide);
    }

    public boolean isFirstCommentGuide() {
        return getBoolean(KEY_MISSION_COMMENT_GUIDE, true);
    }

    public void setFirstCommentGuide(boolean firstCommentGuide) {
        setBoolean(KEY_MISSION_COMMENT_GUIDE, firstCommentGuide);
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
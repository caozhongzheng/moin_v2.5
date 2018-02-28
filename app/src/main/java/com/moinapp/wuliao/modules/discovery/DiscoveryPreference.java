package com.moinapp.wuliao.modules.discovery;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by moin on 15/7/2.
 */
public class DiscoveryPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String KEY_SHARE_TO_WX = "share_to_wx";

    // ===========================================================
    // Fields
    // ===========================================================
    private static DiscoveryPreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private DiscoveryPreference() {
        super("discovery");
    }

    public static DiscoveryPreference getInstance() {
        if (sInstance == null) {
            sInstance = new DiscoveryPreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    public int getShareToWXFlag() {
        return getInt(KEY_SHARE_TO_WX, 0);
    }

    public void setShareToWXFlag(int flag) {
        setInt(KEY_SHARE_TO_WX, flag);
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
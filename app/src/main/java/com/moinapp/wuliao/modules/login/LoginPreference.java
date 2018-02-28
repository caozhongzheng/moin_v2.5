package com.moinapp.wuliao.modules.login;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by moin on 16/5/15.
 */
public class LoginPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String KEY_LOGIN_FAIL_TIMES = "login_fail_times";
    // ===========================================================
    // Fields
    // ===========================================================
    private static LoginPreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private LoginPreference() {
        super("mine");
    }

    public static LoginPreference getInstance() {
        if (sInstance == null) {
            sInstance = new LoginPreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    public int getLoginFailedTimes() {
        return getInt(KEY_LOGIN_FAIL_TIMES, 0);
    }

    public void setLoginFailedTimes(int times) {
        setInt(KEY_LOGIN_FAIL_TIMES, times);
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
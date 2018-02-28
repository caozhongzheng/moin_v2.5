package com.moinapp.wuliao.commons.info;

import android.content.Context;

/**
 * Created by liujiancheng on 15/5/6.
 * 和服务器交互时需要上传的常规信息，包括UID,SessionId,IMEI
 */
public class CommonConnectInfo {
    private String mUid;
    private String mPassport;
    private String mIMEI;

    private static CommonConnectInfo mInstance;
    public static synchronized CommonConnectInfo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CommonConnectInfo(context);
        }

        return mInstance;
    }

    private CommonConnectInfo(Context context) {
        mUid = ClientInfo.getUID();
        mPassport = ClientInfo.getPassport();
        mIMEI = MobileInfo.getImei(context);
    }

    public String getUid() {
        return mUid;
    }

    public String getmPassport() {
        return mPassport;
    }

    public String getmIMEI() {
        return mIMEI;
    }
}

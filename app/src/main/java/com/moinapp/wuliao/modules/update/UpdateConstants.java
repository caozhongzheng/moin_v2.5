package com.moinapp.wuliao.modules.update;


import com.moinapp.wuliao.AppConfig;

/**
 * Created by liujiancheng on 15/9/2.
 */
public class UpdateConstants {
    /**
     * 版本更新模块：服务器基本domain
     */
    public static final String UPDATE_BASE_URL = AppConfig.getBaseUrl() + "user/";

    /**
     * 版本更新接口的url
     */
    public static final String GET_UPDATE_URL = UPDATE_BASE_URL + "getappversion";

    public static final String VERSION_CODE = "versionCode";
    public static final String CHANNEL = "channel";

    public static final String UPDATE_CACHE = "Moin/Update/";
}

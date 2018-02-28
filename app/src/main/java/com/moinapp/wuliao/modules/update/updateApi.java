package com.moinapp.wuliao.modules.update;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.util.TDevice;

/**
 * Created by liujiancheng on 15/9/2.
 */
public class updateApi {
    /**
     * 获取版本更细信息
     * @param handler
     */
    public static void checkUpdate(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.VERSION_CODE, String.valueOf(TDevice.getVersionCode()));
        params.put(LoginConstants.CHANNEL, ClientInfo.getChannelId());
        ApiHttpClient.post(LoginConstants.GET_UPDATE_URL, params, handler);
    }
}

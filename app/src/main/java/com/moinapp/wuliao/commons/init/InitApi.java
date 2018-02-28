package com.moinapp.wuliao.commons.init;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;

/**
 * Created by liujiancheng on 15/12/30.
 */
public class InitApi {
    /**
     * 开机联网获取开机启动画面的接口
     * @param updatedAt: 上一次获取到的最新启动图的时间
     * @param handler
     */
    public static void getBootImage(long updatedAt, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(InitConstants.UPDATE_AT, updatedAt);
        ApiHttpClient.post(InitConstants.INIT_GET_BOOT_IMAGE_URL, params, handler);
    }
}

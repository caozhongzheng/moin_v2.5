package com.moinapp.wuliao.modules.events;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;

/**
 * Created by liujiancheng on 16/6/8.
 */
public class EventsApi {

    /**
     * 获取活动列表
     * @param lastid：最后一个活动ID，可选
     * @param pageNum：每页显示数量，默认为20，可选
     * @param isHot：是否为首页展示，1 是 0 否，默认为0，可选*
     * @param handler: callback
     */
    public static void getEvents(String lastid, int pageNum, int isHot, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(EventsConstants.LASTID, lastid);
        params.put(EventsConstants.PAGENUM, pageNum);
        params.put(EventsConstants.ISHOT, isHot);
        ApiHttpClient.post(EventsConstants.GET_EVENTS_LIST_URL, params, handler);
    }

    /**
     * 获取分享的活动
     * @param handler: callback
     */
    public static void getShareEvents(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        ApiHttpClient.post(EventsConstants.GET_SHARE_EVENTS_URL, params, handler);
    }
}

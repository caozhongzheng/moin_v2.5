package com.moinapp.wuliao.modules.events;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.events.result.GetEventsListResult;
import com.moinapp.wuliao.modules.events.result.GetShareEventResult;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

/**
 * Created by liujiancheng on 16/6/8.
 */
public class EventsManager {
    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger("EventsManager");
    private static EventsManager mInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private EventsManager() {
    }

    public static synchronized EventsManager getInstance() {
        if (mInstance == null) {
            mInstance = new EventsManager();
        }

        return mInstance;
    }

    // ===========================================================
    // Interfaces & public methods
    // ===========================================================
    /**
     * 获取活动列表
     * @param lastid：最后一个活动ID，可选
     * @param pageNum：每页显示数量，默认为20，可选
     * @param isHot：是否为首页展示，1 是 0 否，默认为0，可选
     * @param listener: callback
     */
    public void getEvents(String lastid, int pageNum, int isHot, IListener listener) {
        EventsApi.getEvents(lastid, pageNum, isHot, buildGetEventsCallback(listener));
    }

    /**
     * 获取分享的活动接口
     * @param listener: callback
     */
    public void getShareEvents(IListener listener) {
        EventsApi.getShareEvents(buildGetShareEventCallback(listener));
    }

    // ===========================================================
    // Inner class & private methods
    // ===========================================================
    private AsyncHttpResponseHandler buildGetEventsCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetEventsListResult result = XmlUtils.JsontoBean(GetEventsListResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getWebList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetShareEventCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetShareEventResult result = XmlUtils.JsontoBean(GetShareEventResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getActivity());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }
}

package com.moinapp.wuliao.modules.events.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.events.model.EventsInfo;

import java.util.List;

/**
 * 获取分享的活动结果
 */
public class GetShareEventResult extends BaseHttpResponse {
    private EventsInfo activity;

    public EventsInfo getActivity() {
        return activity;
    }

    public void setActivity(EventsInfo activity) {
        this.activity = activity;
    }
}

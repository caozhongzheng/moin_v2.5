package com.moinapp.wuliao.modules.events.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.events.model.EventsInfo;

import java.util.List;

/**
 * 服务器返回的活动列表结果
 * Created by liujiancheng on 15/10/9.
 */
public class GetEventsListResult extends BaseHttpResponse {
    /**
     * 活动列表
     */
    private List<EventsInfo> webList;

    public List<EventsInfo> getWebList() {
        return webList;
    }

    public void setWebList(List<EventsInfo> webList) {
        this.webList = webList;
    }
}

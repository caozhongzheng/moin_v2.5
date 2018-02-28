package com.moinapp.wuliao.modules.events.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 活动列表信息
 * Created by liujiancheng on 16/6/8.
 */
public class EventsInfoList extends Entity implements ListEntity<EventsInfo> {
    private List<EventsInfo> webList;

    public List<EventsInfo> getList() {
        return webList;
    }

    public void setEventsList(List<EventsInfo> webList) {
        this.webList = webList;
    }
}

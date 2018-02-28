package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;

import java.util.List;

/**
 * Created by liujiancheng on 16/2/25.
 */
public class GetTopicDetailResult extends BaseHttpResponse {
    private TagPop topic;
    private List<CosplayInfo> cosplayList;

    public TagPop getTopic() {
        return topic;
    }

    public void setTopic(TagPop topic) {
        this.topic = topic;
    }

    public List<CosplayInfo> getCosplayList() {
        return cosplayList;
    }

    public void setCosplayList(List<CosplayInfo> cosplayList) {
        this.cosplayList = cosplayList;
    }
}

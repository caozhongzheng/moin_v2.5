package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;

import java.util.List;

/**
 * Created by liujiancheng on 16/2/25.
 */
public class GetTopicListResult extends BaseHttpResponse {
    private List<TagPop> topicList;

    public List<TagPop> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<TagPop> list) {
        this.topicList = list;
    }
}

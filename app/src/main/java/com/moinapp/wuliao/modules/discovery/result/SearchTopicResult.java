package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.TagPop;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/12.
 */
public class SearchTopicResult extends BaseHttpResponse {
    private List<TagPop> topicList;


    public List<TagPop> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<TagPop> tags) {
        this.topicList = tags;
    }

}

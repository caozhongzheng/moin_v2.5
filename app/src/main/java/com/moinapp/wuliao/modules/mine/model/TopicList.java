package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;
import com.moinapp.wuliao.modules.discovery.model.TagPop;

import java.util.List;

/**
 * Created by guyunfei on 16/3/1.11:41.
 */
public class TopicList extends Entity implements ListEntity<TagPop> {
    private List<TagPop> topicList;

    public List<TagPop> getTagInfos() {
        return topicList;
    }

    public void setTagInfos(List<TagPop> topicList) {
        this.topicList = topicList;
    }

    @Override
    public List<TagPop> getList() {
        return this.topicList;
    }

}

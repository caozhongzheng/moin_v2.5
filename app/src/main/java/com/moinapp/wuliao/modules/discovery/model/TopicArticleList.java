package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 话题帖子列表(3.2.7)
 */
@SuppressWarnings("serial")
public class TopicArticleList extends Entity implements ListEntity<CosplayInfo> {
    private List<CosplayInfo> postList;

    public void setPostList(List<CosplayInfo> list) {
        this.postList = list;
    }

    @Override
    public List<CosplayInfo> getList() {
        return postList;
    }
}

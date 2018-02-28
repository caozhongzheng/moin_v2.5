package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;

import java.util.List;

/**
 * 标签列表类, 给我关注的标签页面用
 * Created by liujiancheng on 15/10/22.
 */
public class TagInfoList extends Entity implements ListEntity<TagInfo> {
    private List<TagInfo> tagInfos;

    public List<TagInfo> getTagInfos() {
        return tagInfos;
    }

    public void setTagInfos(List<TagInfo> tagInfos) {
        this.tagInfos = tagInfos;
    }

    @Override
    public List<TagInfo> getList() {
        return this.tagInfos;
    }

}

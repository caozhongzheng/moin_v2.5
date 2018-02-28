package com.moinapp.wuliao.modules.discovery.model;

/**
 * 发现频道列表页面的item,包括热门标签和大咖秀图片
 * Created by liujiancheng on 15/10/10.
 */
public class HotItem {
    private CosplayInfo cosplay;
    private TagInfo tag;

    public CosplayInfo getCosplay() {
        return cosplay;
    }

    public void setCosplay(CosplayInfo cosplay) {
        this.cosplay = cosplay;
    }

    public TagInfo getTag() {
        return tag;
    }

    public void setTag(TagInfo tag) {
        this.tag = tag;
    }
}

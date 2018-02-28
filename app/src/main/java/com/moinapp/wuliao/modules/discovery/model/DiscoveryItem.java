package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;

import java.io.Serializable;

/**
 * 发现频道列表页面的item,包括热门标签和大咖秀图片
 * Created by liujiancheng on 15/10/10.
 */
public class DiscoveryItem extends Entity implements Serializable {
    private CosplayInfo cosplay;
    private TagInfo optag;

    public CosplayInfo getCosplay() {
        return cosplay;
    }

    public void setCosplay(CosplayInfo cosplay) {
        this.cosplay = cosplay;
    }

    public TagInfo getOptag() {
        return optag;
    }

    public void setOptag(TagInfo tag) {
        this.optag = tag;
    }

    public boolean isCosplay() {
        if(cosplay != null && optag == null) {
            return true;
        }
        return false;
    }

    public boolean isTag() {
        if(optag != null && cosplay == null) {
            return true;
        }
        return false;
    }

    public boolean isOnlyCosplay() {
        return isCosplay() && !isTag();
    }

    public boolean isOnlyTag() {
        return !isCosplay() && isTag();
    }

    @Override
    public String toString() {
        return "DiscoveryItem{" +
                "cosplay=" + cosplay +
                ", tag=" + optag +
                '}';
    }
}

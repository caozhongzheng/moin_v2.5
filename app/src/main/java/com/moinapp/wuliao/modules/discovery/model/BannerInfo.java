package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;

/**
 * 发现频道首页banner
 * Created by liujiancheng on 15/12/30.
 */
public class BannerInfo extends Entity {
    /**
     * banner的类型
     */
    private String type;

    /**
     * banner图片
     */
    private BaseImage picture;

    /**
     * 话题对象
     */
    private TagPop tagPop;

    /**
     * BANNER跳转的url
     */
    private String url;

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public TagPop getTagPop() {
        return tagPop;
    }

    public void setTagPop(TagPop tagPop) {
        this.tagPop = tagPop;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BannerInfo{" +
                "type='" + type + '\'' +
                ", picture=" + picture +
                ", url='" + url + '\'' +
                ", tagPop=" + tagPop +
                '}';
    }
}

package com.moinapp.wuliao.modules.sticker.model;

/**
 * 发布时描述贴纸信息的类
 * Created by liujiancheng on 15/9/16.
 */
public class StickerId {
    private String stickerId;
    private String parentid;

    public StickerId() {
    }

    public StickerId(String stickerId, String parentid) {
        this.stickerId = stickerId;
        this.parentid = parentid;
    }

    public String getStickerId() {
        return stickerId;
    }

    public void setStickerId(String id) {
        this.stickerId = id;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }
}

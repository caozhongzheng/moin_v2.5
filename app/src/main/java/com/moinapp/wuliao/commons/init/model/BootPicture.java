package com.moinapp.wuliao.commons.init.model;


import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;

/**
 * Created by liujiancheng on 15/12/30.
 * 启动页图片类
 */
public class BootPicture extends Entity {
    /**
     * 启动图
     */
    private BaseImage picture;

    /**
     * 图片修改时间
     */
    private long updatedAt;

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

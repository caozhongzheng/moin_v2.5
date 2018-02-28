package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;

/**
 * 一张大咖秀图片的详细信息
 */
public class CosplayInfoDetail extends Entity {
    private CosplayInfo cosplay;

    public CosplayInfo getCosplay() {
        return cosplay;
    }

    public void setCosplay(CosplayInfo cosplay) {
        this.cosplay = cosplay;
    }
}
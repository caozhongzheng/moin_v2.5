package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;

/**
 * 连续点赞用的大咖秀对象
 * Created by liujiancheng on 15/12/30.
 */
public class LikeCosplay extends Entity {
    /**
     * cosplay id
     */
    private String ucid;

    /**
     * cosplay增加的点赞数量
     */
    private int likeNum;

    public LikeCosplay(String ucid, int likeNum) {
        this.ucid = ucid;
        this.likeNum = likeNum;
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
}

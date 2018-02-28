package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;

/**
 * 连续点赞用的话题对象
 * Created by liujiancheng on 15/12/30.
 */
public class LikeTopic extends Entity {
    /**
     * 话题id
     */
    private String topicid;

    /**
     * 话题增加的点赞数量
     */
    private int likeNum;

    public LikeTopic(String topicid, int likeNum) {
        this.topicid = topicid;
        this.likeNum = likeNum;
    }

    public String getTopicid() {
        return topicid;
    }

    public void setTopicid(String topicid) {
        this.topicid = topicid;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
}

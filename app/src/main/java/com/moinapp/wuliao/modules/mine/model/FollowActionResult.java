package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 关注／取消关注用户结果
 * Created by liujiancheng on 15/9/10.
 */
public class FollowActionResult extends BaseHttpResponse {
    private String userid;
    private int relation;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }
}

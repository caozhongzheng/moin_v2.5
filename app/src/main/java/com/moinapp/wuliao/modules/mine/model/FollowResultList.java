package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/** 关注／取消关注用户结果列表
 * Created by liujiancheng on 15/9/10.
 */
public class FollowResultList extends BaseHttpResponse{
    private List<FollowActionResult> follow;

    public List<FollowActionResult> getFollow() {
        return follow;
    }

    public void setFollow(List<FollowActionResult> follow) {
        this.follow = follow;
    }
}

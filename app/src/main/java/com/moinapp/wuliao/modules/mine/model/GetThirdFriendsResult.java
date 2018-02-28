package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * 获取我的三方好友列表结果类
 * Created by liujiancheng on 15/9/7.
 */
public class GetThirdFriendsResult extends BaseHttpResponse {
    private List<UserInfo> friends;
    private List<UserInfo> invites;

    public List<UserInfo> getFriends() {
        return friends;
    }

    public void setFriends(List<UserInfo> list) {
        this.friends = list;
    }

    public List<UserInfo> getInvites() {
        return invites;
    }

    public void setInvites(List<UserInfo> invites) {
        this.invites = invites;
    }
}

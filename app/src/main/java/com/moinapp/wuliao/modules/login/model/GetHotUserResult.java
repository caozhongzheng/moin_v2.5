package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 热门用户
 * Created by liujiancheng on 15/9/11.
 */
public class GetHotUserResult extends BaseHttpResponse {
    private List<HotUser> users;

    public List<HotUser> getUsers() {
        return users;
    }

    public void setUsers(List<HotUser> users) {
        this.users = users;
    }


}

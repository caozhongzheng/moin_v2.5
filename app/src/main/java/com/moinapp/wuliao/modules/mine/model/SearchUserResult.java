package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * 获取我的粉丝列表结果类
 * Created by liujiancheng on 15/9/7.
 */
public class SearchUserResult extends BaseHttpResponse {
    private List<UserInfo> users;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}

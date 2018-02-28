package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class GetLikeListResult extends BaseHttpResponse {
    private List<UserInfo> users;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}

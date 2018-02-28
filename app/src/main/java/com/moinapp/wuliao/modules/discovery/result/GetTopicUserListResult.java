package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;

import java.util.List;

/**
 * Created by liujiancheng on 16/2/25.
 */
public class GetTopicUserListResult extends BaseHttpResponse {
    private List<UserInfo> users;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}

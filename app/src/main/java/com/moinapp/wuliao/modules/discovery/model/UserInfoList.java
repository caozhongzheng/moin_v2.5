package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/** 用户列表
 * Created by guyunfei on 16/2/29.16:33.
 */
public class UserInfoList extends Entity implements ListEntity<UserInfo> {

    private List<UserInfo> users;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    @Override
    public List<UserInfo> getList() {
        return users;
    }
}

package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;
import java.util.List;

/**
 * 推荐用户列表
 * Created by liujiancheng on 15/12/31.
 */
@SuppressWarnings("serial")
public class HotUserList extends Entity implements ListEntity<HotUser> {

    private List<HotUser> users;

    public List<HotUser> getHotUsers() {
        return users;
    }

    public void setHotUsers(List<HotUser> users) {
        this.users = users;
    }

    @Override
    public List<HotUser> getList() {
        return users;
    }
}


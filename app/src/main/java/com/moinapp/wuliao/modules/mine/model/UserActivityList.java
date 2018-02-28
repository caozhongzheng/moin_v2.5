package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 用户的动态列表
 * Created by liujiancheng on 15/12/31.
 */
@SuppressWarnings("serial")
public class UserActivityList extends Entity implements ListEntity<UserActivity> {

    private List<UserActivity> list;

    public List<UserActivity> getUserActivity() {
        return list;
    }

    public void setUserActivity(List<UserActivity> activities) {
        this.list = activities;
    }

    @Override
    public List<UserActivity> getList() {
        return list;
    }
}


package com.moinapp.wuliao.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/9/9.
 */
@SuppressWarnings("serial")
public class FollowersList extends Entity implements ListEntity<UserInfo> {

    private List<UserInfo> idol;

    public List<UserInfo> getFollowerlist() {
        return idol;
    }

    public void setFollowerlist(List<UserInfo> followerlist) {
        this.idol = followerlist;
    }

    @Override
    public List<UserInfo> getList() {
        return idol;
    }
}


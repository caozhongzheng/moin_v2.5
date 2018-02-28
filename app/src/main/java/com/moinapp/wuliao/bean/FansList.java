package com.moinapp.wuliao.bean;

import java.util.List;

/**
 * Created by liujiancheng on 15/9/9.
 */
@SuppressWarnings("serial")
public class FansList extends Entity implements ListEntity<UserInfo> {

    private List<UserInfo> fans;

    public List<UserInfo> getFans() {
        return fans;
    }

    public void setFans(List<UserInfo> fans) {
        this.fans = fans;
    }

    @Override
    public List<UserInfo> getList() {
        return fans;
    }
}


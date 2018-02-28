package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;

import java.util.List;

/**
 * 推荐用户信息和ta的作品集
 * Created by liujiancheng on 15/12/30.
 */
public class HotUser extends Entity {
    private UserInfo user;
    private List<CosplayInfo> userCosplays;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public List<CosplayInfo> getUserCosplays() {
        return userCosplays;
    }

    public void setUserCosplays(List<CosplayInfo> userCosplays) {
        this.userCosplays = userCosplays;
    }
}

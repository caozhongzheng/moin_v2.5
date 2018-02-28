package com.moinapp.wuliao.bean;

/**
 * Created by moying on 16/4/8.
 */
public class AliasEvent {
    private UserInfo userInfo;

    public AliasEvent() {
    }

    public AliasEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "AliasEvent{" +
                "userInfo=" + userInfo +
                '}';
    }
}

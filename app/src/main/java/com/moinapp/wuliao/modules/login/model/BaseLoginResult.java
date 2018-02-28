package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

/**
 * 登录接口返回的基本数据，目前登录请求，注册请求和手机找回密码请求均用此结果类
 * Created by liujiancheng on 15/5/11.
 */
public class BaseLoginResult extends BaseHttpResponse {
    private String uid;
    private String passport;
    private UserInfo user;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPassport() {
        return this.passport;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}

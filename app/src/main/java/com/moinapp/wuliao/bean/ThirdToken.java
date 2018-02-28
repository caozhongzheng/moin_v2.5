package com.moinapp.wuliao.bean;

/**
 * Created by liujiancheng on 16/4/7.
 * 第三方登陆平台用户的token对象
 */
public class ThirdToken {
    private String access_token;
    private String refresh_token;
    private String refresh_token_expires;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token_expires() {
        return refresh_token_expires;
    }

    public void setRefresh_token_expires(String refresh_token_expires) {
        this.refresh_token_expires = refresh_token_expires;
    }
}

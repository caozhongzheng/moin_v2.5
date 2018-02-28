package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.bean.Entity;

/**
 * 第三方登录用户token等信息
 * Created by liujiancheng on 15/11/17.
 */
public class ThirdInfo extends Entity{
    public String access_token;
    public String refresh_token;
    public String openid;
    public long expires_in;
    public long refresh_token_expires;
    public String unionid;
    public String uid;
}

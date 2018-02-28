package com.moinapp.wuliao.modules.login.model;

/**
 * 第三方登录用户token等信息
 * @name 微信,QQ和微博等平台名
 * Created by liujiancheng on 15/11/17.
 */
public class ThridToken {
    private String name;
    private ThirdInfo info;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setInfo(ThirdInfo info) {
        this.info = info;
    }

    public ThirdInfo getInfo() {
        return this.info;
    }
}

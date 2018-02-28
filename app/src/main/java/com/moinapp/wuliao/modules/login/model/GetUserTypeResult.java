package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * Created by liujiancheng on 15/9/7.
 * type: 1 常规用户 2 第三方用户；
 * namelist：第三方账户的名称数组集合，如[qq, weichat, sina] 表示同时绑定了QQ、微信和微博三个第三方平台。
 */
public class GetUserTypeResult extends BaseHttpResponse {
    private int type;
    private List<String> namelist;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getNamelist() {
        return namelist;
    }

    public void setNamelist(List<String> namelist) {
        this.namelist = namelist;
    }
}

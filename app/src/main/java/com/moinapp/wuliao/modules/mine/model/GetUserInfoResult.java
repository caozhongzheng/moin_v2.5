package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

/**
 * Created by liujiancheng on 15/9/7.
 * 获取用户信息返回的结果类
 */
public class GetUserInfoResult extends BaseHttpResponse {
    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}

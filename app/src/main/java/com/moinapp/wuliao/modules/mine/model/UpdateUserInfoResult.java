package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

/**
 * 更新用户信息的结果类，更新成功会返回用户的全部信息
 * Created by liujiancheng on 15/9/7.
 */
public class UpdateUserInfoResult extends BaseHttpResponse {
    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}

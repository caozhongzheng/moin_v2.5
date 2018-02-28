package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * 获取我的粉丝列表结果类
 * Created by liujiancheng on 15/9/7.
 */
public class GetMyFansResult extends BaseHttpResponse {
    private List<UserInfo> fans;

    public List<UserInfo> getFans() {
        return fans;
    }

    public void setFans(List<UserInfo> fans) {
        this.fans = fans;
    }
}

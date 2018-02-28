package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 获取用户消息/动态的http结果类
 * Created by liujiancheng on 16/1/14.
 */
public class GetUserActivityResult extends BaseHttpResponse {
    private List<UserActivity> list;

    public List<UserActivity> getList() {
        return list;
    }

    public void setList(List<UserActivity> list) {
        this.list = list;
    }
}

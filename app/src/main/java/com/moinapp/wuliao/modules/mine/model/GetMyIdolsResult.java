package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * 获取我关注的人列表结果类
 * Created by liujiancheng on 15/9/7.
 */
public class GetMyIdolsResult extends BaseHttpResponse {
    private List<UserInfo> idol;

    public List<UserInfo> getIdol() {
        return idol;
    }

    public void setIdol(List<UserInfo> idol) {
        this.idol = idol;
    }
}

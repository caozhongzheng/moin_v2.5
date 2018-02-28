package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * 分享接口的结果类
 * Created by liujiancheng on 16/7/20.
 */
public class GetMoinBeanResult extends BaseHttpResponse {
    private MoinBean moinBean;
    private List<UserInfo> rankList;

    public MoinBean getMoinBean() {
        return moinBean;
    }

    public void setMoinBean(MoinBean moinbean) {
        this.moinBean = moinbean;
    }

    public List<UserInfo> getRankList() {
        return rankList;
    }

    public void setRankList(List<UserInfo> rankList) {
        this.rankList = rankList;
    }
}

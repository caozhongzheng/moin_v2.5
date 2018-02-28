package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.MoinBean;

/**
 * 分享接口的结果类
 * Created by liujiancheng on 16/7/20.
 */
public class ShareResult extends BaseHttpResponse {
    private MoinBean moinBean;

    public MoinBean getMoinBean() {
        return moinBean;
    }

    public void setMoinBean(MoinBean moinbean) {
        this.moinBean = moinbean;
    }
}

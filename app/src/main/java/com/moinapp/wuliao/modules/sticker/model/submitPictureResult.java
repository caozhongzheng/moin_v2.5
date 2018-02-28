package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.MoinBean;

/**
 * 大咖秀和帖子发布结果类
 * Created by liujiancheng on 15/9/18.
 */
public class submitPictureResult extends BaseHttpResponse {
    private String ucid;
    private MoinBean moinBean;

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public MoinBean getMoinBean() {
        return moinBean;
    }

    public void setMoinBean(MoinBean moinbean) {
        this.moinBean = moinbean;
    }
}

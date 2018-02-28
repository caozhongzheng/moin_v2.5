package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.MoinBean;

/**
 * Created by liujiancheng on 15/10/9.
 * 服务器返回的评论结果
 */
public class CommentResult extends BaseHttpResponse {
    private String cid;
    private MoinBean moinBean;//评论成功后获得的魔豆对象

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public MoinBean getMoinBean() {
        return moinBean;
    }

    public void setMoinBean(MoinBean moinbean) {
        this.moinBean = moinbean;
    }
}

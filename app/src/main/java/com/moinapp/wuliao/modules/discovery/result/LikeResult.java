package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.MoinBean;

/**
 * Created by liujiancheng on 16/7/20.
 * 服务器返回的点赞结果
 */
public class LikeResult extends BaseHttpResponse {
    private MoinBean moinBean;//评论成功后获得的魔豆对象

    public MoinBean getMoinBean() {
        return moinBean;
    }

    public void setMoinBean(MoinBean moinbean) {
        this.moinBean = moinbean;
    }
}

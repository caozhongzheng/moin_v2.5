package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * Created by liujiancheng on 15/10/9.
 * 服务器返回的转发结果
 */
public class ForwardResult extends BaseHttpResponse {
    private String ucid;

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }
}

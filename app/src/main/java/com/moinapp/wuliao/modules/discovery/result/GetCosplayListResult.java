package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 * 服务器返回的图片列表结果
 */
public class GetCosplayListResult extends BaseHttpResponse {
    private List<CosplayInfo> cosplayList;

    public List<CosplayInfo> getCosplayList() {
        return cosplayList;
    }

    public void setCosplayList(List<CosplayInfo> cosplayList) {
        this.cosplayList = cosplayList;
    }
}

package com.moinapp.wuliao.modules.discovery.result;

import com.google.gson.annotations.SerializedName;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/10.
 * 获取发现频道(首页)的图片列表结果类
 */
public class GetDiscoveryItemResult extends BaseHttpResponse {
    private List<CosplayInfo> list;

    public List<CosplayInfo> getList() {
        return list;
    }

    public void setList(List<CosplayInfo> list) {
        this.list = list;
    }
}

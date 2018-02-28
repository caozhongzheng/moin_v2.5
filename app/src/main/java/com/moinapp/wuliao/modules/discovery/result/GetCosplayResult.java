package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 * 服务器返回的图片详情结果
 */
public class GetCosplayResult extends BaseHttpResponse {
    private CosplayInfo cosplay;
    private List<CosplayInfo> cosplayList;//增加cosplayList 猜你喜欢图片列表
    public CosplayInfo getCosplay() {
        return cosplay;
    }

    public void setCosplay(CosplayInfo cosplay) {
        this.cosplay = cosplay;
    }

    public List<CosplayInfo> getCosplayList() {
        return cosplayList;
    }

    public void setCosplayList(List<CosplayInfo> cosplayList) {
        this.cosplayList = cosplayList;
    }
}

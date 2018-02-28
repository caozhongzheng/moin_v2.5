package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;

import java.util.List;

/**
 * Created by liujiancheng on 16/6/15.
 */
public class GetTopicCosplayResult extends BaseHttpResponse {
    private List<CosplayInfo> postList;

    public List<CosplayInfo> getPostList() {
        return postList;
    }

    public void setPostList(List<CosplayInfo> list) {
        this.postList = list;
    }
}

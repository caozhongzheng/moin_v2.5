package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;

import java.util.List;

/**
 * 热词列表结果类
 * Created by liujiancheng on 15/9/17.
 */
public class GetHotListResult extends BaseHttpResponse {
    private List<TagInfo> list;

    public List<TagInfo> getHotList() {
        return list;
    }

    public void setHotList(List<TagInfo> tagList) {
        this.list = tagList;
    }
}

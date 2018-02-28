package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class GetTagListResult extends BaseHttpResponse {
    private List<TagInfo> tags;

    public List<TagInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagInfo> tags) {
        this.tags = tags;
    }
}

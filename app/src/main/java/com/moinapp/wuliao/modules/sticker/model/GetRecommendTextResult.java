package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 请求获取话题推荐文字的结果
 */
public class GetRecommendTextResult extends BaseHttpResponse {
    private List<String> textList;

    public List<String> getTextList() {
        return textList;
    }

    public void setTextList(List<String> textList) {
        this.textList = textList;
    }
}

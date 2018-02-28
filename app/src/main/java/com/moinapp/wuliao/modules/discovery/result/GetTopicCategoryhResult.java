package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.sticker.model.FolderInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 * 服务器返回的评论结果
 */
public class GetTopicCategoryhResult extends BaseHttpResponse {
    private List<FolderInfo> categorys;

    public List<FolderInfo> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<FolderInfo> categorys) {
        this.categorys = categorys;
    }
}

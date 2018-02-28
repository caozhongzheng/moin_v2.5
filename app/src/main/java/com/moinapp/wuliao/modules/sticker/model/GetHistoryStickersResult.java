package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 我的历史贴纸列表结果类
 * Created by liujiancheng on 15/9/17.
 */
public class GetHistoryStickersResult extends BaseHttpResponse {
    private List<StickerInfo> stickerList;

    public List<StickerInfo> getStickerList() {
        return stickerList;
    }

    public void setStickerList(List<StickerInfo> stickerList) {
        this.stickerList = stickerList;
    }
}

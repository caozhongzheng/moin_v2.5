package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 获取贴纸包的列表结果[我的贴纸用]
 * Created by liujiancheng on 15/9/17.
 */
public class GetStickerListResult extends BaseHttpResponse {
    private List<StickerPackage> stickerList;

    public List<StickerPackage> getStickerList() {
        return stickerList;
    }

    public void setStickerList(List<StickerPackage> stickerList) {
        this.stickerList = stickerList;
    }
}

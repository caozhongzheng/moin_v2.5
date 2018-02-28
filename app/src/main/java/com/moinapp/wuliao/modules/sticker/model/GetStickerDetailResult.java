package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 获取贴纸包详情的结果
 * Created by liujiancheng on 15/9/17.
 */
public class GetStickerDetailResult extends BaseHttpResponse {
//    private StickerPackage sticker;
    private StickerPackage stickerGroup;

    public StickerPackage getSticker() {
        return stickerGroup;
    }

    public void setSticker(StickerPackage sticker) {
        this.stickerGroup = sticker;
    }
}

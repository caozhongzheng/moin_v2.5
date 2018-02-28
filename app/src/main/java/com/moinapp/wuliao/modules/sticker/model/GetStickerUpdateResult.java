package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 获取内置贴纸更新包结果类
 * Created by liujiancheng on 15/9/18.
 */
public class GetStickerUpdateResult extends BaseHttpResponse {
    private StickerPackage sticker;

    public StickerPackage getSticker() {
        return sticker;
    }

    public void setSticker(StickerPackage sticker) {
        this.sticker = sticker;
    }
}

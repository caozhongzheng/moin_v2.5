package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 贴纸包下载结果类
 * Created by liujiancheng on 15/9/17.
 */
public class DownloadStickerResult extends BaseHttpResponse {
    private StickerPackage sticker;

    public StickerPackage getSticker() {
        return sticker;
    }

    public void setSticker(StickerPackage sticker) {
        this.sticker = sticker;
    }
}

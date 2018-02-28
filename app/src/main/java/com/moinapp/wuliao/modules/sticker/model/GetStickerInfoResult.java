package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 获取贴纸信息结果类
 * Created by liujiancheng on 15/9/17.
 */
public class GetStickerInfoResult extends BaseHttpResponse {
    private StickerInfo sticker;

    public StickerInfo getSticker() {
        return sticker;
    }

    public void setSticker(StickerInfo sticker) {
        this.sticker = sticker;
    }
}

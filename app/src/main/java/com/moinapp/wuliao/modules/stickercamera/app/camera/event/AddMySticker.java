package com.moinapp.wuliao.modules.stickercamera.app.camera.event;


import com.moinapp.wuliao.modules.sticker.model.StickerPackage;

/**
 * 使用贴纸时加入到我的贴纸列表
 */
public  class AddMySticker {
    private StickerPackage mSticker;

    public AddMySticker(StickerPackage sticker) {
        this.mSticker = sticker;
    }

    public StickerPackage getSticker() {
        return mSticker;
    }
}
package com.moinapp.wuliao.modules.stickercamera.app.model;

import com.moinapp.wuliao.modules.sticker.model.StickerProject;

/**
 * Created by liujiancheng on 15/9/22.
 * decode一个工程文件后parse出来的贴纸,滤镜和原图信息
 */
public class StickerDecode {
    private StickerProject sticker;
    private String imageFile;

    public StickerProject getSticker() {
        return sticker;
    }

    public void setSticker(StickerProject sticker) {
        this.sticker = sticker;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}

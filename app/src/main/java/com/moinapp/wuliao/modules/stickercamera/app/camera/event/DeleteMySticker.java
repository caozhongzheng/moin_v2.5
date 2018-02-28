package com.moinapp.wuliao.modules.stickercamera.app.camera.event;

/**
 * 删除我的贴纸包EVENT
 * Created by liujiancheng on 15/9/23.
 */
public class DeleteMySticker {
    private String mId;

    public DeleteMySticker(String id) {
        this.mId = id;
    }

    public String getmId() {
        return mId;
    }
}

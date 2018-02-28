package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 贴纸列表
 * Created by liujiancheng on 16/2/2.
 */
public class StickerInfoList extends Entity implements ListEntity<StickerInfo> {

    private List<StickerInfo> stickerList;

    public List<StickerInfo> getStickers() {
        return stickerList;
    }

    public void setStickers(List<StickerInfo> stickers) {
        this.stickerList = stickers;
    }

    @Override
    public List<StickerInfo> getList() {
        return stickerList;
    }
}

package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 贴纸商城内贴纸包列表结果类
 * Created by liujiancheng on 16/2/2.
 */
public class StickerGroupList extends Entity implements ListEntity<StickerPackage> {

    private List<StickerPackage> groupList;

    public List<StickerPackage> getStickers() {
        return groupList;
    }

    public void setStickers(List<StickerPackage> stickers) {
        this.groupList = stickers;
    }

    @Override
    public List<StickerPackage> getList() {
        return groupList;
    }
}

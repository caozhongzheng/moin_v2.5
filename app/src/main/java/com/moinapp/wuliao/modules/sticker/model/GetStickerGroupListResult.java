package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 贴纸商城特定分类下的贴纸包列表结果类
 * Created by liujiancheng on 15/9/17.
 */
public class GetStickerGroupListResult extends BaseHttpResponse {
    private List<StickerPackage> groupList;

    public List<StickerPackage> getStickerList() {
        return groupList;
    }

    public void setStickerList(List<StickerPackage> stickerList) {
        this.groupList = stickerList;
    }
}

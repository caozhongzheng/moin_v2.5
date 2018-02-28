package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 请求贴纸商城的分类的结果
 */
public class GetFolderResult extends BaseHttpResponse {
    private List<FolderInfo> folderList;

    public List<FolderInfo> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<FolderInfo> folderList) {
        this.folderList = folderList;
    }
}

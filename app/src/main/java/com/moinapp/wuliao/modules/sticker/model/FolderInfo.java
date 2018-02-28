package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;

import java.util.List;

/**
 * 贴纸商城的分类信息
 */
public class FolderInfo extends Entity {
    /**
     * 如
     * {name:'主题', type:'folder', list: [{name:'秦时明月', type: 'ip'}, {name: '美人鱼', type: 'ip'}]},
     * {name:'大咖', type:'folder', list: [name:'陈妍希', type: 'star', name:'王祖蓝', type: 'star']}
     */
    private String name;
    private String type;
    private List<TagInfo> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TagInfo> getList() {
        return list;
    }

    public void setList(List<TagInfo> list) {
        this.list = list;
    }
}

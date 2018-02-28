package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.util.StringUtil;

/**
 * 热门标签的信息,用于发现频道的标签列表展示
 * Created by liujiancheng on 15/10/9.
 */
public class TagInfo extends Entity {
    /**
     * 标签id
     */
    private String id;

    /**
     * 标签的icon
     */
    private BaseImage icon;

    /**
     * 名字
     */
    private String name;

    /**
     * 标签类型: 比如IP OP, bbs表示帖子的标签类型
     */
    private String type;

    /**
     * 图片数量(搜索用到)
     */
    private int picNum;

    private int isFollow;

    public String getTagId() {
        return id;
    }

    public void setTagId(String id) {
        this.id = id;
    }


    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    /**
     * 获取官方,精品,达人等类型的帖子标题
     * @return
     */
    public String getBbsName() {
        if (StringUtil.isNullOrEmpty(type)) {
            return null;
        }
        if ("bbs".equals(type.toLowerCase())) {
            return name;
        }

        return null;
    }

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

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public int getPicNum() {
        return picNum;
    }

    public void setPicNum(int picNum) {
        this.picNum = picNum;
    }

    @Override
    public String toString() {
        return "TagInfo{" +
                "id='" + id + '\'' +
                ", icon=" + icon +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", picNum=" + picNum +
                ", isFollow=" + isFollow +
                '}';
    }
}

package com.moinapp.wuliao.modules.mine.emoji;

import com.moinapp.wuliao.bean.BaseImage;

import java.io.Serializable;
import java.util.List;

/**
 * 单个表情的模型类
 * Created by liujiancheng on 15/5/22.
 */
public class EmojiInfo implements Serializable {
    private static final long serialVersionUID = 7689657161342714419L;

    private BaseImage icon;//缩略图
    private BaseImage picture;//表情大图
    private List<String> tags;//描述标签，如大笑、OK、打脸等
    private String parentid;//所属表情专辑的id
    private int id;//标识ID，和emojiResourceSet的id联合形成表情主键
    private int useStat;//使用次数
    private int type; //类型 0 系统表情 1 用户自定义表情(大咖秀)*/
    // TODO name

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getId() {
        return id;
    }

    public void setId(int _id) {
        this.id = _id;
    }

    public int getUseStat() {
        return useStat;
    }

    public void setUseStat(int useStat) {
        this.useStat = useStat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EmojiInfo{" +
                "icon=" + icon +
                ", picture=" + picture +
                ", tags=" + tags +
                ", parentid='" + parentid + '\'' +
                ", id=" + id +
                ", useStat=" + useStat +
                '}';
    }
}

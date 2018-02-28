package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;

/**
 * 贴纸包中的每一张贴纸信息
 * Created by liujiancheng on 15/9/16.
 */
public class StickerInfo extends Entity {
    private int id;
    private String parentid;

    /**
     * 每一张贴纸的唯一id, 2.7以后单张贴纸都有唯一标示了
     */
    private String stickerId;

    /**
     * icon是小图,给编辑界面展示用
     */
    private BaseImage icon;

    private String name;

    private int type;

    private StickerTextInfo info;

    /**
     *贴纸放置到舞台上的百分比，默认65；如果是100则表示放置到舞台上时充满屏幕
     */
    private int zoom;

    /**
     * picture是大图,给实际贴到图片上用
     */
    private BaseImage picture;

    /**
     *  引导图
     */
    private BaseImage samplePic;

    /**
     * 使用次数
     */
    private int useNum;

    /**
     * 作者
     */
    private String author;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getStickerId() {
        return stickerId;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public StickerTextInfo getInfo() {
        return info;
    }

    public void setInfo(StickerTextInfo info) {
        this.info = info;
    }

    public BaseImage getSamplePic() {
        return samplePic;
    }

    public void setSamplePic(BaseImage samplePic) {
        this.samplePic = samplePic;
    }

    public int getUseNum() {
        return useNum;
    }

    public void setUseNum(int useNum) {
        this.useNum = useNum;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StickerInfo{" +
                "id=" + id +
                ", parentid='" + parentid + '\'' +
                ", stickerId='" + stickerId + '\'' +
                ", icon=" + icon.toString() +
                ", type=" + type +
                ", zoom=" + zoom +
                ", picture=" + picture.toString() +
                (info != null ? ", info=" + info.toString() : "") +
                '}';
    }
}

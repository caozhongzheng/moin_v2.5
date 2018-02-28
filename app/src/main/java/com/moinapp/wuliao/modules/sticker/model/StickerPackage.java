package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.modules.mine.emoji.MoinPictures;

import java.util.List;

/**
 * 贴纸包的基本信息, 从贴纸商城下发的静态包, 在贴纸商城的贴纸列表中用到
 * Created by liujiancheng on 15/9/16.
 */
public class StickerPackage extends Entity{

    public final static int STICKER_NORMAL = 1;
    public final static int STICKER_INTIME = 2;
    public final static int STICKER_TEXT = 3;
    public final static int STICKER_SPECIAL = 4;
    public final static int STICKER_BUBBLE = 5;
    public final static int STICKER_FRAME = 6;

    private String id;//贴纸包的主键id
    private String name;//贴纸包名称
    private BaseImage icon;//贴纸包的icon
    private MoinPictures pics;//贴纸包图片
    private List<StickerInfo> stickers;
    private int stickerNum;//贴纸包的贴纸数量
    private String desc;//描述信息        
    private int size;//贴纸包大小字节数
    private long updatedAt;
    private String author;

    private int isDownload;

    public String getStickerPackageId() {
        return id;
    }

    public void setStickerPackage(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public int getStickerNum() {
        return stickerNum;
    }

    public void setStickerNum(int stickerNum) {
        this.stickerNum = stickerNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int isDownload() {
        return isDownload;
    }

    public void setIsDownload(int isDownload) {
        this.isDownload = isDownload;
    }

    public MoinPictures getPics() {
        return pics;
    }

    public void setPics(MoinPictures pics) {
        this.pics = pics;
    }

    public List<StickerInfo> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerInfo> stickers) {
        this.stickers = stickers;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "StickerPackage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon=" + icon +
                ", pics=" + pics +
//                ", stickers=" + stickers +
                ", stickerNum=" + stickerNum +
                ", desc='" + desc + '\'' +
                ", size=" + size +
                ", updatedAt=" + updatedAt +
                ", isDownload=" + isDownload +
                '}';
    }
}

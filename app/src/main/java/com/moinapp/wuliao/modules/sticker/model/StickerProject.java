package com.moinapp.wuliao.modules.sticker.model;

import java.io.Serializable;
import java.util.List;

/**
 * 图片编辑对应的工程文件，包括贴纸滤镜等信息
 * Created by liujiancheng on 15/9/16.
 */
public class StickerProject implements Serializable {
    /**
     * 图片所用贴纸的信息
     */
    private List<StickerEditInfo> stickers;

    /**
     * 图片所用的滤镜
     */
    private int filter;

    /**
     * 亮度, 实际的亮度(浮点数) * 100 后取整
     */
    private int brightness = 0;

    /**
     * 对比度, 实际的对比度(浮点数) * 100 后取整
     */
    private int contrast = 100;

    /**
     * 饱和度, 实际的饱和度(浮点数) * 100 后取整
     */
    private int saturation = 100;

    private int platform;//1:android 2:ios

    public List<StickerEditInfo> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerEditInfo> stickers) {
        this.stickers = stickers;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}

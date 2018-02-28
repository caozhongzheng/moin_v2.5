package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseImage;

import java.io.Serializable;

/**
 * 描述图片编辑画布上每一个贴纸的信息，包括旋转，缩放，位置等信息
 * Created by liujiancheng on 15/9/16.
 */
public class StickerEditInfo implements Serializable{
    private int id;
    private String parentid;
    private String stickerId;//2.7新加入的贴纸唯一id
    private BaseImage icon;// 贴纸对应的预览图对象，包括 width, height, uri, size
    private BaseImage picture; // 贴纸对应的大图对象，包括 width, height, uri, size
    private int x;// 贴纸锚点在X轴方向距离原点的距离万分比
    private int y;// 贴纸锚点在Y轴方向距离原点的距离万分比
    private int width;// 贴纸宽度所占图片编辑区域的万分比，比如2500
    private int height;// 贴纸高度所占图片编辑区域的万分比，比如2500
    private int rotaion;//旋转的角度，从0到359.
    private boolean mirrorH;//是否水平镜像
    private boolean mirrorV;//是否垂直镜像
    private boolean isLock;//是否锁定，锁定后则转发不能再编辑
    private int type;//默认为1，文本框为5, 声音为7, 彩色文字为8
    private int zoom;//贴纸放置到舞台上的百分比，默认65；如果是100则表示放置到舞台上时充满屏幕
    private StickerTextInfo info;//type为5的时候，增加的额外信息, 包括 text, width, height, x, y
    private StickerColorTextInfo ctInfo;//type为8的时候，增加的额外信息, 包括 text, color, typeface
    private StickerAudioInfo audio;//type为7的时候，增加的音频信息

    public int getId() {
        return id;
    }

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

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRotaion() {
        return rotaion;
    }

    public void setRotaion(int rotaion) {
        this.rotaion = rotaion;
    }

    public boolean isMirrorH() {
        return mirrorH;
    }

    public void setMirrorH(boolean mirrorH) {
        this.mirrorH = mirrorH;
    }

    public boolean isMirrorV() {
        return mirrorV;
    }

    public void setMirrorV(boolean mirrorV) {
        this.mirrorV = mirrorV;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public StickerTextInfo getInfo() {
        return info;
    }

    public void setInfo(StickerTextInfo info) {
        this.info = info;
    }

    public StickerColorTextInfo getCtInfo() {
        return ctInfo;
    }

    public void setCtInfo(StickerColorTextInfo ctInfo) {
        this.ctInfo = ctInfo;
    }

    public StickerAudioInfo getAudio() {
        return audio;
    }

    public void setAudio(StickerAudioInfo audio) {
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "StickerEditInfo{" +
                "type=" + type +
                ", id=" + id +
                ", parentid='" + parentid + '\'' +
                ", stickerId='" + stickerId + '\'' +
                ", zoom='" + zoom + '\'' +
                ", picture=" + picture +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", rotaion=" + rotaion +
                ", mirrorH=" + mirrorH +
                ", mirrorV=" + mirrorV +
                ", isLock=" + isLock + ", " +
                (getInfo() != null ? getInfo().toString() : "") +
                (getCtInfo() != null ? getCtInfo().toString() : "") +
                '}';
    }
}

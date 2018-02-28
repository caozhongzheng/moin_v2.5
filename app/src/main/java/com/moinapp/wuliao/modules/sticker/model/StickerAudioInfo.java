package com.moinapp.wuliao.modules.sticker.model;

import java.io.Serializable;

/**
 * 描述贴纸中的声音信息
 */
public class StickerAudioInfo implements Serializable {
    /**
     * 声音长度
     */
    private long length;

    /**
     * 声音文件大小
     */
    private long size;

    /**
     * 声音类型，如mp3, aam, dwg等
     */
    private String type;

    /*
     * 横坐标位置，声音贴纸锚点距离原点的万分比数字
    */
    private float x;

    /*
     * 纵坐标位置，声音贴纸锚点距离原点的万分比数字
     */
    private float y;

    /*
     * 宽度占屏幕的万分比数字
    */
    private float width;

    /*
     * 高度占屏幕的万分比数字
     */
    private float height;

    /**
     *  是否水平镜像
     */
    private boolean mirrorH;

    /**
     *  文件保存地址，以/audio开头的一个相对路径
     */
    private String uri;

    /**
     *  引导图
     */
    private String samplePic;

    /**
     *  文字描述信息,比如"滚!" "哈!哈!"
     */
    private String text;

    /**
     *  资源id
     */
    private String audioId;

    /**
     *  播放状态 1:播放 0未播放
     */
    private int playing;

    public StickerAudioInfo() {
    }

    public StickerAudioInfo(long length, long size, String type, int x, int y, boolean mirrorH,
                            String uri, String samplePic, String text, String audioId) {
        this.length = length;
        this.size = size;
        this.type = type;
        this.x = x;
        this.y = y;
        this.mirrorH = mirrorH;
        this.uri = uri;
        this.samplePic = samplePic;
        this.text = text;
        this.audioId = audioId;
    }

    public StickerAudioInfo(long length, String text, String uri,  String audioId) {
        this.length = length;
        this.uri = uri;
        this.text = text;
        this.audioId = audioId;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean getMirrorH() {
        return mirrorH;
    }

    public void setMirrorH(boolean mirrorH) {
        this.mirrorH = mirrorH;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSamplePic() {
        return samplePic;
    }

    public void setSamplePic(String samplePic) {
        this.samplePic = samplePic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public int getPlaying() {
        return playing;
    }

    public void setPlaying(int playing) {
        this.playing = playing;
    }

    @Override
    public String toString() {
        return "StickerAudioInfo{" +
                "length=" + length +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", uri='" + uri + '\'' +
                ", audioId='" + audioId + '\'' +
                '}';
    }
}

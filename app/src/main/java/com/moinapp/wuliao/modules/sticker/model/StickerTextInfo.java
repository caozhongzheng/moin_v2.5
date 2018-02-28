package com.moinapp.wuliao.modules.sticker.model;

import java.io.Serializable;

/**
 * 描述文字气泡类文本的信息
 * Created by liujiancheng on 15/9/16.
 */
public class StickerTextInfo implements Serializable {
    private String text;//气泡内的文字

//    private String textColor;//气泡内的文字颜色, FFffffff 两位Alpha和六位RGB的一个组合值.如果不足8位,判断是不是6位的,或者有可能是3位的rgb
//    private int textSize;//气泡内的文字大小, 单位是px, Paint所使用的

    // 气泡内文字显示区域,都是一个万分比的值
    /** x:矩形区域左上角相对于气泡整体在宽度上的一个位置万分比*/
    private int x;
    /** y:矩形区域左上角相对于气泡整体在高度上的一个位置万分比*/
    private int y;
    /** width:矩形区域相对于气泡整体宽度的万分比*/
    private int width;
    /** height:矩形区域相对于气泡整体高度的万分比*/
    private int height;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    @Override
    public String toString() {
        return "StickerTextInfo{" +
                "text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

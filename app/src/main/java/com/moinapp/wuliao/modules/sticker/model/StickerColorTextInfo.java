package com.moinapp.wuliao.modules.sticker.model;

/**
 * 描述彩色文字类文本,颜色,样式的信息
 * Created by moying on 16/4/26.
 */
public class StickerColorTextInfo extends StickerTextInfo {
    private String color;//彩色文字颜色 "#FFFFFFFF"
    private int typeface;//彩色文字样式 0,1,2分别代表预制的3中样式

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTypeface() {
        return typeface;
    }

    public void setTypeface(int typeface) {
        this.typeface = typeface;
    }

    @Override
    public String toString() {
        return "StickerColorTextInfo{" +
                "text='" + getText() + '\'' +
                "color='" + color + '\'' +
                ", typeface=" + typeface +
                '}';
    }
}

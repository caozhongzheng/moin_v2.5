package com.moinapp.wuliao.modules.stickercamera.app.camera.colortext;

import android.graphics.Typeface;

/**
 * 彩色文字样式,风格
 * Created by moying on 16/4/21.
 */
public class ColorTextStyle {
    Typeface tf; // 字体
    int style; // 字体的样式
    int flag; // 字体的风格

    public ColorTextStyle(Typeface tf, int style, int flag) {
        this.tf = tf;
        this.style = style;
        this.flag = flag;
    }

    public Typeface getTf() {
        return tf;
    }

    public void setTf(Typeface tf) {
        this.tf = tf;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "ColorTextStyle{" +
                "tf=" + tf +
                ", style=" + style +
                ", flag=" + flag +
                '}';
    }
}

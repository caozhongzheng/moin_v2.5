package com.moinapp.wuliao.modules.stickercamera.app.camera.colortext;

/**
 * 彩色字
 * Created by moying on 16/4/21.
 */
public class ColorTextColor {

    private int type; // 分组用
    private String value;// 颜色值 "#FFFFFFFF"
    private int bgDrw;// 背景drawable[shape]
    private int fgDrw;// 前景drawable[shape]
    private int fgAlphaDrw;// 前景半透明情况下的drawable[shape]
    private String name;// 颜色名字

    public ColorTextColor(int type, String value, int bgDrw, int fgDrw, int fgAlphaDrw, String name) {
        this.type = type;
        this.value = value;
        this.bgDrw = bgDrw;
        this.fgDrw = fgDrw;
        this.fgAlphaDrw = fgAlphaDrw;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getBgDrw() {
        return bgDrw;
    }

    public int getFgAlphaDrw() {
        return fgAlphaDrw;
    }

    public int getFgDrw() {
        return fgDrw;
    }

    public String getName() {
        return name;
    }
}

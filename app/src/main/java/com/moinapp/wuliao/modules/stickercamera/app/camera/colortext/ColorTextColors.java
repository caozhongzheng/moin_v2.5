package com.moinapp.wuliao.modules.stickercamera.app.camera.colortext;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 彩色字的颜色列表
 * Created by moying on 16/4/21.
 */
public enum ColorTextColors {

    Moin0(1, AppContext.getInstance().getString(R.string.color_text_color_0), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_0, R.drawable.shape_color_text_alpha0, "白"),
    Moin1(1, AppContext.getInstance().getString(R.string.color_text_color_1), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_1, R.drawable.shape_color_text_alpha1, "黑"),
    Moin2(1, AppContext.getInstance().getString(R.string.color_text_color_2), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_2, R.drawable.shape_color_text_alpha2, "红"),
    Moin3(1, AppContext.getInstance().getString(R.string.color_text_color_3), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_3, R.drawable.shape_color_text_alpha3, "绿"),
    Moin4(1, AppContext.getInstance().getString(R.string.color_text_color_4), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_4, R.drawable.shape_color_text_alpha4, "黄"),
    Moin5(1, AppContext.getInstance().getString(R.string.color_text_color_5), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_5, R.drawable.shape_color_text_alpha5, "蓝"),
    Moin6(1, AppContext.getInstance().getString(R.string.color_text_color_6), R.drawable.shape_color_text_bg0, R.drawable.shape_color_text_6, R.drawable.shape_color_text_alpha6, "Cyan");

    private int type; // 分组用
    private String value;// 颜色值 "#FFFFFFFF"
    private int bgDrw;// 背景drawable[shape]
    private int fgDrw;// 前景drawable[shape]
    private int fgAlphaDrw;// 前景半透明情况下的drawable[shape]
    private String name;// 颜色名字

    private ColorTextColors(int type, String value, int bgDrw, int fgDrw, int fgAlphaDrw, String name) {
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

    public int getFgDrw() {
        return fgDrw;
    }

    public int getFgAlphaDrw() {
        return fgAlphaDrw;
    }

    public String getName() {
        return name;
    }

    private static ColorTextColor getColorTextColor(ColorTextColors data) {
        return new ColorTextColor(data.getType(), data.getValue(), data.getBgDrw(), data.getFgDrw(), data.getFgAlphaDrw(), data.getName());
    }

    public static List<ColorTextColor> getAllColors() {
        List<ColorTextColor> datas = new ArrayList<ColorTextColor>();
        for (ColorTextColors data : values()) {
            datas.add(getColorTextColor(data));
        }
        return datas;
    }

    public static List<ColorTextColor> getColorsByType(int type) {
        List<ColorTextColor> datas = new ArrayList<ColorTextColor>(values().length);
        for (ColorTextColors data : values()) {
            if (data.getType() == type) {
                datas.add(getColorTextColor(data));
            }
        }
        return datas;
    }
}

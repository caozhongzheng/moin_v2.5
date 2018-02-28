package com.moinapp.wuliao.modules.stickercamera.app.camera.colortext;

import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 16/4/21.
 */
public class ColorTextUtils {

    private static ColorTextUtils mInstance;

    public static ColorTextUtils getInst() {
        if (mInstance == null) {
            synchronized (ColorTextUtils.class) {
                if (mInstance == null)
                    mInstance = new ColorTextUtils();
            }
        }
        return mInstance;
    }

    public List<ColorTextStyle> getLocalColorTextStyle() {
        List<ColorTextStyle> colorTextStyleList = new ArrayList<>();
        colorTextStyleList.add(new ColorTextStyle(Typeface.DEFAULT, Typeface.NORMAL, Paint.ANTI_ALIAS_FLAG));
        colorTextStyleList.add(new ColorTextStyle(Typeface.DEFAULT, Typeface.BOLD, Paint.ANTI_ALIAS_FLAG));
        colorTextStyleList.add(new ColorTextStyle(Typeface.DEFAULT, Typeface.BOLD, Paint.UNDERLINE_TEXT_FLAG));

        return colorTextStyleList;
    }

}

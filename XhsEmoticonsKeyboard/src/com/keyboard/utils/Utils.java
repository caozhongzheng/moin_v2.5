package com.keyboard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.keyboard.bean.EmoticonBean;

import java.io.File;

/**
 * Utils
 * @author zhongdaxia 2014-9-2 12:05:55
 */

public class Utils {

    private static final String EXTRA_ISINITDB= "ISINITDB";
    private static final String EXTRA_DEF_KEYBOARDHEIGHT = "DEF_KEYBOARDHEIGHT";
    public static final String STICKER_PIC_EXTENSION = ".s";
    /** 200表示话题贴纸包的类型*/
    public static final int STICKER_TYPE_TOPIC = 200;
    /** 100表示最近贴纸包的类型*/
    public static final int STICKER_TYPE_RECENTLY = 100;

    /** 键盘默认高度 (dp) */
    private static int sDefKeyboardHeight = 300;
    public static int sDefRow = 7;
    public static int sDefLine = 3;


    public static String STICK_FOLDER = "MOIN/.StickRes/";
    public static String BITMAP_STICKRES = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + STICK_FOLDER;


    public static boolean isInitDb(Context context) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(EXTRA_ISINITDB, false);
    }

    public static void setIsInitDb(Context context,boolean b) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putBoolean(EXTRA_ISINITDB, b).commit();
    }

    public static int getDefKeyboardHeight(Context context) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int height = settings.getInt(EXTRA_DEF_KEYBOARDHEIGHT, 0);
        if (height > 0 && sDefKeyboardHeight != height) {
            Utils.setDefKeyboardHeight(context,height);
        }
        return sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context,int height) {
        if(sDefKeyboardHeight != height){
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings.edit().putInt(EXTRA_DEF_KEYBOARDHEIGHT, height).commit();
        }
        Utils.sDefKeyboardHeight = height;
    }

    /** 屏幕宽度   */
    private static int DisplayWidthPixels = 0;
    /** 屏幕高度   */
    private static int DisplayheightPixels = 0;

    private static void getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        DisplayWidthPixels = dm.widthPixels;// 宽度
        DisplayheightPixels = dm.heightPixels;// 高度
    }

    public static int getDisplayWidthPixels(Context context) {
        if (context == null) {
            return -1;
        }
        if (DisplayWidthPixels == 0) {
            getDisplayMetrics(context);
        }
        return DisplayWidthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 开启软键盘
     */
    public static void openSoftKeyboard(EditText et) {
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }

    /**
     * 关闭软键盘
     */
    public static void closeSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /** 判断贴纸大图是否存在 */
    public static boolean isEmojiPicExist(EmoticonBean emoticonBean) {
        if (emoticonBean == null) return false;
        String picPath = getStickerPicPath(emoticonBean);
        if (picPath.isEmpty()) return false;
        File file = new File(picPath);
        return file.exists();
    }


    /**
     * 单张贴纸的大图的本地地址
     */
    public static String getStickerPicPath(EmoticonBean emoticonBean) {
        if (emoticonBean == null) {
            return "";
        }
        if (emoticonBean.getStickerId() != null && !emoticonBean.getStickerId().isEmpty()) {
            return BITMAP_STICKRES + emoticonBean.getStickerId() + STICKER_PIC_EXTENSION;
        }
        if (emoticonBean.getParentId() != null && !emoticonBean.getParentId().isEmpty()
                        && emoticonBean.getId() != null && !emoticonBean.getId().isEmpty()) {
            return BITMAP_STICKRES + emoticonBean.getParentId() + "/" + emoticonBean.getParentId() + "_" + emoticonBean.getId() + STICKER_PIC_EXTENSION;
        }
        return "";
    }

}

package com.moinapp.wuliao.util;

import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.modules.discovery.model.EmojiInfo;

/**
 * Created by moying on 15/7/13.
 */
public class EmojiUtils {
//    public static String getEmojiPath(String emojiResourceId) {
//        if(AppTools.existsSDCARD()) {
//            FileUtil.createFolder(BitmapUtil.BITMAP_STICKRES + emojiResourceId + File.separator);
//        }
//        return BitmapUtil.BITMAP_STICKRES + emojiResourceId + File.separator;
//    }


    /**
     * 表情专辑的本地文件夹地址
     */
    public static String getEmjSetFolder(String emojiResourceId) {
        if (StringUtil.isNullOrEmpty(emojiResourceId)) {
            return "";
        }
        return BitmapUtil.BITMAP_STICKRES + emojiResourceId + "/";
    }

    /**
     * 表情专辑ICON的本地地址
     */
    public static String getEmjSetPath(String emojiResourceId) {
        if (StringUtil.isNullOrEmpty(emojiResourceId)) {
            return "";
        }
        return BitmapUtil.BITMAP_EMOJI + emojiResourceId + "/" + emojiResourceId + Constants.JPG_EXTENSION;
    }


    /**
     * 表情专辑ICON的本地地址
     */
    public static String getEmjSetPath(EmojiInfo emoji) {
        if (emoji == null) {
            return null;
        }
        return BitmapUtil.BITMAP_EMOJI + emoji.getParentid() + "/" + emoji.getParentid() + Constants.JPG_EXTENSION;
    }


    /**
     * 表情ICON的本地地址:大图
     */
    public static String getEmjPath(EmojiInfo emoji) {
        if (emoji == null) {
            return null;
        }
        return BitmapUtil.BITMAP_EMOJI + emoji.getParentid() + "/" + emoji.getParentid() + "_" + emoji.getId() + Constants.GIF_EXTENSION;
    }


    /**
     * 表情缩略图的本地地址:小图
     */
    public static String getThumbPath(EmojiInfo emoji) {
        if (emoji == null) {
            return null;
        }
        return BitmapUtil.BITMAP_EMOJI + emoji.getParentid() + "/" + emoji.getParentid() + "_" + emoji.getId() + Constants.JPG_EXTENSION;
    }

    /**
     * 表情ICON的缩放大小
     *
     * @param width 表情ICON的宽度
     * @param height 表情ICON的高度
     * @param minWidth 表情ICON的显示最小宽度
     */
    public static int[] getScaleEmjHeight(int width, int height, int minWidth) {
        int[] params = new int[2];
        if(width >= minWidth) {
            params[0] = width;
            params[1] = height;
            return params;
        }
        float newheight = ((float) height * minWidth / width);
        params[0] = minWidth;
        params[1] = (int) newheight;
        return params;
    }



    /**
     * 图片的缩放大小
     *
     * @param width 图片的宽度
     * @param height 图片的高度
     * @param mPicMaxWidth 图片的显示最大宽度
     */
    public static int[] getScaleHeight(int width, int height, int mPicMaxWidth) {
        int[] params = new int[2];
        if(width <= mPicMaxWidth) {
            float scale = (float) mPicMaxWidth / (float) width;
            scale = scale > 2 ? 2 : scale;
            params[0] = (int) (width * scale);
            params[1] = (int) (height * scale);
            return params;
        }
        float newheight = ((float) height * mPicMaxWidth / width);
        params[0] = mPicMaxWidth;
        params[1] = (int) newheight;
        return params;
    }

}

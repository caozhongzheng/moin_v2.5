package com.moinapp.wuliao.util;

import com.moinapp.wuliao.AppContext;

public class DistanceUtil {

    /**这是一行4个，每个之间间隔2dp，然后每个之间又内部padding 4dp*/
    public static int getCameraAlbumWidth() {
        return (AppContext.getApp().getScreenWidth() - AppContext.getApp().dp2px(10)) / 4 - AppContext.getApp().dp2px(4);
    }

    /**相机照片列表高度计算 */
    public static int getCameraPhotoAreaHeight() {
        return getCameraPhotoWidth() + AppContext.getApp().dp2px(10);
    }
    /**相机照片列表单个宽度计算 一行有7个 */
    public static int getCameraPhotoWidth() {
//        return (AppContext.getApp().getScreenWidth() - AppContext.getApp().dp2px(14)) / 6;
        return AppContext.getApp().getScreenWidth() / 7 - AppContext.getApp().dp2px(2);
    }

    //活动标签页grid图片高度
    public static int getActivityHeight() {
        return (AppContext.getApp().getScreenWidth() - AppContext.getApp().dp2px(24)) / 3;
    }
}

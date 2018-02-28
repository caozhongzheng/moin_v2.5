package com.moinapp.wuliao.modules.stickercamera.app.camera.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.utils.Utils;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.model.StickerColorTextInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerEditInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.model.StickerTextInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoProcessActivity;
import com.moinapp.wuliao.modules.stickercamera.app.ui.Sticker;
import com.moinapp.wuliao.modules.stickercamera.base.BaseActivity;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.DisplayUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by liujiancheng on 15/9/23.
 */
public class StickerUtils {
    static ILogger MyLog = LoggerFactory.getLogger(StickerUtils.class.getSimpleName());
    public static final String STICKER_ICON_EXTENSION = ".i";
    public static final String STICKER_PIC_EXTENSION = Utils.STICKER_PIC_EXTENSION;

    public static String[] BUBBLE_TEXT = AppContext.getInstance().getString(R.string.bubble_text_arr).replaceAll(" ", "").split("\n");
//            {
//            "吧啦吧啦…",
//            "打死不说话",
//            "美妞儿,穿帮了",
//            "拖出去喂狗",
//            "7456",
//            "二硫碘化鉀",
//            "醬紫啊",
//            "吼吼",
//    };

    /**
     * 单张贴纸的ICON的本地地址
     */
    public static String getStickerIconPath(StickerInfo sticker) {
        if (sticker == null) {
            return null;
        }
        if (!StringUtil.isNullOrEmpty(sticker.getStickerId())) {
            return BitmapUtil.BITMAP_STICKRES + sticker.getStickerId() + STICKER_ICON_EXTENSION;
        }
        return BitmapUtil.BITMAP_STICKRES + sticker.getParentid() + "/" + sticker.getParentid() + "_" + sticker.getId() + STICKER_ICON_EXTENSION;
    }

    /**
     * 单张贴纸的ICON的本地地址
     */
    public static String getStickerIconPath(StickerEditInfo stickerEditInfo) {
        if (stickerEditInfo == null) {
            return null;
        }
        if (!StringUtil.isNullOrEmpty(stickerEditInfo.getStickerId())) {
            return BitmapUtil.BITMAP_STICKRES + stickerEditInfo.getStickerId() + STICKER_ICON_EXTENSION;
        }
        return BitmapUtil.BITMAP_STICKRES + stickerEditInfo.getParentid() + "/" + stickerEditInfo.getParentid() + "_" + stickerEditInfo.getId() + STICKER_ICON_EXTENSION;
    }

    /**
     * 单张贴纸的大图的本地地址
     */
    public static String getStickerPicPath(StickerInfo sticker) {
        if (sticker == null) {
            return null;
        }
        if (!StringUtil.isNullOrEmpty(sticker.getStickerId())) {
            return BitmapUtil.BITMAP_STICKRES + sticker.getStickerId() + STICKER_PIC_EXTENSION;
        }
        return BitmapUtil.BITMAP_STICKRES + sticker.getParentid() + "/" + sticker.getParentid() + "_" + sticker.getId() + STICKER_PIC_EXTENSION;
    }

    /**
     * 单张贴纸的大图的本地地址
     */
    public static String getStickerPicPath(StickerEditInfo stickerEditInfo) {
        if (stickerEditInfo == null) {
            return null;
        }
        if (!StringUtil.isNullOrEmpty(stickerEditInfo.getStickerId())) {
            return BitmapUtil.BITMAP_STICKRES + stickerEditInfo.getStickerId() + STICKER_PIC_EXTENSION;
        }
        return BitmapUtil.BITMAP_STICKRES + stickerEditInfo.getParentid() + "/" + stickerEditInfo.getParentid() + "_" + stickerEditInfo.getId() + STICKER_PIC_EXTENSION;
    }

    /**
     * 贴纸包的ICON的本地地址
     */
    public static String getStickePackagerIconPath(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        return BitmapUtil.STICK_PACKAGE_FOLDER + id + STICKER_ICON_EXTENSION;
//        return BitmapUtil.BITMAP_STICKRES + id + "/" + id + STICKER_ICON_EXTENSION;
    }

    /**
     * 贴纸包的ICON的本地地址
     */
    public static String getStickePackagerIconPath(int type) {
        if (type < 2) {
            return null;
        }
        return BitmapUtil.STICK_PACKAGE_FOLDER + type + STICKER_ICON_EXTENSION;
    }

    /**
     * 贴纸是否已经下载, 简单的判断本地大图是否存在即可
     */
    public static boolean isStickerDownload(StickerInfo stickerInfo) {
        String path = getStickerPicPath(stickerInfo);
        if (path.isEmpty()) return false;

        return new File(path).exists();
    }

    /**获取单张贴纸icon最大的大小*/
    public static String getSingleStickerIconUrl(String url) {
        return StringUtil.nullToEmpty(url);
//        if(StringUtil.isNullOrEmpty(url)) {
//            return "";
//        }
//        int screenHeight = AppContext.getInstance().getScreenHeight();
//        int screenWidth = AppContext.getInstance().getScreenWidth();
//        Resources res = AppContext.getInstance().getResources();
//        int maxPagerHeight = screenHeight
//                - screenWidth
//                - res.getDimensionPixelSize(R.dimen.height_100px)
//                - res.getDimensionPixelSize(R.dimen.bar_height) * 2;
//        int itemHeight = Math.min(screenWidth / 4, maxPagerHeight / 2);
//        return ImageLoaderUtils.buildNewUrl(url, new ImageSize(itemHeight, itemHeight));
    }

    /**获取单张贴纸大图picture最大的大小*/
    public static String getSingleStickerPictureUrl(String url) {
        return StringUtil.nullToEmpty(url);
//        if(StringUtil.isNullOrEmpty(url)) {
//            return "";
//        }
//        int screenWidth = AppContext.getInstance().getScreenWidth();
//        return ImageLoaderUtils.buildNewUrl(url, new ImageSize(screenWidth, screenWidth));
    }

    /**获取贴纸包icon*/
    public static String getStickerPackageIconUrl(String url) {
        return StringUtil.nullToEmpty(url);
//        if(StringUtil.isNullOrEmpty(url)) {
//            return "";
//        }
//        int iv_icon_width = AppContext.context().getResources().getDimensionPixelSize(com.keyboard.view.R.dimen.bar_width);
//        return ImageLoaderUtils.buildNewUrl(url, new ImageSize(iv_icon_width, iv_icon_width));
    }


    /** 画贴纸的文字气泡本质就是在一个矩形区域内画一段文字,*/
    public static void drawStickText(Canvas canvas, Sticker sticker) {
        StickerTextInfo textInfo = sticker.getStickerTextInfo();
        if(textInfo == null) {
            return;
        }
        MyLog.i("画贴纸的文字气泡 StickerTextInfo[" + textInfo.toString() + "]");
        int bmpWidth = StickerUtils.getStickerTextBmpWidth(sticker);
        int bmpHeight = StickerUtils.getStickerTextBmpHeight(sticker);
        // 获取画图的起点,xy
        float bmpX = sticker.getMapPointsDst()[0];
        float bmpY = sticker.getMapPointsDst()[1];
        MyLog.i("画贴纸的文字气泡 画图的起点,bmpXY[" + bmpX + "*" + bmpY + "]");
        MyLog.i("画贴纸的文字气泡 画图的宽高,bmpWH[" + bmpWidth + "*" + bmpHeight + "]");
        // 获取气泡内文字的起点,x
        int marginLeft = 0;
        if(sticker.isMirrorH()) {
            marginLeft = bmpWidth * (10000 - textInfo.getWidth() - textInfo.getX()) / 10000;
        } else {
            marginLeft = bmpWidth * textInfo.getX() / 10000;
        }
        int x = (int) bmpX + marginLeft;
        MyLog.i("画贴纸的文字气泡 气泡内文字的起点,x[" + x + ", marginLeft=" + marginLeft + "]");
        // 获取气泡内文字的起点,y
        int marginTop = 0;
        if(sticker.isMirrorV()) {
            marginTop = bmpHeight * (10000 - textInfo.getHeight() - textInfo.getY()) / 10000;
        } else {
            marginTop = bmpHeight * textInfo.getY() / 10000;
        }
        int y = (int) bmpY + marginTop;
        MyLog.i("画贴纸的文字气泡 气泡内文字的起点,y[" + y + ", marginTop=" + marginTop + "]");
        int width = bmpWidth * textInfo.getWidth() / 10000;
        int height = bmpHeight * textInfo.getHeight() / 10000;
        MyLog.i("画贴纸的文字气泡 气泡内区域大小[" + width + "*" + height + "]");

        drawRectTxt(canvas, sticker,
                new Rect(x, y, x + width, y + height));
    }

    /**
     * 在一个矩形区域内画一段文字
     *
     * */
    //@param needScale 当矩形区域填充不了文字的时候,矩形区域是否需要进行按比例缩放
    public static void drawRectTxt(Canvas canvas, Sticker sticker, Rect targetRect) {
        StickerTextInfo textInfo = sticker.getStickerTextInfo();
        if(textInfo == null) {
            return;
        }
        ArrayList<BubbleText> bubbleTextList = sticker.getBubbleTextList();
        if(bubbleTextList == null || bubbleTextList.isEmpty()) {
            return;
        }
        MyLog.i("准备画气泡文字string[" + textInfo.getText() + "]");
        MyLog.i("准备画气泡文字 Rect [" + targetRect.toShortString() + "], width=" + targetRect.width() + ", height=" + targetRect.height());
        Paint paint = getPaint();

//        paint.setColor(Color.CYAN);
//        canvas.drawRect(targetRect, paint);

        paint.setColor(Color.BLACK);
        // 原理就是计算后的文字能够适当填充矩形区域
        int x = targetRect.left;
        int y = targetRect.top;
        // 矩形区域的宽度
        int rect_width = targetRect.right - targetRect.left;
        int rect_height = targetRect.bottom - targetRect.top;

        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        int margin = DisplayUtil.dip2px(BaseApplication.context(), 2);
        if(bubbleTextList.size() == 1) {
            // 一行就能显示完, 需要进行居中显示
            MyLog.i("一行就能显示完 ");
            int left = x + ((rect_width - bubbleTextList.get(0).getWidth()) / 2);
            int fontHeight = fmi.descent - fmi.ascent;
            int bottom = y + fontHeight + ((rect_height - fontHeight) / 2) - margin; // 此处还是以baseline来进行画
//            int bottom = y + rect_height - ((rect_height - (fontHeight)) / 2); // 此处还是以baseline来进行画
            MyLog.i(bubbleTextList.get(0).getUsername() + " 's fontHeight = " + fontHeight);
            if(!StringUtil.isNullOrEmpty(textInfo.getText())) {
                canvas.drawText(bubbleTextList.get(0).getUsername(), left, bottom, paint);
            }
            MyLog.i("left = " + left);
            MyLog.i("bottom = " + bottom);
        } else {
            // 一行画不完的情况下,分行画
            MyLog.i("print文字串 开始------------");
            int totalHeight = 0;
            int rowCount = 0;
            for(BubbleText bt : bubbleTextList) {
                totalHeight += bt.getHeight();
                rowCount++;
            }
            // 垂直方向上居中
            int padding, padding_half = 0;
            if(rect_height > totalHeight) {
                padding = (rect_height - totalHeight) / rowCount;
                padding_half = padding / 2;
            } else {
                padding = (rect_height - totalHeight) / 2;
            }
//            MyLog.i("有"+rowCount+"行,每行"+35+",一共"+totalHeight+"高度,rect_height="+rect_height+", padding="+padding+",padding_half="+padding_half);
            int height = 0;
            for (int i = 0; i < bubbleTextList.size(); i++) {
                BubbleText bt = bubbleTextList.get(i);
                MyLog.i(bt.toString());
                height += bt.getHeight();

                // 水平方向上居中
                int left = x + ((rect_width - bt.getWidth()) / 2);
                int bottom = y + height + padding * i + padding_half - margin;
                if(!StringUtil.isNullOrEmpty(textInfo.getText())) {
//                    if (i == 0) {
//                        paint.setColor(Color.YELLOW);
//                        canvas.drawRect(left, bottom - bt.getHeight(), left + bt.getWidth(), bottom + margin, paint);
//                    }
                    paint.setColor(Color.BLACK);
                    if(rect_height > totalHeight) {
                        canvas.drawText(bt.getUsername(), left, bottom, paint);
                    } else {
                        canvas.drawText(bt.getUsername(), left, y + height + (i == 0 ? padding: 0), paint);
                    }
                }
            }
            MyLog.i("print获取文字串 END------------height=" + height);
        }
    }

    /**
     * 在一个矩形区域内画一段文字
     *
     * */
    //@param needScale 当矩形区域填充不了文字的时候,矩形区域是否需要进行按比例缩放
    public static void drawRectTxt(Canvas canvas, String textString, Rect targetRect) {
        MyLog.i("准备画气泡文字string[" + textString + "]");
        MyLog.i("准备画气泡文字 Rect [" + targetRect.toShortString() + "], width=" + targetRect.width() + ", height=" + targetRect.height());
        Paint paint = getPaint();

        paint.setColor(Color.CYAN);
        canvas.drawRect(targetRect, paint);

        paint.setColor(Color.BLACK);
        // 原理就是计算后的文字能够适当填充矩形区域
        int x = targetRect.left;
        int y = targetRect.top;
        // 矩形区域的宽度
        int rect_width = targetRect.right - targetRect.left;
        int rect_height = targetRect.bottom - targetRect.top;

        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();


        // 获取一段文字的长度
        int textLen = getLenByPaint(textString, paint);

        if(textLen <= rect_width) {
            // 一行就能显示完, 需要进行居中显示
            MyLog.i("一行就能显示完 ");
            int left = x + ((rect_width - textLen) / 2);
            int fontHeight = fmi.descent - fmi.ascent;
            int bottom = y + fontHeight + ((rect_height - fontHeight) / 2); // 此处还是以baseline来进行画
//            int bottom = y + rect_height - ((rect_height - (fontHeight)) / 2); // 此处还是以baseline来进行画
            MyLog.i(textString + " 's fontHeight = " + fontHeight);
            if(!StringUtil.isNullOrEmpty(textString)) {
                canvas.drawText(textString, left, bottom, paint);
            }
            MyLog.i("left = " + left);
            MyLog.i("bottom = " + bottom);
        } else {
            // 一行画不完的情况下,分行画
            // 原理就是按照2的倍数去查找,或者每次递增上次行数的一半, 又或者每次递增2行??
            // 获取时,按照历次的计算时的最大长度的字符串长度给一个参考 sunstringLength, 如果不行,可以进行减少.

            // 第几行,这行的长度[可以没有],这行的高度,这行的字符串. 每次可以提供上一个截取的字符串的长度作为一个参考值
            List<BubbleText> bubbleTextList = getBubbleTextList(textString, rect_width, rect_height, paint);
            MyLog.i("print文字串 开始------------");
            int totalHeight = 0;
            int rowCount = 0;
            for(BubbleText bt : bubbleTextList) {
                totalHeight += bt.getHeight();
                rowCount++;
            }
            // 垂直方向上居中
            int padding, padding_half = 0;
            if(rect_height > totalHeight) {
                padding = (rect_height - totalHeight) / rowCount;
                padding_half = padding / 2;
            } else {
                padding = (rect_height - totalHeight) / 2;
            }
//            MyLog.i("有"+rowCount+"行,每行"+35+",一共"+totalHeight+"高度,rect_height="+rect_height+", padding="+padding+",padding_half="+padding_half);
            int height = 0;
            for (int i = 0; i < bubbleTextList.size(); i++) {
                BubbleText bt = bubbleTextList.get(i);
                MyLog.i(bt.toString());
                height += bt.getHeight();

                // 水平方向上居中
                int left = x + ((rect_width - bt.getWidth()) / 2);
//                int bottom = y + height + padding * i + padding_half;
                if(!StringUtil.isNullOrEmpty(textString)) {
//                    paint.setColor(Color.YELLOW);
//                    canvas.drawRect(left, y + height + padding * i + padding_half - bt.getHeight() - 0, left + bt.getWidth(), y + height + padding * i + padding_half, paint);

//                    paint.setColor(Color.BLACK);
                    if(rect_height > totalHeight) {
                        canvas.drawText(bt.getUsername(), left, y + height + padding * i + padding_half, paint);
                    } else {
                        canvas.drawText(bt.getUsername(), left, y + height + (i == 0 ? padding: 0), paint);
                    }
                }
            }
            MyLog.i("print获取文字串 END------------height=" + height);
        }

    }

    // 获取一段文字在宽度为rect_width的矩形区域内,能画几行
    private static ArrayList<BubbleText> getBubbleTextList(String txt, int rect_width, int rect_height, Paint paint) {

        int tlen = txt.length();
        ArrayList<BubbleText> bubbleTextList = new ArrayList<>();

        BubbleText bubbleText = null;
        BubbleText lastBubbleText = null;
        do {
            bubbleText = getBubbleText(txt.substring(lastBubbleText == null ? 0 : lastBubbleText.getEnd(), txt.length()),
                    rect_width, paint, lastBubbleText);

            if (bubbleText.getLength() > 0) {
                bubbleTextList.add(bubbleText);
                lastBubbleText = bubbleText;
            }

        } while (bubbleText.getLength() > 0 && bubbleText.getEnd() < tlen);

        return bubbleTextList;
    }


    // 根据 [rectW, rectH] 大小的矩形,显示txt需要的合适文字宽度.
    public static Bundle getScaledRectWidth(String txt, float rectW, float rectH) {
        float orignScale = (float) rectW / (float) rectH;
        int margin = txt.length() < 10 ? 6 : 10;
        return getScaledRectWidth(txt, rectW, rectH, margin, 0, orignScale, 0);
    }

    // 根据 [rectW, rectH] 大小的矩形,显示txt需要的合适文字宽度.
    public static Bundle getScaledRectWidth(String txt, float rectW, float rectH, int margin, int historyOK, float orignScale, float lastDeltaScale) {
        int tlen = txt.length();
        if (tlen == 0) {
            // 没有文字的时候,默认显示内有4个文字的大小
            txt = "天了噜啦";
        }
        Paint paint = getPaint();
        float width = rectW;
        float height = rectH;
        ArrayList<BubbleText> bubbleTextList = getBubbleTextList(txt, (int) width, (int) height, paint);
        // 所画文字的高度之和
        int txtHeight = 0;
        int txtWidth = 0;
        int oneRowHeight = bubbleTextList.get(0).getHeight();
        for (BubbleText bubbleText : bubbleTextList) {
            txtHeight += bubbleText.getHeight();
            if(txtWidth < bubbleText.getWidth()) {
                txtWidth = bubbleText.getWidth();
            }
        }
        float txtScale = ((float) txtWidth / (float) txtHeight);
        float deltaScale = orignScale - txtScale;
//        if(Math.abs(lastDeltaScale) < Math.abs(deltaScale))
        MyLog.i("文字比例 [" + txtScale + "] , 和原始比例 [" + orignScale + "] , 相差 [" + deltaScale + "]");
        MyLog.i("文字比例 上次差[" + lastDeltaScale + "] , 这次差 [" + deltaScale + "] , 比例是不是更接近了? [" + (Math.abs(lastDeltaScale) > Math.abs(deltaScale)) + "]");
        // 判断文字高度是否在合适的范围内
        boolean isOKH = txtHeight <= height && txtHeight >= (height - margin);
        boolean isOKH2 = txtHeight <= height && txtHeight >= (height - oneRowHeight);
        boolean isOKW = txtWidth <= width && txtWidth >= (width - margin);
        boolean isOKWabs = Math.abs(txtWidth - width) < 5;
        boolean isOKHabs = Math.abs(txtHeight - height) < 5;
        boolean isOKScale = (Math.abs(deltaScale) < Math.abs(lastDeltaScale));
        if(isOKScale) {
            // TODO 第一次进来的时候 historyOK不能这么赋值啊,会出错
            historyOK++;
        }
        if ((isOKScale && isOKW && isOKH2) ||
//                (isOKWabs && isOKHabs) ||
//                (isOKW && isOKH) ||
                isOKH) {
            // 返回 width 这样宽度的矩形.
            MyLog.i("根据~1 [" + rectW + ", " + rectH + "] 大小的矩形,显示[" + txt + "]需要的合适矩形是. [" + width + ", " + height + "]");
            return setScaledRectWidthResult(width, bubbleTextList);
        } else {
            // 第二次也成功的时候,就结束
            if(isOKScale && historyOK > 2) {
                MyLog.i("根据~2 [" + rectW + ", " + rectH + "] 大小的矩形,显示[" + txt + "]需要的合适矩形是. [" + width + ", " + height + "]");
                float tmp = 1f;
                if(txtHeight > height) {
                    tmp = ((float) txtHeight / (float) height);
                    MyLog.i("根据~2 文字比区域高[" + txtHeight + " > " + height + "] 应该放大[" + tmp + "]");
                }
                if(txtWidth > width) {
                    tmp = Math.max (tmp, (float) txtWidth / (float) width);
                    MyLog.i("根据~2 文字比区域宽[" + txtWidth + " > " + width + "] 应该放大[" + tmp + "]");
                }
                return setScaledRectWidthResult(width * tmp, bubbleTextList);
            } else if (txtHeight > height) {
                // 如果是超出了高度,应该放大些矩形
                float x = getZZ(txtHeight, height);
                MyLog.i("如果是超出了高度,应该放大些矩形 tmpWidth = " + width + " [" + txtHeight + " > " + height + "] x = " + x);
                width *= (1.05f * x);
            } else {
                // 如果是没有到达高度,应该缩小些矩形
//                int x = (int) (tmpHeight / txtHeight/ 2f);
//                if (x < 1) {
//                    x = 1;
//                }
                float x = getZZ(height, txtHeight);
                MyLog.i("如果是没有到达高度,应该缩小些矩形 tmpWidth = " + width + " [" + txtHeight + " < " + height + "] x = " + x);
                width *= (0.95f / x);
            }
            height = width * rectH / rectW;
            // 根据这个大小再算一次,如果不可以的话,再递归算
            return getScaledRectWidth(txt, width, height, margin, historyOK, orignScale, deltaScale);
        }
    }

    private static Bundle setScaledRectWidthResult(float width, ArrayList<BubbleText> bubbleTextList) {
        Bundle bundle = new Bundle();
        bundle.putFloat(Constants.KEY_RECT_WIDTH, width);
        bundle.putParcelableArrayList(Constants.KEY_BUBBLE_TEXT_LIST, bubbleTextList);
        return bundle;
    }

    static final float ZZ = 1.414f;
    private static float getZZ(float txtHeight, float tmpHeight) {

//        float x = txtHeight / tmpHeight / ZZ;
        float x = (float) Math.sqrt((double) txtHeight / tmpHeight);
        if (x < ZZ) {
            x = 1f;
        }
        return x;
    }

    private static Paint getPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        // 15sp
        paint.setTextSize(DisplayUtil.sp2px(BaseApplication.context(), 15));
        paint.setColor(Color.BLACK);
        return paint;
    }

    /**从一段文字text中获取用画笔paint所画合适长度rect_width的文字*/
    public static BubbleText getBubbleText(String text, int rect_width, Paint paint, BubbleText lastBubbleText) {
        MyLog.i("\n");
        MyLog.i("获取文字串 开始----text--------[" + text + "]");
        MyLog.i("获取文字串 开始----rect_width--[" + rect_width + "]");
        if(lastBubbleText == null) {
            MyLog.i("获取文字串 开始----lastBubbleText--[ == null ]");
        } else {
            MyLog.i("获取文字串 开始----lastBubbleText--[" + lastBubbleText.toString() + "]");
        }
        BubbleText bubbleText = new BubbleText(0,0,"",0,0);
        if(StringUtil.isNullOrEmpty(text)) {
            return bubbleText;
        }

        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        int tlen = text.length();
        int start = getStart(paint, text, rect_width, lastBubbleText, 0);

//        MyLog.i("start = " + start + ", tlen = " + tlen);
        for (int i = start; i <= tlen; i++) {
            String tmp = text.substring(0, i);
            int measureLen = getLenByPaint(tmp, paint);
//            MyLog.i("start from " + i + ", tmp = " + tmp + ", measureLen = " + measureLen);
            if(measureLen > rect_width) {
//                MyLog.i(i + ", measureLen > rect_width ");
                break;
            } else {
                bubbleText.setUsername(tmp);
                bubbleText.setStart(lastBubbleText == null ? 0 : lastBubbleText.getEnd());
                bubbleText.setLength(tmp.length());
                bubbleText.setWidth(measureLen);
                bubbleText.setHeight(fmi.descent - fmi.ascent);
            }
        }
        MyLog.i("获取文字串 END------------bubbleText=" + bubbleText.toString());
        return bubbleText;
    }

    /**递归获取下一个串的start*/
    public static int getStart(Paint paint, String text, int rect_width, BubbleText lastBubbleText, int minus) {
        int tlen = text.length();
        // 从上一行减少minus个文字来判断长度
        int start = lastBubbleText == null ? 1 : lastBubbleText.getLength() - minus;
        MyLog.i("获取下一个起始位置1 start = " + start);
        start = start < 1 ? 1 : start;
        MyLog.i("获取下一个起始位置2 start = " + start);
        start = start > tlen ? tlen : start;
        MyLog.i("获取下一个起始位置3 start = " + start);

        String tmp = text.substring(0, start);
        int measureLen = getLenByPaint(tmp, paint);
        MyLog.i("获取下一个起始位置4 ["+tmp+"] len= " + measureLen + ", rect_width=" + rect_width);
        if(measureLen > rect_width && start != 1) { // 防止一个字的时候就超出了这个图的最大宽度
            // 每次减少一个字符
            return getStart(paint, text, rect_width, lastBubbleText, ++minus);
        }
        return start;
    }

    /** 获取一段文字的长度[宽度] */
    public static int getLenByPaint(String textString, Paint paint) {
        if(StringUtil.isNullOrEmpty(textString)) {
            return 0;
        }
        int textLen = (int) paint.measureText(textString);
//        MyLog.i("获取一段文字["+textString+"]的长度 measureText = " + textLen);
//        Rect bound = new Rect();
//        paint.getTextBounds(textString, 0, textString.length(), bound);
//        MyLog.i("获取一段文字["+textString+"]的长度 getTextBounds = " + (bound.right - bound.left));
//        textLen = textLen > (bound.right - bound.left) ? textLen : (bound.right - bound.left);
//        MyLog.i("获取一段文字["+textString+"]的长度 textLen = " + textLen);
        return textLen;
    }


    public static final int FROM_DB = 1;
    public static final int FROM_STICK_EDITINFO = 2;
    /**将编辑框的贴纸[DB内存储的与预置贴纸]和工程文件中贴纸类型,转换一下*/
    public static int convertStickType(int from, int type) {
        if(from == FROM_DB && type == 5) {
            return Messages.ACTION_STICKER_BUBBLE;
        }
        if(from == FROM_STICK_EDITINFO && type == 2) {
            return Messages.ACTION_STICKER_BUBBLE;
        }
        return type;
    }

    public static int getStickerType(int to, int type) {
        if(to == FROM_DB && type == Messages.ACTION_STICKER_BUBBLE) {
            return 5;
        }
        if(to == FROM_STICK_EDITINFO && type == Messages.ACTION_STICKER_BUBBLE) {
            return 2;
        }
        return type;
    }

    /**
     * 是否是边框贴纸
     */
    public static boolean isFrame(int stickerType) {
        return stickerType == StickerConstants.STICKER_FRAME;
    }

    /**
     * 是否是最近贴纸
     */
    public static boolean isRecent(int stickerType) {
        return stickerType == StickerConstants.STICKER_RECENT;
    }

    /**
     * 是否是推荐贴纸
     */
    public static boolean isRecommended(int stickerType) {
        return stickerType == StickerConstants.STICKER_RECOMMEND;
    }

    /**
     * 是否是音频贴纸
     */
    public static boolean isVoice(int stickerType) {
        return stickerType == StickerConstants.STICKER_AUDIO;
    }

    /**
     * 是否是音频贴纸
     */
    public static boolean isVoice(Sticker sticker) {
        if (sticker == null) {
            return false;
        }
        return isVoice(sticker.getStickerType());
    }

    /**
     * 是否是彩色文字贴纸
     */
    public static boolean isColorText(int stickerType) {
        return stickerType == StickerConstants.STICKER_COLOR_TEXT;
    }

    public static boolean isColorText(Sticker sticker) {
        if (sticker == null) {
            return false;
        }
        return isColorText(sticker.getStickerType());
    }

    /**
     * 是否是文字气泡贴纸
     */
    public static boolean isTextBubble(int stickerType) {
        return stickerType == StickerConstants.STICKER_TEXT_BUBBLE;
    }

    public static boolean isTextBubble(Sticker sticker) {
        if (sticker == null) {
            return false;
        }
        return isTextBubble(sticker.getStickerType());//Messages.ACTION_STICKER_BUBBLE;
    }

    public static boolean isTextBubble(StickerEditInfo stickerEditInfo) {
        if (stickerEditInfo == null) {
            return false;
        }
        return isTextBubble(stickerEditInfo.getType());
    }

    public static String getRandomBubbleText() {
        Random random = new Random();
        int index = random.nextInt(BUBBLE_TEXT.length);
        return BUBBLE_TEXT[index];
    }

    public static StickerColorTextInfo getStickerColorTextInfo(EmoticonBean bean) {
        if(bean == null || StringUtil.isNullOrEmpty(bean.getBubbleText())) {
            return null;
        }
        Gson mGson = new Gson();
        StickerColorTextInfo info = mGson.fromJson(bean.getBubbleText(), StickerColorTextInfo.class);

//        MyLog.i("添加的彩色文字[: " + info.getText() + "], color=" + info.getColor() + "], tf=" + info.getTypeface());
        return info;
    }

    public static StickerTextInfo getStickerTextInfo(EmoticonBean bean) {
        if(bean == null || StringUtil.isNullOrEmpty(bean.getBubbleText())) {
            return null;
        }
        Gson mGson = new Gson();
        StickerTextInfo stickerTextInfo = mGson.fromJson(bean.getBubbleText(), StickerTextInfo.class);
//        MyLog.i("getStickerTextInfo bean.txt=[" + bean.getBubbleText() + "]");
//        MyLog.i("getStickerTextInfo bean.stickerTextInfo=[" + stickerTextInfo.toString() + "]");

        if (isTextBubble(bean.getStickType())) {
            stickerTextInfo.setText(getRandomBubbleText());
        }
        MyLog.i("添加的文字气泡内添加个随机文字: " + stickerTextInfo.getText());
        return stickerTextInfo;
    }

    public static StickerTextInfo getStickerTextInfo(StickerEditInfo stickerEditInfo) {
        if(stickerEditInfo == null) {
            return null;
        }
        try {
            MyLog.i("getStickerTextInfo stickerEditInfo=[" + stickerEditInfo.getInfo() == null ? "null" : stickerEditInfo.getInfo().toString() + "]");
        } catch (Exception e) {
        }
        return stickerEditInfo.getInfo();
    }

//    private static final String KEY_ONE_ROW_TEXT_COUNT = "key_oneRowTextCount";
//    private static final String KEY_ROW_COUNT = "key_rowCount";
//    private static final String KEY_SCALE = "key_scale";
    /**
     * 计算一段文字在一个矩形区域内画时,需要将文字区域缩放多少
     *
     * 宽度需要放大的比例 = 一行宽 / rectW
     * 高度需要放大的比例 = 所有行高 / rectH
     *
     * @param txt 文字区域内的文字
     * @param rectW 文字区域的宽度
     * @param rectH 文字区域的高度
     * */
    public static Bundle scaleBubbleText(String txt, int rectW, int rectH) {
        Bundle bundle = new Bundle();

//        String txt = "你好,我是新人,请多关照.珍惜淡定的心境 苦过后更加清\n" +
//                "万般过去亦无味但有领会留下\n" +
//                "今天先记得听过人说这叫半生瓜";
        int tlen = txt.length();
        if(tlen == 0) {
            // 如果清除文字的话,默认留[天了撸~]的内容大小
            tlen = "天了撸~".length();
        }
        int cal1 = calculateOneRowTextCount(tlen, rectW, rectH, true);
        int row1 = 0;
        if (cal1 != 0) {
            row1 = tlen / cal1;
            if (tlen % cal1 != 0) {
                row1++;
            }
        }

        int cal2 = calculateOneRowTextCount(tlen, rectW, rectH, false);
        int row2 = 0;
        if (cal2 != 0) {
            row2 = tlen / cal2;
            if (tlen % cal2 != 0) {
                row2++;
            }
        }


        // 方案1时需要放大的倍数
        double scale1_w = ONE_WORD_WIDTH * cal1 / rectW;
        double scale1_h = ONE_WORD_HEIGHT * row1 / rectH;
        double scale1 = Math.max(scale1_w, scale1_h);
        MyLog.i("能整除时 一行文字 " + cal1 + " 个,一共 " + row1 + " 行,需要缩放 " + scale1 + " scale1_w=" + scale1_w + " scale1_h=" + scale1_h);

        // 方案2时需要放大的倍数
        double scale2_w = ONE_WORD_WIDTH * cal2 / rectW;
        double scale2_h = ONE_WORD_HEIGHT * row2 / rectH;
        double scale2 = Math.max(scale2_w, scale2_h);
        MyLog.i("不能整除时 一行文字 " + cal2 + " 个,一共 " + row2 + " 行,需要缩放 " + scale2 + " scale2_w=" + scale2_w + " scale2_h=" + scale2_h);

        // 取个相对缩放得较大的就可以,更省空间
        if(scale1 < scale2) {
            bundle.putInt(Constants.KEY_ONE_ROW_TEXT_COUNT, cal1);
            bundle.putInt(Constants.KEY_ROW_COUNT, row1);
            bundle.putDouble(Constants.KEY_SCALE, scale1);
        } else {
            bundle.putInt(Constants.KEY_ONE_ROW_TEXT_COUNT, cal2);
            bundle.putInt(Constants.KEY_ROW_COUNT, row2);
            bundle.putDouble(Constants.KEY_SCALE, scale2);
        }

//        double scaleMin = Math.min(scale1, scale2);
//        double scaleMax = Math.max(scale1, scale2);
//        double scale;
//        if(scaleMin > 1) {
//            // 如果是放大时,取个相对放大得较小的就可以,更省空间
//            scale = scaleMin;
//        } else if(scaleMax < 1) {
//            // 如果是缩小时,取个相对缩放较小的就可以,更省空间
//            scale = scaleMin;
//        }

        return bundle;
    }

    // 粗略计算下这个贴纸能包含几行文字,每行文字多少
    /*

    一行文字宽 约= 一行文字数 * 每个文字宽度
    文字行数   = 文字总数 / 一行文字数 (如果余数>0则行数 +1)

        在文字大小为30的情况下,一个中文大概有27*35的宽高比

    RectWidth     一行文字宽[所有行文字宽之最大值]  27*一行文字数
    ---------- = --------------------------- = --------------
    RectHeight    所有行文字高之和                35*(tlen/一行文字数 是否能整除? 不能则+1)

    一行文字数 =
        */
    public static final double ONE_WORD_HEIGHT = 35D;
    public static final double ONE_WORD_WIDTH = 27D;

    /** 一行文字数
     * @param divide_exactly false:不能整除时; true:能整除时
     * */
    public static int calculateOneRowTextCount(int tlen, int rectW, int rectH, boolean divide_exactly) {
        double W = rectW;
        double H = rectH;
        if(divide_exactly) {
            int count = (int) Math.sqrt(W * ONE_WORD_HEIGHT * tlen / H / ONE_WORD_WIDTH);
//            Log.i(TAG, "tlen = " + tlen + ", rectW=" + W + ", rectH=" + H + ", count=" + count);
            return count;
        } else {
            double a = ONE_WORD_WIDTH * H;
            double b = -ONE_WORD_HEIGHT * W;
            double c = b * tlen;
            double b2_4ac = Math.pow(b, 2.0D) - 4 * a * c;
            double delta = Math.sqrt(b2_4ac);
            double chushu = delta - b;
//            Log.i(TAG, "a = " + a + ", b=" + b + ", c=" + c + ", b2_4ac=" + b2_4ac + ", delta=" + delta + ", chushu=" + chushu);
            if (chushu > 0D) {
                double count = chushu / (2 * a);
//                Log.i(TAG, "tlen = " + tlen + ", rectW=" + W + ", rectH=" + H + ", count=" + count);
                return (int) count;
            }
            return 0;
        }
    }

    /**从工程文件中添加贴纸时用*/
    public static int getStickerTextWidth(StickerEditInfo sei, Bitmap bitmap) {
        int result = 0;
        if(sei == null) {
            return 0;
        }
        StickerTextInfo sti = StickerUtils.getStickerTextInfo(sei);
        if(sti != null) {
            result = bitmap.getWidth() * sei.getWidth()  * sti.getWidth() / 100000000;
        }
        return result;
    }

    /**从工程文件中添加贴纸时用*/
    public static int getStickerTextHeight(StickerEditInfo sei, Bitmap bitmap) {
        int result = 0;
        if(sei == null) {
            return 0;
        }
        StickerTextInfo sti = StickerUtils.getStickerTextInfo(sei);
        if(sti != null) {
            result = bitmap.getHeight() * sei.getHeight()  * sti.getHeight() / 100000000;
        }
        return result;
    }

    /**首次添加贴纸时用*/
    public static int getStickerTextWidth(StickerTextInfo sti, Bitmap bitmap) {
        int result = 0;
        if(sti == null) {
            MyLog.i("getStickerTextWidth sti == null  return 0");
            return 0;
        }
//        MyLog.i("getStickerTextWidth bitmap.getWidth()=" + bitmap.getWidth());
//        MyLog.i("getStickerTextWidth sti.getWidth()=" + sti.getWidth());
        result = bitmap.getWidth() * sti.getWidth() / 10000;
//        MyLog.i("getStickerTextWidth result=" + result);
        return result;
    }

    /**首次添加贴纸时用*/
    public static int getStickerTextHeight(StickerTextInfo sti, Bitmap bitmap) {
        int result = 0;
        if(sti == null) {
            MyLog.i("getStickerTextHeight sti == null  return 0");
            return 0;
        }
//        MyLog.i("getStickerTextHeight bitmap.getHeight()=" + bitmap.getHeight());
//        MyLog.i("getStickerTextHeight sti.getHeight()=" + sti.getHeight());
        result = bitmap.getHeight() * sti.getHeight() / 10000;
//        MyLog.i("getStickerTextHeight result=" + result);
        return result;
    }

    /**文字编辑时,传参用*/
    public static int getStickerTextWidth(Sticker sticker) {
        if(sticker == null) {
            return 0;
        }
        if(sticker.getStickerTextInfo() == null) {
            return 0;
        }
        int bmpWidth = (int) (sticker.getMapPointsDst()[2] - sticker.getMapPointsDst()[0]);

        return bmpWidth * sticker.getStickerTextInfo().getWidth() / 10000;
    }

    /**文字编辑时,传参用*/
    public static int getStickerTextHeight(Sticker sticker) {
        if(sticker == null) {
            return 0;
        }
        if(sticker.getStickerTextInfo() == null) {
            return 0;
        }
        int bmpHeight = (int) (sticker.getMapPointsDst()[5] - sticker.getMapPointsDst()[1]);

        return bmpHeight * sticker.getStickerTextInfo().getHeight() / 10000;
    }

    /**文字编辑时,传参用*/
    public static int getStickerTextBmpWidth(Sticker sticker) {
        if(sticker == null) {
            return 0;
        }
        if(sticker.getStickerTextInfo() == null) {
            return 0;
        }
        return (int) (sticker.getMapPointsDst()[2] - sticker.getMapPointsDst()[0]);
    }

    /**文字编辑时,传参用*/
    public static int getStickerTextBmpHeight(Sticker sticker) {
        if(sticker == null) {
            return 0;
        }
        return (int) (sticker.getMapPointsDst()[5] - sticker.getMapPointsDst()[1]);
    }

    public static int getZoom(int zoom) {
        if (zoom <= 0 || zoom > 100) {
            return 65;
        }
        return zoom;
    }

    public static boolean PhotoProcessIsRunning() {
        Intent intent = new Intent(BaseApplication.context(), PhotoProcessActivity.class);
        ComponentName cmpName = intent.resolveActivity(BaseApplication.context().getPackageManager());
        boolean bIsExist = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            MyLog.i("---startAndExit---cmpName:" + cmpName);
            ActivityManager am = (ActivityManager) BaseApplication.context().getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(100);
            MyLog.i("---startAndExit---taskInfoList.size:" + taskInfoList.size());
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                MyLog.i("---startAndExit---taskInfo:"
                        + taskInfo.baseActivity);
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    bIsExist = true;
                    break;
                }
            }
        }

        return bIsExist;
    }

    /** 转换 TODO 需要设置parentStickerType 哦
     * */
    public static EmoticonBean convertStickerInfoToEmoticonBean(int type, StickerPackage sticker, StickerInfo stickerInfo) {
        if(stickerInfo == null)
            return null;
        EmoticonBean eb = new EmoticonBean();
        eb.setId(String.valueOf(stickerInfo.getId()));
        //2.7之后每张贴纸有自己的sitckerID
        eb.setStickerId(stickerInfo.getStickerId());
        if (!TextUtils.isEmpty(stickerInfo.getParentid())) {
            eb.setParentId(stickerInfo.getParentid());
        } else {
            eb.setParentId(sticker != null ? sticker.getStickerPackageId() : null);
        }

        if(stickerInfo.getIcon() != null) {
            eb.setIconUrl(stickerInfo.getIcon().getUri());
        }
        eb.setIconUri("file://" + StickerUtils.getStickerIconPath(stickerInfo));
        if(stickerInfo.getPicture() != null) {
            eb.setGifUrl(stickerInfo.getPicture().getUri());
        }
        eb.setGifUri("file://" + StickerUtils.getStickerPicPath(stickerInfo));
        // TODO 如果将文字气泡和普通类型的贴纸 聚合到一个包内的话,应该用stickerInfo自己的type了
        if (stickerInfo.getType() > 0 && stickerInfo.getType() < 7) {
            eb.setStickType(stickerInfo.getType());
        } else {
            eb.setStickType(type);
        }
        // TODO 需要设置parentStickerType 哦
        // 如果是文字气泡的话,需要设置sticker_text信息
//            if (type == 5) { // v2.7是用的单个贴纸的类型
        if (isTextBubble(stickerInfo.getType())) {
            Gson mGson = new Gson();
            eb.setBubbleText(mGson.toJson(stickerInfo.getInfo()));
        }
        eb.setEventType(EmoticonBean.FACE_TYPE_USERDEF);
        eb.setZoom(stickerInfo.getZoom());
        return eb;
    }


    /**工程文件中贴纸信息,转换为EmoticonBean*/
    public static EmoticonBean convertStickerEditInfoToEmoticonBean(StickerEditInfo stickerEditInfo) {
        EmoticonBean bean = new EmoticonBean();
        bean.setStickType(stickerEditInfo.getType());
        if (StickerUtils.isColorText(stickerEditInfo.getType())) {
            Gson mGson = new Gson();
            bean.setBubbleText(mGson.toJson(stickerEditInfo.getCtInfo()));
        } else {
            bean.setParentStickType(1);
            bean.setId(String.valueOf(stickerEditInfo.getId()));
            bean.setParentId(stickerEditInfo.getParentid());
            if (stickerEditInfo.getIcon() != null) {
                bean.setIconUrl(stickerEditInfo.getIcon().getUri());
            }
            bean.setIconUri("file://" + StickerUtils.getStickerIconPath(stickerEditInfo));
            if (stickerEditInfo.getPicture() != null) {
                bean.setGifUrl(stickerEditInfo.getPicture().getUri());
            }
            bean.setGifUri("file://" + StickerUtils.getStickerPicPath(stickerEditInfo));
            if (StickerUtils.isTextBubble(stickerEditInfo.getType())) {
                Gson mGson = new Gson();
                bean.setBubbleText(mGson.toJson(stickerEditInfo.getInfo()));
            }
            bean.setZoom(stickerEditInfo.getZoom());
        }
        // v2.7增加了stickerID
        bean.setStickerId(stickerEditInfo.getStickerId());
        bean.setEventType(2);
        return bean;
    }

    public static String savePicWithWatermark(BaseActivity activity, String uri, String destPath, boolean isFile) {
        String destFilePath = destPath;
        if (StringUtil.isNullOrEmpty(destFilePath)) {
            destFilePath = FileUtil.getInst().getSystemPhotoPath()
                    + File.separator + TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss") + ".jpg";
        }

        Bitmap baseboard = null;
        if (isFile) {
            baseboard = BitmapFactory.decodeFile(uri);
        } else if (uri.startsWith("http")) {
            int sw = DisplayUtil.getDisplayWidth(activity);
            baseboard = ImageLoaderUtils.getImageFromCache(ImageLoaderUtils.buildNewUrl(uri, new ImageSize(sw, sw)));
            if (baseboard == null) {
//                MyLog.i("swzz http1 bitmap==null");
                String fileName = BitmapUtil.getTmpWatermarkImagePath();
                if (HttpUtil.download(uri, fileName)) {
//                    MyLog.i("swzz http2  下载文件成功");
                    baseboard = BitmapFactory.decodeFile(fileName);
                    if (baseboard != null) {
                        FileUtil.deleteFileWithPath(fileName);
//                        MyLog.i("swzz fileName wh=" + baseboard.getWidth() + "*" + baseboard.getHeight());
                    } else {
//                        MyLog.i("swzz fileName NULL 1~~~~");
                    }
                } else {
//                    MyLog.i("swzz http2  下载文件失败");
//                    MyLog.i("swzz fileName NULL 2~~~~");
                }
            } else {
//                MyLog.i("swzz http bitmap !=     null");
            }
        }
        if (baseboard == null) {
            AppContext.toast(activity, activity.getString(R.string.download_fail));
            return null;
        }
        saveWaterMarkFile(activity, baseboard, destFilePath);

        return destFilePath;
    }

    public static void saveWaterMarkFile(Context context, Bitmap src, String destFilePath) {
        try {
            int sw = DisplayUtil.getDisplayWidth(context);
            if (src.getWidth() != sw || src.getHeight() != sw) {
                src = BitmapUtil.zoomBitmap(src, sw, sw);
            }

            if (MinePreference.getInstance().isSaveWatermark()) {
                Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.watermark);
                Bitmap mergeBitmap = BitmapUtil.setWaterMark(context, src, watermark);

                ImageUtils.saveToFile(destFilePath, false, mergeBitmap);
            } else {
                ImageUtils.saveToFile(destFilePath, false, src);
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse(destFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getAudioBitmap(Bitmap background, long audioLength, boolean mirrorH) {
        String text = audioLength + "”";
        //建立一个空的Bitmap
        int width = background.getWidth();
        int hight = background.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
        // 初始化画布绘制的图像到icon上
        Canvas canvas = new Canvas(bitmap);
        // 建立画笔
        Paint photoPaint = new Paint();
        // 获取更清晰的图像采样，防抖动
        photoPaint.setDither(true);
        // 过滤一下，抗剧齿
        photoPaint.setFilterBitmap(true);

        Rect src = new Rect(0, 0, background.getWidth(), background.getHeight());// 创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
        canvas.drawBitmap(background, src, dst, photoPaint);// 将photo 缩放或则扩大到dst使用的填充区photoPaint
        TextPaint textPaint = getTextPaint();
        int delta_x = 0;
        if (mirrorH) {
            delta_x = (int) TDevice.dpToPixel(5f);
        } else {
            delta_x = width - (StickerUtils.getLenByPaint(text, textPaint) + (int) TDevice.dpToPixel(5f));
        }
        drawText(canvas, textPaint, text, delta_x, hight / 4, width);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bitmap;
    }

    //设置画笔的字体和颜色
    private static TextPaint getTextPaint(){
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
        int TEXT_SIZE = DisplayUtil.sp2px(BaseApplication.context(), 13);
        textPaint.setTextSize(TEXT_SIZE);// 字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
        textPaint.setColor(Color.WHITE);// 采用的颜色
        return textPaint;
    }

    private static void drawText(Canvas canvas, TextPaint Paint,String textString,int x,int y,int width) {
        //int Width=Math.round(width* getRATIO());
        int start_x = Math.round(x);
        int start_y = Math.round(y);
        StaticLayout staticLayout = new StaticLayout(textString, Paint, width- (int) TDevice.dpToPixel(5f)*2,
                Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);

        //绘制的位置
        canvas.translate(start_x, start_y);
        staticLayout.draw(canvas);
    }
}

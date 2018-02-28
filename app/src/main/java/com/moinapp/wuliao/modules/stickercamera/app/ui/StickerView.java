package com.moinapp.wuliao.modules.stickercamera.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.keyboard.bean.EmoticonBean;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.model.StickerColorTextInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerEditInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerTextInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.BubbleText;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.Point2D;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UiUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 大咖秀VIEW [含有底图,滤镜和上面的各种贴纸]
 * Created by Yangchen on 2015/9/7.
 */
public class StickerView extends View {

    ILogger MyLog = LoggerFactory.getLogger(StickerView.class.getSimpleName());
    private Context mContext;
    private Activity activity;
    /**
     * 最大放大倍数
     */
    public static final float MAX_SCALE_SIZE = 50.0f;
    public static final float MIN_SCALE_SIZE = 0.01f;
    public static final float HOR_SWING_ANGLE = 3.5f;

    /**
     * 整个编辑框
     */
    private RectF mViewRect;

    private float mLastPointX, mLastPointY, deviation;
    private long mLastClickTimeMills;
    private int mLastFocusPosition = -2; // 表示没有选择任何,包括focus=-1[没选中的情况]

    private Bitmap mControllerBitmap, mDeleteBitmap, mLockBitmap, mUnLockBitmap, bgBitmap;
    private Bitmap mReversalHorBitmap, mReversalVerBitmap;//水平反转和垂直反转bitmap
    private Bitmap mBubbleTextBitmap;//文字气泡bitmap
    private float mControllerWidth, mControllerHeight, mDeleteWidth, mDeleteHeight;
    private float mReversalHorWidth, mReversalHorHeight, mReversalVerWidth, mReversalVerHeight;
    private float mLockWidth, mLockHeight, mUnLockWidth, mUnLockHeight;
    private float mBubbleTextWidth, mBubbleTextHeight;
    private static final int LOCKAREA_HEIGHT_DP = 0;// 2.7版本不要锁定区域了,之前是45dp
    private float minMarginBorder;//按钮距离边界最小距离
    private float minBtnDistance;//按钮之间最小距离

    private boolean mInController, mInBubbleText, mInLock, mInMove;
    private boolean mInReversalHorizontal, mInReversalVertical;

    private boolean mInDelete = false;

    //    private Sticker currentSticker;
    private List<Sticker> stickers = new ArrayList<Sticker>();

    /**
     * 焦点贴纸索引
     */
    private int focusStickerPosition = -1;

    private float radius = 0f;
    private float bdWidth = 0f;
    private int exRadius = 50;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {

        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_control);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();
        radius = mControllerWidth / 2;
//        MyLog.i("按钮半径: " + radius);

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_delete);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();

        mReversalHorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fliplr_edit);
        mReversalHorWidth = mReversalHorBitmap.getWidth();
        mReversalHorHeight = mReversalHorBitmap.getHeight();

        mReversalVerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flipud_edit);
        mReversalVerWidth = mReversalVerBitmap.getWidth();
        mReversalVerHeight = mReversalVerBitmap.getHeight();

        mLockBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_lock);
        mUnLockBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_unlock);
        mLockWidth = mUnLockWidth = mControllerWidth;
        mLockHeight = mUnLockHeight = mControllerHeight;

        mBubbleTextBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sticker_bubble_text);
        mBubbleTextWidth = mBubbleTextBitmap.getWidth();
        mBubbleTextHeight = mBubbleTextBitmap.getHeight();

        minMarginBorder = Math.min(2 * mControllerWidth, TDevice.dpToPixel(30));
        minBtnDistance = Math.min(4 * mControllerWidth, TDevice.dpToPixel(100));

//        android.util.Log.i("stv", "mControllerWidth=" + mControllerWidth);
//        android.util.Log.i("stv", "mBubbleTextWidth=" + mBubbleTextWidth);
//        android.util.Log.i("stv", "TDevice.dpToPixel(30)=" + TDevice.dpToPixel(30));
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 在编辑步骤,点击贴纸资源时,添加到编辑区域内
     */
    public void setWaterMark(Bitmap bitmap, Bitmap bgBitmap, boolean isLockByParent, EmoticonBean bean) {
        this.bgBitmap = bgBitmap;
        Bitmap zoomBitmap = null;
        Point point = UiUtils.getDisplayWidthPixels(getContext());
        bdWidth = point.x;
        if (bitmap != null) {
            Sticker sticker = null;
            StickerTextInfo sti = null;
            StickerColorTextInfo scti = null;
            ArrayList<BubbleText> bubbleTextArrayList = null;
            MyLog.i("你添加了一个" + bean.toString());
            // v3.2.1是边框自动添加到最底层
            boolean needAddedToBottom = false;
            if (StickerUtils.isVoice(bean.getStickType())) {
                //如果时音频 7,不缩放,按原图大小
                zoomBitmap = bitmap;
            } else if (StickerUtils.isColorText(bean.getStickType())) {
                // 设置彩色文字 8的文字
                scti = StickerUtils.getStickerColorTextInfo(bean);
            } else if(StickerUtils.isFrame(bean.getStickType())) {
                // 如果是边框 6,刚好适配当前屏幕宽度
                if (bitmap.getWidth() != bdWidth) {
                    zoomBitmap = BitmapUtil.zoomBitmap(bitmap, (int) bdWidth, (int) bdWidth);
                }
                needAddedToBottom = true;
            } else if(StickerUtils.isTextBubble(bean.getStickType())) {
                // 如果是文字气泡 5 将数据库中的5类型,转换为逻辑中的305类型,以便和工程文件中的 StickerEditInfo.type == 2做一个一致
//                bean.setStickType(StickerUtils.convertStickType(StickerUtils.FROM_DB, bean.getStickType()));

                // 如果是文字气泡,至少能填充[天了撸~]等随机文字的宽度来适配
                sti = StickerUtils.getStickerTextInfo(bean);
                if(sti != null) {
                    int rect_width = StickerUtils.getStickerTextWidth(sti, bitmap);
                    int rect_height = StickerUtils.getStickerTextHeight(sti, bitmap);
                    try {
                        MyLog.i("你添加了一个文字气泡贴纸 " + sti.toString());
                        MyLog.i("文字气泡贴纸大小是        " + bitmap.getWidth() + "*" + bitmap.getHeight());
                        MyLog.i("文字气泡贴纸编辑区域大小是 " + rect_width + "*" + rect_height);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                    Bundle bundle = StickerUtils.getScaledRectWidth(sti.getText(), rect_width, rect_height);
                    float newWidth = bundle.getFloat(Constants.KEY_RECT_WIDTH, rect_width);
                    float scale = newWidth / (float) rect_width;
                    bubbleTextArrayList = bundle.getParcelableArrayList(Constants.KEY_BUBBLE_TEXT_LIST);

                    zoomBitmap = BitmapUtil.zoomBitmap(bitmap, scale);
                }
            } else {
                // 如果图的大小超过屏幕宽度的65%,就缩放为65%
                int zoom = StickerUtils.getZoom(bean.getZoom());
                int scale = (int) (100 * Math.max(bitmap.getWidth(), bitmap.getHeight()) / bdWidth);
                if (scale != zoom) {
                    float scaleTo = zoom / (float) scale;
                    zoomBitmap = BitmapUtil.zoomBitmap(bitmap, scaleTo);
                }

                needAddedToBottom = (zoom == 100);
            }

            // 如果是边框的话,就不要和前一个添加的贴纸错开了
            sticker = new Sticker(zoomBitmap == null ? bitmap : zoomBitmap, point.x, point.x, StickerUtils.isFrame(bean.getStickType()) ? null : getPrepoints(), isLockByParent, bean);
            if(StickerUtils.isTextBubble(sticker)) {
                sticker.setStickerTextInfo(sti);
                sticker.setBubbleTextList(bubbleTextArrayList);
            } else if(StickerUtils.isColorText(sticker)) {
                // 彩色文字的话需要将文字,颜色和样式都设置进去
                sticker.setStickerColorTextInfo(scti);
            }

            if (isNotMax()) {
                if (needAddedToBottom) {
                    stickers.add(0, sticker);
                } else {
                    stickers.add(sticker);
                    //处理声音贴纸
                    adjustAudioSticker();
                }
            }
        }
//        MyLog.i("setWaterMark BEAN ~ then refocus point=" + point.x + "," + point.y);
        refocus();
        postInvalidate();
    }

    /**
     * 根据模板或者改图时提交的工程文件,将贴纸添加到贴纸编辑内
     */
    public Sticker setWaterMark(Bitmap bitmap, Bitmap bgBitmap, boolean isLockByParent, StickerEditInfo stickerEditInfo) {
        this.bgBitmap = bgBitmap;
        Point point = UiUtils.getDisplayWidthPixels(getContext());
        bdWidth = point.x;
        Sticker sticker = null;
        if (bitmap != null) {
            sticker = new Sticker(bitmap, point.x, point.x, null, isLockByParent, StickerUtils.convertStickerEditInfoToEmoticonBean(stickerEditInfo));
            sticker.setStickerEditInfo(stickerEditInfo);
            if (StickerUtils.isVoice(sticker)) {
                //处理音频
                sticker.setAudioLength(stickerEditInfo.getAudio().getLength());
                sticker.setAudioUri(stickerEditInfo.getAudio().getUri());
                sticker.setStickerId(stickerEditInfo.getAudio().getAudioId());
            } else if(StickerUtils.isColorText(sticker)) {
                //处理彩色文字
                sticker.setStickerColorTextInfo(stickerEditInfo.getCtInfo());
            } else if(StickerUtils.isTextBubble(sticker)) {
                //处理文字气泡
                sticker.setStickerTextInfo(StickerUtils.getStickerTextInfo(stickerEditInfo));
            }

            if (isNotMax()) {
                stickers.add(sticker);
                adjustAudioSticker();
            }
        }
//        MyLog.i("setWaterMark SEI ~ ="+stickerEditInfo.toString());
        refocus();
        postInvalidate();
        return sticker;
    }

    private boolean isNotMax() {
        if (stickers.size() < 20) {
            return true;
        } else {
            AppContext.showToast("最多可以添加20个贴纸!");
            return false;
        }
    }

    /**
     * 此处只做除平移外的镜像和缩放旋转动作. 平移需要拿到最新的中心点位置,所以在onDraw中处理translate
     */
    public void reDraw(StickerEditInfo stickerEditInfo, Sticker sticker) {
        if (stickerEditInfo.isMirrorH()) {
            doReversalHorizontal(true);
        }
        if (stickerEditInfo.isMirrorV()) {
            doReversalVertical(true);
        }
        // 旋转
        float rotation = stickerEditInfo.getRotaion();
        if (rotation > 180f) {
            rotation -= 360f;
        }
//
//        int top = stickers.size() - 1;
//        if (top < 0) {
//            return;
//        }
        if (sticker == null) return;
        doRotation(sticker, rotation);

        // 缩放
        float rx = stickerEditInfo.getWidth() / 10000.0f * (float) bdWidth;
        float ry = stickerEditInfo.getHeight() / 10000.0f * (float) bdWidth;
        float scale_bubble_text = 1f;
        ArrayList<BubbleText> bubbleTextArrayList = null;
        if (StickerUtils.isTextBubble(stickerEditInfo)) {
            float rect_width = rx * stickerEditInfo.getInfo().getWidth() / 10000;
            float rect_height = ry * stickerEditInfo.getInfo().getHeight() / 10000;
            Bundle bundle = StickerUtils.getScaledRectWidth(stickerEditInfo.getInfo().getText(), rect_width, rect_height);
            float newWidth = bundle.getFloat(Constants.KEY_RECT_WIDTH, rect_width);
            scale_bubble_text = newWidth / rect_width;
            bubbleTextArrayList = bundle.getParcelableArrayList(Constants.KEY_BUBBLE_TEXT_LIST);
        }
        sticker.setBubbleTextList(bubbleTextArrayList);
//        int width = Point2D.floatToInt(px);
//        int height = Point2D.floatToInt(py);
        float scale = scale_bubble_text * rx / sticker.getBitmap().getWidth();
        float nowsc = sticker.getScaleSize() * scale;
//        MyLog.i("reDraw stickerEditInfo=" + stickerEditInfo.toString());
//        MyLog.i("reDraw bmpWidth=" + stickers.get(top).getBitmap().getWidth());
//        MyLog.i("reDraw rx=" + rx);
//        MyLog.i("reDraw scale=" + scale);
//        MyLog.i("reDraw nowsc=" + nowsc);
//        if (nowsc >= MIN_SCALE_SIZE && nowsc <= MAX_SCALE_SIZE) {
        sticker.getmMatrix().postScale(scale, scale, sticker.getMapPointsDst()[8], sticker.getMapPointsDst()[9]);
        sticker.setScaleSize(nowsc);
//        }

        refocus();
        postInvalidate();
    }

    private void printMap(int top) {
//        for (int i = 0; i < stickers.get(top).getMapPointsDst().length; i++) {
//            MyLog.i("printMap getMapPointsDst["+i+"]=" + stickers.get(top).getMapPointsDst()[i]);
//        }
//        MyLog.i("printMap -------------------");
//        for (int i = 0; i < stickers.get(top).getMapPointsSrc().length; i++) {
//            MyLog.i("printMap getMapPointsSrc["+i+"]=" + stickers.get(top).getMapPointsSrc()[i]);
//        }
    }

    private List<PointD> getPrepoints() {
        if (stickers == null || stickers.size() == 0) {
            return null;
        }
        List<PointD> pointDs = new ArrayList<>();
        for (Sticker sticker : stickers) {
            PointD p = new PointD(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1]);
            pointDs.add(p);
        }
        return pointDs;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bgBitmap != null)
            canvas.drawBitmap(bgBitmap, 0, 0, null);

        if (stickers.size() <= 0) {
            return;
        }


        for (int i = 0; i < stickers.size(); i++) {

//            MyLog.i("reDraw    平移before: ");
            stickers.get(i).getmMatrix().mapPoints(stickers.get(i).getMapPointsDst(), stickers.get(i).getMapPointsSrc());
            stickers.get(i).getmMatrix().mapRect(stickers.get(i).getmContentRect(), stickers.get(i).getmOriginContentRect());
//            printMap(i);
            //平移
            if (translate(i)) {
                stickers.get(i).getmMatrix().mapPoints(stickers.get(i).getMapPointsDst(), stickers.get(i).getMapPointsSrc());
                stickers.get(i).getmMatrix().mapRect(stickers.get(i).getmContentRect(), stickers.get(i).getmOriginContentRect());
            }

            //如果是声音贴纸,把声音时长画上去
            Bitmap bitmap = stickers.get(i).getBitmap();
            if (StickerUtils.isVoice(stickers.get(i).getStickerType())) {
                bitmap = StickerUtils.getAudioBitmap(bitmap, stickers.get(i).getAudioLength(), stickers.get(i).isMirrorH());
            }
            canvas.drawBitmap(bitmap, stickers.get(i).getmMatrix(), null);

//            if(StickerUtils.isTextBubble(stickers.get(i))) {
//                MyLog.i("扩大后的图变成多大了: " + stickers.get(i).getBitmap().getWidth() + "*" + stickers.get(i).getBitmap().getHeight());
//                MyLog.i("扩大后的图变成多大matrix1: " + stickers.get(i).getmMatrix().toString());
//                for (int j = 0; j < stickers.get(i).getMapPointsDst().length; j++) {
//                    MyLog.i("扩大后的图变成多大了: " + j + "=" + stickers.get(i).getMapPointsDst()[j]);
//                }
//                MyLog.i("扩大后的图变成多大Width =" + (StickerUtils.getStickerTextBmpWidth(stickers.get(i))));
//                MyLog.i("扩大后的图变成多大Height =" + (StickerUtils.getStickerTextBmpHeight(stickers.get(i))));
//
//                MyLog.i("扩大后的图变成多大Width =" + (stickers.get(i).getMapPointsDst()[2] - stickers.get(i).getMapPointsDst()[0]));
//                MyLog.i("扩大后的图变成多大Height=" + (stickers.get(i).getMapPointsDst()[5] - stickers.get(i).getMapPointsDst()[1]));
//            }
            if(StickerUtils.isTextBubble(stickers.get(i))) {
                try {
                    MyLog.i("要画的文字气泡是: " + stickers.get(i).getStickerTextInfo().toString());
                    for (int j = 0; j < stickers.get(i).getBubbleTextList().size(); j++) {
                        MyLog.i((j+1)+" 行文字气泡是[" + stickers.get(i).getBubbleTextList().get(j).toString() + "]");
                    }

                } catch (Exception e) {
                    MyLog.e(e);
                }
                StickerUtils.drawStickText(canvas, stickers.get(i));
            }
//            MyLog.i("reDraw    平移after: ");
//            printMap(i);

        }


        for (int i = 0; i < stickers.size(); i++) {
            if (stickers.get(i).isFocusable()) {
                if (getLock()) {
                    stickers.get(i).getmBorderPaint().setColor(Color.RED);
                } else {
                    stickers.get(i).getmBorderPaint().setColor(Color.WHITE);
                }

                float[] rect = getRectMap(stickers.get(i).getMapPointsDst());

//                MyLog.i(i + " getLock()=" + getLock() + ", !mInMove=" + !mInMove);
                if (getLock() || !mInMove) {
//                    MyLog.i("锁了或者没移动都要划线");
                    canvas.drawLine(rect[0], rect[1], rect[2], rect[1], stickers.get(i).getmBorderPaint());
                    canvas.drawLine(rect[2], rect[1], rect[2], rect[3], stickers.get(i).getmBorderPaint());
                    canvas.drawLine(rect[2], rect[3], rect[0], rect[3], stickers.get(i).getmBorderPaint());
                    canvas.drawLine(rect[0], rect[3], rect[0], rect[1], stickers.get(i).getmBorderPaint());
                }

                if (getLock()) {
                    // leftTop: unlock
                    canvas.drawBitmap(mUnLockBitmap, rect[0] - radius, rect[1] - radius, null);
                } else if (!mInMove) {
                    // TODO 应该后draw Control 按钮,否则4个按钮会叠加,touch事件先响应的是control,之后才是删除,垂直镜像和水平镜像
                    // TODO 图层操作区域不应该叠加到STICKERVIEW的区域底部.否则不好点击四个角按钮了.建议UI修改一下
                    // TODO 镜像按钮需要重新出图和删除/控制按钮一样大小
                    // leftTop: vr, 2.7版本后是lock
                    canvas.drawBitmap(mLockBitmap, rect[0] - radius, rect[1] - radius, null);
                    // leftBottom: hr
//                    canvas.drawBitmap(mReversalHorBitmap, rect[0] - radius, rect[3] - radius, null);
                    // rightTop: del
                    canvas.drawBitmap(mDeleteBitmap, rect[2] - radius, rect[1] - radius, null);
                    // rightBottom: control/bubble_text
                    if(StickerUtils.isTextBubble(stickers.get(i))) {
                        canvas.drawBitmap(mBubbleTextBitmap, rect[2] - radius, rect[3] - radius, null);
                    } else if (!StickerUtils.isVoice(stickers.get(i).getStickerType())) {
                        canvas.drawBitmap(mControllerBitmap, rect[2] - radius, rect[3] - radius, null);
                    } else {
                    }

                    /*
                    // 画实际内容的黄色虚边线
                    stickers.get(i).getmBorderPaint().setColor(Color.YELLOW);
                    float[] tmp = stickers.get(i).getMapPointsDst();
    //                float[] tmp = calculateArchorPoint(stickers.get(i).getMapPointsDst(), radius);

                    Path path = new Path();
                    PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
                    stickers.get(i).getmBorderPaint().setPathEffect(effects);
                    path.moveTo(tmp[0], tmp[1]);
                    for (int j = 0; j < 4; j++) {
                        path.lineTo(tmp[(2 * j + 2) % 8], tmp[(2 * j + 3) % 8]);
                    }
                    canvas.drawPath(path, stickers.get(i).getmBorderPaint());
//
//                    canvas.drawLine(tmp[0], tmp[1], tmp[2], tmp[3], stickers.get(i).getmBorderPaint());
//                    canvas.drawLine(tmp[2], tmp[3], tmp[4], tmp[5], stickers.get(i).getmBorderPaint());
//                    canvas.drawLine(tmp[4], tmp[5], tmp[6], tmp[7], stickers.get(i).getmBorderPaint());
//                    canvas.drawLine(tmp[6], tmp[7], tmp[0], tmp[1], stickers.get(i).getmBorderPaint());
*/
                }
            } else {
/*                // 画未选中贴纸实际内容的蓝色虚边线
                stickers.get(i).getmBorderPaint().setColor(Color.BLUE);
                float[] tmp = stickers.get(i).getMapPointsDst();
                canvas.drawLine(tmp[0], tmp[1], tmp[2], tmp[3], stickers.get(i).getmBorderPaint());
                canvas.drawLine(tmp[2], tmp[3], tmp[4], tmp[5], stickers.get(i).getmBorderPaint());
                canvas.drawLine(tmp[4], tmp[5], tmp[6], tmp[7], stickers.get(i).getmBorderPaint());
                canvas.drawLine(tmp[6], tmp[7], tmp[0], tmp[1], stickers.get(i).getmBorderPaint());*/
            }

        }
//        MyLog.i("reDraw    onDraw 按钮半径END: " + radius);
    }


    /**返回贴纸所在的合适的矩形区域,返回的点是left,top,right,bottom*/
    private float[] getRectMap(float[] mapPointsDst) {
        return getRectMap(mapPointsDst, true);
    }

    /**返回贴纸所在的合适的矩形区域,返回的点是left,top,right,bottom
     * @param mapPointsDst 内容区域
     * @param needBorder 是否要考虑边框*/
    private float[] getRectMap(float[] mapPointsDst, boolean needBorder) {
        float border = needBorder ? minMarginBorder : 0;
        float[] result = mapPointsDst;
        float leftX = bdWidth;
        float rightX = 0;
        float topY = bdWidth;
        float bottomY = 0;
        for (int i = 0; i < 4; i++) {
            leftX = Math.min(result[i * 2], leftX);
            rightX = Math.max(result[i * 2], rightX);
            topY = Math.min(result[i * 2 + 1], topY);
            bottomY = Math.max(result[i * 2 + 1], bottomY);
        }
        // 最左边的距离
        if(leftX < border) {
            leftX = border;
        } else if(leftX > (bdWidth - border - minBtnDistance)) {
            leftX = bdWidth - border - minBtnDistance;
        }
        // 最右边的距离
        if(rightX > bdWidth - border) {
            rightX = bdWidth - border;
        } else if(rightX < (border + minBtnDistance)) {
            rightX = border + minBtnDistance;
        }
        // 最上边的距离
        if(topY < border) {
            topY = border;
        } else if(topY > (bdWidth - border - minBtnDistance)) {
            topY = bdWidth - border - minBtnDistance;
        }
        // 最下边的距离
        if(bottomY > bdWidth - border - TDevice.dpToPixel(LOCKAREA_HEIGHT_DP)) {
            bottomY = bdWidth - border - TDevice.dpToPixel(LOCKAREA_HEIGHT_DP);
        } else if(bottomY < (border + minBtnDistance)) {
            bottomY = border + minBtnDistance;
        }

        /*
        // 宽度不足时
        if(rightX - leftX < minBtnDistance) {
            // 往外扩展
            float deltX = (minBtnDistance - (rightX - leftX)) / 2;
            leftX -= deltX;
            rightX += deltX;
            // 扩展出边界时,平移
            if(leftX < border) {
                rightX = rightX + border - leftX;
                leftX = border;
            }
            if(rightX > bdWidth - border) {
                leftX = bdWidth - border + leftX - rightX;
                rightX = bdWidth - border;
            }
        }

        // 高度不足时
        if(bottomY - topY < minBtnDistance) {
            // 往外扩展
            float deltY = (minBtnDistance - (bottomY - topY)) / 2;
            topY -= deltY;
            bottomY += deltY;
            // 扩展出边界时,平移
            if(topY < border) {
                bottomY = bottomY + border - topY;
                topY = border;
            }
            float btm = bdWidth - border - TDevice.dpToPixel(LOCKAREA_HEIGHT_DP);
            if(bottomY > btm) {
                topY = btm + topY - bottomY;
                bottomY = btm;
            }
        }
*/
        float max = minBtnDistance;
        /*如果想用方形的边框,则用下面的代码*/
//        max = Math.max(rightX - leftX, bottomY - topY);
//        max = Math.max(max, minBtnDistance);
        // 宽度比高度值小时
        if(rightX - leftX < max) {
            // 往外扩展
            float deltX = (max - (rightX - leftX)) / 2;
            leftX -= deltX;
            rightX += deltX;
            // 扩展出边界时,平移
            if(leftX < border) {
                rightX = rightX + border - leftX;
                leftX = border;
            }
            if(rightX > bdWidth - border) {
                leftX = bdWidth - border + leftX - rightX;
                rightX = bdWidth - border;
            }
        }

        // 高度比宽度值小时
        if(bottomY - topY < max) {
            // 往外扩展
            float deltY = (max - (bottomY - topY)) / 2;
            topY -= deltY;
            bottomY += deltY;
            // 扩展出边界时,平移
            if(topY < border) {
                bottomY = bottomY + border - topY;
                topY = border;
            }
            float btm = bdWidth - border - TDevice.dpToPixel(LOCKAREA_HEIGHT_DP);
            if(bottomY > btm) {
                topY = btm + topY - bottomY;
                bottomY = btm;
            }
        }

        float[] frame = new float[4];
        frame[0] = leftX;
        frame[1] = topY;
        frame[2] = rightX;
        frame[3] = bottomY;
        return frame;
    }

    /**
     * 此处只做平移动作. 平移需要拿到最新的中心点位置,所以在onDraw中处理translate
     */
    private boolean translate(int i) {
        boolean result = false;
        Sticker sticker = stickers.get(i);
        if (sticker == null) {
            return result;
        }
        if (sticker.getStickerEditInfo() != null) {
            StickerEditInfo stickerEditInfo = sticker.getStickerEditInfo();
            float targetX = stickerEditInfo.getX() / 10000.0f * (float) bdWidth;
            float targetY = stickerEditInfo.getY() / 10000.0f * (float) bdWidth;
//            MyLog.i("reDraw targetX=" + targetX);
//            MyLog.i("reDraw targetY=" + targetY);
//            MyLog.i("reDraw 目前的点位置是x=" + sticker.getMapPointsDst()[8]);
//            MyLog.i("reDraw 目前的点位置是y=" + sticker.getMapPointsDst()[9]);

            float cX = targetX - sticker.getMapPointsDst()[8];
            float cY = targetY - sticker.getMapPointsDst()[9];
//            MyLog.i("reDraw 需要移动x=" + cX);
//            MyLog.i("reDraw 需要移动y=" + cY);

            sticker.getmMatrix().postTranslate(cX, cY);

            stickers.get(i).setStickerEditInfo(null);
            result = true;
        }
        return result;
    }

    /**
     * 重新计算四个顶点在区域内的坐标
     *
     * @param mapPointsDst 原始点坐标集
     * @param radius       四边按钮图的半径
     * @return
     */
    private float[] calculateArchorPoint(float[] mapPointsDst, float radius) {
        float[] result = mapPointsDst;
        // 获取第一个在区域内的点
        int inbox = -1;
        for (int i = 0; i < 4; i++) {
            if ((result[i * 2] - radius) < 0 || (result[i * 2] + radius) > bdWidth) {
//                MyLog.i(i+" point out_bound lr");
                continue;
            } else if ((result[1 + i * 2] - radius) < 0 || (result[1 + i * 2] + radius) > bdWidth) {
//                MyLog.i(i+" point out_bound tb");
                continue;
            }
            inbox = i;
//            MyLog.i(i+" point NOT out_bound inbox = " + inbox);
            break;
        }
        // 全部出界 TODO 如果是全部出界的话,可以最多算6轮即可
        if (inbox < 0) {
            inbox = 0;
        }

        boolean allInbox = false;
        for (int i = inbox; i < (4 + inbox) || !allInbox; i++) {
            // 当前点01
            float x1 = result[(0 + i * 2) % 8];
            float y1 = result[(1 + i * 2) % 8];
            // 顺时针第一个点23
            float x2 = result[(2 + i * 2) % 8];
            float y2 = result[(3 + i * 2) % 8];
            // 对角点45
            float x3 = result[(4 + i * 2) % 8];
            float y3 = result[(5 + i * 2) % 8];
            // 逆时针第一个点67
            float x4 = result[(6 + i * 2) % 8];
            float y4 = result[(7 + i * 2) % 8];

            // 按顺时针计算
            if (isOutOfBound(x2, y2)) {
//                MyLog.i(i+", 顺时针 point out_bound xy = " + x2 + " * " + y2);
                // 得到该点新坐标
                float[] point = getBJPointD(x1, y1, x2, y2);
//                MyLog.i(i+", 顺时针 point new xy = " + point[0] + " * " + point[1]);
                result[(2 + i * 2) % 8] = point[0];
                result[(3 + i * 2) % 8] = point[1];
                // 计算对角线点的坐标
                float[] diagonalPoint = getDiagonalPoint(x1, y1, point[0], point[1], x3, y3, x4, y4);
                result[(4 + i * 2) % 8] = diagonalPoint[0];
                result[(5 + i * 2) % 8] = diagonalPoint[1];
//                MyLog.i(i+", 对角线点 new xyB = " + result[(4+i*2)%8] + " * " + result[(5+i*2)%8]);
            } else {
                allInbox = true;
//                MyLog.i(i + ", 顺时针 point NOT out_bound xy = " + x2 + " * " + y2);
            }
        }

        return result;
    }

    /**
     * 获取对角线点的新坐标,x2y2是新变化后的点坐标
     */
    private float[] getDiagonalPoint(float x1, float y1, float vx, float vy, float x3, float y3, float x4, float y4) {
        float[] point = new float[2];

//        x1 - x2 = x4 - x3
//        x3 = x4 - x1 + x2
        point[0] = x4 - x1 + vx;
//        y1 - y2 = y4 - y3
//        y3 = y4 - y1 + y2
        point[1] = y4 - y1 + vy;

        return point;
    }

    /**
     * 判断左右上下是否越界
     */
    private boolean isOutOfBound(float x2, float y2) {
        return (x2 - radius) < 0 || (x2 + radius) > bdWidth || (y2 - radius) < 0 || (y2 + radius) > bdWidth;
    }

    /**
     * 获取边界点坐标
     */
    private float[] getBJPointD(float x1, float y1, float x2, float y2) {
        float[] xy = new float[2];
        xy[0] = x2;
        xy[1] = y2;
        boolean left = isLeft(x2);
        boolean right = isRight(x2);
        boolean top = isTop(y2);
        boolean bottom = isBottom(y2);

        if (left || right) {
            xy = getHorizontalPoint(x1, y1, x2, y2, left);
            top = isTop(xy[1]);
            bottom = isBottom(xy[1]);
            if (top || bottom) {
                xy = getVerticalPoint(x1, y1, xy[0], xy[1], top);
            }

        } else if (top || bottom) {
            xy = getVerticalPoint(x1, y1, x2, y2, top);
            left = isLeft(xy[0]);
            right = isRight(xy[0]);
            if (left || right) {
                xy = getHorizontalPoint(x1, y1, xy[0], xy[1], left);
            }
        }

        return xy;
    }

//                    x1-x               y1-y
//                    ------  = scale = -------
//                    x1-x2              y1-y2
//                    y = y1 - (y1-y2)*scale
//                    x = x1 - (x1-x2)*scale

    private float[] getHorizontalPoint(float x1, float y1, float x2, float y2, boolean isLeft) {
        float[] point = new float[2];
        point[0] = isLeft ? radius : (bdWidth - radius);
        float scale = (x1 - point[0]) / (x1 - x2);
        point[1] = y1 - (y1 - y2) * scale;

        return point;
    }

    private float[] getVerticalPoint(float x1, float y1, float x2, float y2, boolean isTop) {
        float[] point = new float[2];
        point[1] = isTop ? radius : (bdWidth - radius);
        float scale = (y1 - point[1]) / (y1 - y2);
        point[0] = x1 - (x1 - x2) * scale;

        return point;
    }

    private boolean isLeft(float x) {
        return (x - radius) < 0;
    }

    private boolean isRight(float x) {
        return (x + radius) > bdWidth;
    }

    private boolean isTop(float y) {
        return (y - radius) < 0;
    }

    private boolean isBottom(float y) {
        return (y + radius) > bdWidth;
    }

    /**
     * 是否在控制点区域, 右下
     *
     * @param x
     * @param y
     * @param rect
     * @return
     */
    private boolean isInController(float x, float y, float[] rect) {
        if(judgeFailed(rect)) {
            return false;
        }
        float rx = rect[2];
        float ry = rect[3];
        RectF rectF = new RectF(rx - mControllerWidth / 2 - exRadius,
                ry - mControllerHeight / 2 - exRadius,
                rx + mControllerWidth / 2 + exRadius,
                ry + mControllerHeight / 2 + exRadius);
        if (rectF.contains(x, y)) {
            return true;
        }
        return false;

    }

    /**
     * 是否在删除点区域, 右上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInDelete(float x, float y, float[] rect) {
        if(judgeFailed(rect)) {
            return false;
        }
        float rx = rect[2];
        float ry = rect[1];
        RectF rectF = new RectF(rx - mDeleteWidth / 2 - exRadius,
                ry - mDeleteHeight / 2 - exRadius,
                rx + mDeleteWidth / 2 + exRadius,
                ry + mDeleteHeight / 2 + exRadius);
        if (rectF.contains(x, y)) {
            return true;
        }
        return false;

    }

    /**
     * 是否在水平反转点区域, 左下
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInReversalHorizontal(float x, float y, float[] rect) {
//        if(judgeFailed(rect)) {
//            return false;
//        }
//        float rx = rect[0];
//        float ry = rect[3];
//
//        RectF rectF = new RectF(rx - mReversalHorWidth / 2, ry - mReversalHorHeight / 2, rx + mReversalHorWidth / 2, ry + mReversalHorHeight / 2);
//        if (rectF.contains(x, y))
//            return true;

        return false;

    }

    /**
     * 是否在垂直反转点区域, 左上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInReversalVertical(float x, float y, float[] rect) {
//        if(judgeFailed(rect)) {
//            return false;
//        }
//        float rx = rect[0];
//        float ry = rect[1];
//
//        RectF rectF = new RectF(rx - mReversalVerWidth / 2, ry - mReversalVerHeight / 2, rx + mReversalVerWidth / 2, ry + mReversalVerHeight / 2);
//        if (rectF.contains(x, y))
//            return true;
        return false;
    }


    /**
     * 是否在锁定,解锁点区域, 左上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInLock(float x, float y, float[] rect) {
        if (judgeFailed(rect)) {
            return false;
        }
        float rx = rect[0];
        float ry = rect[1];

        RectF rectF = new RectF(rx - mReversalVerWidth / 2 - exRadius,
                ry - mReversalVerHeight / 2 - exRadius,
                rx + mReversalVerWidth / 2 + exRadius,
                ry + mReversalVerHeight / 2 + exRadius);
        if (rectF.contains(x, y))
            return true;
        return false;
    }


    /**
     * 是否在文字气泡点区域, 右下
     *
     * @param x
     * @param y
     * @param rect
     * @return
     */
    private boolean isInBubbleText(float x, float y, float[] rect) {
        return isInController(x, y, rect);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
        }

        if (stickers.size() <= 0) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();
        float[] rect = null;
//        MyLog.i("dispatchTouchEvent " + x + "," + y + ",focusStickerPosition=" + focusStickerPosition);
        if(focusStickerPosition >= 0) {
//            float[] p1 = stickers.get(focusStickerPosition).getMapPointsDst();
//            MyLog.i("dispatchTouchEvent " + p1[0] + "," + p1[1] + " " + p1[2] + "," + p1[3] + " " + p1[4] + "," + p1[5] + " " + p1[6] + "," + p1[7]);
            rect = getRectMap(stickers.get(focusStickerPosition).getMapPointsDst());
//            p1 = stickers.get(focusStickerPosition).getMapPointsDst();
//            MyLog.i("dispatchTouchEvent " + p1[0] + "," + p1[1] + " " + p1[2] + "," + p1[3] + " " + p1[4] + "," + p1[5] + " " + p1[6] + "," + p1[7]);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onMove(!(stickers.size() > 1 && focusStickerPosition >= 0));
//                MyLog.i("dispatchTouchEvent ACTION_DOWN focusStickerPosition=" + focusStickerPosition);
                if (!getLock()) {
                    if (isInController(x, y, rect) && !StickerUtils.isTextBubble(stickers.get(focusStickerPosition))
                            && !StickerUtils.isVoice(stickers.get(focusStickerPosition).getStickerType())) {
                        mInController = true;
                        mLastPointY = y;
                        mLastPointX = x;

                        // 左上角到中心的距离
                        float nowLenght = caculateLength(stickers.get(focusStickerPosition).getMapPointsDst()[0], stickers.get(focusStickerPosition).getMapPointsDst()[1]);
                        // 点击点到中心距离
                        float touchLenght = caculateLength(x, y);
                        deviation = touchLenght - nowLenght;
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN incontrol");
                        break;
                    }

                    if (isInDelete(x, y, rect)) {
                        mInDelete = true;
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN isInDelete");
                        break;
                    }

                    if (isInReversalHorizontal(x, y, rect)) {
                        mInReversalHorizontal = true;
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN isInReversalHorizontal");
                        break;
                    }

                    if (isInBubbleText(x, y, rect) && StickerUtils.isTextBubble(stickers.get(focusStickerPosition))) {
                        // 点击了文字按钮
//                        MyLog.i("点击了文字按钮");
                        mInBubbleText = true;
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN isInBubbleText");
                        break;
                    }
                }
                /* 2.7版本去除垂直镜像, 添加锁定/解锁
                if (isInReversalVertical(x, y, rect)) {
                    mInReversalVertical = true;
                    MyLog.i("dispatchTouchEvent ACTION_DOWN isInReversalVertical");
                    break;
                }
                */
                if (isInLock(x, y, rect)) {
                    mInLock = true;
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN isInLock");
                    break;
                }

                if (isFocusSticker(x, y)) {
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN isFocusSticker");
                    mLastPointY = y;
                    mLastPointX = x;

                    // 2.7 锁定后,文字气泡不能编辑咯
                    if (!getLock()) {
                        mInMove = true;
                        if (focusStickerPosition == mLastFocusPosition) {
                            if (isDoubleClick()) {
//                                MyLog.i("dispatchTouchEvent ACTION_DOWN isFocusSticker 双击");
                                mLastFocusPosition = -2;
                                isDoubleClick = true;
                            } else {
//                                MyLog.i("dispatchTouchEvent ACTION_DOWN isFocusSticker 未双击 记录下时间 " + mLastClickTimeMills);
                                mLastClickTimeMills = System.currentTimeMillis();
                            }
                        } else {
//                            MyLog.i("dispatchTouchEvent ACTION_DOWN isFocusSticker 你点击了 " + focusStickerPosition);
                            mLastFocusPosition = focusStickerPosition;
                            if (focusStickerPosition >= 0) {
                                mLastClickTimeMills = System.currentTimeMillis();
//                                MyLog.i("dispatchTouchEvent ACTION_DOWN isFocusSticker 记录下时间 " + mLastClickTimeMills);
                            }
                        }
                    }

                    invalidate();
                } else {
//                    MyLog.i("dispatchTouchEvent ACTION_DOWN NOT isFocusSticker");
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
//                MyLog.i("dispatchTouchEvent ACTION_UP");
                if (mInDelete && isInDelete(x, y, rect)) {
                    doDeleteSticker();
                    break;
                }
                if (mInController && !boundIndexError()
                        && StickerUtils.isColorText(stickers.get(focusStickerPosition))
                        && (withinHorizontalSwingAngel(stickers.get(focusStickerPosition).getRotation()))) {
                    // 彩色字如果在水平振幅内,则水平摆正
                    doRotation(stickers.get(focusStickerPosition), 0 - stickers.get(focusStickerPosition).getRotation());
                    invalidate();
                    break;
                }

                if (mInBubbleText && isInBubbleText(x, y, rect)) {
                    startEditText(StickerConstants.STICKER_TEXT_BUBBLE);
                    break;
                }
                if (mInReversalHorizontal && isInReversalHorizontal(x, y, rect)) {
                    doReversalHorizontal(true);
                    break;
                }
                /* 2.7版本去除垂直镜像, 添加锁定/解锁
                if (isInReversalVertical(x, y, rect) && mInReversalVertical) {
                    doReversalVertical();
                    break;
                }
                */
                if (mInLock && isInLock(x, y, rect)) {
                    doLock();
                    break;
                }
                if(isDoubleClick) {
                    doDoubleClick();
                    if (mInMove) {
                        mInMove = false;
                    }
                    break;
                }
            case MotionEvent.ACTION_CANCEL:
//                MyLog.i("dispatchTouchEvent ACTION_CANCEL");
                if (mInMove) {
                    mInMove = false;
                    invalidate();
                }
                onMove(!(stickers.size() > 1 && focusStickerPosition >= 0));
                mLastPointX = 0;
                mLastPointY = 0;
                mInController = false;
                mInBubbleText = false;
                mInLock = false;
                mInDelete = false;
                mInReversalHorizontal = false;
                mInReversalVertical = false;
                break;
            case MotionEvent.ACTION_MOVE:
//                MyLog.i("dispatchTouchEvent ACTION_MOVE");
                if(boundIndexError()) {
                    return true;
                }
                onMove(true);
                if (mInController) {
                    float rotation = rotation(event);
                    doRotation(stickers.get(focusStickerPosition), rotation);
                    float nowLenght = caculateLength(stickers.get(focusStickerPosition).getMapPointsDst()[0], stickers.get(focusStickerPosition).getMapPointsDst()[1]);
                    float touchLenght = caculateLength(x, y) - deviation;
                    if (Math.sqrt((nowLenght - touchLenght) * (nowLenght - touchLenght)) > 0.0f) {
                        float scale = touchLenght / nowLenght;

//                        float nowsc = stickers.get(focusStickerPosition).getScaleSize() * scale;
//                        if (/*nowsc >= MIN_SCALE_SIZE && */nowsc <= MAX_SCALE_SIZE) {
                        doScale(focusStickerPosition, scale);
//                            stickers.get(focusStickerPosition).getmMatrix().postScale(scale, scale, stickers.get(focusStickerPosition).getMapPointsDst()[8], stickers.get(focusStickerPosition).getMapPointsDst()[9]);
//                            stickers.get(focusStickerPosition).setScaleSize(nowsc);
//                        }
                    }

                    invalidate();
                    mLastPointX = x;
                    mLastPointY = y;
                    break;

                }

                if (mInMove == true) {
                    float cX = x - mLastPointX;
                    float cY = y - mLastPointY;
                    mInController = false;

                    if (Math.sqrt(cX * cX + cY * cY) > 2.0f && canStickerMove(cX, cY)) {
                        stickers.get(focusStickerPosition).getmMatrix().postTranslate(cX, cY);
                        postInvalidate();
                        mLastPointX = x;
                        mLastPointY = y;
                    }
                    break;
                }


                return true;

        }
        return true;
    }

    /** 是否在水平方向上下震荡角度内 */
    private boolean withinHorizontalSwingAngel(float rotation) {
        return Math.abs(rotation) < HOR_SWING_ANGLE || (360 - rotation) < HOR_SWING_ANGLE;
    }

    private void doRotation(Sticker sticker, float rotation) {
        if (rotation == 0) {
            return;
        }
        sticker.getmMatrix().postRotate(rotation, sticker.getMapPointsDst()[8], sticker.getMapPointsDst()[9]);
        sticker.addRotation(rotation);
    }

    private void doDoubleClick() {
        if(boundIndexError()) {
            return;
        }
        if(StickerUtils.isTextBubble(stickers.get(focusStickerPosition))) {
            startEditText(StickerConstants.STICKER_TEXT_BUBBLE);
        } else if (StickerUtils.isColorText(stickers.get(focusStickerPosition))) {
            startEditText(StickerConstants.STICKER_COLOR_TEXT);
        } else {
            resetSticker();
        }
        MyLog.i("doDoubleClick 双击了["+focusStickerPosition+"] " +
                        stickers.get(focusStickerPosition).getStickerType()
                        + ", text=" + (stickers.get(focusStickerPosition).getStickerTextInfo() + "/" + stickers.get(focusStickerPosition).getStickerColorTextInfo())
        );
        isDoubleClick = false;
        mLastClickTimeMills = 0;
    }

    private boolean isDoubleClick;
    private boolean isDoubleClick() {
        return System.currentTimeMillis() - mLastClickTimeMills < 200;
    }

    // 打开文字编辑界面
    private void startEditText(int stickerType) {
        switch (stickerType) {
            case StickerConstants.STICKER_TEXT_BUBBLE:
                mInBubbleText = false;
                EditTextActivity.openTextEdit(getActivity(), getBubbleBundle(), Constants.ACTION_EDIT_BUBBLETEXT);
                break;
            case StickerConstants.STICKER_COLOR_TEXT:
                EditTextActivity.openTextEdit(getActivity(), getColorTextBundle(), Constants.ACTION_EDIT_COLOR_TEXT);
                break;
            default:
                break;
        }
    }

    /**将贴纸在100%和zoom之间缩放, 并清除旋转和镜像*/
    private void resetSticker() {
        Sticker sticker = stickers.get(focusStickerPosition);
        // 清除旋转
        doRotation(sticker, 0 - sticker.getRotation());

        // 重设缩放
        double widthPoint = Point2D.distance(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3]);
        double heightPoint = Point2D.distance(sticker.getMapPointsDst()[4], sticker.getMapPointsDst()[5], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3]);
        int scale = (int) (100 * Math.max(widthPoint, heightPoint) / bdWidth);
        if (scale != 100) {
            // 缩放到100%
            float scaleTo = 100 / (float) scale;
            doScale(focusStickerPosition, scaleTo);
        } else {
            // 缩放到贴纸制定的zoom大小, 默认缩放到65%
            int zoom = StickerUtils.getZoom(sticker.getZoom());
            float scaleTo = zoom / (float) scale;
            doScale(focusStickerPosition, scaleTo);
        }
        // 清除水平镜像
        if (sticker.isMirrorH()) {
            doReversalHorizontal(false);
        }
        // 清除垂直镜像
        if (sticker.isMirrorV()) {
            doReversalVertical(false);
        }

        // 清除平移
        float cX = bdWidth / 2 - sticker.getMapPointsDst()[8];
        float cY = bdWidth / 2 - sticker.getMapPointsDst()[9];
        sticker.getmMatrix().postTranslate(cX, cY);

        invalidate();
    }

    /**彩色文字编辑的参数
     * 只是设置彩色文字最长字符数
     * 并且将彩色文字本身的文字带过去*/
    private Bundle getColorTextBundle() {
        Bundle bundle = new Bundle();
        String text = "";
        try {
            text = stickers.get(focusStickerPosition).getStickerColorTextInfo().getText();
        } catch (Exception e) {
            MyLog.e(e);
        }
        bundle.putString(Constants.KEY_DEFAULT_TEXT, text);
        bundle.putInt(Constants.KEY_MAX_LENGTH, mContext.getResources().getInteger(R.integer.color_text_max_len));
        bundle.putInt(Constants.PARAM_STICKER_TYPE, StickerConstants.STICKER_COLOR_TEXT);
        bundle.putInt(Constants.KEY_FOCUS_STICKER_POSITION, focusStickerPosition);

        return bundle;
    }

    /**文字编辑的参数*/
    private Bundle getBubbleBundle() {
        String text = "";
        try {
            text = stickers.get(focusStickerPosition).getStickerTextInfo().getText();
        } catch (Exception e) {
            MyLog.e(e);
        }
        // 此处应该把 focusStickerPosition 参数传递过去,然后回来后,也可以即刻重新focus这个贴纸了啊.
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_DEFAULT_TEXT, text);
        bundle.putInt(Constants.KEY_MAX_LENGTH, mContext.getResources().getInteger(R.integer.bubbletext_max_len));
        bundle.putInt(Constants.KEY_FOCUS_STICKER_POSITION, focusStickerPosition);
        int rect_width = StickerUtils.getStickerTextWidth(stickers.get(focusStickerPosition));
        int rect_height = StickerUtils.getStickerTextHeight(stickers.get(focusStickerPosition));
        bundle.putInt(Constants.KEY_RECT_WIDTH, rect_width);
        bundle.putInt(Constants.KEY_RECT_HEIGHT, rect_height);

        try {
            MyLog.i("------------------------打开文字编辑界面 getBubbleBundle start------------------------");
            MyLog.i("KEY_FOCUS_STICKER_POSITION= " + focusStickerPosition);
            MyLog.i("KEY_RECT_WIDTH= " + rect_width);
            MyLog.i("KEY_RECT_HEIGHT= " + rect_height);

            MyLog.i("Bitmap_WIDTH= " + stickers.get(focusStickerPosition).getBitmap().getWidth());
            MyLog.i("Bitmap_HEIGHT= " + stickers.get(focusStickerPosition).getBitmap().getHeight());

            MyLog.i("rect_W_H_rate= " + stickers.get(focusStickerPosition).getStickerTextInfo().toString());
            MyLog.i("------------------------打开文字编辑界面 getBubbleBundle end------------------------");
        } catch (Exception e) {
            MyLog.e(e);
        }

        return bundle;
    }

    /**缩放贴纸*/
    private void doScale(int pos, float scale) {
        if(pos < 0 || pos >= stickers.size()) {
            return;
        }
        if (scale == 1) {
            return;
        }
        stickers.get(pos).getmMatrix().postScale(scale, scale, stickers.get(pos).getMapPointsDst()[8], stickers.get(pos).getMapPointsDst()[9]);
        float nowsc = stickers.get(pos).getScaleSize() * scale;
        stickers.get(pos).setScaleSize(nowsc);
    }


    public boolean isColorTextNeedRefresh(Intent data) {
        if (data == null) {
            return false;
        }
        // 这个focus位置应该也要通过参数传递,就好了,然后回来后,重新focus这个文字气泡贴纸
        int focusPos = data.getIntExtra(Constants.KEY_FOCUS_STICKER_POSITION, 0);
        setFocusSticker(focusPos);

        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return false;

        String text = data.getStringExtra(Constants.PARAM_EDIT_TEXT);
        if (StringUtils.isEmpty(text)) {
            //如果把文字都删除的话,提示"内容不能为空!"
            AppContext.showToast("内容不能为空!");
            return false;
        }

        if (text.equals(stickers.get(focusStickerPosition).getStickerColorTextInfo().getText())) {
            // 文字没变化的时候不用处理
            return false;
        } else {
            stickers.get(focusStickerPosition).getStickerColorTextInfo().setText(text);
        }

        return true;
    }

    /**刷新彩色文字, 重设宽高
     * @param bitmap */
    public void refreshColorText(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        Sticker sticker = stickers.get(focusStickerPosition);

        if (bitmap.getWidth() != sticker.getBitmap().getWidth() || bitmap.getHeight() != sticker.getBitmap().getHeight()) {
            float[] mapPointsSrc = sticker.getMapPointsSrc();
            mapPointsSrc[2] = mapPointsSrc[4] = bitmap.getWidth();
            mapPointsSrc[8] = bitmap.getWidth() / 2;
            mapPointsSrc[5] = mapPointsSrc[7] = bitmap.getHeight();
            mapPointsSrc[9] = bitmap.getHeight() / 2;

            RectF oriRect = stickers.get(focusStickerPosition).getmOriginContentRect();

            sticker.getmMatrix().mapPoints(sticker.getMapPointsDst(), mapPointsSrc);
            checkCTOutOfFrame(sticker);
            /*MyLog.i("bmp from " + oriRect.right + "*" + oriRect.bottom + " to " + bitmap.getWidth() + "*" + bitmap.getHeight()
                            + "\nsticker " + focusStickerPosition + " new pos: lt ["
                            + (int) sticker.getMapPointsDst()[0] + "*" +
                            + (int) sticker.getMapPointsDst()[1] + "], rt [" +
                            + (int) sticker.getMapPointsDst()[2] + "*" +
                            + (int) sticker.getMapPointsDst()[3] + "], rb [" +
                            + (int) sticker.getMapPointsDst()[4] + "*" +
                            + (int) sticker.getMapPointsDst()[5] + "], lb [" +
                            + (int) sticker.getMapPointsDst()[6] + "*" +
                            + (int) sticker.getMapPointsDst()[7] + "], c [" +
                            + (int) sticker.getMapPointsDst()[8] + "*" +
                            + (int) sticker.getMapPointsDst()[9]
            );*/
            oriRect.right = bitmap.getWidth();
            oriRect.bottom = bitmap.getHeight();
        }

        sticker.setBitmap(bitmap);

        setFocusSticker(focusStickerPosition);
        invalidate();
    }

    /**
     * 检查新彩色文字是否超出贴纸编辑区域,并归位到中心
     *
     * @return 偏移到左1, 上2, 右3, 下4; 0:未超出
     */
    private int checkCTOutOfFrame(Sticker sticker) {
        float[] mapPointsDst = sticker.getMapPointsDst();
        int out = 0;
        boolean outL = (mapPointsDst[0] < 0 && mapPointsDst[2] < 0 && mapPointsDst[4] < 0 && mapPointsDst[6] < 0);
        if (!outL) {
            boolean outR = (mapPointsDst[0] > bdWidth && mapPointsDst[2] > bdWidth && mapPointsDst[4] > bdWidth && mapPointsDst[6] > bdWidth);
            if (!outR) {
                boolean outT = (mapPointsDst[1] < 0 && mapPointsDst[3] < 0 && mapPointsDst[5] < 0 && mapPointsDst[7] < 0);
                if (!outT) {
                    boolean outB = (mapPointsDst[1] > bdWidth && mapPointsDst[3] > bdWidth && mapPointsDst[5] > bdWidth && mapPointsDst[7] > bdWidth);
                    if (outB) {
                        out = 4;
                    }
                } else {
                    out = 2;
                }
            } else {
                out = 3;
            }
        } else {
            out = 1;
        }

        if (out > 0) {
            // 清除平移
            float cX = bdWidth / 2 - sticker.getMapPointsDst()[8];
            float cY = bdWidth / 2 - sticker.getMapPointsDst()[9];
            sticker.getmMatrix().postTranslate(cX, cY);
        }

        return out;
    }

    /**刷新文字气泡text内容
     * @param data*/
    public void refreshTextPop(Intent data) {
        if(data == null) {
            return;
        }

        try {
            // 这个focus位置应该也要通过参数传递,就好了,然后回来后,重新focus这个文字气泡贴纸
            int focusPos = data.getIntExtra(Constants.KEY_FOCUS_STICKER_POSITION, 0);
            setFocusSticker(focusPos);

            if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
                return;

            String text = data.getStringExtra(Constants.PARAM_EDIT_TEXT);
            if (StringUtil.isNullOrEmpty(text)) {
                //如果把文字都删除的话,提示"内容不能为空!"
                AppContext.showToast("内容不能为空!");
                return;
            }
//            int rect_width = StickerUtils.getStickerTextBmpWidth(stickers.get(focusStickerPosition)) * stickers.get(focusStickerPosition).getStickerTextInfo().getWidth() / 10000;
//            int rect_height = StickerUtils.getStickerTextBmpHeight(stickers.get(focusStickerPosition)) * stickers.get(focusStickerPosition).getStickerTextInfo().getHeight() / 10000;
//            int rect_height = stickers.get(focusStickerPosition).getBitmap().getHeight() * stickers.get(focusStickerPosition).getStickerTextInfo().getHeight() / 10000;
//            int rectW = data.getIntExtra(Constants.KEY_RECT_WIDTH, rect_width);
//            int rectH = data.getIntExtra(Constants.KEY_RECT_HEIGHT, rect_height);
//            int oneRowCount = data.getIntExtra(Constants.KEY_ONE_ROW_TEXT_COUNT, 0);
//            int rowCount = data.getIntExtra(Constants.KEY_ROW_COUNT, 0);
            double scale = data.getFloatExtra(Constants.KEY_SCALE, 1f); // 意思是保持原来大小
            MyLog.i("应该缩放比例 " + scale);

//            MyLog.i("------------------------刷新文字气泡text内容 refreshTextPop start------------------------");
//            MyLog.i("KEY_FOCUS_STICKER_POSITION= " + focusStickerPosition);
//            MyLog.i("focusPos=" + focusPos);
//            MyLog.i("KEY_RECT_WIDTH= " + rectW);
//            MyLog.i("KEY_RECT_HEIGHT= " + rectH);
//
//            MyLog.i("Bitmap_WIDTH= " + stickers.get(focusStickerPosition).getBitmap().getWidth());
//            MyLog.i("Bitmap_HEIGHT= " + stickers.get(focusStickerPosition).getBitmap().getHeight());
//
//            MyLog.i("rect_W_H_rate= " + stickers.get(focusStickerPosition).getStickerTextInfo().toString());
//
//            MyLog.i("bitmap_RECT_WIDTH= " + rect_width);
//            MyLog.i("bitmap_RECT_HEIGHT= " + rect_height);
//
//            MyLog.i("KEY_ONE_ROW_TEXT_COUNT=" + oneRowCount);
//            MyLog.i("KEY_ROW_COUNT=" + rowCount);
//            MyLog.i("KEY_SCALE=" + scale);
//            MyLog.i("PARAM_EDIT_TEXT=[" + text + "]");
//
//            MyLog.i("bitmap_origin_scale_size=" + stickers.get(focusStickerPosition).getScaleSize());
//            MyLog.i("------------------------刷新文字气泡text内容 refreshTextPop end------------------------");
            doScale(focusStickerPosition, (float) scale);

            ArrayList<BubbleText> bubbleTextArrayList = data.getParcelableArrayListExtra(Constants.KEY_BUBBLE_TEXT_LIST);
            stickers.get(focusStickerPosition).setBubbleTextList(bubbleTextArrayList);
            stickers.get(focusStickerPosition).getStickerTextInfo().setText(text);

            invalidate();
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    private void onMove(boolean canMode) {
        if (mOnStickMoveClickListener != null) {
            mOnStickMoveClickListener.onStickMove(canMode);
        }
    }

    public boolean isColorTxtOrVoiceStickerExist() {
        if (stickers == null || stickers.isEmpty())
            return false;
        for (Sticker sticker : stickers) {
            if (StickerUtils.isVoice(sticker) || StickerUtils.isColorText(sticker)) {
                return true;
            }
        }
        return false;
    }

    public boolean isColorTxtStickerSelected() {
        if (stickers == null || stickers.isEmpty())
            return false;
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return false;
        return StickerUtils.isColorText(stickers.get(focusStickerPosition));
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public int getFocusStickerPosition() {
        return focusStickerPosition;
    }

    /**
     * 锁定贴纸【图层】
     * 2.7版本后变为 锁定/解锁贴纸【锁定后不能再移动,只能解锁; 解锁后可以移动和操作了】
     */
    public void doLock() {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return;
        if (stickers.get(focusStickerPosition).isLockedByParent())
            return;
        boolean locked = stickers.get(focusStickerPosition).isLocked();
        stickers.get(focusStickerPosition).setLocked(!locked);
        invalidate();
        mInLock = false;
    }

    /**
     * 获取贴纸【图层】锁定状态
     */
    public boolean getLock() {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return false;
        return stickers.get(focusStickerPosition).isLocked();
    }

    /**
     * 获取贴纸【图层】的原始锁定状态【是否被前作者锁定】
     */
    public boolean getLockByParent() {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return false;
        return stickers.get(focusStickerPosition).isLockedByParent();
    }

    /**
     * 删除所选贴纸
     */
    private void doDeleteSticker() {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return;
        //要回调删除了声音贴纸给PhtotProcessActivity
        if (StickerUtils.isVoice(stickers.get(focusStickerPosition).getStickerType())) {
            if (mOnStickMoveClickListener != null) {
                mOnStickMoveClickListener.onDeleteAudio(true);
            }
        }
        stickers.remove(focusStickerPosition);
//        MyLog.i("doDeleteSticker ~ thenn refocus");
        refocus();

        invalidate();
    }

    /**
     * 重新选择焦点
     */
    private void refocus() {
        focusStickerPosition = stickers.size() - 1;
        // 如果贴纸没有了,刷新编辑界面的title
        if (focusStickerPosition == -1) {
            setFocusSticker(focusStickerPosition);
        }
        for (int i = stickers.size() - 1; i >= 0; i--) {
            if (stickers.get(i).isLockedByParent()) {
                focusStickerPosition = i - 1;
                continue;
            }
            focusStickerPosition = i;
            MyLog.i("refocus~focusStickerPosition=" + focusStickerPosition);
            setFocusSticker(focusStickerPosition);
            break;
        }

        onMove(!(stickers.size() > 1 && focusStickerPosition >= 0));
    }


    /**
     * 图片水平反转
     * @param invalidate
     */
    public void doReversalHorizontal(boolean invalidate) {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return;
        float[] floats = new float[]{
                -1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f};
        Matrix tmpMatrix = new Matrix();
        tmpMatrix.setValues(floats);
        stickers.get(focusStickerPosition).setBitmapWithMatrix(tmpMatrix, true);
//        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
//                mBitmap.getHeight(), tmpMatrix, true);
        if (invalidate) {
            invalidate();
        }
        mInReversalHorizontal = false;
    }


    /**
     * 图片垂直反转
     * @param invalidate
     */
    public void doReversalVertical(boolean invalidate) {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return;
        float[] floats = new float[]{
                1f, 0f, 0f,
                0f, -1f, 0f,
                0f, 0f, 1f};
        Matrix tmpMatrix = new Matrix();
        tmpMatrix.setValues(floats);
        stickers.get(focusStickerPosition).setBitmapWithMatrix(tmpMatrix, false);
//        Bitmap tmp = stickers.get(focusStickerPosition).getBitmap();
//        tmp = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(),
//                tmp.getHeight(), tmpMatrix, true);
//        stickers.get(focusStickerPosition).setBitmap(tmp);

//        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
//                mBitmap.getHeight(), tmpMatrix, true);
        if (invalidate) {
            invalidate();
        }
        mInReversalVertical = false;
    }

    private boolean canStickerMove(float cx, float cy) {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return false;
        float px = cx + stickers.get(focusStickerPosition).getMapPointsDst()[8];
        float py = cy + stickers.get(focusStickerPosition).getMapPointsDst()[9];
        if (mViewRect.contains(px, py)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 计算左上角到中心点位置
     */
    private float caculateLength(float x, float y) {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return 0f;
        float cx = x - stickers.get(focusStickerPosition).getMapPointsDst()[8];
        float cy = y - stickers.get(focusStickerPosition).getMapPointsDst()[9];
        return FloatMath.sqrt(cx * cx + cy * cy);
//        return (float)Utils.lineSpace(x, y, stickers.get(focusStickerPosition).getMapPointsDst()[8], stickers.get(focusStickerPosition).getMapPointsDst()[9]);
    }


    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return 0f;
        double delta_x = x - stickers.get(focusStickerPosition).getMapPointsDst()[8];
        double delta_y = y - stickers.get(focusStickerPosition).getMapPointsDst()[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 是否点击在贴纸区域
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isFocusSticker(float x, float y) {
//        MyLog.i("isFocusSticker start");
        for (int i = stickers.size() - 1; i >= 0; i--) {
            Sticker sticker = stickers.get(i);
            if ((isInContentNew(x, y, sticker)) && !sticker.isLockedByParent()) {
                setFocusSticker(i);
//                MyLog.i("isFocusSticker end " + i);
                return true;
            }
        }
        setFocusSticker(-1);
//        MyLog.i("isFocusSticker end  -1");
        return false;
    }

    private boolean judgeFailed(float[] rect) {
        if(boundIndexError()) {
            return true;
        }
        if(rect == null) {
            return true;
        }
        return false;
    }

    /**是否越界*/
    private boolean boundIndexError() {
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size()) {
            return true;
        }
        return false;
    }

    /**判断点是否在content所在的边框区域内*/
    private boolean isInContentRect(float x, float y, Sticker sticker) {
        float[] rect = getRectMap(sticker.getMapPointsDst(), false);
        RectF rectF = new RectF(rect[0],
                rect[1],
                rect[2],
                rect[3]);
        if (rectF.contains(x, y)) {
//            MyLog.i("isInContentRect contains xy=[" + x + "," + y + "] ");
            return true;
        }
//        MyLog.i("isInContentRect NG xy=[" + x + "," + y + "] " + rectF.toShortString());
        return false;
    }

    /**
     * 判断点是否在内容指定区域内
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInContent(float x, float y, Sticker currentSticker) {
        // 点击虚拟白框区域也可以选中
//        if (isInContentRect(x, y, currentSticker)) {
//            return true;
//        }
        long startTime = System.currentTimeMillis();
        float[] pointsDst = currentSticker.getMapPointsDst();
//        MyLog.i("isInContent start xy=[" + x + "," + y + "] pointsDst=" + pointsDst.toString());

//        判断是否在宽度一半距离内
//        MyLog.i("isInContent start xy=[" + x + "," + y + "] pointsDst=" + pointsDst);
        PointD pointF_1 = Point2D.getMidpointCoordinate(pointsDst[0], pointsDst[1], pointsDst[2], pointsDst[3]);
        double a1 = Point2D.distance(pointsDst[8], pointsDst[9], pointF_1.getX(), pointF_1.getY());
        double b1 = Point2D.distance(pointsDst[8], pointsDst[9], x, y);
        if (b1 <= a1) {
//            MyLog.i("isInContent1 OK xy=[" + x + "," + y + "] ");
            return true;
        }
        // s1是三角形面积的海伦公式. 2*s1相当于将这个三角形拼成一个平行四边形,那么如果面积比a1*a1大,(x,y)肯定在外头了
        double c1 = Point2D.distance(pointF_1.getX(), pointF_1.getY(), x, y);
        double p1 = (a1 + b1 + c1) / 2;
        double s1 = Math.sqrt(p1 * (p1 - a1) * (p1 - b1) * (p1 - c1));
        double d1 = 2 * s1 / a1;
        if (d1 > a1) {
//            MyLog.i("isInContent2 NG xy=[" + x + "," + y + "] ");
            return false;
        }

        // 另一侧的计算
        PointD pointF_2 = Point2D.getMidpointCoordinate(pointsDst[2], pointsDst[3], pointsDst[4], pointsDst[5]);
        double a2 = a1;
        double b2 = b1;
        double c2 = Point2D.distance(pointF_2.getX(), pointF_2.getY(), x, y);
        double p2 = (a2 + b2 + c2) / 2;
        double temp = p2 * (p2 - a2) * (p2 - b2) * (p2 - c2);
        double s2 = Math.sqrt(temp);
        double d2 = 2 * s2 / a2;
        if (d2 > a1) {
//            MyLog.i("isInContent3 NG xy=[" + x + "," + y + "] ");
            return false;
        }
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        if (d1 <= a1 && d2 <= a1) {
//            MyLog.i("isInContent4  OK xy=[" + x + "," + y + "] ");
            return true;
        }

//        MyLog.i("isInContent9 NG xy=[" + x + "," + y + "] ");
        return false;
    }

    private final Path mPath = new Path();
    private final Region mRegion = new Region();
    /**
     * 判断点是否在Sticker内容指定区域内
     *
     */
    private boolean isInContentNew(float x, float y, Sticker sticker) {
        mPath.reset();
        float[] tmp = sticker.getMapPointsDst();
        mPath.moveTo(tmp[0], tmp[1]);
        for (int j = 0; j < 4; j++) {
            mPath.lineTo(tmp[(2 * j + 2) % 8], tmp[(2 * j + 3) % 8]);
        }
        mPath.close();
        //构造一个区域对象，左闭右开的。
        RectF r = new RectF();
        //计算控制点的边界
        mPath.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        mRegion.setPath(mPath, new Region((int) r.left, (int) r.top, (int) r.right, (int)r.bottom));
        //在封闭的path内返回true 不在返回false
        return mRegion.contains((int)x, (int)y);
    }


    public void saveBitmapToFile() {
        int bgWidth = bgBitmap.getWidth();
        int bgHeight = bgBitmap.getHeight();
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(bgBitmap, 0, 0, null);
        for (int i = 0; i < stickers.size(); i++) {
            cv.drawBitmap(stickers.get(i).getBitmap(), stickers.get(i).getmMatrix(), null);

        }
//        cv.drawBitmap(stickers.get(focusStickerPosition).getBitmap(), stickers.get(focusStickerPosition).getmMatrix(), null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
//        bgBitmap = newbmp;
        try {
            saveBitmapToSDCardString(mContext, newbmp, null, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }


        stickers.clear();
        focusStickerPosition = -1;
        invalidate();
    }


    /**
     * 通过绝对地址获取Bitmap
     * use ImageLoader.getInstance().decodeSampledBitmapFromResource();
     *
     * @param filePath
     * @return
     */

    public static String saveBitmapToSDCardString(Context context, Bitmap bitmap, String filePath, int quality) throws IOException {
        if (bitmap != null) {
            String BITMAP_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Moin/MOIN/";
            String md5_path = "stickdemo.jpg";
            File file = new File(BITMAP_DOWNLOAD);
            if (!file.exists())
                file.mkdirs();
            FileOutputStream fos = new FileOutputStream(BITMAP_DOWNLOAD + md5_path);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            byte[] bytes = stream.toByteArray();
            fos.write(bytes);
            fos.close();

            return BITMAP_DOWNLOAD + md5_path;
        } else {
            return "";
        }

    }

    /**
     * 获取焦点贴纸
     *
     * @param position
     */
    private int getFocusSticker(int position) {
        int focusPosition = position;
        for (int i = 0; i < stickers.size(); i++) {
            if (i == position) {
                focusPosition = i;
                stickers.get(i).setFocusable(true);
            } else {
                stickers.get(i).setFocusable(false);
            }
        }
        return focusPosition;
    }

    /**
     * 设置焦点贴纸
     *
     * @param position
     */
    private void setFocusSticker(int position) {
//        MyLog.i("setFocusSticker START position " + position + ", focusPos=" + focusStickerPosition);
        focusStickerPosition = getFocusSticker(position);

        if (mOnStickMoveClickListener != null) {
            if (focusStickerPosition < 0) {
                mOnStickMoveClickListener.onLayerEnable(false);
                lockable = false;
                upable = false;
                downable = false;
            } else {
                mOnStickMoveClickListener.onLayerEnable(true);
                lockable = false;
                mOnStickMoveClickListener.onLockEnable(getLock());

                int topPosition = containAudio() ? stickers.size() - 2 : stickers.size() - 1;
                if (stickers.size() == 1) {
                    //only one layer
                    upable = false;
                    downable = false;
                    mOnStickMoveClickListener.onUpwardEnable(false);
                    mOnStickMoveClickListener.onDownwardEnable(false);
                } else if (focusStickerPosition == topPosition && focusStickerPosition > 0) {
                    //top layer
                    mOnStickMoveClickListener.onUpwardEnable(false);
                    mOnStickMoveClickListener.onDownwardEnable(true);
                    upable = false;
                    downable = true;
                } else if (focusStickerPosition < topPosition && focusStickerPosition > 0) {
                    //mid layer
                    mOnStickMoveClickListener.onUpwardEnable(true);
                    mOnStickMoveClickListener.onDownwardEnable(true);
                    upable = true;
                    downable = true;
                } else if (focusStickerPosition < topPosition && focusStickerPosition == 0) {
                    //bottom layer
                    mOnStickMoveClickListener.onUpwardEnable(true);
                    mOnStickMoveClickListener.onDownwardEnable(false);
                    upable = true;
                    downable = false;
                }
            }
        }
//        MyLog.i("setFocusSticker END position " + position + ", focusPos=" + focusStickerPosition);
    }

    /**
     * 声音贴纸永远保持在最顶层
     */
    private void adjustAudioSticker() {
        Sticker audio = null;
        for (int i = 0; i < stickers.size(); i++) {
            if (StickerUtils.isVoice(stickers.get(i).getStickerType())) {
                audio = stickers.remove(i);
                break;
            }
        }
        if (audio != null) {
            stickers.add(audio);
        }
    }

    /*
     * 是否包含声音贴纸
     */
    public boolean containAudio() {
        if (stickers == null || stickers.size() < 1) return false;
        for (Sticker sticker : stickers) {
            if (StickerUtils.isVoice(sticker.getStickerType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 向上向下
     */
    public void zOrder(boolean isUpward) {
//        MyLog.i("zOrder isUP " + isUpward + ", focusPos=" + focusStickerPosition);
        if (focusStickerPosition < 0 || focusStickerPosition >= stickers.size())
            return;
        if (!upable && isUpward || !downable && !isUpward)
            return;
        Sticker sticker = stickers.remove(focusStickerPosition);
        focusStickerPosition = focusStickerPosition + (isUpward ? 1 : -1);
        stickers.add(focusStickerPosition, sticker);
        setFocusSticker(focusStickerPosition);
        invalidate();
    }

    public void setmOnStickMoveClickListener(OnStickMoveClickListener mOnStickMoveClickListener) {
        this.mOnStickMoveClickListener = mOnStickMoveClickListener;
    }

    OnStickMoveClickListener mOnStickMoveClickListener;
    boolean lockable, upable, downable;

    public interface OnStickMoveClickListener {
        void onStickMove(boolean isMoving);

        void onLayerEnable(boolean enable);

        void onLockEnable(boolean enable);

        void onUpwardEnable(boolean enable);

        void onDownwardEnable(boolean enable);

        void onDeleteAudio(boolean enable);
    }
}

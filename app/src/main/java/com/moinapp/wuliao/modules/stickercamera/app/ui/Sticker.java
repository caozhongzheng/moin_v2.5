package com.moinapp.wuliao.modules.stickercamera.app.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.keyboard.bean.EmoticonBean;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.model.StickerColorTextInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerEditInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerTextInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.BubbleText;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 大咖秀编辑时用的贴纸类
 * Created by Yangchen on 2015/9/7.
 */
public class Sticker {
    ILogger MyLog = LoggerFactory.getLogger("st");
    /**
     * 表情ID
     */
    private String id;

    /**
     * 所属表情专辑ID
     */
    private String parentId;
    /**
     * 2.7新加入的贴纸唯一id
     */
    private String stickerId;

    private String picUrl;

    private boolean mirrorH;//是否水平镜像
    private boolean mirrorV;//是否垂直镜像

    private Bitmap bitmap;

    private int zoom;

    /**贴纸原始大小的rectF*/
    private RectF mOriginContentRect;
    /**盛放贴纸变化后的rectF，从 mOriginContentRect 拷贝来*/
    private RectF mContentRect;

    /**
     * 是否获取焦点
     */
    private boolean focusable;

    /**
     * 是否被锁
     */
    private boolean locked;

    /**
     * 是否被前面的作者锁定
     */
    private boolean lockedByParent;

    private Matrix mMatrix;

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 边框画笔
     */
    private Paint mBorderPaint;
    /**长度10的float数组，盛放原始贴纸四边的float值*/
//    private float[] mOriginPoints;
    private float[] mapPointsSrc;
    /**长度10的float数组，盛放变化后贴纸四边的float值*/
//    private float[] mPoints;
    private float[] mapPointsDst = new float[10];

    private float scaleSize = 1.0f;

    private float rotation = 0.0f;

    private int stickerType;

    private long audioLength;
    private String audioUri;

    private StickerEditInfo stickerEditInfo;

    private StickerTextInfo stickerTextInfo;

    private StickerColorTextInfo stickerColorTextInfo;

    // TODO 一个文字气泡贴纸内的每行文字描述
    private ArrayList<BubbleText> bubbleTextList;

    public Sticker(Bitmap bitmap, int bgWidth, int bgHeight, List<PointD> prepoints, boolean isLockByParent, EmoticonBean bean) {
        this.bitmap = bitmap;
        this.lockedByParent = isLockByParent;
        this.id = bean.getId();
        this.parentId = bean.getParentId();
        this.stickerId = bean.getStickerId();
        this.picUrl = bean.getGifUrl();
        this.stickerType = bean.getStickType();
        this.zoom = bean.getZoom();
        this.audioLength = bean.getAudioLength();
        if (StickerUtils.isVoice(stickerType)) {
            this.audioUri = bean.getContent();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setColor(Color.WHITE);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2.0f);
        mBorderPaint.setColor(Color.WHITE);

        mMatrix = new Matrix();
        float initLeft = (bgWidth - bitmap.getWidth()) / 2;
        float initTop = (bgHeight - bitmap.getHeight()) / 2;
        if(prepoints != null && prepoints.size() > 0) {
            float deltaX = initLeft - (float) prepoints.get(prepoints.size()-1).getX();
            float deltaY = initTop - (float) prepoints.get(prepoints.size()-1).getY();
            if(Math.abs(deltaX) < 20 && Math.abs(deltaY) < 20) {
                initLeft += 10;
                initTop += 10;
            }
        }
        mMatrix.postTranslate(initLeft, initTop);

        float px = bitmap.getWidth();
        float py = bitmap.getHeight();
        // 盛放左上角(0,0),右上角(px, 0),右下角(px,py),左下角(0, py),中心点(px / 2, py / 2)的坐标
        mapPointsSrc = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};

        setRectPaint();

        mOriginContentRect = new RectF(0, 0, px, py);
        mContentRect = new RectF();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStickerId() {
        return stickerId;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isMirrorH() {
        return mirrorH;
    }

    public void setMirrorH(boolean mirrorH) {
        this.mirrorH = mirrorH;
    }

    public boolean isMirrorV() {
        return mirrorV;
    }

    public void setMirrorV(boolean mirrorV) {
        this.mirrorV = mirrorV;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public RectF getmOriginContentRect() {
        return mOriginContentRect;
    }

    public RectF getmContentRect() {
        return mContentRect;
    }
    public float[] getMapPointsDst() {
        return mapPointsDst;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public Paint getmBorderPaint() {
        return mBorderPaint;
    }

    public long getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(long audioLength) {
        this.audioLength = audioLength;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    Paint mRectPaint;
    public Paint getRectPaint() {
        return mRectPaint;
    }

    public void setRectPaint() {

        Paint mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setFilterBitmap(true);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(2.0f);
        mRectPaint.setColor(Color.BLUE);

    }

    public float[] getMapPointsSrc() {
        return mapPointsSrc;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLockedByParent() {
        return lockedByParent;
    }

    public void setLockedByParent(boolean lockedByParent) {
        this.lockedByParent = lockedByParent;
    }

    public void setBitmapWithMatrix(Matrix mMatrix, boolean isHorizontal) {
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), mMatrix, true);
        if (isHorizontal) {
            toggleHorizontal();
        } else {
            toggleVertical();
        }
    }

    private void toggleHorizontal() {
        if(isMirrorH()) {
            setMirrorH(false);
        } else {
            setMirrorH(true);
        }
    }

    private void toggleVertical() {
        if(isMirrorV()) {
            setMirrorV(false);
        } else {
            setMirrorV(true);
        }
    }

    public void setMapPointsSrc(float[] mapPointsSrc) {
        this.mapPointsSrc = mapPointsSrc;

    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public int getStickerType() {
        return stickerType;
    }

    public void setStickerType(int stickerType) {
        this.stickerType = stickerType;
    }

    public StickerEditInfo getStickerEditInfo() {
        return stickerEditInfo;
    }

    public void setStickerEditInfo(StickerEditInfo stickerEditInfo) {
        this.stickerEditInfo = stickerEditInfo;
    }

    public StickerTextInfo getStickerTextInfo() {
        return stickerTextInfo;
    }

    public void setStickerTextInfo(StickerTextInfo stickerTextInfo) {
        this.stickerTextInfo = stickerTextInfo;
    }

    public StickerColorTextInfo getStickerColorTextInfo() {
        return stickerColorTextInfo;
    }

    public void setStickerColorTextInfo(StickerColorTextInfo stickerColorTextInfo) {
        this.stickerColorTextInfo = stickerColorTextInfo;
    }

    public ArrayList<BubbleText> getBubbleTextList() {
        return bubbleTextList;
    }

    public void setBubbleTextList(ArrayList<BubbleText> bubbleTextList) {
        this.bubbleTextList = bubbleTextList;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public void setmBorderPaint(Paint mBorderPaint) {
        this.mBorderPaint = mBorderPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public void setRotation(float rotation) {
        if(rotation > 180) {
            this.rotation = rotation - 360;
        } else {
            this.rotation = rotation;
        }
    }

    public float getRotation() {
        return (rotation + 360) % 360;
    }

    public void addRotation(float rotation) {
        this.rotation += rotation;
//        MyLog.i("rotation = " + this.rotation);
    }

    @Override
    public String toString() {
        return "Sticker{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", stickerId='" + stickerId + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", mirrorH=" + mirrorH +
                ", mirrorV=" + mirrorV +
                ", zoom=" + zoom +
                ", focusable=" + focusable +
                ", locked=" + locked +
                ", lockedByParent=" + lockedByParent +
                ", scaleSize=" + scaleSize +
                ", rotation=" + rotation +
                ", stickerType=" + stickerType +
                ", stickerTextInfo=" + stickerTextInfo +
                ", stickerColorTextInfo=" + stickerColorTextInfo +
                ", stickerEditInfo=" + stickerEditInfo +
                '}';
    }
}

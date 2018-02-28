package com.moinapp.wuliao.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;

/**
 * 照相机井字线
 * Created by sky on 2015/7/7.
 */
public class CameraGrid extends View {

    private int gridWidth = AppContext.getApp().getScreenWidth();
    private int marginTop = 0;
    private int topBannerWidth = 0;

    private Paint mPaint;

    public CameraGrid(Context context) {
        this(context,null);
    }

    public CameraGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        marginTop = context.getResources().getDimensionPixelOffset(R.dimen.camera_top_h);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(120);
        mPaint.setStrokeWidth(1f);
    }


    //画一个井字,上下画两条灰边，中间为正方形
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = gridWidth;
        int height = gridWidth;
        if (width < height) {
            topBannerWidth = height - width;
        }
        if (showGrid) {
            // 竖着的两条线
            canvas.drawLine(width / 3, 0 + marginTop, width / 3, height + marginTop, mPaint);
            canvas.drawLine(width * 2 / 3, 0 + marginTop, width * 2 / 3, height + marginTop, mPaint);
            // 横着的两条线
            canvas.drawLine(0, height / 3 + marginTop, width, height / 3 + marginTop, mPaint);
            canvas.drawLine(0, height * 2 / 3 + marginTop, width, height * 2 / 3 + marginTop, mPaint);
        }
    }

    private boolean showGrid = true;

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public int getTopWidth() {
        return topBannerWidth;
    }
}

package com.moinapp.wuliao.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;

/**
 * 照相机边框
 */
public class CameraBorder extends View {

    private int gridWidth = AppContext.getApp().getScreenWidth();
    private int marginTop = 0;
    private int borderWidth = 0;

    private Paint mPaint;

    public CameraBorder(Context context) {
        this(context, null);
    }

    public CameraBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        marginTop = context.getResources().getDimensionPixelOffset(R.dimen.camera_top_h);
        borderWidth = context.getResources().getDimensionPixelOffset(R.dimen.height_28px);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(120);
    }

    private Paint getLinePaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(120);
        paint.setStrokeWidth(1f);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = gridWidth;
        int height = gridWidth;
        // 竖着的边框
        canvas.drawRect(new Rect(0, marginTop, borderWidth, height + marginTop), mPaint);
        canvas.drawRect(new Rect(width - borderWidth, marginTop, width, height + marginTop), mPaint);
        // 横着的边框
        canvas.drawRect(new Rect(borderWidth, marginTop, width - borderWidth, borderWidth + marginTop), mPaint);
        canvas.drawRect(new Rect(borderWidth, marginTop + height - borderWidth, width - borderWidth, height + marginTop), mPaint);

        Paint linePaint = getLinePaint();
        // 竖着的两条线
        canvas.drawLine(borderWidth, marginTop + borderWidth, borderWidth, height - borderWidth + marginTop, linePaint);
        canvas.drawLine(width - borderWidth, marginTop + borderWidth, width - borderWidth, height - borderWidth + marginTop, linePaint);
        // 横着的两条线
        canvas.drawLine(borderWidth, marginTop + borderWidth, width - borderWidth, marginTop + borderWidth, linePaint);
        canvas.drawLine(borderWidth, height - borderWidth + marginTop, width - borderWidth, height - borderWidth + marginTop, linePaint);
    }

}

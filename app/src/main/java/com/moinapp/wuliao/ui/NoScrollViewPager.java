package com.moinapp.wuliao.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by guyunfei on 16/1/27.17:20.
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //重写此方法,触摸时什么都不做,从而实现对滑动时间的禁用
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    //不拦截子控件的事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}

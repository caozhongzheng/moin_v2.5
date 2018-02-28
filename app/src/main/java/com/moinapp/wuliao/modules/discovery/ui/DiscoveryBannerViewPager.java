package com.moinapp.wuliao.modules.discovery.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 发现频道banner
 * Created by guyunfei on 15/12/30.13:45.
 */
public class DiscoveryBannerViewPager extends ViewPager {

    private int startX;
    private int startY;

    public DiscoveryBannerViewPager(Context context) {
        super(context);
    }

    public DiscoveryBannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    View view = this;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);// 不要拦截,
                // 这样是为了保证ACTION_MOVE调用
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getRawX();
                int endY = (int) ev.getRawY();

                if (Math.abs(endX - startX) > Math.abs(endY - startY)) {// 左右滑动
                    while (!(view instanceof SwipeRefreshLayout)) {
                        view = (View) view.getParent();
                    }
                    view.setEnabled(false);
                    if (endX > startX) {// 右划
                        if (getCurrentItem() == 0) {// 第一个页面, 需要父控件拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {// 左划
                        if (getCurrentItem() == getAdapter().getCount() - 1) {// 最后一个页面,
                            // 需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {// 上下滑动
                    while (!(view instanceof SwipeRefreshLayout)) {
                        view = (View) view.getParent();
                    }
                    view.setEnabled(true);
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                while (!(view instanceof SwipeRefreshLayout)) {
                    view = (View) view.getParent();
                }
                view.setEnabled(true);
                getParent().requestDisallowInterceptTouchEvent(false);
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}

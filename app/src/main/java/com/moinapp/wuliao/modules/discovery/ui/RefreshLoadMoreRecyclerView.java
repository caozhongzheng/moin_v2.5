package com.moinapp.wuliao.modules.discovery.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * 可以加载更多的RecyclerView
 * Created by guyunfei on 16/6/30.14:50.
 */
public class RefreshLoadMoreRecyclerView extends RecyclerView {

    private static final ILogger MyLog = LoggerFactory.getLogger(RefreshLoadMoreRecyclerView.class.getSimpleName());

    //首次进入
    private boolean mFirstEnter = true;

    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public RefreshLoadMoreRecyclerView(Context context) {
        super(context);
    }

    public RefreshLoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshLoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (!canScrollDown(this)) {
            onLoadMoreListener.onLoadMore();
        }

        if (mFirstEnter) {
            mFirstEnter = false;
        }
    }

    /**
     * RecycleView是否可以滑动
     */
    private boolean canScrollDown(RecyclerView recyclerView) {
        return ViewCompat.canScrollVertically(recyclerView, 1);
    }

}

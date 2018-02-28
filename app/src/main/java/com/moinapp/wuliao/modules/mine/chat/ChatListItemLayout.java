package com.moinapp.wuliao.modules.mine.chat;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 聊天列表页Item布局
 * Created by guyunfei on 16/4/10.21:00.
 */
public class ChatListItemLayout extends FrameLayout {

    private View contentView; //内容布局
    private View deleteView;//删除区域布局
    private int deleteHeight;//删除区域高度
    private int deleteWidth;//删除区域宽度
    private int contentWidth;  //内容区域宽度

    private ViewDragHelper viewDragHelper;
    private float downX;
    private float downY;

    private State currentState = State.close;

    enum State {
        open, close;
    }

    public ChatListItemLayout(Context context) {
        super(context);
        init();
    }

    public ChatListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        contentView.layout(0, 0, contentWidth, deleteHeight);
        deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取宽高
        deleteHeight = deleteView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
        contentWidth = contentView.getMeasuredWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        //如果当前有打开的,则需要直接拦截,交给OnTouch处理
        if (!ChatLayoutManager.getInstance().isShouldSwipe(this)) {
            // 要滑动的不是已经打开的布局,先关闭已打开的,不要放到onTouch中,可能会造成UI卡顿
            ChatLayoutManager.getInstance().closeCurrentLayout();
            result = true;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (stickerSort){
            return false;
        }
        //如果有打开的布局,不可以滑动
        if (!ChatLayoutManager.getInstance().isShouldSwipe(this)) {
            requestDisallowInterceptTouchEvent(true);
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float x = moveX - downX;//X方向移动距离
                float y = moveY - downY;//Y方向移动距离
                if (Math.abs(x) > Math.abs(y)) {
                    // 表示移动是偏向于水平方向,那么本布局处理,请求父控件不处理
                    requestDisallowInterceptTouchEvent(true);
                }else {
                    // 表示垂直移动,关闭打开的布局
                    ChatLayoutManager.getInstance().closeCurrentLayout();
                }

                //更新downX和downY
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        // 触摸事件交给viewDragHelper处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }
    public boolean stickerSort = false;

    public void setStickerSort(boolean b){
        stickerSort = b;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //监听滑动
            return child == contentView || child == deleteView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            //拖拽范围
            return deleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //控制移动
            if (child == contentView) {
                if (left > 0) left = 0;
                if (left < -deleteWidth) left = -deleteWidth;
            } else if (child == deleteView) {
                if (left > contentWidth) left = contentWidth;
                if (left < (contentWidth - deleteWidth)) left = contentWidth - deleteWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            // 伴随移动
            if (changedView == contentView) {
                //手动移动deleteView
                deleteView.layout(deleteView.getLeft() + dx, deleteView.getTop() + dy,
                        deleteView.getRight() + dx, deleteView.getBottom() + dy);
            } else if (changedView == deleteView) {
                //手动移动contentView
                contentView.layout(contentView.getLeft() + dx, contentView.getTop() + dy,
                        contentView.getRight() + dx, contentView.getBottom() + dy);
            }

            //判断deleteView是打开还是关闭
            if (contentView.getLeft() == 0 && currentState != State.close) {
                //关闭状态
                currentState = State.close;

                //当前布局已经关闭,让Manager清空
                ChatLayoutManager.getInstance().clearCurrentLayout();
            } else if (contentView.getLeft() == -deleteWidth && currentState != State.open) {
                //打开状态
                currentState = State.open;

                //当前布局已经打开,让manager记录
                ChatLayoutManager.getInstance().setChatItmeLayout(ChatListItemLayout.this);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -deleteWidth / 2) {
                // 打开删除区域
                open();
            } else {
                // 关闭删除区域
                close();
            }
        }
    };

    public void open() {
        viewDragHelper.smoothSlideViewTo(contentView, -deleteWidth, contentView.getTop());
        //刷新
        ViewCompat.postInvalidateOnAnimation(ChatListItemLayout.this);
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        //刷新
        ViewCompat.postInvalidateOnAnimation(ChatListItemLayout.this);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}

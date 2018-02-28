package com.moinapp.wuliao.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * 实现ListView下拉放大顶部图片的效果
 * Created by guyunfei on 16/6/13.10:07.
 */
public class ParallaxListView extends ListView implements AbsListView.OnScrollListener{

    private static final ILogger MyLog = LoggerFactory.getLogger(ParallaxListView.class.getSimpleName());

    private float startY = 0;
    private float allMoveY = 0;

    public ParallaxListView(Context context) {
        super(context);
        initView();
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (getAdapter() == null) {
                    return;
                }
                // 暂时设为当下拉到距离底部最后一个元素还有2个时开始加载下一页的数据
                if (view.getLastVisiblePosition() >= getAdapter().getCount() - 1 - 2) {
                    listener.onLoadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private int maxHeight;
    private ImageView imageView;
    private int orignalHeight;//ImageView最初的高度

    public void setParallaxImageView(final ImageView imageView) {
        this.imageView = imageView;

        //设定最大高度
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                orignalHeight = imageView.getHeight();
                maxHeight = (int) (orignalHeight * 3);
            }
        });

    }

    /**
     * 在listview滑动到头的时候执行，可以获取到继续滑动的距离和方向
     * deltaX：继续滑动x方向的距离
     * deltaY：继续滑动y方向的距离     负：表示顶部到头   正：表示底部到头
     * maxOverScrollX:x方向最大可以滚动的距离
     * maxOverScrollY：y方向最大可以滚动的距离
     * isTouchEvent: true: 是手指拖动滑动     false:表示fling靠惯性滑动;
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (deltaY < 0 && isTouchEvent) {
            //表示顶部到头，并且是手动拖动到头的情况
            //我们需要不断的增加ImageView的高度
            if (imageView != null) {
                listener.onTouchMove();

                int newHeight = imageView.getHeight() - deltaY / 2;
                if (newHeight > maxHeight) newHeight = maxHeight;

                imageView.getLayoutParams().height = newHeight;
                imageView.requestLayout();//使ImageView的布局参数生效
            }
        }

        if (deltaY >= 0 && isTouchEvent) {
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = (int) ev.getRawY();
                if ((endY - startY) < 0) {
                    //向上滑
//                    if (imageView.getHeight() > orignalHeight) {
//                        int newHeight = imageView.getHeight() + (endY - startY) * 2;
//                        if (newHeight > maxHeight) newHeight = maxHeight;
//
//                        imageView.getLayoutParams().height = newHeight;
//                        imageView.requestLayout();//使ImageView的布局参数生效
//                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                listener.onTouchUp();

                if (imageView.getHeight() > (orignalHeight * 1.2)) {
                    listener.onRefresh();
                }
                //需要将ImageView的高度缓慢恢复到最初高度
                ValueAnimator animator = ValueAnimator.ofInt(imageView.getHeight(), orignalHeight);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        //获取动画的值，设置给imageview
                        int animatedValue = (Integer) animator.getAnimatedValue();

                        imageView.getLayoutParams().height = animatedValue;
                        imageView.requestLayout();//使ImageView的布局参数生效
                    }
                });
                animator.setInterpolator(new OvershootInterpolator(1));//弹性的差值器
                animator.setDuration(350);
                animator.start();

                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public OnRefreshListener listener;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();

        void onTouchMove();

        void onTouchUp();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

}

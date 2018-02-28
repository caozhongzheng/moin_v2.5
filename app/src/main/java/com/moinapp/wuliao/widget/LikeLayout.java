package com.moinapp.wuliao.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.BackgroundService;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.MultiClickEvent;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;

/**
 * Created by guyunfei on 16/3/30.12:45.
 */
public class LikeLayout extends LinearLayout {
    private static final int DEFAULT_ANIM_TIME = 300;
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#ffffff");
    private Context context;
    private float textSize;
    private int textColor = DEFAULT_TEXT_COLOR;
    private TextView mTvLikeNum;
    private ImageView mIvLike;
    private View mBackground;

    private int mLikeNum = 0;
    private int mAddedNum = 0;
    private Object object;

    public LikeLayout(Context context) {
        super(context);
        initUI(context, null);
    }

    public LikeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    public LikeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.LikeView);
            textColor = arr.getColor(R.styleable.LikeView_likeTxtColor, DEFAULT_TEXT_COLOR);
            textSize = arr.getDimension(R.styleable.LikeView_likeTxtSize, 10f);

            View rootView = LayoutInflater.from(context).inflate(R.layout.layout_like, this, true);
            mTvLikeNum = (TextView) rootView.findViewById(R.id.tv_like_num_on_cosplay);
            mIvLike = (ImageView) rootView.findViewById(R.id.iv_like);
            mBackground = rootView.findViewById(R.id.view_bg);

            setBackgroundImage(R.drawable.like_bg);
            setLikeImage(R.drawable.icon_like_on_cosplay);
            mTvLikeNum.setTextColor(textColor);
            if (textSize > 0 && textSize != 10) {
                mTvLikeNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            if (isInEditMode()) {
                return;
            }

            arr.recycle();
        }
    }

    public void setBackgroundImage(int resid) {
        mBackground.setBackgroundResource(resid);
    }

    public void setLikeImage(int resid) {
        mIvLike.setBackgroundResource(resid);
    }

    public void setTextColor(int color) {
        mTvLikeNum.setTextColor(color);
    }

    /**
     * 设置点赞的对象 目前支持 图片CosplayInfo, 话题TagPop
     */
    public void setContent(Object obj) {
        object = obj;
        if (obj instanceof CosplayInfo) {
            CosplayInfo cosplay = (CosplayInfo) obj;
            setLikeNum((cosplay.getLikeNum() + BackgroundService.getMultiLikeNum(cosplay)));
        } else if (obj instanceof TagPop) {
            TagPop tagPop = (TagPop) obj;
            setLikeNum((tagPop.getLikeNum() + BackgroundService.getMultiLikeNum(tagPop)));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                showAnim();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void showAnim() {
        if (!AppContext.getInstance().isLogin()) {
            AppContext.toast(BaseApplication.context(), "先登录再点赞~");
            UIHelper.showLoginActivity(context);
            return;
        }
        ScaleAnimation imageAnim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.5f);
        imageAnim.setDuration(DEFAULT_ANIM_TIME);
        mIvLike.startAnimation(imageAnim);
        ScaleAnimation textViewAnim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f);
        textViewAnim.setDuration(DEFAULT_ANIM_TIME);
        setLikeNum(++mAddedNum + mLikeNum);
        mTvLikeNum.startAnimation(textViewAnim);

        EventBus.getDefault().post(new MultiClickEvent.Builder()
                .setObject(object).setOriginNum(mLikeNum).setClickNum(mAddedNum).build());
    }

    // 拦截子控件的触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private void setLikeNum(int num) {
        mLikeNum = num;
        mAddedNum = 0;
        mTvLikeNum.setText(StringUtil.humanNumber(num));
    }

    public int getClickNum() {
        return mAddedNum;
    }

    public int getOriginNum() {
        return mLikeNum;
    }

    public int getTotalNum() {
        return mLikeNum + mAddedNum;
    }
}

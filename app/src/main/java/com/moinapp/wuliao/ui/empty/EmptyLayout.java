package com.moinapp.wuliao.ui.empty;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;

public class EmptyLayout extends LinearLayout implements
        android.view.View.OnClickListener {// , ISkinUIObserver {

    public static final int HIDE_LAYOUT = 4;
    public static final int NETWORK_ERROR = 1;
    public static final int NETWORK_LOADING = 2;
    public static final int NODATA = 3;
    public static final int NODATA_ENABLE_CLICK = 5;
    public static final int NO_LOGIN = 6;
    public static final int IMG_DELETE = 10;
    public static final int POST_DELETE = 11;

    private ImageView img;
    private ProgressBar animProgress;
    private View title_down_grey_area;
    private TextView tv;
    private TextView tvTitle;
    private Button btn;

    private boolean clickEnable = true;
    private final Context context;
    private android.view.View.OnClickListener listener;
    private int mErrorState;
    private String strNoDataContent = "";
    private SpannableStringBuilder unloginSpan;

    public EmptyLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.view_error_layout, null);
        title_down_grey_area = view.findViewById(R.id.title_down_grey_area);
        img = (ImageView) view.findViewById(R.id.img_error);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        animProgress = (ProgressBar) view.findViewById(R.id.animProgress);
        tv = (TextView) view.findViewById(R.id.tv_content);
        btn = (Button) view.findViewById(R.id.btn_go_to_discovery);
        SpannableString spanStr = new SpannableString(context.getResources().getString(R.string.go2login));
        unloginSpan = new SpannableStringBuilder(spanStr);
        unloginSpan.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                if (AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(context);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.go2login_text));
                ds.setUnderlineText(true);
            }

        }, 3, 5, 0);

        setBackgroundColor(Color.WHITE);
        setOnClickListener(this);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickCallback(v);
            }
        });

        addView(view);
        changeErrorLayoutBgMode(context);
    }

    public void changeErrorLayoutBgMode(Context context1) {
        // mLayout.setBackgroundColor(SkinsUtil.getColor(context1,
        // "bgcolor01"));
        // tv.setTextColor(SkinsUtil.getColor(context1, "textcolor05"));
    }

    public void dismiss() {
        mErrorState = HIDE_LAYOUT;
        setVisibility(View.GONE);
    }

    public int getErrorState() {
        return mErrorState;
    }

    public boolean isLoadError() {
        return mErrorState == NETWORK_ERROR;
    }

    public boolean isLoading() {
        return mErrorState == NETWORK_LOADING;
    }

    @Override
    public void onClick(View v) {
        onClickCallback(v);
    }

    private void onClickCallback(View v) {
        if (clickEnable) {
            // setErrorType(NETWORK_LOADING);
            if (listener != null)
                listener.onClick(v);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // MyApplication.getInstance().getAtSkinObserable().registered(this);
        onSkinChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // MyApplication.getInstance().getAtSkinObserable().unregistered(this);
    }

    public void onSkinChanged() {
        // mLayout.setBackgroundColor(SkinsUtil
        // .getColor(getContext(), "bgcolor01"));
        // tv.setTextColor(SkinsUtil.getColor(getContext(), "textcolor05"));
    }

    public void setDayNight(boolean flag) {}

    public void setErrorMessage(int msg) {
        setErrorMessage(context.getString(msg));
    }

    public void setErrorMessage(String msg) {
        tv.setText(msg);
    }


    public int emptyImgResId;

    /**
     * 设置无数据时的背景
     */
    public void setEmptyImage(int imgResource) {
        emptyImgResId = imgResource;
        if (emptyImgResId == 0) {
            emptyImgResId = R.drawable.no_content_black;
        }
        try {
            img.setBackgroundResource(R.drawable.no_content_black);
        } catch (Exception e) {
        }
    }

    public void setErrorType(int i) {
        setVisibility(View.VISIBLE);

        img.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        animProgress.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);

        switch (i) {
        case NETWORK_ERROR:
            mErrorState = NETWORK_ERROR;
            // img.setBackgroundDrawable(SkinsUtil.getDrawable(context,"pagefailed_bg"));
            if (TDevice.hasInternet()) {
                tv.setText(R.string.error_view_load_error_click_to_refresh);
                img.setBackgroundResource(R.drawable.no_network_black);
            } else {
                tv.setText(R.string.error_view_network_error_click_to_refresh);
                img.setBackgroundResource(R.drawable.no_network_black);
            }
            img.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.error_view_no_network);
            clickEnable = true;
            break;
        case NETWORK_LOADING:
            mErrorState = NETWORK_LOADING;
            // animProgress.setBackgroundDrawable(SkinsUtil.getDrawable(context,"loadingpage_bg"));
            animProgress.setVisibility(View.VISIBLE);
            tv.setText(R.string.error_view_loading);
            clickEnable = false;
            break;
        case NODATA:
            mErrorState = NODATA;
            // img.setBackgroundDrawable(SkinsUtil.getDrawable(context,"page_icon_empty"));
            setEmptyImage(emptyImgResId);
//            img.setBackgroundResource(R.drawable.no_content_black);
            img.setVisibility(View.VISIBLE);
            setTvNoDataContent();
            clickEnable = true;
            break;
        case NODATA_ENABLE_CLICK:
            mErrorState = NODATA_ENABLE_CLICK;
            setEmptyImage(emptyImgResId);
//            img.setBackgroundResource(R.drawable.no_content_black);
            // img.setBackgroundDrawable(SkinsUtil.getDrawable(context,"page_icon_empty"));
            img.setVisibility(View.VISIBLE);
//            btn.setVisibility(VISIBLE);
            setTvNoDataContent();
            clickEnable = true;
            break;
        case NO_LOGIN:
            mErrorState = NO_LOGIN;
            title_down_grey_area.setVisibility(View.VISIBLE);
            img.setBackgroundResource(R.drawable.not_logged_in_black);
            img.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(unloginSpan, TextView.BufferType.SPANNABLE);
            tv.setText(R.string.error_view_unlogin);
            clickEnable = true;
            break;
        case IMG_DELETE:
            mErrorState = IMG_DELETE;
            // img.setBackgroundDrawable(SkinsUtil.getDrawable(context,"page_icon_empty"));
            img.setBackgroundResource(R.drawable.icon_removed);
            img.setVisibility(View.VISIBLE);
            tv.setText(R.string.cosplay_delete);
            clickEnable = true;
            break;
        case POST_DELETE:
            mErrorState = IMG_DELETE;
            img.setBackgroundResource(R.drawable.icon_removed);
            img.setVisibility(View.VISIBLE);
            tv.setText(R.string.post_delete);
            clickEnable = true;
            break;
        case HIDE_LAYOUT:
                setVisibility(View.GONE);
                break;

            default:
            break;
        }
    }

    /**
     * 隐藏登陆btin
     */
    public void hideNoLogin() {
        title_down_grey_area.setVisibility(View.GONE);

        img.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        animProgress.setVisibility(View.VISIBLE);
        btn.setVisibility(View.GONE);
    }

    /**
     * 设置按钮显示
     */
    public void setBtnVisibility(int visibility) {
        btn.setVisibility(visibility);
    }

    /**
     * 设置按钮点击事件
     */
    public void setBtnClickListener(View.OnClickListener listener) {
        btn.setOnClickListener(listener);
    }

    /**
     * 设置按钮文字
     */
    public void setBtnText(String text) {
        btn.setText(text);
    }

    /**
     * 设置无数据时提示文字
     */
    public void setNoDataContent(String noDataContent) {
        strNoDataContent = noDataContent;
        setTvNoDataContent();
    }

    /**
     * 隐藏顶部留白
     */
    public void hideTitleDownArea() {
        title_down_grey_area.setVisibility(View.GONE);
    }

    public void setOnLayoutClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setTvNoDataContent() {
        if (!StringUtil.isNullOrEmpty(strNoDataContent))
            tv.setText(strNoDataContent);
        else
            tv.setText(R.string.error_view_no_data);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.error_view_nothing);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mErrorState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }
}

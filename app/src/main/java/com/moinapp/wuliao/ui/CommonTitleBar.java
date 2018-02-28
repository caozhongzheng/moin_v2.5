package com.moinapp.wuliao.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;

/**
 * 首页标题栏
 * 
 * @author ouyezi
 */
public class CommonTitleBar extends RelativeLayout {

    // 防重复点击时间
    private static final int BTN_LIMIT_TIME = 500;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#ffffff");
    private static final int DEFAULT_TITLE_COLOR = Color.DKGRAY;
    private static final int DEFAULT_RIGHT_TEXT_COLOR = Color.DKGRAY;
    private static final int DEFAULT_LEFT_TEXT_COLOR = Color.DKGRAY;

    private TextView         leftButton;
    private ImageView        leftButtonImg;
    private TextView         middleButton;
    private TextView         rightButton;
    private ImageView        rightButtonImg;
    private int              leftBtnIconId;
    private String           leftBtnStr;
    private String           titleTxtStr;
    private String           rightBtnStr;
    private int              rightBtnIconId;
    private int bgColor = DEFAULT_BACKGROUND_COLOR;
    private int leftTextColor = DEFAULT_LEFT_TEXT_COLOR;
    private int titleTextColor = DEFAULT_TITLE_COLOR;
    private int rightTextColor = DEFAULT_RIGHT_TEXT_COLOR;



    public CommonTitleBar(Context context) {
        super(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        // 如果后续有文字按钮，可使用该模式设置
        leftBtnStr = arr.getString(R.styleable.CommonTitleBar_leftBtnTxt);
        leftBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_leftBtnIcon, 0);
        titleTxtStr = arr.getString(R.styleable.CommonTitleBar_titleTxt);
        rightBtnStr = arr.getString(R.styleable.CommonTitleBar_rightBtnTxt);
        rightBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_rightBtnIcon, 0);

        bgColor = arr.getColor(R.styleable.CommonTitleBar_bgColor, DEFAULT_BACKGROUND_COLOR);
        leftTextColor = arr.getColor(R.styleable.CommonTitleBar_leftBtnColor, DEFAULT_LEFT_TEXT_COLOR);
        titleTextColor = arr.getColor(R.styleable.CommonTitleBar_titleColor, DEFAULT_TITLE_COLOR);
        rightTextColor = arr.getColor(R.styleable.CommonTitleBar_rightBtnColor, DEFAULT_RIGHT_TEXT_COLOR);

        View root = LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
        root.findViewById(R.id.title_out_frame).setBackgroundColor(bgColor);
        if (isInEditMode()) {
            return;
        }

        arr.recycle();
    }

    protected void onFinishInflate() {

        if (isInEditMode()) {
            return;
        }
        leftButtonImg = (ImageView) findViewById(R.id.title_left_btn);
        leftButton = (TextView) findViewById(R.id.title_left);
        middleButton = (TextView) findViewById(R.id.title_middle);
        rightButtonImg = (ImageView) findViewById(R.id.title_right_btn);
        rightButton = (TextView) findViewById(R.id.title_right);

        setLeftBtnIcon(leftBtnIconId);
        setRightBtnIcon(rightBtnIconId);

        setLeftTxtBtn(leftBtnStr);
        setTitleTxt(titleTxtStr);
        setRightTxtBtn(rightBtnStr);
    }

    public void setRightBtnIcon(int rightBtnIconId) {
        if (rightBtnIconId > 0) {
            rightButtonImg.setImageResource(rightBtnIconId);
            rightButtonImg.setVisibility(View.VISIBLE);
        } else {
            rightButtonImg.setVisibility(View.GONE);
        }
    }

    public void setLeftBtnIcon(int leftBtnIconId) {
        if (leftBtnIconId > 0) {
            leftButtonImg.setImageResource(leftBtnIconId);
            leftButtonImg.setVisibility(View.VISIBLE);
        } else {
            leftButtonImg.setVisibility(View.GONE);
        }
    }

    public void setRightTxtBtn(String btnTxt) {
        if (!TextUtils.isEmpty(btnTxt)) {
            rightButton.setText(btnTxt);
            rightButton.setTextColor(rightTextColor);
            rightButton.setVisibility(View.VISIBLE);
        } else {
            rightButton.setVisibility(View.GONE);
        }
    }

    public void setLeftTxtBtn(String leftBtnStr) {
        if (!TextUtils.isEmpty(leftBtnStr)) {
            leftButton.setText(leftBtnStr);
            leftButton.setTextColor(leftTextColor);
            leftButton.setVisibility(View.VISIBLE);
        } else {
            leftButton.setVisibility(View.GONE);
        }
    }

    public void setTitleTxt(String title) {
        if (!TextUtils.isEmpty(title)) {
            middleButton.setText(title);
            middleButton.setTextColor(titleTextColor);
            middleButton.setVisibility(View.VISIBLE);
        } else {
            middleButton.setVisibility(View.GONE);
        }
    }

    public void hideLeftBtn() {
        leftButton.setVisibility(View.GONE);
        leftButtonImg.setVisibility(View.GONE);
        findViewById(R.id.title_left_area).setOnClickListener(null);
    }

    public void hideRightBtn() {
        rightButton.setVisibility(View.GONE);
        rightButtonImg.setVisibility(View.GONE);
        findViewById(R.id.title_right_area).setOnClickListener(null);
    }

    public void displayRightBtn() {
        rightButton.setVisibility(View.VISIBLE);
    }

    public void setRightBtnColor(int color) {
        rightButton.setTextColor(color);
    }

    public void setLeftBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_left_area).setOnClickListener(myListener);
    }

    public void setRightBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_right_area).setOnClickListener(myListener);
    }

    public void setTitleOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_middle).setOnClickListener(myListener);
    }

    public void setRightBtnClickAble(boolean clickAble) {
        findViewById(R.id.title_right_area).setClickable(clickAble);
    }
}

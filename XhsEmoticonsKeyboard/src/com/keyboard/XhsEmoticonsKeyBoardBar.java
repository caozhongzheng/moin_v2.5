package com.keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.keyboard.view.AutoHeightLayout;
import com.keyboard.view.EmoticonsEditText;
import com.keyboard.view.EmoticonsIndicatorView;
import com.keyboard.view.EmoticonsPageView;
import com.keyboard.view.EmoticonsToolBarView;
import com.keyboard.view.I.IEmoticonsKeyboard;
import com.keyboard.view.I.IView;
import com.keyboard.view.I.OnEmoticonsPageViewListener;
import com.keyboard.view.R;

public class XhsEmoticonsKeyBoardBar extends AutoHeightLayout implements IEmoticonsKeyboard, View.OnClickListener,EmoticonsToolBarView.OnToolBarItemClickListener {

    public static int FUNC_CHILLDVIEW_EMOTICON = 0;
    public static int FUNC_CHILLDVIEW_APPS = 1;
    public int mChildViewPosition = -1;

    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private EmoticonsToolBarView mEmoticonsToolBarView;

    private EmoticonsEditText et_chat;
    private LinearLayout ly_input, ly_voice;
    private RelativeLayout rl_input;
    private LinearLayout ly_foot_func;
    private ImageView btn_face, btn_emj;
    private ImageView btn_multimedia, btn_photo;
    private Button btn_send;
    private Button btn_voice;
    private ImageView btn_voice_or_text;
    private ImageView btn_sofekeyboard_show_or_hide;

    private boolean mIsMultimediaVisibility = true;
    private boolean mIsFaceVisibility = true;

    public XhsEmoticonsKeyBoardBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_keyboardbar, this);
        initView();
    }

    private void initView() {
        mEmoticonsPageView = (EmoticonsPageView) findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) findViewById(R.id.view_eiv);
        mEmoticonsToolBarView = (EmoticonsToolBarView) findViewById(R.id.view_etv);

        ly_voice = (LinearLayout) findViewById(R.id.ly_voice);
        ly_input = (LinearLayout) findViewById(R.id.ly_input);
        rl_input = (RelativeLayout) findViewById(R.id.rl_input);

        ly_foot_func = (LinearLayout) findViewById(R.id.ly_foot_func);
        btn_face = (ImageView) findViewById(R.id.btn_face);
        btn_emj = (ImageView) findViewById(R.id.btn_emj);
        btn_voice_or_text = (ImageView) findViewById(R.id.btn_voice_or_text);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        btn_multimedia = (ImageView) findViewById(R.id.btn_multimedia);
        btn_photo = (ImageView) findViewById(R.id.btn_photo);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_sofekeyboard_show_or_hide = (ImageView) findViewById(R.id.btn_sofekeyboard_show_or_hide);
        et_chat = (EmoticonsEditText) findViewById(R.id.et_chat);

        setAutoHeightLayoutView(ly_foot_func);
        btn_voice_or_text.setOnClickListener(this);
        btn_multimedia.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_face.setOnClickListener(this);
        btn_emj.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
        btn_sofekeyboard_show_or_hide.setOnClickListener(this);

        mEmoticonsPageView.setOnIndicatorListener(new OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                mEmoticonsIndicatorView.playBy(oldPosition, newPosition);
            }
        });

        mEmoticonsPageView.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (et_chat != null) {
                    et_chat.setFocusable(true);
                    et_chat.setFocusableInTouchMode(true);
                    et_chat.requestFocus();

                    // 删除
                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    }
                    // 用户自定义
                    else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        return;
                    }

                    int index = et_chat.getSelectionStart();
                    Editable editable = et_chat.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getContent());
                    } else {
                        editable.insert(index, bean.getContent());
                    }
                }
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
//                android.util.Log.i("emj", "return false 2, true 1 XhsEmoticonsKeyBoardBar " + bean.toString());
                // 删除
                if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                    return true;
                }
                // 用户自定义
                else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                    return false;
                }
                return false;
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) { }

            @Override
            public void onPageChangeTo(int position) {
                mEmoticonsToolBarView.setToolBtnSelect(position);
            }
        });

        mEmoticonsToolBarView.setOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
                mEmoticonsPageView.setPageSelect(position);
            }
        });

        et_chat.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!et_chat.isFocused()) {
                    et_chat.setFocusable(true);
                    et_chat.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        et_chat.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
        et_chat.setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if(mKeyBoardBarViewListener != null){
                            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState,-1);
                        }
                    }
                });
            }
        });
        et_chat.setOnTextChangedInterface(new EmoticonsEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                String str = arg0.toString();
                if (TextUtils.isEmpty(str)) {
                    btn_send.setBackgroundResource(R.drawable.btn_send_bg_disable);
                }
                // -> 发送
                else {
                    btn_send.setBackgroundResource(R.drawable.btn_send_bg);
                }
            }
        });
    }

    private void setEditableState(boolean b) {
        if (b) {
            et_chat.setFocusable(true);
            et_chat.setFocusableInTouchMode(true);
            et_chat.requestFocus();
            rl_input.setBackgroundResource(R.drawable.input_bg_green);
        } else {
            et_chat.setFocusable(false);
            et_chat.setFocusableInTouchMode(false);
            rl_input.setBackgroundResource(R.drawable.input_bg_gray);
        }
    }

    public void setInputVisibility(int visibility){
        rl_input.setVisibility(visibility);
    }

    public void setBtnSoftKeyVisibility(int visibility) {
        btn_sofekeyboard_show_or_hide.setVisibility(visibility);
    }

    public EmoticonsToolBarView getEmoticonsToolBarView() {
        return mEmoticonsToolBarView;
    }

    public EmoticonsPageView getEmoticonsPageView() {
        return mEmoticonsPageView;
    }

    public ImageView getBtn_emj() {
        return btn_emj;
    }

    public ImageView getBtn_photo() {
        return btn_photo;
    }

    public EmoticonsEditText getEt_chat() {
        return et_chat;
    }

    public Button getSendBtn() {
        return btn_send;
    }

    public void addToolView(int icon){
        if(mEmoticonsToolBarView != null && icon > 0){
            mEmoticonsToolBarView.addData(icon);
        }
    }

    public void addFixedView(View view , boolean isRight){
        if(mEmoticonsToolBarView != null){
            mEmoticonsToolBarView.addFixedView(view, isRight);
        }
    }

    public int getTooBtnSize() {
        return mEmoticonsToolBarView.getTooBtnSize();
    }

    public void clearEditText(){
        if(et_chat != null){
            et_chat.setText("");
        }
    }

    public void del(){
        if(et_chat != null){
            int action = KeyEvent.ACTION_DOWN;
            int code = KeyEvent.KEYCODE_DEL;
            KeyEvent event = new KeyEvent(action, code);
            et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        }
    }

    public void setSendBtnSendable(boolean b) {
        if(b) {
            btn_send.setBackgroundResource(R.drawable.btn_send_bg);
        } else {
            btn_send.setBackgroundResource(R.drawable.btn_send_bg_disable);
        }
        btn_send.setClickable(b);
    }

    public void setVoiceVisibility(boolean b){
        if(b){
            btn_voice_or_text.setVisibility(VISIBLE);
        }
        else{
            btn_voice_or_text.setVisibility(GONE);
        }
    }

    public void setSentVisibility(int visibility){
        btn_send.setVisibility(visibility);
    }

    public void setMultimediaVisibility(boolean b){
        mIsMultimediaVisibility = b;
        if(b){
            btn_multimedia.setVisibility(VISIBLE);
        }
        else{
            btn_multimedia.setVisibility(GONE);
        }
    }

    public void setPhotoVisibility(boolean b){
        if(b){
            btn_photo.setVisibility(VISIBLE);
        }
        else{
            btn_photo.setVisibility(GONE);
        }
    }

    public void toggleMultiMediaVisibility(boolean b) {
        setMultimediaVisibility(b);
        setPhotoVisibility(!b);
    }

    public void setFaceVisibility(boolean b){
        mIsFaceVisibility = b;
        if(b){
            btn_face.setVisibility(VISIBLE);
            btn_emj.setVisibility(GONE);
        }
        else{
            btn_emj.setVisibility(VISIBLE);
            btn_face.setVisibility(GONE);
        }
    }

    @Override
    public void setBuilder(final EmoticonsKeyboardBuilder builder) {
        mEmoticonsPageView.setBuilder(builder);
        mEmoticonsToolBarView.setBuilder(builder);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (ly_foot_func != null && ly_foot_func.isShown()) {
                    hideAutoView();
                    btn_face.setImageResource(R.drawable.btn_emoj_grey);
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_face) {

            switch (mKeyboardState){
                case KEYBOARD_STATE_NONE:
                case KEYBOARD_STATE_BOTH:
                    show(FUNC_CHILLDVIEW_EMOTICON);
                    btn_face.setImageResource(R.drawable.btn_emoj);
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if(mChildViewPosition == FUNC_CHILLDVIEW_EMOTICON){
                        btn_face.setImageResource(R.drawable.btn_emoj_grey);
                        Utils.openSoftKeyboard(et_chat);
                    }
                    else {
                        show(FUNC_CHILLDVIEW_EMOTICON);
                        btn_face.setImageResource(R.drawable.btn_emoj);
                    }
                    break;
            }
        }
        else if (id == R.id.btn_emj) {
            if(mKeyBoardBarViewListener != null){
                mKeyBoardBarViewListener.OnEmjBtnClick();
            }
        }
        else if (id == R.id.btn_send) {
            if(mKeyBoardBarViewListener != null){
                mKeyBoardBarViewListener.OnSendBtnClick(et_chat.getText().toString());
            }
        }
        else if (id == R.id.btn_multimedia) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_NONE:
                case KEYBOARD_STATE_BOTH:
                    show(FUNC_CHILLDVIEW_APPS);
                    btn_face.setImageResource(R.drawable.btn_emoj_grey);
                    ly_input.setVisibility(VISIBLE);
                    btn_voice.setVisibility(GONE);
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    break;
                case KEYBOARD_STATE_FUNC:
                    btn_face.setImageResource(R.drawable.btn_emoj_grey);
                    if(mChildViewPosition == FUNC_CHILLDVIEW_APPS){
                        hideAutoView();
                    }
                    else {
                        show(FUNC_CHILLDVIEW_APPS);
                    }
                    break;
            }
        }
        else if (id == R.id.btn_photo) {
            if(mKeyBoardBarViewListener != null){
                mKeyBoardBarViewListener.OnPhotoBtnClick();
            }
        }
        else if (id == R.id.btn_voice_or_text) {
            if(ly_input.isShown()){
                hideAutoView();
                ly_input.setVisibility(GONE);
                btn_send.setVisibility(GONE);
                btn_voice.setVisibility(VISIBLE);
            }
            else{
                ly_input.setVisibility(VISIBLE);
                btn_send.setVisibility(VISIBLE);
                btn_voice.setVisibility(GONE);
                setEditableState(true);
                Utils.openSoftKeyboard(et_chat);
            }
        }
        else if (id == R.id.btn_voice) {
            if(mKeyBoardBarViewListener != null){
                mKeyBoardBarViewListener.OnVoiceBtnClick();
            }
        }
        else if (id == R.id.btn_sofekeyboard_show_or_hide) {
            if (ly_foot_func != null && ly_foot_func.isShown()) {
                hideAutoView();
                btn_sofekeyboard_show_or_hide.setImageResource(R.drawable.icon_up_nomal);
            } else {
                showAutoView();
                btn_sofekeyboard_show_or_hide.setImageResource(R.drawable.icon_down_nomal);
            }
        }

    }

    public void add(View view){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ly_foot_func.addView(view, params);
    }

    public void show(int position){
        int childCount = ly_foot_func.getChildCount();
        if(position < childCount){
            for(int i = 0 ; i < childCount ; i++){
                if(i == position){
                    ly_foot_func.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition  = i;
                } else{
                    ly_foot_func.getChildAt(i).setVisibility(GONE);
                }
            }
        }
        post(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
                }
            }
        });
    }

    public LinearLayout getFootLayout() {
        return ly_foot_func;
    }

    @Override
    public void OnSoftPop(final int height) {
        super.OnSoftPop(height);
        post(new Runnable() {
            @Override
            public void run() {
                btn_face.setImageResource(R.drawable.btn_emoj_grey);
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
                }
            }
        });
    }

    @Override
    public void OnSoftClose(int height) {
        super.OnSoftClose(height);
        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState,height);
        }
    }

    @Override
    public void OnSoftChanegHeight(int height) {
        super.OnSoftChanegHeight(height);
        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState,height);
        }
    }

    KeyBoardBarViewListener mKeyBoardBarViewListener;
    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) { this.mKeyBoardBarViewListener = l; }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public interface KeyBoardBarViewListener {
        void OnKeyBoardStateChange(int state, int height);

        void OnEmjBtnClick();

        void OnPhotoBtnClick();

        void OnSendBtnClick(String msg);

        void OnVoiceBtnClick();

        void OnMultimediaBtnClick();
    }
}

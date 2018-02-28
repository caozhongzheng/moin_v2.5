/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moinapp.wuliao.emoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.emoji.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.util.StringUtils;

/**
 * 聊天界面 表情键盘
 * @author caozz
 * 
 */
public class MoinEmojiFragment extends Fragment implements
        SoftKeyboardStateListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(MoinEmojiFragment.class.getSimpleName());
    private LinearLayout mRootView;

    private View mEmojiTitle;
    private LinearLayout mEmojiContent;
    // TODO 不要底部
    private RadioGroup mEmojiBottom;
    private View[] mEmojiTabs;

    private EditText mEt;
    private Button mSend;
    private CheckBox mCBox;
    private ViewPager mEmojiPager;

    private MoinEmojiPagerAdapter adapter;
    private OnSendClickListener listener;
    private OnSendClickListener listener2;
    private OnEmojiClickListener emjlistener;
    // TODO 这个为了标注有几种类型的表情.还是比较有用的
    public static int EMOJI_TAB_CONTENT;

    private SoftKeyboardStateHelper mKeyboardHelper;

    // TODO 不要切换按钮
    private CheckBox mCboxFlag;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = (LinearLayout) inflater.inflate(R.layout.frag_moin_emj,
                container, false);
        initWidget(mRootView);
        return mRootView;
    }

    public LinearLayout getRootView() {
        return mRootView;
    }

    private void initWidget(View rootView) {
        // title
        mEmojiTitle = rootView.findViewById(R.id.emoji_title);

        // TODO 不要切换按钮
        mCboxFlag = (CheckBox) mEmojiTitle.findViewById(R.id.emoji_title_flag);
        mCboxFlag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickFlagButton();
                }
            }
        });

        mSend = (Button) mEmojiTitle.findViewById(R.id.emoji_title_send);
        mEt = (EditText) mEmojiTitle.findViewById(R.id.emoji_titile_input);
        TextWatcher watcher = new TextWatcher() {
            private int editStart;
            private int added;
            private int maxlength = getActivity().getResources().getInteger(R.integer.chat_max_len);
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                editStart = mEt.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                added = count - before;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String finalStr = editable.toString();
                //过滤掉@字符
                finalStr = finalStr.replace("@", "");
                if (!finalStr.equalsIgnoreCase(editable.toString())) {
                    mEt.setText(finalStr);
                    mEt.setSelection(mEt.length());
                }
                if(finalStr.length() > 0 ) {
                    if (added > 0) {
                        String inputChar = finalStr.substring(editStart, editStart + 1);
                        if (finalStr.length() == 1 && inputChar.equals("@")) {
                            mSend.setBackgroundResource(R.drawable.send_but_gray_chat);
                        } else if (!StringUtils.isBlank(StringUtils.unifyLineSeparators(mEt.getText().toString()))) {
                            mSend.setBackgroundResource(R.drawable.send_but_pink_chat);
                        }
                    } else if (!StringUtils.isBlank(StringUtils.unifyLineSeparators(mEt.getText().toString()))) {
                        mSend.setBackgroundResource(R.drawable.send_but_pink_chat);
                    }
                } else {
                    mSend.setBackgroundResource(R.drawable.send_but_gray_chat);
                }

                if (added > 0) {
                    if (finalStr.length() > maxlength) {
                        mEt.removeTextChangedListener(this);
                        mEt.getText().delete(editStart, editStart + added);
                        mEt.setSelection(editStart);
                        mEt.addTextChangedListener(this);
                        return;
                    }
                    String inputChar = finalStr.substring(editStart, editStart + 1);
                    if (inputChar.equals("@")) {
                        mEt.removeTextChangedListener(this);
                        mEt.getText().delete(editStart, editStart + added);
                        mEt.setSelection(editStart);
                        mEt.addTextChangedListener(this);
                    }
                }
            }
        };
        mEt.addTextChangedListener(watcher);
        mEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    hideSoftKeyboard();
                }
            }
        });
        mCBox = (CheckBox) mEmojiTitle.findViewById(R.id.emoji_title_menu);
        mCBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    showEmojiKeyBoard();
                    hideSoftKeyboard();
                } else {
                    showSoftKeyboard();
                }
            }
        });
        // bottom
        mEmojiBottom = (RadioGroup) rootView.findViewById(R.id.emoji_bottom);
        EMOJI_TAB_CONTENT = mEmojiBottom.getChildCount() - 1; // 减一是因为有一个删除按钮

        // TODO 3.2.3版本暂时只有一类表情,所以不需要增加其他类型以及删除按钮.
        EMOJI_TAB_CONTENT = 1;

        mEmojiTabs = new View[EMOJI_TAB_CONTENT];
        if (EMOJI_TAB_CONTENT <= 1) { // 只有一个分类的时候就不显示了
            mEmojiBottom.setVisibility(View.GONE);
        }
        for (int i = 0; i < EMOJI_TAB_CONTENT; i++) {
            mEmojiTabs[i] = mEmojiBottom.getChildAt(i);
            mEmojiTabs[i].setOnClickListener(getBottomBarClickListener(i));
        }
        mEmojiBottom.findViewById(R.id.emoji_bottom_del).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputHelper.backspace(mEt);
                    }
                });

        // content必须放在bottom下面初始化
        mEmojiContent = (LinearLayout) rootView
                .findViewById(R.id.emoji_content);
        mEmojiPager = (ViewPager) mEmojiContent.findViewById(R.id.emoji_pager);
        if (getActivity() instanceof OnEmojiClickListener) {
            emjlistener = (OnEmojiClickListener) getActivity();
        }
        adapter = new MoinEmojiPagerAdapter(getFragmentManager(),
                EMOJI_TAB_CONTENT, emjlistener);
        mEmojiPager.setAdapter(adapter);

        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow()
                .getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
        if (getActivity() instanceof OnSendClickListener) {
            listener = (OnSendClickListener) getActivity();
        }
        if (listener2 != null) {
            mSend.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!StringUtils.isBlank(StringUtils.unifyLineSeparators(mEt.getText().toString()))) {
                                listener2.onClickSendButton(mEt.getText().toString());
                                MyLog.i("mEmojiTitle.onClick send.....[" + mEt.getText() + "]");
                                mEt.setHint(R.string.chat_input_hint);
//                            hideAllKeyBoard();
                            }
                        }
                    });
        }
    }

    public OnSendClickListener getListener() {
        return listener2;
    }

    public void setListener(OnSendClickListener listener) {
        this.listener2 = listener;
    }

    public void setOnEmojiClickListener(OnEmojiClickListener l) {
        this.emjlistener = l;
    }

    public OnEmojiClickListener getEmjlistener() {
        return emjlistener;
    }

    /**
     * 底部栏点击事件监听器
     * 
     * @param index
     * @return
     */
    private OnClickListener getBottomBarClickListener(final int index) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiPager.setCurrentItem(index);
            }
        };
    }


    /******************************* preference *************************************/

    public void clean() {
        mEt.setText(null);
        mEt.setTag(null);
    }

    public void setHint(String hint) {
        mEt.setHint(hint);
    }

    public void setHint(int hint) {
        mEt.setHint(hint);
    }

    public void hideAllKeyBoard() {
        hideSoftKeyboard();
        hideEmojiKeyBoard();
    }

    /**
     * 隐藏Emoji并显示软键盘
     */
    public void hideEmojiKeyBoard() {
        mEmojiBottom.setVisibility(View.GONE);
        mEmojiContent.setVisibility(View.GONE);
        mCBox.setChecked(false);
        setEmojiBtn(false);
    }

    /**
     * 显示Emoji并隐藏软键盘
     */
    public void showEmojiKeyBoard() {
        mEmojiContent.setVisibility(View.VISIBLE);
        if (EMOJI_TAB_CONTENT > 1) {
            mEmojiBottom.setVisibility(View.VISIBLE);
        }
        mCBox.setChecked(true);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEt.getWindowToken(), 0);
        setEmojiBtn(true);
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        mEt.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(mEt,
                InputMethodManager.SHOW_FORCED);
        setEmojiBtn(false);
    }

    public View getEmojiTitle() {
        return mEmojiTitle;
    }

    public Editable getTextString() {
        return mEt.getText();
    }

    public EditText getEditText() {
        return mEt;
    }

    public boolean isShowEmojiKeyBoard() {
        if (mCBox == null) {
            return false;
        } else {
            return mCBox.isChecked();
        }
    }

    /**
     * 当软键盘显示时回调
     */
    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        if (mEmojiBottom != null) {
            mEmojiBottom.setVisibility(View.GONE);
        }
        if (mEmojiContent != null) {
            mEmojiContent.setVisibility(View.GONE);
        }
        if (mCBox != null) {
            mCBox.setChecked(false);
            setEmojiBtn(false);
        }
        if(baseFragment != null) {
            ((MainActivity) baseFragment.getActivity()).hideFloatingBtn();
        }
    }

    /**隐藏掉表情按钮*/
    public void hideEmjButton() {
        if (mCBox != null) {
            mCBox.setVisibility(View.GONE);
        }
    }

    /**隐藏掉工具和表情按钮的切换按钮*/
    public void hideFlagButton() {
        if (mCboxFlag != null) {
            mCboxFlag.setVisibility(View.GONE);
        }
    }

    public void setEmojiBtn(boolean show) {
        if (show) {
            mCBox.setButtonDrawable(R.drawable.keyboard_inputchat_copy);
        } else {
            mCBox.setButtonDrawable(R.drawable.add_inputchat);
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        if(baseFragment != null) {
            try {
                ((MainActivity) baseFragment.getActivity()).showFloatingBtn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftKeyboard();
    }

    private BaseFragment baseFragment;
    public void setParentView(BaseFragment fragment) {
        baseFragment = fragment;
    }
}

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
import android.util.Log;
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
import com.moinapp.wuliao.emoji.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import com.moinapp.wuliao.ui.MainActivity;

/**
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class KJEmojiFragment extends Fragment implements
        SoftKeyboardStateListener {
    private LinearLayout mRootView;

    private View mEmojiTitle;
    private LinearLayout mEmojiContent;
    private RadioGroup mEmojiBottom;
    private View[] mEmojiTabs;

    private EditText mEt;
    private Button mSend;
    private CheckBox mCBox;
    private ViewPager mEmojiPager;

    private EmojiPagerAdapter adapter;
    private OnSendClickListener listener;
    private OnSendClickListener listener2;
    public static int EMOJI_TAB_CONTENT;

    private SoftKeyboardStateHelper mKeyboardHelper;

    private CheckBox mCboxFlag;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = (LinearLayout) inflater.inflate(R.layout.frag_main,
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
            private int maxlength = getActivity().getResources().getInteger(R.integer.comment_max_len);
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
                            mSend.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
                        } else {
                            mSend.setBackgroundResource(R.drawable.tag_btn_solid_bg);
                        }
                    } else {
                        mSend.setBackgroundResource(R.drawable.tag_btn_solid_bg);
                    }
                } else {
                    mSend.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
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
            public void onFocusChange(View view, boolean b) {
                if(!b) {
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
        adapter = new EmojiPagerAdapter(getFragmentManager());
        mEmojiPager.setAdapter(adapter);

        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow()
                .getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
        if (getActivity() instanceof OnSendClickListener) {
            listener = (OnSendClickListener) getActivity();
        }
        if (listener != null) {
            mSend.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("ljc", "mEmojiTitle.onclick.....");
                            listener.onClickSendButton(mEt.getText().toString());
                            mEt.setHint("说点什么吧");
                            hideAllKeyBoard();
                        }
                    });
        }
        if (listener2!= null) {
            mSend.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("ljc", "mEmojiTitle.onclick.....");
                            listener2.onClickSendButton(mEt.getText().toString());
                            mEt.setHint("说点什么吧");
                            hideAllKeyBoard();
                        }
                    });
        }
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

    public void setOnSendClickListener(OnSendClickListener l) {
        this.listener2 = l;
    }

    /******************************* preference *************************************/

    public void clean() {
        mEt.setText(null);
        mEt.setTag(null);
    }

    public void setHint(String hint) {
        mEt.setHint(hint);
    }

    public void hideAllKeyBoard() {
        hideEmojiKeyBoard();
        hideSoftKeyboard();
    }

    /**
     * 隐藏Emoji并显示软键盘
     */
    public void hideEmojiKeyBoard() {
        mEmojiBottom.setVisibility(View.GONE);
        mEmojiContent.setVisibility(View.GONE);
        mCBox.setChecked(false);
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
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        mEt.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(mEt,
                InputMethodManager.SHOW_FORCED);
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
        if (mEmojiBottom != null && mEmojiContent != null) {
            mEmojiBottom.setVisibility(View.GONE);
            mEmojiContent.setVisibility(View.GONE);
        }
        if (mCBox != null) {
            mCBox.setChecked(false);
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

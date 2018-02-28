package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.FragmentSkip;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.umeng.analytics.MobclickAgent;


/**
 * 找回密码界面_设置新密码
 * Created by moying on 15/5/7.
 */
public class RetrievePassword3_Fragment extends Fragment {
    private static ILogger MyLog = LoggerFactory.getLogger("login");

    private FragmentSkip callback;

    private EditText mEtPwd1, mEtPwd2;
    private ImageView mClearPassword1, mClearPassword2;
    private String mStrPwd1, mStrPwd2;

    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int mPwdState1 = NULL, mPwdState2 = NULL;
    private TextView submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.retrive_phone2_layout, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (FragmentSkip) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        ((TextView) getActivity().findViewById(R.id.title_center)).setText("设置新密码 (2/2)");
        mEtPwd1 = (EditText) getActivity().findViewById(R.id.password1);
        mEtPwd2 = (EditText) getActivity().findViewById(R.id.password2);
        mClearPassword1 = (ImageView) getActivity().findViewById(R.id.visible_password1);
        mClearPassword2 = (ImageView) getActivity().findViewById(R.id.visible_password2);

        mEtPwd1.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return StringUtil.passwordDigits();
            }

            @Override
            public int getInputType() {
                return mEtPwd1.getInputType();
            }
        });
        mClearPassword1.setOnClickListener(v -> {
            mEtPwd1.setText("");
            mClearPassword1.setVisibility(View.INVISIBLE);
        });
        mClearPassword2.setOnClickListener(v -> {
            mEtPwd2.setText("");
            mClearPassword2.setVisibility(View.INVISIBLE);

        });
        mEtPwd2.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return StringUtil.passwordDigits();
            }

            @Override
            public int getInputType() {
                return mEtPwd2.getInputType();
            }
        });

        mEtPwd1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                setLoginBtnBackground();
                if (!StringUtil.isNullOrEmpty(mEtPwd1.getText().toString())) {
                    mClearPassword1.setVisibility(View.VISIBLE);
                } else {
                    mClearPassword1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                mStrPwd1 = arg0.toString();
                if (mStrPwd1.length() == 0) {
                    mPwdState1 = NULL;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else if (mStrPwd1.length() < 6) {
                    mPwdState1 = ERROR;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else {
                    mPwdState1 = CORRECT;
//                    phone_ok.setVisibility(View.VISIBLE);
                }
            }
        });

        mEtPwd1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !StringUtil.isNullOrEmpty(mEtPwd1.getText().toString())) {
                    mClearPassword1.setVisibility(View.VISIBLE);
                } else {
                    mClearPassword1.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEtPwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                setLoginBtnBackground();
                if (!StringUtil.isNullOrEmpty(mEtPwd2.getText().toString())) {
                    mClearPassword2.setVisibility(View.VISIBLE);
                } else {
                    mClearPassword2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                mStrPwd2 = arg0.toString();
                if (mStrPwd2.length() == 0) {
                    mPwdState2 = NULL;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else if (mStrPwd2.length() < 6) {
                    mPwdState2 = ERROR;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else {
                    mPwdState2 = CORRECT;
//                    phone_ok.setVisibility(View.VISIBLE);
                }
            }
        });

        mEtPwd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !StringUtil.isNullOrEmpty(mEtPwd2.getText().toString())) {
                    mClearPassword2.setVisibility(View.VISIBLE);
                } else {
                    mClearPassword2.setVisibility(View.INVISIBLE);
                }
            }
        });

        submit = (TextView) getActivity().findViewById(R.id.retrive_sure_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void setLoginBtnBackground(){
        if (!StringUtil.isNullOrEmpty(mEtPwd1.getText().toString())
                && !StringUtil.isNullOrEmpty(mEtPwd2.getText().toString())){
            submit.setClickable(true);
            submit.setBackgroundResource(R.drawable.but_login_black);
        } else {
            submit.setClickable(false);
            submit.setBackgroundResource(R.drawable.but_login_gray);
        }
    }

    private void resetPassword() {
        if(mPwdState1 == CORRECT && mPwdState2 == CORRECT) {
            if(mStrPwd1.equals(mStrPwd2)) {
                callback.skip(3, mStrPwd1);
            } else {
                AppContext.showToastShort(R.string.retrieve_err5);
            }
        } else {
            AppContext.showToastShort(R.string.regist_password_tip);
        }
    }

    class MyNumberKeyListener extends NumberKeyListener {
        EditText mEtPassword;
        public MyNumberKeyListener(EditText mEt) {
            mEtPassword = mEt;
        }
        @Override
        protected char[] getAcceptedChars() {
            return StringUtils.passwordDigits();
        }

        @Override
        public int getInputType() {
            return mEtPassword.getInputType();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.RETRIEVE_PASSWORD3_FRAGMENT); //统计页面，
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.RETRIEVE_PASSWORD3_FRAGMENT);
    }
}

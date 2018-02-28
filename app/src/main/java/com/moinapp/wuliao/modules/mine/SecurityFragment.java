package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.MD5;
import com.moinapp.wuliao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 账户安全界面
 * 
 */
public class SecurityFragment extends BaseFragment {

    @InjectView(R.id.title_bar)
    CommonTitleBar mTitleBar;
    @InjectView(R.id.password1)
    EditText password1_et;
    @InjectView(R.id.password2)
    EditText password2_et;
    private String pwd1_str, pwd2_str;

    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int pwd1_state = NULL, pwd2_state = NULL;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_security, container,
                false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        password1_et.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return StringUtil.passwordDigits();
            }

            @Override
            public int getInputType() {
                return password1_et.getInputType();
            }
        });
        password2_et.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return StringUtil.passwordDigits();
            }

            @Override
            public int getInputType() {
                return password2_et.getInputType();
            }
        });

        password1_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                pwd1_str = arg0.toString();
                if (pwd1_str.length() == 0) {
                    pwd1_state = NULL;
                } else if (pwd1_str.length() < 6) {
                    pwd1_state = ERROR;
                } else {
                    pwd1_state = CORRECT;
                }
            }
        });
        password2_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                pwd2_str = arg0.toString();
                if (pwd2_str.length() == 0) {
                    pwd2_state = NULL;
                } else if (pwd2_str.length() < 6) {
                    pwd2_state = ERROR;
                } else {
                    pwd2_state = CORRECT;
                }
            }
        });
    }

    @Override
    public void initData() {
        mTitleBar.setLeftBtnOnclickListener(v -> {
            getActivity().finish();
        });

        mTitleBar.setRightTxtBtn(getString(R.string.retrieve_sure));
        mTitleBar.setRightBtnOnclickListener(v -> {
            changePassword();
        });
    }


    private void changePassword() {
        if(pwd1_state == NULL) {
            AppContext.getInstance().showToast(R.string.retrieve_password_hint);
        } else if(pwd2_state == NULL) {
            AppContext.getInstance().showToast(R.string.retrieve_password_sure_hint);
        } else if(pwd1_state == CORRECT && pwd2_state == CORRECT) {
            if(pwd1_str.equals(pwd2_str)) {
                /**更新的密码 * 返回：result 0 失败，1 成功 * error: -1 找不到当前用户 -2 密码不正确或者session信息有误 -3 信息有误，保存数据库失败 */
                MineManager.getInstance().updatePassword(MD5.md5(pwd1_str), new IListener2() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showWaitDialog("");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideWaitDialog();
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        AppContext.getInstance().showToast(R.string.retrieve_success);
                        getActivity().finish();
                    }

                    @Override
                    public void onNoNetwork() {
                        AppContext.getInstance().showToast(R.string.no_network);
                    }

                    @Override
                    public void onErr(Object object) {
                        AppContext.getInstance().showToast(R.string.retrieve_fail);
                    }
                });

            } else {
                AppContext.getInstance().showToast(R.string.retrieve_err5);
            }
        } else {
            AppContext.getInstance().showToast(R.string.regist_password_tip);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SECURITY_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SECURITY_FRAGMENT);
    }
}

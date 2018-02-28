package com.moinapp.wuliao.modules.login;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 注册界面[第一步]
 * Created by liujiancheng on 15/10/28.
 */
public class RegistFragment_step1 extends BaseFragment implements View.OnClickListener {
    protected static final String TAG = LoginActivity.class.getSimpleName();
    private ILogger MyLog = LoggerFactory.getLogger(RegistFragment_step1.class.getSimpleName());
//
//    @InjectView(R.id.iv_phone_drawable)
//    ImageView mIvPhone;

    @InjectView(R.id.get_code_sms)
    TextView mTvGetCode;

    @InjectView(R.id.et_phone)
    EditText mEtPhone;

    @InjectView(R.id.et_password)
    EditText mEtPassword;
//
//    @InjectView(R.id.iv_pwd_visible)
//    ImageView mIvEye;

    @InjectView(R.id.et_code)
    EditText mEtCode;

    @InjectView(R.id.tv_agreement)
    TextView mTvAgreement;

    @InjectView(R.id.btn_verify)
    TextView mBtnVerify;

    @InjectView(R.id.title_bar)
    CommonTitleBar titleBar;

    @InjectView(R.id.clear_username)
    ImageView clearUsername;

    @InjectView(R.id.clear_password)
    ImageView clearPassword;

    private String phone_str, code_str, password_str, resend_str;
    private int recLen_sms = 60;

    private final int NULL = 0, ERROR = -1, CORRECT = 1;
    private int phone_state = NULL, code_state = NULL, password_state = NULL;

    private int mGotoFollow;

    private final int PASSWORD_LENGTH_MIN = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_step1, null);
        ButterKnife.inject(this, view);
        mTvAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        resend_str = AppContext.getInstance().getString(R.string.regist_resend);
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                setLoginBtnBackground();
                if (!StringUtil.isNullOrEmpty(mEtPhone.getText().toString())) {
                    clearUsername.setVisibility(View.VISIBLE);
                } else {
                    clearUsername.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                phone_str = arg0.toString();
                if (phone_str.length() == 0) {
                    phone_state = NULL;
                } else if (!StringUtils.isCellphone(phone_str)) {
                    phone_state = ERROR;
                } else {
                    phone_state = CORRECT;
                }
            }
        });

        mEtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !StringUtil.isNullOrEmpty(mEtPhone.getText().toString())) {
                    clearUsername.setVisibility(View.VISIBLE);
                } else {
                    clearUsername.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                setLoginBtnBackground();
                if (!StringUtil.isNullOrEmpty(mEtPassword.getText().toString())) {
                    clearPassword.setVisibility(View.VISIBLE);
                } else {
                    clearPassword.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                password_str = arg0.toString();
                if (password_str.length() == 0) {
                    password_state = NULL;
                } else if (password_str.length() < 6 || password_str.length() > 16) {
                    password_state = ERROR;
                } else {
                    password_state = CORRECT;
                }

            }
        });

        mEtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !StringUtil.isNullOrEmpty(mEtPassword.getText().toString())) {
                    clearPassword.setVisibility(View.VISIBLE);
                } else {
                    clearPassword.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEtPassword.setKeyListener(keyListener);

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                setLoginBtnBackground();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                setLoginBtnBackground();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                setLoginBtnBackground();
                code_str = arg0.toString();
                if (code_str.length() == 0) {
                    code_state = NULL;
                } else {
                    code_state = ERROR;
                }
            }
        });

        titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void setLoginBtnBackground(){
        if (!StringUtil.isNullOrEmpty(mEtPhone.getText().toString())
                && !StringUtil.isNullOrEmpty(mEtPassword.getText().toString())
                && !StringUtil.isNullOrEmpty(mEtCode.getText().toString())) {
            mBtnVerify.setClickable(true);
            mBtnVerify.setBackgroundResource(R.drawable.but_login_black);
        } else {
            mBtnVerify.setClickable(false);
            mBtnVerify.setBackgroundResource(R.drawable.but_login_gray);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mGotoFollow = args.getInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, 0);
        }

        LoginManager.getInstance().addActivity(getActivity());
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    stopCountDown();
                    AppContext.showToast(R.string.regist_mobile_existed);
                    mEtPhone.requestFocus();
                    break;
                case 1:
                    getSmsCode();
                    mEtCode.requestFocus();
                    break;
                case 8:
                    //验证成功,下一步跳转注册activity
                    Bundle b = new Bundle();
                    b.putString(RegistActivity.PHONE_NUMBER, phone_str);
                    b.putString(RegistActivity.SMS_CODE,code_str);
                    b.putString(RegistActivity.PASSWORD, password_str);
                    b.putInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, mGotoFollow);
                    AppTools.toIntent(getActivity(), b, RegistActivity.class);
                    //fix友盟bug 0091, 这里finish的话有可能造成AppTools.toIntent方法里面getActivity()异常了
//                    getActivity().finish();
                    break;
                case -3:// 校验失败
                    stopCountDown();
                    AppContext.showToast(msg.obj.toString());
                    mEtCode.requestFocus();
                    break;
                case -4:
                    stopCountDown();
                    AppContext.showToast(R.string.regist_get_verify_code_fail);
                    mEtCode.requestFocus();
                    break;
                case -5:
                    stopCountDown();
                    AppContext.showToast(R.string.get_code_no_internet);
                    mEtCode.requestFocus();
                    break;
            }
        }
    };

    private void checkPhone() {
        phone_str = mEtPhone.getText().toString();
        if (phone_str.length() == 0) {
            AppContext.showToast(R.string.regist_phone_null);
            mTvGetCode.setClickable(true);
        } else if (!StringUtils.isCellphone(phone_str)) {
            AppContext.showToast(R.string.regist_phone_format_err);
            mTvGetCode.setClickable(true);
        } else {
            startCountDown();
            try {
                // 验证手机号有没有注册过
                LoginManager.getInstance()
                        .checkPhone(phone_str, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                if ((int) obj == 1) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                } else if ((int) obj == 0) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = -1;
                                    handler.sendMessage(msg);
                                }
                            }

                            @Override
                            public void onNoNetwork() {
                                Message msg = handler.obtainMessage();
                                msg.what = -5;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onErr(Object obj) {
                                Message msg = handler.obtainMessage();
                                msg.what = -1;
                                handler.sendMessage(msg);
                            }
                        });
            } catch (Exception e) {
                mTvGetCode.setClickable(true);
                e.printStackTrace();
            } finally {
            }

        }
    }

    BaseKeyListener keyListener = new NumberKeyListener() {
        @Override
        protected char[] getAcceptedChars() {
            return StringUtils.passwordDigits();
        }

        @Override
        public int getInputType() {
            return mEtPassword.getInputType();
        }
    };

    /**
     * 获取验证码
     */
    private void getSmsCode() {
        LoginManager.getInstance()
                .getPhoneSms(phone_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("getSmsCode=" + phone_str + ", recLen=" + recLen_sms);
                    }

                    @Override
                    public void onNoNetwork() {
                        Message msg = handler.obtainMessage();
                        msg.what = -5;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErr(Object obj) {
                        Message msg = handler.obtainMessage();
                        msg.what = -4;
                        handler.sendMessage(msg);
                    }
                });

    }

    private void startCountDown() {
        mTvGetCode.setClickable(false);
        handler.removeCallbacks(runnable_sms);
        recLen_sms = 60;
        mTvGetCode.setTextColor(AppContext.getInstance().getResources().getColor(R.color.chat_input_hint));
        handler.postDelayed(runnable_sms, 1000);
    }

    private void stopCountDown() {
        handler.removeCallbacks(runnable_sms);
        mTvGetCode.setText(R.string.regist_get_code);
        mTvGetCode.setTextColor(AppContext.getInstance().getResources().getColor(R.color.comment_text));
        mTvGetCode.setClickable(true);
    }

    Runnable runnable_sms = new Runnable() {
        @Override
        public void run() {
            recLen_sms--;
            if (recLen_sms > 0) {
                mTvGetCode.setClickable(false);
                mTvGetCode.setText(recLen_sms + resend_str);
                handler.postDelayed(this, 1000);
            } else {
                stopCountDown();
            }
        }
    };

    private void phoneVerify() {
        if (mEtPassword.getText().toString().length() < PASSWORD_LENGTH_MIN) {
            AppContext.showToastShort(R.string.regist_password_error);
            return;
        }

        LoginManager.getInstance()
                .verifyPhone(phone_str, code_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        handler.removeCallbacks(runnable_sms);
                        Message msg = handler.obtainMessage();
                        msg.what = 8;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onNoNetwork() {
                        Message msg = handler.obtainMessage();
                        msg.what = -5;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErr(Object object) {
                        //test
//                        Message msg = handler.obtainMessage();
//                        msg.what = 8;
//                        handler.sendMessage(msg);

                        Message msg = handler.obtainMessage();
                        msg.what = -3;
                        StringBuilder sb = new StringBuilder();
                        switch ((int) object) {
                            case -2:
                                sb.append(AppContext.getInstance().getString(R.string.verify_failed));//"-2 信息有误，数据库保存失败"
                                break;
                            case -1:
                                sb.append(AppContext.getInstance().getString(R.string.regist_verify_code_wrong));
                                break;
                        }
                        msg.obj = sb.toString();
                        handler.sendMessage(msg);
                    }
                });
    }

    @Override
    @OnClick({R.id.get_code_sms, R.id.tv_agreement, R.id.btn_verify, R.id.clear_username, R.id.clear_password})
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.get_code_sms:
                if (!Tools.isFastDoubleClick()) {
                    mTvGetCode.setClickable(false);
                    MyLog.i("fast click get sms:" + System.currentTimeMillis());
                    checkPhone();
                }
                break;
            case R.id.tv_agreement:
                UIHelper.showAboutProtocal(getActivity());
//                AppTools.toIntent(getActivity(), AgreementActivity.class);
                break;
            case R.id.btn_verify:
                if (!Tools.isFastDoubleClick()) {
                    if (phone_state == CORRECT && code_state != NULL) {
                        if (!"".equals(code_str)) {
                            phoneVerify();
                        }
                    } else {
                        AppContext.showToast(R.string.regist_input_error);
                    }
                }

                //test
//                Bundle b = new Bundle();
//                b.putString(RegistActivity.PHONE_NUMBER,"13567665545");
//                b.putString(RegistActivity.SMS_CODE,"4545");
//                AppTools.toIntent(getActivity(), b, RegistActivity.class);

//                AppTools.toIntent(getActivity(), UserinfoShortActivity.class);
                break;
//            case R.id.iv_pwd_visible:
//                togglePwdVisible();
//                break;
            case R.id.clear_username:
                mEtPhone.setText("");
                clearUsername.setVisibility(View.INVISIBLE);
                break;
            case R.id.clear_password:
                mEtPassword.setText("");
                clearPassword.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.REGIST_FRAGMENT_1); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.REGIST_FRAGMENT_1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().removeActivity(getActivity());
    }
}

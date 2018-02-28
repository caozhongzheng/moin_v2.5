package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.FragmentSkip;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.Tools;
import com.umeng.analytics.MobclickAgent;

/**
 * 找回密码界面_输入手机号
 * Created by moying on 15/5/6.
 */
public class RetrievePassword1_Fragment extends Fragment {

    private ILogger MyLog = LoggerFactory.getLogger("login");
    private Activity activity;
    private FragmentSkip callback;
    private EditText phone_et, code_et;

    private TextView get_code_sms, submit_tv;
    private int recLen_sms = 60;

    private String phone_str, code_str, resend_str;
    private final int NULL = 0, ERROR = -1, CORRECT = 1;
    private int phone_state = NULL, code_state = NULL;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    stopCountDown();
                    AppContext.showToastShort(R.string.retrieve_phone_unregister);
                    phone_et.requestFocus();
                    break;
                case 1:
                    getSmsCode();
                    code_et.requestFocus();
                    break;
                case -3:// verify failed
                    stopCountDown();
                    AppContext.showToastShort(msg.obj.toString());
                    break;
                case -4:
                    stopCountDown();
                    AppContext.showToastShort(R.string.regist_get_verify_code_fail);
                    break;
                case -5:
                    stopCountDown();
                    AppContext.showToastShort(R.string.get_code_no_internet);
                    code_et.requestFocus();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.retrive_phone1_layout, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = getActivity();
        callback = (FragmentSkip) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        ((TextView) activity.findViewById(R.id.title_center)).setText("找回密码 (1/2)");
        phone_et = (EditText) activity.findViewById(R.id.phone);
        code_et = (EditText) activity.findViewById(R.id.code);

        get_code_sms = (TextView) activity.findViewById(R.id.get_code_sms);
        get_code_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Tools.isFastDoubleClick())
                    checkPhone();
            }
        });

        ImageView clearUsername = (ImageView) activity.findViewById(R.id.clear_username);
        clearUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_et.setText("");
                clearUsername.setVisibility(View.INVISIBLE);
            }
        });

        resend_str = getString(R.string.regist_resend);

        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!StringUtil.isNullOrEmpty(phone_et.getText().toString())) {
                    clearUsername.setVisibility(View.VISIBLE);
                } else {
                    clearUsername.setVisibility(View.INVISIBLE);
                }
                if ((!StringUtil.isNullOrEmpty(code_et.getText().toString()) && !StringUtil.isNullOrEmpty(phone_et.getText().toString()))) {
                    submit_tv.setClickable(true);
                    submit_tv.setBackgroundResource(R.drawable.but_login_black);
                } else {
                    submit_tv.setClickable(false);
                    submit_tv.setBackgroundResource(R.drawable.but_login_gray);
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
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else if (!StringUtil.isCellphone(phone_str)) {
                    phone_state = ERROR;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else {
                    phone_state = CORRECT;
//                    phone_ok.setVisibility(View.VISIBLE);
                }
            }
        });

        phone_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !StringUtil.isNullOrEmpty(phone_et.getText().toString())) {
                    clearUsername.setVisibility(View.VISIBLE);
                } else {
                    clearUsername.setVisibility(View.INVISIBLE);
                }
            }
        });

        code_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!StringUtil.isNullOrEmpty(code_et.getText().toString()) && !StringUtil.isNullOrEmpty(phone_et.getText().toString())) {
                    submit_tv.setClickable(true);
                    submit_tv.setBackgroundResource(R.drawable.but_login_black);
                } else {
                    submit_tv.setClickable(false);
                    submit_tv.setBackgroundResource(R.drawable.but_login_gray);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                code_str = arg0.toString();
                if (code_str.length() == 0) {
                    code_state = NULL;
                } else {
                    code_state = ERROR;
                }
            }
        });

        submit_tv = (TextView) activity.findViewById(R.id.retrive_verify_submit);
        submit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Tools.isFastDoubleClick()) {
                    phoneRetrive();
                }
            }
        });

    }

    private void phoneRetrive() {
        MyLog.i("phoneRetrive phone=" + phone_str + ", code=" + code_str);
        if (phone_state == CORRECT && code_state != NULL) {
            String code = code_et.getText().toString();
            if (!"".equals(code)) {
                LoginManager.getInstance().retrievePasswordByPhone(phone_str,
                        code_str, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                handler.removeCallbacks(runnable_sms);
                                MyLog.i("phoneRetrive onSuccess=" + phone_str + ", code=" + code_str + ", obj=" + obj);
                                callback.skip(1, phone_str, code_str, null, obj.toString());
                                //认为已经登陆成功了
                                getActivity().sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));
                                LoginPreference.getInstance().setLoginFailedTimes(0);
                            }

                            @Override
                            public void onNoNetwork() {
                                Message msg = handler.obtainMessage();
                                msg.what = -5;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("phoneRetrive onErr=" + object.toString());
                                Message msg = handler.obtainMessage();
                                msg.what = -3;
                                msg.obj = getErrInfo((int) object);
                                handler.sendMessage(msg);

                                code_et.requestFocus();
                            }
                        });
            } else
                AppContext.showToastShort(R.string.verify_failed);
        } else {
            AppContext.showToastShort(R.string.verify_failed);
            phone_et.requestFocus();
        }
    }

    private String getErrInfo(int object) {
        switch (object) {
            case 2:
                return getString(R.string.retrieve_err2);
            case -2:
                return getString(R.string.verify_failed);
            case -3:
                return getString(R.string.retrieve_err3);
            case -1:
                return getString(R.string.login_phone_not_register);
            case ErrorCode.ERROR_PHONE_NOT_REGISTER:
                return getString(R.string.login_phone_not_register);
            case ErrorCode.ERROR_USER_FORBIDDEN:
                return getString(R.string.login_user_forbidden);
        }
        return "";
    }

    private void checkPhone() {
        phone_str = phone_et.getText().toString();
        if (StringUtil.isNullOrEmpty(phone_str)) {
            AppContext.showToastShort(R.string.regist_phone_null);
        } else if (!StringUtil.isCellphone(phone_str)) {
            AppContext.showToastShort(R.string.regist_phone_format_err);
            phone_et.requestFocus();
        } else {
            startCountDown();

            try {
                // 验证手机号有没有注册过
                LoginManager.getInstance()
                        .checkPhone(phone_str, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                Message msg = handler.obtainMessage();
                                msg.what = -1;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onNoNetwork() {
                                Message msg = handler.obtainMessage();
                                msg.what = -5;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onErr(Object obj) {
                                /**error（出错信息）-1 手机号码已存在，-2 手机号码格式有误*/
                                if ((int) obj == -2) { // phone format err
                                    Message msg = handler.obtainMessage();
                                    msg.what = -1;
                                    handler.sendMessage(msg);
                                } else if ((int) obj == -1) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 获取验证码
     */
    private void getSmsCode() {
        LoginManager.getInstance()
                .getPhoneSms(phone_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
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
        get_code_sms.setClickable(false);
        handler.removeCallbacks(runnable_sms);
        get_code_sms.setTextColor(getResources().getColor(R.color.chat_input_hint));
        recLen_sms = 60;
        handler.postDelayed(runnable_sms, 1000);
    }

    private void stopCountDown() {
        handler.removeCallbacks(runnable_sms);
        get_code_sms.setText(R.string.regist_get_code);
        get_code_sms.setTextColor(getResources().getColor(R.color.comment_text));
        get_code_sms.setClickable(true);
    }

    Runnable runnable_sms = new Runnable() {
        @Override
        public void run() {
            recLen_sms--;
            if (recLen_sms > 0) {
                get_code_sms.setClickable(false);
                get_code_sms.setText(recLen_sms + resend_str);
                handler.postDelayed(this, 1000);
            } else {
                stopCountDown();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.RETRIEVE_PASSWORD1_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.RETRIEVE_PASSWORD1_FRAGMENT);
    }
}

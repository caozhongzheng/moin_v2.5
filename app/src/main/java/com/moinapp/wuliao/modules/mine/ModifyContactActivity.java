package com.moinapp.wuliao.modules.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.Tools;
import com.umeng.analytics.MobclickAgent;

import butterknife.OnClick;

/**
 * 修改手机号
 * Created by liujiancheng on 16/1/28.
 */
public class ModifyContactActivity extends BaseActivity implements View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("fillphone");

    public static final String KEY_CONTACT = "contact";
    TextView mTvGetCode;
    EditText mEtPhone;
    EditText mEtCode;
    ImageView mClearPhone;
    protected CommonTitleBar title;

    private String phone_str, code_str,resend_str;
    private int recLen_sms = 60;

    private final int NULL = 0, ERROR = -1, CORRECT = 1;
    private int phone_state = NULL, code_state = NULL;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_contact;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        title = (CommonTitleBar) findViewById(R.id.title_layout);
        mEtCode = (EditText) findViewById(R.id.et_code);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mTvGetCode = (TextView) findViewById(R.id.get_code_sms);
        mClearPhone = (ImageView) findViewById(R.id.clear_username);

        title.setTitleTxt(getString(R.string.i_cellphone));
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setRightTxtBtn(getString(R.string.save));
        title.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isFastDoubleClick()) {
                    if (phone_state == CORRECT && code_state != NULL) {
                        if (!"".equals(code_str)) {
                            phoneVerify();
                        }
                    } else {
                        AppContext.showToast(R.string.regist_input_error);
                    }
                }
            }
        });

        resend_str = getString(R.string.regist_resend);
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (!StringUtil.isNullOrEmpty(mEtPhone.getText().toString())) {
                    mClearPhone.setVisibility(View.VISIBLE);
                } else {
                    mClearPhone.setVisibility(View.INVISIBLE);
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

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    //验证成功
                    UserInfo userInfo = new UserInfo();
                    userInfo.setContact(phone_str);
                    MineManager.getInstance().updateUserInfo(userInfo, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            Intent data = new Intent();
                            data.putExtra(KEY_CONTACT, phone_str);
                            ModifyContactActivity.this.setResult(0, data);
                            finish();
                        }

                        @Override
                        public void onErr(Object obj) {

                        }

                        @Override
                        public void onNoNetwork() {

                        }
                    });

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
                    AppContext.showToast(R.string.no_network);
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
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception e) {
                mTvGetCode.setClickable(true);
                e.printStackTrace();
            } finally {
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
//        mTvGetCode.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
        handler.removeCallbacks(runnable_sms);
        recLen_sms = 60;
        handler.postDelayed(runnable_sms, 1000);
    }

    private void stopCountDown() {
        handler.removeCallbacks(runnable_sms);
//        mTvGetCode.setBackgroundResource(R.drawable.tag_btn_solid_bg);
        mTvGetCode.setText(R.string.regist_get_code);
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
        LoginManager.getInstance()
                .verifyPhone(phone_str, code_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        Bundle b = new Bundle();
                        b.putString("name", phone_str);
                        b.putString("phone", phone_str);
                        b.putInt("type", 1);
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
                        Message msg = handler.obtainMessage();
                        msg.what = -3;
                        StringBuilder sb = new StringBuilder();
                        switch ((int) object) {
                            case -2:
                                sb.append(getString(R.string.verify_failed));//"-2 信息有误，数据库保存失败"
                                break;
                            case -1:
                                sb.append(getString(R.string.regist_verify_code_wrong));
                                break;
                        }
                        msg.obj = sb.toString();
                        handler.sendMessage(msg);
                    }
                });
    }

    @Override
    @OnClick({R.id.get_code_sms, R.id.clear_username})
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
            case R.id.clear_username:
                mEtPhone.setText("");
                mClearPhone.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.FILL_PHONE_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.FILL_PHONE_FRAGMENT);
    }
}

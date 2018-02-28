package com.moinapp.wuliao.modules.login;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.ui.MainViewPagerFragment;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.model.UploadAvatarResult;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.MD5;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.umeng.analytics.MobclickAgent;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 注册界面
 * Created by moying on 15/5/6.
 */
public class RegistActivity extends BaseActivity {
    private ILogger MyLog = LoggerFactory.getLogger(RegistActivity.class.getSimpleName());
    public static final String PHONE_NUMBER = "phone";
    public static final String SMS_CODE = "sms_code";
    public static final String PASSWORD = "password";

    @InjectView(R.id.title_layout)
    CommonTitleBar mTitleLayout;

    @InjectView(R.id.iv_avatar)
    AvatarView mAvatar;

    @InjectView(R.id.et_username)
    EditText mEtUserName;

    @InjectView(R.id.gender_group)
    RadioGroup mCgGender;

    @InjectView(R.id.gender_male)
    RadioButton mCbMale;

    @InjectView(R.id.gender_female)
    RadioButton mCbFemale;

    @InjectView(R.id.tv_gender)
    EditText mTvGender;

    private String username_str;

    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int username_state = NULL;

    private String mGender;
    private String mPhone;
    private String mSmsCode;
    private String mPassword;
    private String mAvatarId;
    private String mAvatarUrl;

    private int mGotoFollow;

    private boolean isRGChecked = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        LoginManager.getInstance().addActivity(this);

        mTitleLayout.setLeftBtnOnclickListener(v -> {
            finish();
        });

        mTitleLayout.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isFastDoubleClick()) {
                    phoneRegister();
                }
            }
        });
        mEtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEtUserName.getText().toString().length() >= 1 && isRGChecked) {
                    mTitleLayout.setRightBtnClickAble(true);
                    mTitleLayout.setRightBtnColor(getResources().getColor(R.color.common_title_grey));
                } else {
                    mTitleLayout.setRightBtnClickAble(false);
                    mTitleLayout.setRightBtnColor(getResources().getColor(R.color.title_down_line));
                }
                mEtUserName.setError(null);
                username_state = NULL;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        // 校验用户名
        mEtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MyLog.i("mEtUserName.onFocusChange.......hasFocus = " + hasFocus);
                    checkUserName();
                    TDevice.hideSoftKeyboard(mEtUserName);
                }
            }
        });


        mCgGender.clearCheck();
        mCgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                TDevice.hideSoftKeyboard(mEtUserName);
                isRGChecked = true;
                if(mEtUserName.getText().toString()!=null&&mEtUserName.getText().toString().length()>=1){
                    mTitleLayout.setRightBtnClickAble(true);
                    mTitleLayout.setRightBtnColor(getResources().getColor(R.color.common_title_grey));
                }else {
                    mTitleLayout.setRightBtnClickAble(false);
                    mTitleLayout.setRightBtnColor(getResources().getColor(R.color.title_down_line));
                }
                if (checkedId == mCbMale.getId()) {
                    mGender = "male";
                    if (TextUtils.isEmpty(mAvatarId)) {
                        mAvatar.setImageResource(R.drawable.head_male);
                    }
                    mTvGender.setText("男");
                    mCbMale.setBackgroundResource(R.drawable.but_gender_black);
                    mCbFemale.setBackgroundResource(R.drawable.but_gender_gray);
                } else if (checkedId == mCbFemale.getId()) {
                    mGender = "female";
                    mTvGender.setText("女");
                    if (TextUtils.isEmpty(mAvatarId)) {
                        mAvatar.setImageResource(R.drawable.head_female);
                    }
                    mCbFemale.setBackgroundResource(R.drawable.but_gender_black);
                    mCbMale.setBackgroundResource(R.drawable.but_gender_gray);
                }
            }
        });
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.regist_title;
    }

    @Override
    public void initData() {
        mPhone = getIntent().getStringExtra(PHONE_NUMBER);
        mSmsCode = getIntent().getStringExtra(SMS_CODE);
        mPassword = getIntent().getStringExtra(PASSWORD);
        mGotoFollow = getIntent().getIntExtra(Constants.BUNDLE_KEY_GOTO_FOLLOW, 0);
        MyLog.i("mPhone = " + mPhone + ", sms code = " + mSmsCode + ", mPassword = " + mPassword);

        EventBus.getDefault().register(this);
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    AppContext.showToast(R.string.regist_mobile_existed);
                    mEtUserName.requestFocus();
                    break;
                case 1:
                    break;
                case 8:
                    //跳转到获取热门用户界面
                    UIHelper.showHotUser(RegistActivity.this, ClientInfo.getUID(), ClientInfo.getUserName(),mGender,
                            mAvatarUrl, null, mGotoFollow);
                    LoginManager.getInstance().closeLoginUI();
                    break;
                case -3:// 注册失败
                    AppContext.showToast(msg.obj.toString());
                    break;
                case -4:
                    AppContext.showToast(R.string.regist_get_verify_code_fail);
                    break;
                case -5:
                    AppContext.showToast(R.string.get_code_no_internet);
                    break;
            }
        }
    };

    @Override
    @OnClick({R.id.iv_avatar})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_avatar:
                //跳转照片拍摄
                Bundle bundle = new Bundle();
                bundle.putString(DiscoveryConstants.FROM, StringUtil.FROM_REGISTER);
                CameraManager.getInst().openCamera(this, bundle);
                break;
        }
    }

    private void checkUserName() {
        username_str = mEtUserName.getText().toString().trim();
//        username_str = mEtUserName.getText().toString().replaceAll(" ", "");
        MyLog.i("username_str = " + username_str);
        if (username_str.length() == 0) {
            mEtUserName.setError(getString(R.string.regist_username_null));
        } else {
            try {
                if(username_str.length() < 1) {
                    mEtUserName.setError("用户名长度为1至10个字");
                    return;
                }
                if(!AppTools.isName(username_str)) {
                    mEtUserName.setError("用户名首字母不符合要求");
                    return;
                }
                LoginManager.getInstance().checkUserName(username_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        mEtUserName.setError(null);
                        username_state = CORRECT;
                    }

                    @Override
                    public void onErr(Object obj) {
                        mEtUserName.setError(getString(R.string.invalid_username_tip));
                    }

                    @Override
                    public void onNoNetwork() {
                        AppContext.showToastShort(R.string.no_network);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

        }
    }

    private void phoneRegister() {
        if (TextUtils.isEmpty(mGender)) {
            AppContext.toast(this, getString(R.string.regist_select_gender_tip));
            return;
        }
        username_str = mEtUserName.getText().toString().trim();
        if (username_str.length() == 0) {
            mEtUserName.setError(getString(R.string.regist_username_null));
            return;
        }
        if(username_str.length() < 1) {
            mEtUserName.setError("用户名长度为1至10个字");
            return;
        }
        if(!AppTools.isName(username_str)) {
            mEtUserName.setError("用户名首字母不符合要求");
            return;
        }
        LoginManager.getInstance()
                .registerUser(username_str, mPhone, MD5.md5(mPassword), mSmsCode, mGender, mAvatarId, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        Bundle b = new Bundle();
                        b.putString("name", username_str);
                        b.putString("phone", mPhone);
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
                                sb.append(getString(R.string.regist_failed));//"-2 信息有误，数据库保存失败"
                                break;
                            case -3:
                                sb.append(getString(R.string.regist_failed));//"-2 信息有误，数据库保存失败"
                                break;
                            case -1:
                                sb.append(getString(R.string.regist_verify_code_wrong));
                                break;
                            case -10:
                                sb.append(getString(R.string.username_unlegal));
                                break;
                        }
                        msg.obj = sb.toString();
                        handler.sendMessage(msg);
                    }
                });
    }

    public void onEvent(String avatarPath) {
        MyLog.i("received avatarPath:" + avatarPath);
        RegistActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MineManager.getInstance().uploadAvatar(avatarPath, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        UploadAvatarResult result = (UploadAvatarResult) obj;
                        mAvatarId = result.getAvatar();
                        MyLog.i("mAvatarId:" + mAvatarId);
                        RegistActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mAvatar != null && result.getUrl() != null) {
                                    mAvatarUrl = result.getUrl();
                                    ImageLoaderUtils.displayHttpImage(result.getUrl(), mAvatar, null);
                                }
                            }
                        });

                    }

                    @Override
                    public void onErr(Object obj) {

                    }

                    @Override
                    public void onNoNetwork() {

                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().removeActivity(this);
    }
}

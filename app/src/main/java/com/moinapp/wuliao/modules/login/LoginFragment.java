package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.bean.Location;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.login.model.ThirdInfo;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.model.UploadAvatarResult;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.MD5;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/** 登录界面
 * Created by liujiancheng on 15/10/28.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("login");
    protected static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.et_phone_num)
    EditText mEtUserName;

    @InjectView(R.id.et_password)
    EditText mEtPassword;

    @InjectView(R.id.get_code_sms)
    TextView mTvGetCode;

    @InjectView(R.id.et_code)
    EditText mEtCode;

    @InjectView(R.id.register_captcha_form)
    LinearLayout mRlCode;

    @InjectView(R.id.btn_login)
    TextView mBtnLogin;

    @InjectView(R.id.title_bar)
    CommonTitleBar titleBar;

    @InjectView(R.id.clear_username)
    ImageView clearUsername;

    @InjectView(R.id.clear_password)
    ImageView clearPassword;

    private String mPhone = "";
    private String mPassword = "";
    private int mGotoDiscovery;

    private String code_str, resend_str;
    private int recLen_sms = 60;

    private final int NULL = 0, ERROR = -1;
    private int code_state = NULL;
    private int codeLayoutVisibility = -1;
    private boolean mNeedSmsCode;

    /**
     * 第三方登陆第一次上传头像后返回的文件id
     */
    private String mAvatarId;
    private String mAvatarUrl;

    private ImageLoader imageLoader;
    private Activity mActivity;
    private UserInfo mThirdUserInfo;
    private ThirdInfo mThirdInfo;

    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_moin, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
          // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mActivity, Constants.WEICHAT_APPID, Constants.WEICHAT_SECRET);
        mController.getConfig().setSsoHandler(wxHandler);

        // 添加QQ平台
        UMQQSsoHandler qqHandler = new UMQQSsoHandler(mActivity, Constants.QQ_APPID, Constants.QQ_APPKEY);
        mController.getConfig().setSsoHandler(qqHandler);

        // 添加新浪微博平台
        mController.getConfig().setSsoHandler(new SinaSsoHandler(mActivity));

        if (!wxHandler.isClientInstalled()) {
            view.findViewById(R.id.text_wx).setVisibility(View.GONE);
        }
        if (!qqHandler.isClientInstalled()) {
            view.findViewById(R.id.text_qq).setVisibility(View.GONE);
        }

        mEtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLoginBtnBackground();
                if (!StringUtil.isNullOrEmpty(mEtUserName.getText().toString())) {
                    clearUsername.setVisibility(View.VISIBLE);
                } else {
                    clearUsername.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !StringUtil.isNullOrEmpty(mEtUserName.getText().toString())) {
                    clearUsername.setVisibility(View.VISIBLE);
                } else {
                    clearUsername.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLoginBtnBackground();
                if (!StringUtil.isNullOrEmpty(mEtPassword.getText().toString())) {
                    clearPassword.setVisibility(View.VISIBLE);
                } else {
                    clearPassword.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLoginBtnBackground();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        codeLayoutVisibility = mRlCode.getVisibility();

        titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        updateSmsCodeView();
    }

    private void setLoginBtnBackground(){
        if (!StringUtil.isNullOrEmpty(mEtUserName.getText().toString()) && !StringUtil.isNullOrEmpty(mEtPassword.getText().toString())) {
            //手机号和密码都不为空,
            if (codeLayoutVisibility == 0 ) {
                if (!StringUtil.isNullOrEmpty(mEtCode.getText().toString())) {
                    // 验证码布局显示,并且内容不为空
                    mBtnLogin.setClickable(true);
                    mBtnLogin.setBackgroundResource(R.drawable.but_login_black);
                } else {
                    mBtnLogin.setClickable(false);
                    mBtnLogin.setBackgroundResource(R.drawable.but_login_gray);
                }
            } else {
                mBtnLogin.setClickable(true);
                mBtnLogin.setBackgroundResource(R.drawable.but_login_black);
            }
        } else {
            mBtnLogin.setClickable(false);
            mBtnLogin.setBackgroundResource(R.drawable.but_login_gray);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mGotoDiscovery = args.getInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, 0);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        LoginManager.getInstance().addActivity(activity);
    }

    @Override
    @OnClick({R.id.tv_retrive_password, R.id.get_code_sms, R.id.btn_login, R.id.text_qq,
            R.id.text_wx, R.id.text_wb, R.id.phone_regitry, R.id.clear_username, R.id.clear_password})
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.tv_retrive_password:
                if (!Tools.isFastDoubleClick()) {
                    AppTools.toIntent(mActivity, RetrivePasswordActivity.class);
                }
                break;
            case R.id.get_code_sms:
                prepareGetSmscode();
                break;
            case R.id.btn_login:
                handleLogin();
                break;
            case R.id.text_qq:
                authorize(SHARE_MEDIA.QQ);
                break;
            case R.id.text_wx:
                authorize(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.text_wb:
                authorize(SHARE_MEDIA.SINA);
                break;
            case R.id.phone_regitry:
                UIHelper.showRegistryActivity(mActivity, mGotoDiscovery);
                break;
            case R.id.clear_username:
                // 清楚账号输入框
                mEtUserName.setText("");
                clearUsername.setVisibility(View.INVISIBLE);
                break;
            case R.id.clear_password:
                // 清楚账号输入框
                mEtPassword.setText("");
                clearPassword.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    // 当登陆失败3次后要出现验证码
    private void updateSmsCodeView() {
        int times = LoginPreference.getInstance().getLoginFailedTimes();
        ViewGroup.LayoutParams layoutParams = mBtnLogin.getLayoutParams();
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(layoutParams);
        if (times < 3) {
            mRlCode.setVisibility(View.GONE);
            codeLayoutVisibility = mRlCode.getVisibility();
            layoutParams1.topMargin = (int) TDevice.dpToPixel(65);
            mBtnLogin.setLayoutParams(layoutParams1);
            setLoginBtnBackground();
            mNeedSmsCode = false;
        } else {
            mRlCode.setVisibility(View.VISIBLE);
            codeLayoutVisibility = mRlCode.getVisibility();
            layoutParams1.topMargin = (int) TDevice.dpToPixel(10);
            mBtnLogin.setLayoutParams(layoutParams1);
            setLoginBtnBackground();
            resend_str = getString(R.string.regist_resend);
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
            mNeedSmsCode = true;
        }
    }

    private void handleLogin() {

        if (prepareForLogin()) {
            return;
        }

        // if the data has ready
        mPhone = mEtUserName.getText().toString();
        mPassword = mEtPassword.getText().toString();

        showWaitDialog(R.string.progress_login);
        LoginManager.getInstance().userOurLogin(mPhone, MD5.md5(mPassword), code_str, new IListener() {

            @Override
            public void onSuccess(Object obj) {
                handler.removeCallbacks(runnable_sms);
                AppContext.showToast("登录成功");
                hideWaitDialog();
                LoginPreference.getInstance().setLoginFailedTimes(0);
                // 登录成功后如果用户名等为空则应该去补填用户的信息
                if (StringUtil.needFillInfo((Integer) obj)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, mGotoDiscovery);
                    AppTools.toIntent(mActivity, bundle, UserinfoShortActivity.class);
                    mActivity.finish();
                } else {
                    handleLoginSuccess();
                }
            }

            @Override
            public void onErr(Object obj) {
                if (obj != null) {
                    AppContext.showToast(getErrorInfo(obj));
                }
                hideWaitDialog();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSmsCodeView();
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(R.string.get_code_no_internet);
                hideWaitDialog();
            }
        });
    }

    private void prepareGetSmscode() {
        if (!TDevice.hasInternet()){
            AppContext.showToastShort(R.string.get_code_no_internet);
            return;
        }
        mPhone = mEtUserName.getText().toString();
        if (mPhone.length() == 0) {
            AppContext.showToast(R.string.regist_phone_null);
            mTvGetCode.setClickable(true);
        } else if (!StringUtils.isCellphone(mPhone)) {
            AppContext.showToast(R.string.regist_phone_format_err);
            mTvGetCode.setClickable(true);
        } else {
            startCountDown();
            getSmsCode();
        }
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                case -10:
                    AppContext.showToast(R.string.login_download_avatar_error);
                    fillUsername(mThirdUserInfo);
                    break;
                case 10:
                    uploadAvatar();
                    break;
            }
        }
    };

    /**
     * 获取验证码
     */
    private void getSmsCode() {
        LoginManager.getInstance()
                .getPhoneSms(mPhone, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("getSmsCode=" + mPhone + ", recLen=" + recLen_sms);
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
        mTvGetCode.setTextColor(getResources().getColor(R.color.chat_input_hint));
        handler.postDelayed(runnable_sms, 1000);
    }

    private void stopCountDown() {
        handler.removeCallbacks(runnable_sms);
        mTvGetCode.setText(R.string.regist_get_code);
        mTvGetCode.setTextColor(getResources().getColor(R.color.comment_text));
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

    private String getErrorInfo(Object object) {
        // error (出错信息)，-1 用户名密码错误 -2 验证码错误 -3 提交信息有误，数据库保存失败
        StringBuilder sb = new StringBuilder();
        switch ((int) object) {
            case -1:
                sb.append(getString(R.string.login_input_err));
                int times = LoginPreference.getInstance().getLoginFailedTimes();
                times++;
                LoginPreference.getInstance().setLoginFailedTimes(times);
                break;
            case ErrorCode.ERROR_PHONE_NOT_REGISTER:
                sb.append(getString(R.string.login_phone_not_register));
                break;
            case ErrorCode.ERROR_USER_FORBIDDEN:
                sb.append(getString(R.string.login_user_forbidden));
                break;
            case -2:
                sb.append(getString(R.string.verify_failed));
                break;
            case -3:
                sb.append(getString(R.string.login_failed));
                break;
        }
        return sb.toString();
    }

    private void handleLoginSuccess() {
        mActivity.sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));

        if (mGotoDiscovery == 1) {
            //跳转到发现
            UIHelper.gotoMain(mActivity, MainActivity.KEY_TAB_DISCOVERY, false);
        }
        TDevice.hideSoftKeyboard(mEtPassword);
        LoginManager.getInstance().closeLoginUI();
    }

    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.get_code_no_internet);
            return true;
        }
        String username = mEtUserName.getText().toString();
        if (username.startsWith(" ")){
            mEtUserName.setError("开头不能为空");
            mEtUserName.requestFocus();
            return true;
        }
        if (StringUtil.isNullOrEmpty(username)) {
            mEtUserName.setError("请输入手机号");
            mEtUserName.requestFocus();
            return true;
        }
        String password = mEtPassword.getText().toString().replaceAll(" ", "");
        if (StringUtil.isNullOrEmpty(password)) {
            mEtPassword.setError("请输入密码");
            mEtPassword.requestFocus();
            return true;
        }
        if (password.length() < 6) {
            mEtPassword.setError("密码长度至少6位");
            mEtPassword.requestFocus();
            return true;
        }
        if (!StringUtil.isCellphone(username)) {
            mEtUserName.setError("手机号格式不对");
            mEtUserName.requestFocus();
            return true;
        }

        if (mNeedSmsCode && TextUtils.isEmpty(code_str)) {
            mEtCode.setError("请输入验证码");
            mEtCode.requestFocus();
            return true;
        }
        return false;
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
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void authorize(final SHARE_MEDIA platform) {
        MyLog.i("login platform=" + platform.name());
        mController.doOauthVerify(mActivity, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                MyLog.i("授权开始" + platform);
                AppContext.showToastShort("授权开始...");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                MyLog.i("授权失败");
                AppContext.showToastShort("授权失败...");
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                MyLog.i("授权完成" + value);
                AppContext.showToastShort("授权完成...");
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    MyLog.i(value.toString());
                    loginThird(platform, value);
                } else {
                    AppContext.showToastShort("授权失败uid is empty...");
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                MyLog.i("授权取消");
                AppContext.showToastShort("授权取消...");
            }
        });
    }

    private void loginThird(final SHARE_MEDIA platform, final Bundle value) {
        MyLog.i("loginThird: " + platform + "," + value);
        LoginManager.getInstance().userThirdLogin(platform.toString(), value, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("合作账号登录成功: " + platform + "," + obj.toString());
//                AppContext.showToastShort("合作账号登录成功");
                int code = (int) obj;
                if (code == 3) {//3 使用第三方帐号第一次登录，需要update用户个人信息
                    getUserInfo(platform, value);
                } else {
                    handleLoginSuccess();
                }
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(R.string.no_network);
            }

            @Override
            public void onErr(Object object) {
                if (object != null) {
                    MyLog.i("login third failed: " + object.toString());
                    AppContext.showToastShort(getErrorInfo(object));
                }
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform, final Bundle b) {
        mController.getPlatformInfo(mActivity, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == 200 && info != null) {
//                    AppContext.showToastShort(info.toString());
                    MyLog.i(info.toString());
                    MyLog.i("platform:" + platform + ", " + (platform == SHARE_MEDIA.QQ));
                    if (platform == SHARE_MEDIA.WEIXIN) {
                        MyLog.i("WX OK:");
                        onAuthByWeiXin(info);
                    } else if (platform == SHARE_MEDIA.QQ) {
                        MyLog.i("QQ OK:");
                        onAuthByQQ(info);
                    } else if (platform == SHARE_MEDIA.SINA) {
                        MyLog.i("SINA OK:");
                        onAuthBySina(info, b);
                    }
                } else {
                    //如果获取失败
                    AppContext.showToast("获取三方信息失败!");
                }

            }
        });
    }

    private void onAuthBySina(Map<String, Object> info, Bundle bundle) {
        // update user info
        mThirdUserInfo = new UserInfo();
        mThirdUserInfo.setSex(StringUtil.getGender(mActivity, info.get("gender").toString(), false));
        //TODO nickname max length
        String username = info.get("screen_name").toString();
        if (username.length() > 10) username = username.substring(0, 10);
        mThirdUserInfo.setUsername(username);
        Location location = new Location();
        location.setProvince(info.get("location").toString());
        mThirdUserInfo.setLocation(location);
        BaseImage avatar = new BaseImage();
        avatar.setUri(info.get("profile_image_url").toString());
        mThirdUserInfo.setAvatar(avatar);

        mThirdInfo = null;
        if (bundle != null) {
            mThirdInfo = convertBundle2Token (bundle);
        }

//        udpateUserInfo(mThirdUserInfo);
        uploadAvatar(mThirdUserInfo);

//        fillUsername(us, bundle);
    }

    private void onAuthByWeiXin(Map<String, Object> info) {
        // update user info
        mThirdUserInfo = new UserInfo();
        mThirdUserInfo.setSex(StringUtil.getGender(mActivity, info.get("sex").toString(), false));
        //TODO nickname max length
        String username = info.get("nickname").toString();
        if (username.length() > 10) username = username.substring(0, 10);
        mThirdUserInfo.setUsername(username);
        Location location = new Location();
        location.setCountry(info.get("country").toString());
        location.setProvince(info.get("province").toString());
        location.setCity(info.get("city").toString());
        mThirdUserInfo.setLocation(location);
        BaseImage avatar = new BaseImage();
        avatar.setUri(info.get("headimgurl").toString());
        mThirdUserInfo.setAvatar(avatar);

//        udpateUserInfo(mThirdUserInfo);
        uploadAvatar(mThirdUserInfo);

//        fillUsername(us, null);
    }

    private void onAuthByQQ(Map<String, Object> info) {
        // update user info
        mThirdUserInfo = new UserInfo();
        mThirdUserInfo.setSex(StringUtil.getGender(mActivity, info.get("gender").toString(), false));
        //TODO nickname max length
        String username = info.get("screen_name").toString();
        if (username.length() > 10) username = username.substring(0, 10);
        mThirdUserInfo.setUsername(username);
        Location location = new Location();
        location.setProvince(info.get("province").toString());
        location.setCity(info.get("city").toString());
        mThirdUserInfo.setLocation(location);
        BaseImage avatar = new BaseImage();
        avatar.setUri(info.get("profile_image_url").toString());
        mThirdUserInfo.setAvatar(avatar);

//        udpateUserInfo(mThirdUserInfo);
        uploadAvatar(mThirdUserInfo);

//        fillUsername(us, null);
    }

    /**
     * 补充用户名信息
     */
    private void fillUsername(UserInfo us) {
        UIHelper.showHotUser(mActivity, ClientInfo.getUID(), us.getUsername(),
                us.getSex(), mAvatarUrl, mThirdInfo, mGotoDiscovery);
        LoginManager.getInstance().closeLoginUI();
    }

    private ThirdInfo convertBundle2Token(Bundle b) {
        ThirdInfo info = new ThirdInfo();
        info.access_token = b.getString(LoginConstants.ACCESS_TOKEN);
        info.refresh_token = b.getString(LoginConstants.REFRESH_TOKEN);
        info.openid = b.getString(LoginConstants.OPEN_ID);
        info.expires_in = Long.parseLong(b.getString(LoginConstants.EXPIRE_IN));
        info.refresh_token_expires = System.currentTimeMillis();
        info.unionid = b.getString(LoginConstants.UNION_ID);
        info.uid = b.getString(LoginConstants.UID);
        return info;
    }

    private void udpateUserInfo(UserInfo us) {
        MineManager.getInstance().updateUserInfo(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    UserInfo user = (UserInfo) obj;
                    MyLog.i("update userinfo OK:" + user.toString());
                    ClientInfo.setUserName(user.getUsername());

                    fillUsername(mThirdUserInfo);
                }
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("update userinfo NG:" + AppContext.getInstance().getString(R.string.no_network));
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("update userinfo NG:" + object != null ? object.toString() : "null");
            }
        });
    }

    private void updateAvatar() {
        //改变登陆注册逻辑后需要在更改头像后再调用接口更新头像
        MineManager.getInstance().updateUserAvatar(mAvatarId, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("update user avatar succeed!");
            }

            @Override
            public void onErr(Object obj) {
            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    private void uploadAvatar(final UserInfo us) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (us == null || us.getAvatar() == null)
                    return;
                String headimgurl = us.getAvatar().getUri();
                final boolean b = HttpUtil.download(headimgurl, BitmapUtil.getAvatarImagePath(), true);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int result;
                        if (!b) {
                            result = -10;
                        } else {
                            result = 10;
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = result;
                        handler.sendMessage(msg);
                    }
                });

            }
        }).start();
    }

    private void uploadAvatar() {
        MineManager.getInstance().uploadAvatar(BitmapUtil.getAvatarImagePath(), new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    UploadAvatarResult result = (UploadAvatarResult) obj;
                    mAvatarId = result.getAvatar();
                    mAvatarUrl = result.getUrl();

                    //把服务器的头像地址更新
                    updateAvatar();
                    udpateUserInfo(mThirdUserInfo);
                    MyLog.i("upload avatar succeed:mAvatarId:" + mAvatarId);
                }
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("upload avatar NG:" + AppContext.getInstance().getString(R.string.no_network));
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("upload avatar NG:" + object != null ? object.toString() : "null");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.LOGIN_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.LOGIN_FRAGMENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().removeActivity(mActivity);
    }
}

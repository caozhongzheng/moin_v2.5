package com.moinapp.wuliao.modules.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.login.model.ThirdInfo;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.mine.model.UploadAvatarResult;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import butterknife.InjectView;
import butterknife.OnClick;

/** 登录成功后如果用户名等为空则应该去补填用户的信息
 * Created by moying on 15/9/9.
 */
public class UserinfoShortActivity extends BaseActivity {

    private ILogger MyLog = LoggerFactory.getLogger("userinfo");

    @InjectView(R.id.user_avatar)
    ImageView mIvAvatar;
    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.gender_group)
    RadioGroup mCgGender;
    @InjectView(R.id.gender_male)
    RadioButton mCbMale;
    @InjectView(R.id.gender_female)
    RadioButton mCbFemale;

    String mAvatarUrl;
    String mGender;
    private String mAvatarId;//头像上传成功后返回的id

    private int mGotoFollow;
    private ThirdInfo mToken;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo_short;
    }

    @Override
    public void initView() {
        LoginManager.getInstance().addActivity(this);

        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mEtUsername.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == mCbMale.getId()) {
                    mGender = "male";
                } else if (checkedId == mCbFemale.getId()) {
                    mGender = "female";
                }
            }
        });
    }

    @Override
    public void initData() {
        //注册eventbus
        EventBus.getDefault().register(this);

        //接收从第三方登陆传来的用户名 头像 性别
        Intent intent = getIntent();
        if (intent != null) {
            String username_str = StringUtil.nullToEmpty(intent.getStringExtra(Constants.BUNDLE_KEY_USERNAME));
            mEtUsername.setText(username_str);

            mGender = StringUtil.nullToEmpty(intent.getStringExtra(Constants.BUNDLE_KEY_GENDER));
            if (mGender.equals("female")) {
                mCbFemale.setChecked(true);
            } else {
                mCbMale.setChecked(true);
            }

            mAvatarUrl = StringUtil.nullToEmpty(intent.getStringExtra(Constants.BUNDLE_KEY_AVATAR));
            mGotoFollow = intent.getIntExtra(Constants.BUNDLE_KEY_GOTO_FOLLOW, 0);
            mToken = (ThirdInfo) intent.getSerializableExtra(Constants.BUNDLE_KEY_TOKEN);
        }

        //首先上传第三方头像
        uploadThirdAvatar();
    }

    //上传第三方头像
    private void uploadThirdAvatar() {
        MyLog.i("第三方头像url=" + mAvatarUrl);
        if (TextUtils.isEmpty(mAvatarUrl))
            return;
        try {
            ImageLoaderUtils.displayHttpImage(mAvatarUrl, BitmapUtil.getImageLoaderOption(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    MyLog.i("loadSync onLoadingStarted avatar1 url:" + mAvatarUrl);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    MyLog.i("loadSync onLoadingFailed avatar1 url:" + mAvatarUrl);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    MyLog.i("loadSync onLoadingComplete avatar1 url:" + mAvatarUrl);
                    Bitmap avatar = ImageLoaderUtils.getImageFromCache(mAvatarUrl);
                    if (avatar == null) {
                        MyLog.i("loadSync avatar2 url:" + mAvatarUrl);
                        avatar = ImageLoader.getInstance().loadImageSync(mAvatarUrl, BitmapUtil.getImageLoaderOption());
                    }
                    if (avatar == null) {
                        MyLog.i("download1 avatar url:" + mAvatarUrl);
                        boolean b = HttpUtil.download(mAvatarUrl, BitmapUtil.getAvatarImagePath());
                        if (!b) {
                            b = HttpUtil.download(mAvatarUrl, BitmapUtil.getAvatarImagePath());
                            MyLog.i(b + ", download2 avatar url:" + mAvatarUrl);
                        }
                    } else {
                        BitmapUtil.saveUserAvatar(UserinfoShortActivity.this, bitmap);
                    }

                    MyLog.i("upload avatar path:" + BitmapUtil.getAvatarImagePath());
                    MineManager.getInstance().uploadAvatar(BitmapUtil.getAvatarImagePath(), new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            UploadAvatarResult result = (UploadAvatarResult) obj;
                            mAvatarId = result.getAvatar();
                            MyLog.i("mAvatarId:" + mAvatarId);
                        }

                        @Override
                        public void onNoNetwork() {
                            MyLog.i("upload avatar NG:" + getString(R.string.no_network));
                        }

                        @Override
                        public void onErr(Object object) {
                            MyLog.i("upload avatar NG:" + object.toString());
                        }
                    });

                    //显示头像
                    UserinfoShortActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoaderUtils.displayLocalImage(BitmapUtil.getAvatarImagePath(), mIvAvatar, null);
                        }
                    });
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e(e);
        }
    }

    @Override
    @OnClick({R.id.back, R.id.btn_register, R.id.gender_male, R.id.gender_female})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_register:
                if (!Tools.isFastDoubleClick()) {
                    String names = mEtUsername.getText().toString().replaceAll(" ", "");
                    if (StringUtil.isNullOrEmpty(names)) {
                        mEtUsername.setError("请输入用户名");
                        return;
                    }
                    if (!AppTools.isName(names)) {
                        mEtUsername.setError("用户名首字母不符合要求");
                        return;
                    }
                    if (names.length() < 2) {
                        mEtUsername.setError("用户名长度为2至10个字");
                        return;
                    }
                    if (mGender.length() == 0) {
                        AppContext.showToastShort("请选择性别");
                        return;
                    }
                    MyLog.i("开始更新用户名和性别:");
                    checkUserName(names);
                }
                break;
            case R.id.avatar_item:
                //跳转照片拍摄
                Bundle bundle = new Bundle();
                bundle.putString(DiscoveryConstants.FROM, StringUtil.FROM_REGISTER);
                CameraManager.getInst().openCamera(this, bundle);
                break;
        }
    }

    /**
     * 检查用户名唯一性,并更新用户名
     * 0 已存在，不能使用 1 可以使用
     *
     * @param username
     */
    private void checkUserName(final String username) {
        if (StringUtil.isNullOrEmpty(username)) {
            return;
        }
        LoginManager.getInstance().checkUserName(username, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                mEtUsername.setError(null);
                updateUsername(username);
            }

            @Override
            public void onErr(Object obj) {
                mEtUsername.setError(getString(R.string.invalid_username_tip));
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(R.string.no_network);
            }
        });
    }

    //用户名检查通过后调用服务器接口更新用户信息
    private void updateUsername(final String username) {
        UserInfo us = new UserInfo();
        us.setUsername(username);
        us.setSex(mGender);
        MineManager.getInstance().updateUserInfo(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("username update OK: " + username);
                MinePreference.getInstance().setUsername(username);
                AppContext.showToastShort(String.format(getResources().getString(R.string.regist_userinfo_success), username));

                //更新用户信息成功后,单独更新用户头像
                updateUserAvatar();

                //登陆成功
                handleLoginSuccess();
            }

            @Override
            public void onErr(Object obj) {
                AppContext.showToastShort(String.format(getResources().getString(R.string.regist_userinfo_failed), username));
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(R.string.no_network);
            }
        });
    }

    private void updateUserAvatar() {
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

    private void handleLoginSuccess() {
        Intent data = new Intent();
        data.putExtra(Constants.BUNDLE_KEY_REQUEST_CODE, LoginActivity.REQUEST_CODE_INIT);
        setResult(RESULT_OK, data);
        this.sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));

        //跳转填写电话号码的界面 3.2.5以后不需要这个填写联系方式的界面了
//        UIHelper.showFillPhone(this, ClientInfo.getUID(), mGender, mToken, mGotoFollow);
        UIHelper.showHotUser(this, ClientInfo.getUID(), ClientInfo.getUserName(), mGender,
                mAvatarUrl, mToken, mGotoFollow);
        finish();
    }

    public void onEvent(String avatarPath) {
        MyLog.i("received avatarPath:" + avatarPath);
        UserinfoShortActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //用户主动进入拍摄照片界面更改头像后上传修改后的头像
                MineManager.getInstance().uploadAvatar(avatarPath, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        UploadAvatarResult result = (UploadAvatarResult) obj;
                        mAvatarId = result.getAvatar();
                        MyLog.i("mAvatarId:" + mAvatarId);
                        UserinfoShortActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mIvAvatar != null && result.getUrl() != null) {
                                    mAvatarUrl = result.getUrl();
                                    ImageLoaderUtils.displayHttpImage(result.getUrl(), mIvAvatar, null);
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
        MobclickAgent.onPageStart(UmengConstants.USERINFO_SHORT_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.USERINFO_SHORT_ACTIVITY); //
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().removeActivity(this);
    }
}

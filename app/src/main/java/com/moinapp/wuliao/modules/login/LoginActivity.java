package com.moinapp.wuliao.modules.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;


/**
 * 用户登录界面
 */
public class LoginActivity extends BaseActivity {
    //是否登录成功后要跳转到首页
    private int gotoDiscovery;
    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    @Override
    public void initView() {
//        LoginViewPagerFragment fragment = new LoginViewPagerFragment();
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, gotoDiscovery);
        fragment.setArguments(args);

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.content, fragment, "login");
        trans.commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        if (getIntent() != null) {
            gotoDiscovery = getIntent().getIntExtra(Constants.BUNDLE_KEY_GOTO_FOLLOW, 0);
        }
        initView();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    public static final int REQUEST_CODE_INIT = 0;
    public static final int REQUEST_CODE_OPENID = 1000;

    //添加微博第三方登陆的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
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
}

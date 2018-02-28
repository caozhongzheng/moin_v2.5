package com.moinapp.wuliao.modules.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragmentActivity;
import com.moinapp.wuliao.base.FragmentSkip;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.util.MD5;
import com.moinapp.wuliao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 找回密码界面
 * Created by moying on 15/5/6.
 */
public class RetrivePasswordActivity extends BaseFragmentActivity implements FragmentSkip {
    private ILogger MyLog = LoggerFactory.getLogger("login");

    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private String phone, captcha, email, new_password;
    private boolean isneedFillUsername = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.retrive_layout);
        initViewPager();
    }

    public void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragments = new ArrayList<Fragment>();
        Fragment fragment1 = new RetrievePassword1_Fragment();
        Fragment fragment2 = new RetrievePassword2_Fragment();
        Fragment fragment3 = new RetrievePassword3_Fragment();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            goback();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                goback();
        }
    }

    private void goback() {
        finish();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AppContext.showToastShort(R.string.retrieve_success);
                    // TODO skip to MoinActivity or just finish self?
                    MyLog.i("handler isneedFillUsername:" + isneedFillUsername);
                    handleLoginSuccess();
                    break;
                case -1:
                    AppContext.showToastShort(R.string.retrieve_fail);
                    break;
                case -2:
                    AppContext.showToastShort(R.string.no_network);
                    break;
            }
        }
    };

    private void handleLoginSuccess() {
        Intent data = new Intent();
        data.putExtra(Constants.BUNDLE_KEY_REQUEST_CODE, LoginActivity.REQUEST_CODE_INIT);
        setResult(RESULT_OK, data);
        this.sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));
        finish();
        LoginManager.getInstance().closeLoginUI();
    }

    private int page;

    @Override
    public void skip(int position, String... params) {
        if (position == 1) {
            page = position;
            handler.post(runnable);
            phone = params[0];
            captcha = params[1];
            email = params[2];
            isneedFillUsername = StringUtil.needFillInfo(Integer.parseInt(params[3]));
            if(!isneedFillUsername) {
                page++;
            }
            MyLog.i("skip isneedFillUsername:" + isneedFillUsername + ", params[3]:" + params[3]);
        } else if (position == 2) {
            page = position;
            isneedFillUsername = false;
            handler.post(runnable);
        } else if (position == 3) {
            page = position;
            new_password = params[0];
            /**更新的密码 * 返回：result 0 失败，1 成功 * error: -1 找不到当前用户 -2 密码不正确或者session信息有误 -3 信息有误，保存数据库失败 */
            MineManager.getInstance().updatePassword(MD5.md5(new_password), new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }

                @Override
                public void onNoNetwork() {
//                            Message msg = handler.obtainMessage();
//                            msg.what = -2;
//                            handler.sendMessage(msg);
                    AppContext.showToastShort(R.string.no_network);
                }

                @Override
                public void onErr(Object object) {
                    AppContext.showToastShort(R.string.retrieve_fail);
//                            Message msg = handler.obtainMessage();
//                            msg.what = -1;
//                            handler.sendMessage(msg);
                }
            });

        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(page);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

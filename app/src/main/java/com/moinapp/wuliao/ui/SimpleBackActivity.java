package com.moinapp.wuliao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.SimpleBackPage;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.emoji.OnSendClickListener;
import com.moinapp.wuliao.fragment.MessageDetailFragment;
import com.moinapp.wuliao.modules.mine.CropCosplayFragment;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.lang.ref.WeakReference;

public class SimpleBackActivity extends BaseActivity implements
        OnSendClickListener {
    private static final String TAG = "FLAG_TAG";
    protected WeakReference<Fragment> mFragment;
    protected int mPageValue = -1;
    private int mPopup;

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregist(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (mPopup == 1) {
            this.getWindow().setGravity(Gravity.BOTTOM);
            int enterAnimId = R.anim.anim_pop_up;
            this.overridePendingTransition(enterAnimId, 0);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_fragment;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        if (mPageValue == -1) {
            mPageValue = getIntent().getIntExtra(Constants.BUNDLE_KEY_PAGE, 0);
        }

        //fix 友盟bug UMA0100
        if (mPageValue == 0) {
            AppContext.toast(this, getString(R.string.error_view_load_error_click_to_refresh));
            finish();
        } else {
            initFromIntent(mPageValue, getIntent());
        }
    }

    protected void initFromIntent(int pageValue, Intent data) {
        if (data == null) {
            throw new RuntimeException(
                    "you must provide a page info to display");
        }
        SimpleBackPage page = SimpleBackPage.getPageByValue(pageValue);
        if (page == null) {
            throw new IllegalArgumentException("can not find page by value:"
                    + pageValue);
        }

        setActionBarTitle(page.getTitle());

        try {
            Fragment fragment = (Fragment) page.getClz().newInstance();

            Bundle args = data.getBundleExtra(Constants.BUNDLE_KEY_ARGS);
            if (args != null) {
                fragment.setArguments(args);
            }

            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.replace(R.id.container, fragment, TAG);
            trans.commitAllowingStateLoss();

            mFragment = new WeakReference<Fragment>(fragment);

            mPopup = data.getIntExtra(Constants.BUNDLE_KEY_POPUP, 0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                    "generate fragment error. by value:" + pageValue);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
        /**
         if (mFragment.get() instanceof TweetsFragment) {
         setActionBarTitle("话题");
         }**/
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         switch (item.getItemId()) {
         case R.id.public_menu_send:
         if (mFragment.get() instanceof TweetsFragment) {
         sendTopic();
         } else {
         return super.onOptionsItemSelected(item);
         }
         break;
         default:
         break;
         }**/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         if (mFragment.get() instanceof TweetsFragment) {
         getMenuInflater().inflate(R.menu.pub_topic_menu, menu);
         }**/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null && mFragment.get() != null
                && mFragment.get() instanceof BaseFragment) {
            BaseFragment bf = (BaseFragment) mFragment.get();
            if (!bf.onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.ACTION_DOWN
                && mFragment.get() instanceof BaseFragment) {
            ((BaseFragment) mFragment.get()).onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    //微博分享的回调
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
    public void onClick(View v) {
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClickSendButton(String str) {
        if (mFragment.get() instanceof MessageDetailFragment) {
            ((OnSendClickListener) mFragment.get()).onClickSendButton(str);
            ((MessageDetailFragment) mFragment.get()).emojiFragment.clean();
        }
    }

    @Override
    public void onClickFlagButton() {
    }

    public void onEvent(CropCosplayFragment.CropCosplay avatarPath) {
        finish();
    }

}

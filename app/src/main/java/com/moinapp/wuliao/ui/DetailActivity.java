package com.moinapp.wuliao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.base.CommonDetailFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.emoji.Emojicon;
import com.moinapp.wuliao.emoji.MoinEmojiFragment;
import com.moinapp.wuliao.emoji.OnEmojiClickListener;
import com.moinapp.wuliao.emoji.OnSendClickListener;
import com.moinapp.wuliao.emoji.ToolbarFragment;
import com.moinapp.wuliao.emoji.ToolbarFragment.OnActionClickListener;
import com.moinapp.wuliao.emoji.ToolbarFragment.ToolAction;
import com.moinapp.wuliao.fragment.CommentFrament;
import com.moinapp.wuliao.modules.discovery.ui.CosplayDetailFragment;
import com.moinapp.wuliao.modules.post.PostDetailFragment;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 详情activity（包括：资讯、博客、软件、问答、动弹）
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月11日 上午11:18:41
 */
public class DetailActivity extends BaseActivity implements OnSendClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(DetailActivity.class.getSimpleName());

    public static final int DISPLAY_NEWS = 0;
    public static final int DISPLAY_BLOG = 1;
    public static final int DISPLAY_SOFTWARE = 2;
    public static final int DISPLAY_POST = 3;
    public static final int DISPLAY_EVENT = 5;
    public static final int DISPLAY_COMMENT = 10;
    public static final int DISPLAY_DISCOVER_COS = 11;
    public static final int DISPLAY_BANNER = 12;

    private OnSendClickListener currentFragment;
    public MoinEmojiFragment emojiFragment = new MoinEmojiFragment();
    public ToolbarFragment toolFragment = new ToolbarFragment();

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getWindow().setSoftInputMode(mode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected boolean hasActionBar() {
        boolean hasActionBar = getIntent().getBooleanExtra(Constants.BUNDLE_KEY_HAS_ACTIONBAR,
                false);
        if (!hasActionBar) {
            getSupportActionBar().hide();
            return true;
        }
        return super.hasActionBar();
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.actionbar_title_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        int displayType = getIntent().getIntExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DISPLAY_NEWS);
        long clickTime = getIntent().getLongExtra(Constants.BUNDLE_KEY_CLICK_TIME, 0);
        BaseFragment fragment = null;
        int actionBarTitle = 0;
        switch (displayType) {
            case DISPLAY_COMMENT:
                actionBarTitle = R.string.actionbar_title_comment;
                fragment = new CommentFrament();
                break;
            case DISPLAY_DISCOVER_COS:
                actionBarTitle = R.string.actionbar_title_cosdetail;
                fragment = new CosplayDetailFragment(clickTime);
                break;
            case DISPLAY_POST:
                actionBarTitle = R.string.post_detail;
                fragment = new PostDetailFragment(clickTime);
                break;
            default:
                break;
        }
        setActionBarTitle(actionBarTitle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        if (trans == null || fragment == null) {
            finish();
            return;
        }
        trans.replace(R.id.container, fragment);
        trans.commitAllowingStateLoss();
        if (fragment instanceof OnSendClickListener) {
            currentFragment = (OnSendClickListener) fragment;
        } else {
            currentFragment = new OnSendClickListener() {
                @Override
                public void onClickSendButton(String str) {
                }

                @Override
                public void onClickFlagButton() {
                }
            };
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void initView() {
        if (currentFragment instanceof CosplayDetailFragment
                || currentFragment instanceof PostDetailFragment
                || currentFragment instanceof CommentFrament) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.emoji_keyboard, emojiFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.emoji_keyboard, toolFragment).commit();
        }
        toolFragment.setOnActionClickListener(new OnActionClickListener() {
            @Override
            public void onActionClick(ToolAction action) {
                switch (action) {
                    case ACTION_CHANGE:
                    case ACTION_WRITE_COMMENT:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.footer_menu_slide_in,
                                        R.anim.footer_menu_slide_out)
                                .replace(R.id.emoji_keyboard, emojiFragment)
                                .commit();
                        break;
                    case ACTION_FAVORITE:
                        ((CommonDetailFragment) currentFragment)
                                .handleFavoriteOrNot();
                        break;
                    case ACTION_REPORT:
                        ((CommonDetailFragment) currentFragment).onReportMenuClick();
                        break;
                    case ACTION_SHARE:
                        ((CommonDetailFragment) currentFragment).handleShare();
                        break;
                    case ACTION_VIEW_COMMENT:
                        ((CommonDetailFragment) currentFragment)
                                .onCilckShowComment();
                        break;
                    default:
                        break;
                }
            }
        });

        if (emojiFragment.getEmjlistener() == null) {
            emojiFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
                @Override
                public void onDeleteButtonClick(View v) {
                    MyLog.i("MoinEmoji 你点击了删除按钮");
//                    AppContext.getInstance().toast(getActivity(), "你点击了删除按钮");
                }

                @Override
                public void onEmojiClick(Emojicon v) {
                    MyLog.i("MoinEmoji 你点击了表情按钮:" + v.toString());
//                    AppContext.getInstance().toast(getActivity(), "你点击了表情按钮:" + v.toString());
//                    prepareSendMessage(3, v.getRemote());
                    // TODO 图片详情页面中发布预制表情时,用其他方法也可以.
                    if (currentFragment instanceof CosplayDetailFragment) {
                        ((CosplayDetailFragment) currentFragment).sendComment(3, v.getRemote());
                    } else if (currentFragment instanceof PostDetailFragment) {
                        ((PostDetailFragment) currentFragment).sendComment(3, v.getRemote());
                    }
//                    currentFragment.onClickSendButton(v.getRemote());
                }
            });
        }
        if (emojiFragment.getListener() == null) {
            emojiFragment.setListener(new OnSendClickListener() {
                @Override
                public void onClickSendButton(String str) {
                    MyLog.i("MoinEmoji 你点击了发送按钮:" + str.toString());
                    currentFragment.onClickSendButton(str);
                }

                @Override
                public void onClickFlagButton() {

                }
            });
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClickSendButton(String str) {
        MyLog.i("onClickSendButton [" + str.toString() + "]");
        currentFragment.onClickSendButton(str);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (emojiFragment.isShowEmojiKeyBoard()) {
                    emojiFragment.hideAllKeyBoard();
                    return true;
                }
                if (emojiFragment.getEditText().getTag() != null) {
                    emojiFragment.getEditText().setTag(null);
                    emojiFragment.getEditText().setHint(R.string.comment_hint);
                    return true;
                }
            } catch (NullPointerException e) {
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCommentCount(int count) {
        try {
            toolFragment.setCommentCount(count);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClickFlagButton() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.footer_menu_slide_in,
                        R.anim.footer_menu_slide_out)
                .replace(R.id.emoji_keyboard, toolFragment).commit();
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
    public void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
}

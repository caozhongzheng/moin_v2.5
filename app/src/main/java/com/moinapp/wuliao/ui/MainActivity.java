package com.moinapp.wuliao.ui;

/**
 * Created by moying on 16/2/24.
 * 3.2版本
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.keyboard.utils.DefEmoticons;
import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.AppStart;
import com.moinapp.wuliao.BackgroundService;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Notice;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.DataCleanManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.AudioService;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.update.model.App;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.DisplayUtil;
import com.moinapp.wuliao.util.EmoticonsUtils;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.widget.BadgeView;
import com.moinapp.wuliao.widget.MyFragmentTabHost;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;

import java.util.List;

import butterknife.InjectView;

@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends BaseActivity implements
        TabHost.OnTabChangeListener, View.OnTouchListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(MainActivity.class.getSimpleName());

    public final static int KEY_TAB_DISCOVERY = 0;
    public final static int KEY_TAB_FOLLOW = 1;
    public final static int KEY_TAB_EVENT = 3;

    static {
        EmoticonsUtils.initEmoticonsDB(AppContext.context());
    }

    private DoubleClickExitHelper mDoubleClickExit;

    @InjectView(android.R.id.tabhost)
    public MyFragmentTabHost mTabHost;

    private BadgeView mBvNotice;

    public static Notice mNotice;

    @InjectView(R.id.quick_option_iv)
    View mCosplayBtn;

    private UMImage mUMImage;
    private int mTabIndex;
    private int mCancelAppStart;
    private int unreadMsg;
    private boolean hasUnreadChat;
    //    private ProgressDialog _waitDialog;
//    private boolean _isVisible;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        EventBus.getDefault().register(this);
        Intent backService = new Intent(this, BackgroundService.class);
        backService.setAction(BackgroundService.REQUEST_PERIOD_CHECK_ACTION);
        startService(backService);
        MyLog.i("ljc: MainActivity.onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        MyLog.i("ljc: MainActivity.onNewIntent");
    }

    /**
     * 接收传入的参数, 默认进入哪个tab
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (intent == null)
            return;
        String scheme = intent.getScheme();
        MyLog.i("scheme:" + scheme + ", data=" + intent.getDataString());
        if ("moinapp".equals(scheme)) {
            // TODO
//            Uri uri = intent.getData();
        }

        // app启动默认进发现
        mTabIndex = intent.getIntExtra(Constants.BUNDLE_KEY_TABINDEX, KEY_TAB_DISCOVERY);
        mCancelAppStart = intent.getIntExtra(Constants.BUNDLE_KEY_CANCEL_APPSTART, 0);
        MyLog.i("ljc: handleIntent mCancelAppStart=" + mCancelAppStart);
        mTabHost.setCurrentTab(mTabIndex);
    }

    @Override
    public void initView() {
        mDoubleClickExit = new DoubleClickExitHelper(this);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }

        initTabs();

        // 中间按键图片触发
        mCosplayBtn.setOnClickListener(this);

        // 无论用户是否登录,当启动应用的时候都跳到发现界面
        mTabHost.setCurrentTab(mTabIndex);
        mTabHost.setOnTabChangedListener(this);

        if (AppContext.isFristStart()) {
            DataCleanManager.cleanInternalCache(AppContext.getInstance());
            AppContext.setFristStart(false);
        }
        // 检查版本更新

        checkAppUpgrade();

        // 检查预置贴纸更新
        checkStickerUpdate();

        // 获取最优upload server
        StickerManager.getInstance().updateBestServerInfo(this);

        // 删除记录加载时间的日志文件
        DiscoveryManager.getInstance().removeDiscoveryLoadInfo();

        //copy预置音频文件到sd卡
        AudioService.getInst().copyAudioToSD(AudioService.getInst().getLocalAudio());

        MyLog.i("ljc: MainActivity initview mCancelAppStart =" + mCancelAppStart);
        if (mCancelAppStart != 1) {
            Intent intent = new Intent(this, AppStart.class);
            startActivity(intent);
        }
    }

    @Override
    public void initData() {
    }

    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                    null);
            if (i == 2) {
                indicator.setVisibility(View.INVISIBLE);
                mTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);

            if (mainTab.equals(MainTab.ME) && mBvNotice == null) {
                View cn = indicator.findViewById(R.id.tab_mes);
                mBvNotice = new BadgeView(MainActivity.this, cn);
                mBvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                mBvNotice.setText("");
                mBvNotice.setBackgroundResource(R.drawable.shape_message_point);
                mBvNotice.setGravity(Gravity.CENTER);
            }
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }

    /** 检查版本更新 */
    private void checkAppUpgrade() {
        com.moinapp.wuliao.modules.update.UpdateManager.getInstance().checkUpdate(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                final App app = (App) obj;
                if (app == null) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUpgradeDialog(app);
                    }
                });
            }

            @Override
            public void onErr(Object object) {
            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    Dialog mUpgradeDialog;

    /**弹更新dialog*/
    private void showUpgradeDialog(App b) {
        final String version = b.getVersionCode();
        final String url = b.getApkFile().getUrl();

        if (mUpgradeDialog == null) {
            mUpgradeDialog = new Dialog(MainActivity.this, R.style.Translucent_NoTitle);
        }
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.version_update_layout, null);
        int titleId = R.id.nq_update_dialog_title;
        TextView title = (TextView) view.findViewById(titleId);
        title.setText(b.getTitle());

        int contentId = R.id.updatedialog_content;
        TextView content = (TextView) view.findViewById(contentId);
        content.setText(b.getDesc());

        int btnCancelId = R.id.no_update;
        TextView btnCancel = (TextView) view.findViewById(btnCancelId);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpgradeDialog.dismiss();
            }
        });

        int btnOkId = R.id.ok_update;
        TextView btnOk = (TextView) view.findViewById(btnOkId);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                //处理升级
                try {
                    mUpgradeDialog.dismiss();
                    com.moinapp.wuliao.modules.update.UpdateManager.getInstance().downloadApp(url, version);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mUpgradeDialog.setContentView(view);
        mUpgradeDialog.setCanceledOnTouchOutside(true);

        if (!mUpgradeDialog.isShowing()) {
            mUpgradeDialog.show();
        }
    }

    /**
     * 预置贴纸类型2和正常贴纸依次执行更新检查
     */
    private void checkStickerUpdate() {
        new UpdateStickerTask(DefEmoticons.DEFAULT_EMOJISET_UID,
                StickerPackage.STICKER_INTIME, null).execute();

        // 如果已经登陆,获取用户下载的贴纸进行检查更新
        if (AppContext.getInstance().isLogin()) {
            String uid = ClientInfo.getUID();
            List<String> stickers = StickerManager.getInstance().getNeedCheckUpdateStickers(uid);
            if (stickers != null && stickers.size() > 0) {
                for (String packageId : stickers) {
                    new UpdateStickerTask(uid, StickerPackage.STICKER_NORMAL, packageId).execute();
                }
            }
        }
    }

    /**
     * 检查贴纸更新的异步任务
     */
    private class UpdateStickerTask extends AsyncTask<Void, Void, Void> {
        private int type;
        private String packageId;
        private String uid;

        public UpdateStickerTask(String uid, int type, String id) {
            this.type = type;
            this.packageId = id;
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
//                MyLog.i("check sticker update: type =" + type);
                StickerManager.getInstance().checkUpdate(uid, type, packageId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        hasUnreadChat = false;
        unreadMsg = 0;
        showRedPoint();

        // 在发布界面时分享多个平台的逻辑
        String sharePlatform = MinePreference.getInstance().getSharePlatform();
        if (StringUtil.isNullOrEmpty(sharePlatform)) {
            return;
        }

        getUMImage(sharePlatform);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                share();
            }
        }, 2000);
    }

    private UMImage getUMImage(final String sharePlatform) {
        if (mUMImage == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] split = sharePlatform.split(";");
                    if (split != null && split.length > 1) {
                        mUMImage = getShareImg(split[1]);
                    }
                }
            }).start();
        }

        return mUMImage;
    }

    private void showRedPoint() {
        if (AppContext.getInstance().isLogin()) {
            hasUnreadChat = MineManager.getInstance().hasUnreadChat(null);
            unreadMsg = MineManager.getInstance().getUnreadMessages(MinePreference.getInstance().getLastReadTime());
        }
        if (unreadMsg > 0 || hasUnreadChat) {
//            mBvNotice.setText(unreadMsg + "");
            mBvNotice.show();
        } else if (mBvNotice.isShown()){
//            mBvNotice.setText("");
            mBvNotice.hide();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregist(this);
    }

    @Override
    public void onTabChanged(String tabId) {
        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
                if (i == KEY_TAB_EVENT) {
                    MobclickAgent.onEvent(getApplicationContext(), UmengConstants.EVENTS_TAB_CLICK);
                }
            } else {
                v.setSelected(false);
            }
        }
        /* 只有真正的看过未读消息才消失
        if (tabId.equals(getString(MainTab.ME.getResName()))) {
            mBvNotice.setText("");
            mBvNotice.hide();
        }
        */
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // 点击了快速操作按钮
            case R.id.quick_option_iv:
//                startActivity(new Intent(MainActivity.this, PostActivity.class));

                // 进入大咖秀正常制作流程时将之前缓存的临时贴纸包ID或者单张贴纸ID清除
                StickPreference.getInstance().setDefaultUseSticker(null);
                StickerManager.getInstance().clearUseSingleSticker();
                MobclickAgent.onEvent(getApplicationContext(), UmengConstants.COSPLAY_PHOTO);
                CameraManager.getInst().openCamera(MainActivity.this, null);
                break;
            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouchEvent(event);
        boolean consumed = false;
        // use getTabHost().getCurrentTabView to decide if the current tab is
        // touched again
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && v.equals(mTabHost.getCurrentTabView())) {
            // use getTabHost().getCurrentView() to get a handle to the view
            // which is displayed in the tab - and to get this views context
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null
                    && currentFragment instanceof OnTabReselectListener) {
                OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                listener.onTabReselect();
                consumed = true;
            }
        }
        return consumed;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(
                mTabHost.getCurrentTabTag());
    }

    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            if (AppContext.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
                return mDoubleClickExit.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        // 当 API Level > 11 调用这个方法可能导致奔溃（android.os.Build.VERSION.SDK_INT > 11）
    }


    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

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

    /**分享到三方平台*/
    private void share() {
        String sharePlatform = MinePreference.getInstance().getSharePlatform();
        if (StringUtil.isNullOrEmpty(sharePlatform)) {
            return;
        }

        String[] share = sharePlatform.split(";");
        if (share != null) {
            if (share.length <= 3) {
                MinePreference.getInstance().setSharePlatform("");
                // 此时还要把原始图片文件删除
                if (share.length > 1) {
                    FileUtil.deleteFileWithPath(share[1]);
                }
            } else {
                StringBuilder stringBuilder = new StringBuilder(share[0]);
                stringBuilder.append(";").append(share[1]);
                for (int i = 3; i < share.length; i++) {
                    stringBuilder.append(";").append(share[i]);
                }
                MinePreference.getInstance().setSharePlatform(stringBuilder.toString());
            }

            String ucid = share[0];
            String uri = share[1];
            String plat = share[2];
            if (plat.equals(SHARE_MEDIA.SINA.toString())) {
                shareToSinaWeibo(ucid, uri);
            } else if (plat.equals(SHARE_MEDIA.WEIXIN_CIRCLE.toString())) {
                shareToWeiChatFriends(ucid, uri);
            } else if (plat.equals(SHARE_MEDIA.QZONE.toString())) {
                shareToQZone(ucid, uri);
            }
        }
    }

    private UMImage getShareImg(String uri) {
        UMImage img = null;
//        MyLog.i("image uri=" + uri);

        try {
            Bitmap bitmap;
            if (uri.startsWith("http")) {
                int sw = (int) TDevice.getScreenWidth();
                bitmap = ImageLoaderUtils.getImageFromCache(ImageLoaderUtils.buildNewUrl(uri, new ImageSize(sw, sw)));
                if (bitmap == null) {
//                    MyLog.i("http1 bitmap==null");
                    String fileName = BitmapUtil.getTmpShareImagePath();
                    if(HttpUtil.download(uri, fileName)) {
//                        MyLog.i("http2  下载文件成功");
                        bitmap = BitmapFactory.decodeFile(fileName);
                        if(bitmap != null) {
//                            MyLog.i("fileName wh=" + bitmap.getWidth() + "*" + bitmap.getHeight());
                            img = getWatermarkImage(bitmap);
                        } else {
                            img = new UMImage(MainActivity.this, uri);
//                            MyLog.i("fileName NULL 1~~~~");
                        }
                    } else {
//                        MyLog.i("http2  下载文件失败");
                        img = new UMImage(MainActivity.this, uri);
//                        MyLog.i("fileName NULL 2~~~~");
                    }
                } else {
//                    MyLog.i("http bitmap !=     null");
                    img = getWatermarkImage(bitmap);
                }
            } else {
                bitmap = BitmapFactory.decodeFile(uri);
                if (bitmap != null) {
//                    MyLog.i("fileName wh=" + bitmap.getWidth() + "*" + bitmap.getHeight());
                    img = getWatermarkImage(bitmap);
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        }

        return img;
    }

    private UMImage getWatermarkImage(Bitmap bitmap) {
        UMImage img;
        int SW = DisplayUtil.getDisplayWidth(MainActivity.this);
        int SH = SW * 932 / 750;//DisplayUtil.getDisplayHeight(MainActivity.this);
//        Bitmap baseboard = BitmapFactory.decodeResource(getResources(), R.drawable.share_bg);
//        if(baseboard.getWidth() != SW) {
//            baseboard = BitmapUtil.zoomBitmap(baseboard, SW, SH);
//        }
        Bitmap watermark = null;
        if (MinePreference.getInstance().isSaveWatermark()) {
            watermark = BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
            int ww = watermark.getWidth();
            int W = DisplayUtil.dip2px(MainActivity.this, 50);
            if (ww != W) {
                float scale = (float) W / (float) ww;
                watermark = BitmapUtil.zoomBitmap(watermark, scale);
            }
        }

        if(bitmap.getWidth() != SW) {
            bitmap = BitmapUtil.zoomBitmap(bitmap, SW, SW);
        }
        String authorName = "TA";
        Bitmap authorAvatar = null;
        int aw = DisplayUtil.dip2px(MainActivity.this, 61);
        int boardWidth = DisplayUtil.dip2px(MainActivity.this, 6);

        if(AppContext.getInstance().isLogin()) {
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            authorName = ClientInfo.getUserName();
//            MyLog.i("avatar url=" + userInfo.getAvatar().getUri());
            authorAvatar = ImageLoaderUtils.getImageFromCache(userInfo.getAvatar().getUri());
            if (authorAvatar == null) {
                int wp30 = DisplayUtil.dip2px(MainActivity.this, 30);
                String leftTopUrl = ImageLoaderUtils.buildNewUrl(userInfo.getAvatar().getUri(), new ImageSize(wp30, wp30));
                authorAvatar = ImageLoaderUtils.getImageFromCache(leftTopUrl);
            }
            if (authorAvatar == null) {
                String fileName = BitmapUtil.getTmpShareAvatarImagePath();
                String newUrl = ImageLoaderUtils.buildNewUrl(userInfo.getAvatar().getUri(), new ImageSize(aw, aw));
                if(HttpUtil.download(newUrl, fileName, true)) {
                    authorAvatar = BitmapFactory.decodeFile(fileName);
//                    MyLog.i("avatar wh=" + authorAvatar.getWidth() + "*" + authorAvatar.getHeight());
                } else {
//                    MyLog.i("avatar download failed. " + newUrl);
                }
            }
            if (authorAvatar != null) {
//                MyLog.i("avatar cache ok   wh=" + authorAvatar.getWidth() + "*" + authorAvatar.getHeight());
            }
        }
        if(authorAvatar == null) {
            authorAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.share_author_default);
        }
        if(authorAvatar.getWidth() != aw) {
            authorAvatar = BitmapUtil.zoomBitmap(authorAvatar, aw, aw);
        }
        if(StringUtil.isNullOrEmpty(authorName)) {
            authorName = "TA";
        }
        Bitmap mNewBitmap = BitmapUtil.setWaterMark(null, bitmap, watermark, authorAvatar, authorName, SW, SH, aw, boardWidth);
        img = new UMImage(MainActivity.this, mNewBitmap);
        return img;
    }

    private String getShareLink(String ucid) {
        // http://domain/view/cosplay/ucid
        if (AppContext.getInstance().isLogin()) {
            return AppConfig.getBaseShareUrl() + "view/cosplay/" + ucid + "/" + ClientInfo.getUID();
        }
        return AppConfig.getBaseShareUrl() + "view/cosplay/" + ucid;
    }

    private void shareToSinaWeibo(String ucid, String uri) {
        SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
        //如果没有标题文字,微博分享填上默认广告语
        String content = getString(R.string.moin_default_share_string);
        String link = getShareLink(ucid);
        if (!TextUtils.isEmpty(link)) {
            sinaSsoHandler.setTargetUrl(link);
            mController.setShareContent(content + " " + link);
        } else {
            mController.setShareContent(content);
        }
        mController.setShareType(ShareType.SHAKE);

        AppContext.toast(MainActivity.this, "分享到微博");
        if (mUMImage == null) {
            mUMImage = getUMImage(uri);
        }
        mController.setShareImage(mUMImage);

        // 设置新浪微博SSO handler
        mController.getConfig().setSsoHandler(sinaSsoHandler);
        sinaSsoHandler.addToSocialSDK();
        mController.postShare(MainActivity.this, SHARE_MEDIA.SINA, null);
        clearShareCache();
    }

    private void shareToWeiChatFriends(String ucid, String uri) {
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(MainActivity.this,
                Constants.WEICHAT_APPID);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();


        String content = getString(R.string.moin_default_share_string);
        // 设置朋友圈title
//        circleMedia.setTitle(content);
        // 设置分享文字
//        circleMedia.setShareContent(content);

        AppContext.toast(MainActivity.this, "分享到朋友圈");
        // 设置分享图片
        if (mUMImage == null) {
            mUMImage = getUMImage(uri);
        }
        circleMedia.setShareImage(mUMImage);
        // 设置分享内容跳转URL
//        circleMedia.setTargetUrl(getShareLink(ucid));

        mController.setShareMedia(circleMedia);
        mController.postShare(MainActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, null);
        clearShareCache();
    }

    private void shareToQZone(String ucid, String uri) {
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(MainActivity.this,
                Constants.QQ_APPID, Constants.QQ_APPKEY);

        qZoneSsoHandler.addToSocialSDK();
        QZoneShareContent qZoneShareContent = new QZoneShareContent();
        if (!TextUtils.isEmpty(getShareLink(ucid))) {
            qZoneShareContent.setTargetUrl(getShareLink(ucid));
        }
        String title = getString(R.string.qzone_default_share_title);
        if (!TextUtils.isEmpty(title)) {
            qZoneShareContent.setTitle(title);
        }
        String content = getString(R.string.qzone_default_share_content);
        if (!TextUtils.isEmpty(content)) {
            mController.setShareContent(content);
        }
        AppContext.toast(MainActivity.this, "分享到QQ空间");
        if (!StringUtil.isNullOrEmpty(uri)) {
            qZoneShareContent.setShareImage(new UMImage(MainActivity.this, uri));
        }
        qZoneShareContent.setTitle(title);
        qZoneShareContent.setShareContent(content);
        mController.setShareMedia(qZoneShareContent);
        mController.postShare(MainActivity.this, SHARE_MEDIA.QZONE, null);
        clearShareCache();
    }

    private void clearShareCache() {
        mUMImage = null;
    }

//    public void onEvent(PhotoReleaseActivity.ReleaseOkEvent event) {
//        clearShareCache();
//    }

    public void hideFloatingBtn() {
    }

    public void showFloatingBtn() {
    }

    public void onEvent(MineManager.NewChatMessageEvent message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRedPoint();
            }
        });
    }

    public void onEvent(MineManager.ReceivedMessage message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRedPoint();
            }
        });
    }
}

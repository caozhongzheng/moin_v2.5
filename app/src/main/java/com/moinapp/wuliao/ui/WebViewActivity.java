package com.moinapp.wuliao.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Web2NativeParams;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.info.MobileInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.UIHelper;

import java.io.InputStream;

/**
 * webview类只负责处理load网页内容和显示右上角处理的三个点
 * Created by guyunfei on 16/3/16.14:34.
 */
public abstract class WebViewActivity extends Activity {
    private ILogger MyLog = LoggerFactory.getLogger(WebViewActivity.class.getSimpleName());
    public static final String URL = "url";
    private String MOIN_AGENT = " moinapp/";
    private WebView webView;
    protected CommonTitleBar titleBar;
    private String url;
    private String mTtile;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.i("BroadcastReceiver received: refresh");
            refresh();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
        initData();
        loadContent();

        IntentFilter filter = new IntentFilter(Constants.INTENT_GET_MOIN_BEAN);
        filter.addAction(Constants.INTENT_GET_MOIN_BEAN);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            this.unregisterReceiver(mReceiver);
        }
    }

    protected void initView() {
        webView = (WebView) findViewById(R.id.web_view);
        titleBar = (CommonTitleBar) findViewById(R.id.title_layout);

        titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShare();
            }
        });
    }

    protected void initData() {

    }

    public void loadContent() {
        url = getUrl();
        setCookies(url);
        MyLog.i("url=" + url);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新的窗口
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);//设置内容适配手机
        settings.setSupportZoom(true); // 支持缩放
        settings.setUseWideViewPort(true);//关键点
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        settings.setLoadWithOverviewMode(true);
        if (isNoCache()) {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setDomStorageEnabled(true);// 开启 DOM storage API 功能
        }

        //设置useragent告诉服务器来自app
        String userAgent = settings.getUserAgentString();
        MyLog.i("get user agent =" + userAgent);
        if (!StringUtils.isEmpty(userAgent) && !userAgent.contains(MOIN_AGENT)) {
            userAgent = userAgent + MOIN_AGENT + AppTools.getVersionName(this);
        }
        MyLog.i("set user agent =" + userAgent);
        settings.setUserAgentString(userAgent);

        webView.loadUrl(url);
        webView.setWebViewClient(getLoadUrlFinishCallBack());
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                MyLog.i("parse jump url = " + url);
                return parseUrl(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                // 只是将不需要更新的资源从本地资源读取
                try {
                    if (url.endsWith("icon.png")) {

                        Drawable draw = getResources().getDrawable(R.drawable.ic_launcher);
                        InputStream is = BitmapUtil.Drawable2InputStream(draw);

                        WebResourceResponse response = new WebResourceResponse("image/png", "utf-8", is);
                        return response;
                    } else if (url.endsWith("jquery.min.js")) {
                        InputStream is = getResources().openRawResource(R.raw.aimei);

                        WebResourceResponse response = new WebResourceResponse("text/javascript", "utf-8", is);
                        return response;
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    MyLog.e(e);
                }

                return super.shouldInterceptRequest(view, url);
            }
        });
        //获取网页的title
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titleBar.setTitleTxt(title);
                mTtile = title;
            }

        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(wvcc);
    }

    public void refresh() {
        setCookies(url);
        MyLog.i("url=" + url);
        webView.reload();
    }

    public String getWebTitle() {
        return mTtile;
    }
    /**
     * 是否每次进入都刷新页面,不从缓存读取
     * @return
     */
    protected boolean isNoCache() {
        return false;
    }

    /**
     * 页面加载完成的回调
     * @return
     */
    protected WebViewClient getLoadUrlFinishCallBack() {
        return null;
    }

    /**
     * 解析webview里面的链接跳转, 在这里统一处理
     */
    protected boolean parseUrl(WebView view, String url) {
        Web2NativeParams params = StringUtil.parseUrl(url);
        if (params != null) {
            if (params.getResource().equals("tag")) {
                if (params.getAction().equals("view")) {
                    // 查看话题详情
                    if (params.getParams() != null) {
                        String tpid = (String)params.getParams().get("tpid");
                        String type = (String)params.getParams().get("taskType");
                        int mission = parseMission(type);
                        if (!StringUtil.isNullOrEmpty(tpid)) {
                            MyLog.i("parsed tpid = " + tpid + ", mission=" + mission);
                            UIHelper.showTopicDetail(this, tpid, mission);
                        }
                    }
                }
                return true;
            } else if (params.getResource().equals("share")) {
                if (params.getAction().equals("view")) {
                    MyLog.i("跳转到分享页面");
                    UIHelper.showShareMission(this);
                }
                return true;
            } else if (params.getResource().equals("cosplay")) {
                if (params.getAction().equals("show")) {
                    MyLog.i("跳转到照片拍摄页面");
                    Bundle bundle = new Bundle();
                    bundle.putString(DiscoveryConstants.FROM, StringUtil.FROM_MISSION);
                    CameraManager.getInst().openCamera(this, bundle);
                }
                return true;
            } else if (params.getResource().equals("user")) {
                if (params.getAction().equals("login")) {
                    MyLog.i("跳转登录页面");
                    //如果收到登陆跳转请求,但是app已经登陆的话不跳转
                    if (!AppContext.getInstance().isLogin()) {
                        UIHelper.showLoginActivity(this);
                    }
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    };

    abstract protected String getUrl();

    abstract protected void handleShare();

    public void setTitle(String title) {
        titleBar.setTitleTxt(title);
    }

    public void hideRightButton() {
        titleBar.hideRightBtn();
    }

    public void hideLefttButton() {
        titleBar.hideLeftBtn();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private int parseMission(String taskName) {
        int mission = 0;
        switch (taskName) {
            case MissionConstants.MISSION_SHARE_NAME:
                mission = MissionConstants.MISSION_SHARE;
                break;
            case MissionConstants.MISSION_COSPLAY_NAME:
                mission = MissionConstants.MISSION_MAKE_COSPLAY;
                break;
            case MissionConstants.MISSION_LIKE_NAME:
                mission = MissionConstants.MISSION_LIKE;
                break;
            case MissionConstants.MISSION_COMMENT_NAME:
                mission = MissionConstants.MISSION_COMMENT;
                break;
        }

        return mission;
    }

    private void setCookies(String url) {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.setCookie(url, "uid=" + ClientInfo.getUID());
        cookieManager.setCookie(url, "passport=" + ClientInfo.getPassport());
        cookieManager.setCookie(url, "nonce=" + MobileInfo.getImei(this));
        MyLog.i("setCookies: uid=" + ClientInfo.getUID() + ", passport=" + ClientInfo.getPassport()
                + ", imei="+ MobileInfo.getImei(this));
        CookieSyncManager.getInstance().sync();
    }
}
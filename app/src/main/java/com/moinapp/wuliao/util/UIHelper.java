package com.moinapp.wuliao.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.dtr.zxing.activity.CaptureActivity;
import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.Active;
import com.moinapp.wuliao.bean.Comment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.News;
import com.moinapp.wuliao.bean.Notice;
import com.moinapp.wuliao.bean.ShakeObject;
import com.moinapp.wuliao.bean.SimpleBackPage;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.fragment.BrowserFragment;
import com.moinapp.wuliao.interf.ICallbackResult;
import com.moinapp.wuliao.interf.OnWebViewImageListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.adapter.FollowAdapter;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.NodeInfo;
import com.moinapp.wuliao.modules.discovery.ui.CosplayEvolutionActivity;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.modules.events.ui.EventsDetailActivity;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.model.ThirdInfo;
import com.moinapp.wuliao.modules.mission.GetMoinBeanActivity;
import com.moinapp.wuliao.modules.mission.MissionActivity;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.ui.SearchStickerResultFragment;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoGlorifyActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoProcessActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoReleaseActivity;
import com.moinapp.wuliao.service.DownloadService;
import com.moinapp.wuliao.service.DownloadService.DownloadBinder;
import com.moinapp.wuliao.ui.DetailActivity;
import com.moinapp.wuliao.ui.ImagePreviewActivity;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.SimpleBackActivity;
import com.moinapp.wuliao.widget.AvatarView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 界面帮助类
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月10日 下午3:33:36
 */
public class UIHelper {

    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page.js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common.css\">";
    public final static String WEB_STYLE = linkCss;

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";

    /**
     * 回到首页界面
     *
     * @param activity
     */
    public static void gotoMain(Activity activity, int tab, boolean finishSelf) {

        Intent intent = new Intent(BaseApplication.context(), MainActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_TABINDEX, tab);
        intent.putExtra(Constants.BUNDLE_KEY_CANCEL_APPSTART, 1);
        activity.startActivity(intent);

        if (finishSelf) {
            activity.finish();
        }
    }

    /**
     * 显示登录界面
     *
     * @param context
     */
    public static void showLoginActivity(Context context) {
        showLoginActivity(context, 0);
    }

    /**
     * 显示登录界面
     * gotoFollow: 1表示要跳到关注页面
     *
     * @param context
     */
    public static void showLoginActivity(Context context, int gotoFollow) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_GOTO_FOLLOW, gotoFollow);
        context.startActivity(intent);
    }

    /**
     * 显示注册界面
     * @param context
     */
    public static void showRegistryActivity(Context context, int gotoFollow) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, gotoFollow);
        showSimpleBack(context, SimpleBackPage.REGISTRY, args);
    }
    
    /**
     * 显示新闻详情
     *
     * @param context
     * @param newsId
     */
    public static void showNewsDetail(Context context, int newsId,
                                      int commentCount) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, newsId);
        intent.putExtra("comment_count", commentCount);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_NEWS);
        context.startActivity(intent);
    }

    /**
     * 显示博客详情
     *
     * @param context
     * @param blogId
     */
    public static void showBlogDetail(Context context, int blogId, int count) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, blogId);
        intent.putExtra("comment_count", count);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_BLOG);
        context.startActivity(intent);
    }

    /**
     * 显示帖子详情(发帖完成时直接跳转用)
     *
     * @param context
     * @param cosplayInfo
     */
    public static void showPostDetail(Context context, CosplayInfo cosplayInfo) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DiscoveryConstants.COSPLAY_INFO, cosplayInfo);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_POST);
        context.startActivity(intent);
    }

    /**
     * 显示帖子详情 [oschina]
     *
     * @param context
     * @param postId
     */
    public static void showPostDetail(Context context, int postId, int count) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, postId);
        intent.putExtra("comment_count", count);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_POST);
        context.startActivity(intent);
    }

    /**
     * 显示活动详情
     *
     * @param context
     * @param eventId
     */
    public static void showEventDetail(Context context, int eventId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, eventId);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_EVENT);
        context.startActivity(intent);
    }


    /**
     * 显示发现的图片详情
     *
     * @param context     context
     * @param cosplayInfo 发现的大咖秀
     * @param ucid        发现的大咖秀的id
     */
    public static void showDiscoveryCosplayDetail(Context context, CosplayInfo cosplayInfo, String ucid, long clickTime) {
        showDiscoveryCosplayDetail(context, cosplayInfo, ucid, null, clickTime);
    }

    /**
     * 显示发现的图片详情
     *
     * @param context     context
     * @param cosplayInfo 发现的大咖秀
     * @param ucid        发现的大咖秀的id
     */
    public static void showDiscoveryCosplayDetail(Context context, CosplayInfo cosplayInfo, String ucid, String from, long clickTime) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DiscoveryConstants.FROM, StringUtil.nullToEmpty(from));
        bundle.putString(DiscoveryConstants.UCID, ucid);
        bundle.putBoolean(Constants.BUNDLE_KEY_HAS_ACTIONBAR, false);
        bundle.putInt(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_DISCOVER_COS);
        bundle.putLong(Constants.BUNDLE_KEY_CLICK_TIME, clickTime);
        if (cosplayInfo != null) {
            bundle.putSerializable(DiscoveryConstants.COSPLAY_INFO, cosplayInfo);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 显示帖子详情[moin]
     *
     * @param context     context
     * @param ucid        帖子id
     */
    public static void showPostDetail(Context context, String ucid, long clickTime) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DiscoveryConstants.UCID, ucid);
        bundle.putBoolean(Constants.BUNDLE_KEY_HAS_ACTIONBAR, false);
        bundle.putInt(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_POST);
        bundle.putLong(Constants.BUNDLE_KEY_CLICK_TIME, clickTime);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 显示空间图片的单个图片,进行图片裁剪
     *
     * @param context     context
     * @param cosplayInfo 发现的大咖秀
     * @param ucid        发现的大咖秀的id
     */
    public static void showCropCosplay(Context context, CosplayInfo cosplayInfo, String ucid) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DiscoveryConstants.UCID, ucid);
        bundle.putBoolean(Constants.BUNDLE_KEY_HAS_ACTIONBAR, false);
        bundle.putInt(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_DISCOVER_COS);
        if (cosplayInfo != null) {
            bundle.putSerializable("cosplay", cosplayInfo);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 显示软件详情
     *
     * @param context
     * @param ident
     */
    public static void showSoftwareDetail(Context context, String ident) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("ident", ident);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_SOFTWARE);
        context.startActivity(intent);
    }

    public static void showSoftwareDetailById(Context context, int id) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, id);
        intent.putExtra("ident", "");
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_SOFTWARE);
        context.startActivity(intent);
    }

    /**
     * 新闻超链接点击跳转
     *
     * @param context context
     */
    public static void showNewsRedirect(Context context, News news) {
        String url = news.getUrl();
        // 如果是活动则直接跳转活动详情页面
        String eventUrl = news.getNewType().getEventUrl();
        if (!StringUtils.isEmpty(eventUrl)) {
            showEventDetail(context,
                    StringUtils.toInt(news.getNewType().getAttachment()));
            return;
        }
        // url为空-旧方法
        if (StringUtils.isEmpty(url)) {
            int newsId = news.getId();
            int newsType = news.getNewType().getType();
            String objId = news.getNewType().getAttachment();
            switch (newsType) {
                case News.NEWSTYPE_NEWS:
                    showNewsDetail(context, newsId, news.getCommentCount());
                    break;
                case News.NEWSTYPE_SOFTWARE:
                    showSoftwareDetail(context, objId);
                    break;
                case News.NEWSTYPE_POST:
                    showPostDetail(context, StringUtils.toInt(objId),
                            news.getCommentCount());
                    break;
                case News.NEWSTYPE_BLOG:
                    showBlogDetail(context, StringUtils.toInt(objId),
                            news.getCommentCount());
                    break;
                default:
                    break;
            }
        } else {
            showUrlRedirect(context, url);
        }
    }

    /**
     * 动态点击跳转到相关新闻、帖子等
     *
     * @param context context
     * @param active  动态实体类
     *                0其他 1新闻 2帖子 3动弹 4博客
     */
    public static void showActiveRedirect(Context context, Active active) {
        String url = active.getUrl();
        // url为空-旧方法
        if (StringUtils.isEmpty(url)) {
            int id = active.getObjectId();
            int catalog = active.getCatalog();
            switch (catalog) {
                case Active.CATALOG_OTHER:
                    // 其他-无跳转
                    break;
                case Active.CATALOG_NEWS:
                    showNewsDetail(context, id, active.getCommentCount());
                    break;
                case Active.CATALOG_POST:
                    showPostDetail(context, id, active.getCommentCount());
                    break;
                case Active.CATALOG_BLOG:
                    showBlogDetail(context, id, active.getCommentCount());
                    break;
                default:
                    break;
            }
        } else {
            showUrlRedirect(context, url);
        }
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public static void initWebView(WebView webView) {
        if (webView == null) {
            return;
        }
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(15);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        webView.setWebViewClient(UIHelper.getWebViewClient());
    }

    /**
     * 添加网页的点击图片展示支持
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @JavascriptInterface
    public static void addWebImageShow(final Context cxt, WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new OnWebViewImageListener() {
            @Override
            @JavascriptInterface
            public void showImagePreview(String bigImageUrl) {
                if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {
                    UIHelper.showImagePreview(cxt, new String[]{bigImageUrl});
                }
            }
        }, "mWebViewImageListener");
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                showUrlRedirect(view.getContext(), url);
                return true;
            }
        };
    }

    public static String setHtmlCotentSupportImagePreview(String body) {
        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {
            // 过滤掉 img标签的width,height属性
            body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
            // 添加点击图片放大支持
            // 添加点击图片放大支持
            body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                    "$1$2\" onClick=\"showImagePreview('$2')\"");
        } else {
            // 过滤掉 img标签
            body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }
        return body;
    }

    /**
     * 摇一摇点击跳转
     *
     * @param obj
     */
    public static void showUrlShake(Context context, ShakeObject obj) {
        if (StringUtils.isEmpty(obj.getUrl())) {
            if (ShakeObject.RANDOMTYPE_NEWS.equals(obj.getRandomtype())) {
                UIHelper.showNewsDetail(context,
                        StringUtils.toInt(obj.getId()),
                        StringUtils.toInt(obj.getCommentCount()));
            }
        } else {
            if (!StringUtils.isEmpty(obj.getUrl())) {
                UIHelper.showUrlRedirect(context, obj.getUrl());
            }
        }
    }

    /**
     * url跳转
     *
     * @param context
     * @param url
     */
    public static void showUrlRedirect(Context context, String url) {
        if (url == null)
            return;
        if (url.contains("city.oschina.net/")) {
            int id = StringUtils.toInt(url.substring(url.lastIndexOf('/') + 1));
            UIHelper.showEventDetail(context, id);
            return;
        }

        if (url.startsWith(SHOWIMAGE)) {
            String realUrl = url.substring(SHOWIMAGE.length());
            try {
                JSONObject json = new JSONObject(realUrl);
                int idx = json.optInt("index");
                String[] urls = json.getString("urls").split(",");
                showImagePreview(context, idx, urls);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        URLsUtils urls = URLsUtils.parseURL(url);
        if (urls != null) {
            showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
                    urls.getObjKey());
        } else {
            openBrowser(context, url);
        }
    }

    public static void showLinkRedirect(Context context, int objType,
                                        int objId, String objKey) {
        switch (objType) {
            case URLsUtils.URL_OBJ_TYPE_NEWS:
                showNewsDetail(context, objId, -1);
                break;
            case URLsUtils.URL_OBJ_TYPE_QUESTION:
                showPostDetail(context, objId, 0);
                break;
            case URLsUtils.URL_OBJ_TYPE_SOFTWARE:
                showSoftwareDetail(context, objKey);
                break;
            case URLsUtils.URL_OBJ_TYPE_ZONE:
                showUserCenter(context, objId, objKey);
                break;
            case URLsUtils.URL_OBJ_TYPE_BLOG:
                showBlogDetail(context, objId, 0);
                break;
            case URLsUtils.URL_OBJ_TYPE_OTHER:
                openBrowser(context, objKey);
                break;
            case URLsUtils.URL_OBJ_TYPE_TEAM:
                openSysBrowser(context, objKey);
                break;
            case URLsUtils.URL_OBJ_TYPE_GIT:
                openSysBrowser(context, objKey);
                break;
        }
    }

    /**
     * 打开内置浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {

        if (StringUtils.isImgUrl(url)) {
            ImagePreviewActivity.showImagePrivew(context, 0,
                    new String[]{url});
            return;
        }
/**
 if (url.startsWith("http://www.oschina.net/tweet-topic/")) {
 Bundle bundle = new Bundle();
 int i = url.lastIndexOf("/");
 if (i != -1) {
 bundle.putString("topic",
 URLDecoder.decode(url.substring(i + 1)));
 }
 UIHelper.showSimpleBack(context, SimpleBackPage.TWEET_TOPIC_LIST,
 bundle);
 return;
 }**/
        try {
            // 启用外部浏览器
            // Uri uri = Uri.parse(url);
            // Intent it = new Intent(Intent.ACTION_VIEW, uri);
            // context.startActivity(it);
            Bundle bundle = new Bundle();
            bundle.putString(BrowserFragment.BROWSER_KEY, url);
            showSimpleBack(context, SimpleBackPage.BROWSER, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            AppContext.showToastShort("无法浏览此网页");
        }
    }

    /**
     * 打开系统中的浏览器
     *
     * @param context
     * @param url
     */
    public static void openSysBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            AppContext.showToastShort("无法浏览此网页");
        }
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, String[] imageUrls) {
        ImagePreviewActivity.showImagePrivew(context, 0, imageUrls);
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, int index,
                                        String[] imageUrls) {
        ImagePreviewActivity.showImagePrivew(context, index, imageUrls);
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, String[] imageUrls, boolean isFullScreen) {
        ImagePreviewActivity.showImagePrivew(context, 0, imageUrls, isFullScreen);
    }

    public static void showSimpleBackForResult(Fragment fragment,
                                               int requestCode, SimpleBackPage page, Bundle args) {
        Intent intent = new Intent(fragment.getActivity(),
                SimpleBackActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_PAGE, page.getValue());
        intent.putExtra(Constants.BUNDLE_KEY_ARGS, args);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, SimpleBackPage page, Bundle args) {
        showSimpleBackForResult(context, requestCode, page, args, 0);
    }

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, SimpleBackPage page, int popup) {
        showSimpleBackForResult(context, requestCode, page, null, popup);
    }

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, SimpleBackPage page, Bundle args, int popup) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_PAGE, page.getValue());
        intent.putExtra(Constants.BUNDLE_KEY_ARGS, args);
        intent.putExtra(Constants.BUNDLE_KEY_POPUP, popup);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page,
                                      Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ARGS, args);
        intent.putExtra(Constants.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    public static void showComment(Context context, int id, int catalog) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, id);
        intent.putExtra(Constants.BUNDLE_KEY_CATALOG, catalog);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_COMMENT);
        context.startActivity(intent);
    }

    public static void showBlogComment(Context context, int id, int ownerId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_ID, id);
        intent.putExtra(Constants.BUNDLE_KEY_OWNER_ID, ownerId);
        intent.putExtra(Constants.BUNDLE_KEY_BLOG, true);
        intent.putExtra(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_COMMENT);
        context.startActivity(intent);
    }

    public static SpannableString parseActiveAction(int objecttype,
                                                    int objectcatalog, String objecttitle) {
        String title = "";
        int start = 0;
        int end = 0;
        if (objecttype == 32 && objectcatalog == 0) {
            title = "加入了开源中国";
        } else if (objecttype == 1 && objectcatalog == 0) {
            title = "添加了开源项目 " + objecttitle;
        } else if (objecttype == 2 && objectcatalog == 1) {
            title = "在讨论区提问：" + objecttitle;
        } else if (objecttype == 2 && objectcatalog == 2) {
            title = "发表了新话题：" + objecttitle;
        } else if (objecttype == 3 && objectcatalog == 0) {
            title = "发表了博客 " + objecttitle;
        } else if (objecttype == 4 && objectcatalog == 0) {
            title = "发表一篇新闻 " + objecttitle;
        } else if (objecttype == 5 && objectcatalog == 0) {
            title = "分享了一段代码 " + objecttitle;
        } else if (objecttype == 6 && objectcatalog == 0) {
            title = "发布了一个职位：" + objecttitle;
        } else if (objecttype == 16 && objectcatalog == 0) {
            title = "在新闻 " + objecttitle + " 发表评论";
        } else if (objecttype == 17 && objectcatalog == 1) {
            title = "回答了问题：" + objecttitle;
        } else if (objecttype == 17 && objectcatalog == 2) {
            title = "回复了话题：" + objecttitle;
        } else if (objecttype == 17 && objectcatalog == 3) {
            title = "在 " + objecttitle + " 对回帖发表评论";
        } else if (objecttype == 18 && objectcatalog == 0) {
            title = "在博客 " + objecttitle + " 发表评论";
        } else if (objecttype == 19 && objectcatalog == 0) {
            title = "在代码 " + objecttitle + " 发表评论";
        } else if (objecttype == 20 && objectcatalog == 0) {
            title = "在职位 " + objecttitle + " 发表评论";
        } else if (objecttype == 101 && objectcatalog == 0) {
            title = "回复了动态：" + objecttitle;
        } else if (objecttype == 100) {
            title = "更新了动态";
        }
        SpannableString sp = new SpannableString(title);
        // 设置标题字体大小、高亮
        if (!StringUtils.isEmpty(objecttitle)) {
            start = title.indexOf(objecttitle);
            if (objecttitle.length() > 0 && start > 0) {
                end = start + objecttitle.length();
                sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#0e5986")),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return sp;
    }

    /**
     * 组合动态的回复文本
     *
     * @param name
     * @param body
     * @return
     */
    public static SpannableStringBuilder parseActiveReply(String name,
                                                          String body) {
        Spanned span = Html.fromHtml(body.trim());
        SpannableStringBuilder sp = new SpannableStringBuilder(name + "：");
        sp.append(span);
        // 设置用户名字体加粗、高亮
        // sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
        // name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sp;
    }

    /**
     * 发送App异常崩溃报告
     *
     * @param context
     */
    public static void sendAppCrashReport(final Context context) {

        // 有时候这个context从fragment重拿的,那么应该fragment.getActivity否则,这个地方会报错BadTokenException
        try {
            DialogHelp.getConfirmDialog(context, "程序发生异常", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // 退出
                    System.exit(-1);
                }
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送通知广播
     *
     * @param context
     * @param notice
     */
    public static void sendBroadCast(Context context, Notice notice) {
        if (!((AppContext) context.getApplicationContext()).isLogin()
                || notice == null)
            return;
        Intent intent = new Intent(Constants.INTENT_ACTION_NOTICE);
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice_bean", notice);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    /**
     * 显示用户中心页面
     *
     * @param context
     * @param hisuid
     * @param hisuid
     * @param hisname
     */
    public static void showUserCenter(Context context, int hisuid,
                                      String hisname) {
        if (hisuid == 0 && hisname.equalsIgnoreCase("匿名")) {
            AppContext.showToast("提醒你，该用户为非会员");
            return;
        }
        Bundle args = new Bundle();
        args.putInt("his_id", hisuid);
        args.putString("his_name", hisname);
        //showSimpleBack(context, SimpleBackPage.USER_CENTER, args);
    }

    /**
     * 显示用户中心页面
     *
     * @param context
     * @param userid
     */
    public static void showUserCenter(Context context, String userid) {
        if (StringUtil.isNullOrEmpty(userid)) {
            AppContext.showToast("未知用户");
            return;
        }
        Bundle args = new Bundle();
        args.putString(DiscoveryConstants.USERID, userid);
        showSimpleBack(context, SimpleBackPage.USER_ACTIVITY, args);
//        if(userid.equals(ClientInfo.getUID())) {
//            args.putInt(MineViewPagerFragment.BUNDLE_KEY_TABINDEX, 1);
//            showSimpleBack(context, SimpleBackPage.MSG_MINE, args);
//        } else {
//            showSimpleBack(context, SimpleBackPage.UESRINFO_DETAIL, args);
//        }
    }


    /**
     * 显示用户头像大图
     *
     * @param context
     * @param avatarUrl
     */
    public static void showUserAvatar(Context context, String avatarUrl) {
        if (StringUtils.isEmpty(avatarUrl)) {
            return;
        }
        String url = AvatarView.getLargeAvatar(avatarUrl);
        ImagePreviewActivity.showImagePrivew(context, 0, new String[]{url});
    }

    /**
     * 显示扫一扫界面
     *
     * @param context
     */
    public static void showScanActivity(Context context) {
        Intent intent = new Intent(context, CaptureActivity.class);
        context.startActivity(intent);
    }

    /**
     * 显示用户的关注/粉丝列表
     *
     * @param context
     * @param uid     所点击用户的UID
     * @param tabIdx  默认进入哪个TAB（0：关注列表  1：粉丝列表）
     */
    public static void showFriends(Context context, String uid, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        showSimpleBack(context, SimpleBackPage.MY_FRIENDS, args);
    }

    /**
     * 显示贴纸商城/我的贴纸列表
     *
     * @param context
     * @param uid     所点击用户的UID
     * @param from    从哪里跳转而来,大咖秀编辑为1
     * @param tabIdx  默认进入哪个TAB（0：贴纸商城  1：我的贴纸列表）
     */
    public static void showStickerCenter(Context context, String uid, int from, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
//        args.putInt(StickerCenterPageFragment.BUNDLE_KEY_FROM, from);
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        showSimpleBack(context, SimpleBackPage.STICKER_LIST, args);
    }

    /**
     * 贴纸商城里面的搜索贴纸页面
     *
     * @param context
     */
    public static void searchSticker(Context context) {
        Bundle args = new Bundle();
        showSimpleBack(context, SimpleBackPage.SEARCH_STICKER, args);
    }

    /**
     * 显示搜索贴纸的结果列表
     *
     * @param context
     * @param name    搜索的关键词名
     * @param type    类型
     */
    public static void showSearchStickerResult(Context context, String name, String type) {
        Bundle args = new Bundle();
        args.putString(SearchStickerResultFragment.KEY_WORD, name);
        args.putString(SearchStickerResultFragment.KEY_TYPE, type);
        showSimpleBack(context, SimpleBackPage.SEARCH_STICKER_RESULT, args);
    }

    /**
     * 显示赞/评论列表
     *
     * @param context
     * @param ucid    所点击图片的ucid
     * @param tabIdx  默认进入哪个TAB（0：赞列表  1：评论列表）
     */
    public static void showCosplayLikeComment(Context context, String ucid, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
        args.putString(Constants.BUNDLE_KEY_ID, ucid);
        showSimpleBack(context, SimpleBackPage.COSPLAY_LIKE_COMMENT_LIST, args);
    }

    /**
     * 显示赞列表
     *
     * @param context
     * @param ucid    所点击图片的ucid
     */
    public static void showCosplayLikeList(Context context, String ucid) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TYPE, 1);
        args.putString(Constants.BUNDLE_KEY_ID, ucid);
        showSimpleBack(context, SimpleBackPage.COSPLAY_LIKE_COMMENT_LIST, args);
    }

    /**
     * 显示标签详情
     *
     * @param context
     * @param topicName: 标签名称
     * @param type:      标签类型: ip op tp等
     * @param tabIdx     默认进入哪个列表
     */
    public static void showTagDetail(Context context, String topicName, String type, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
        args.putString(Constants.BUNDLE_KEY_TAG, topicName);
        args.putString(Constants.BUNDLE_KEY_TYPE, type);
        showSimpleBack(context, SimpleBackPage.COSPLAY_TAG_DETAIL, args);
    }

    /**
     * 显示话题详情
     *
     * @param context
     * @param topicName:
     * @param tabIdx     默认进入哪个列表（0：最热  1：全部）
     */
    public static void showTopicDetail(Context context, String topicName, String type, String topicID, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
        args.putString(Constants.BUNDLE_KEY_TAG, topicName);
        args.putString(Constants.BUNDLE_KEY_TYPE, type);
        args.putString(Constants.BUNDLE_KEY_ID, topicID);
        showSimpleBack(context, SimpleBackPage.TOPIC_DETAIL, args);
    }

    /**
     * 从任务进入话题详情
     * @param topicID    话题id
     * @param tabIdx     从哪个任务跳转过来, 1分享 2发图 3点赞 4评论
     */
    public static void showTopicDetail(Context context,String topicID, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
        args.putInt(Constants.BUNDLE_KEY_FROM_MISSION, tabIdx);
        args.putString(Constants.BUNDLE_KEY_ID, topicID);
        showSimpleBack(context, SimpleBackPage.TOPIC_DETAIL, args);
    }

    /**
     * 显示我的标签列表
     *
     * @param context
     * @param uid      用户id
     * @param userName 用户名
     * @param tabIdx   默认进入哪个TAB（0：图片  1：表情）
     */
    public static void showMyTagDetail(Context context, String uid, String userName, int tabIdx) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        args.putString(Constants.BUNDLE_KEY_USERNAME, userName);
        showSimpleBack(context, SimpleBackPage.MSG_MY_TAGS, args);
    }

    /**
     * 显示我的@关注的用户的搜索列表
     *
     * @param context
     */
    public static void showAtFollowers(Activity context, int requestCode, int popup) {
        showSimpleBackForResult(context, requestCode, SimpleBackPage.AT_FOLLOWERS, popup);
    }

    /**
     * 显示图片改图权限
     *
     * @param context
     */
    public static void showWriteAuth(Activity context, int requestCode, Bundle bundle, int popup) {
        showSimpleBackForResult(context, requestCode, SimpleBackPage.WRITE_AUTH, bundle, popup);
    }

    /**
     * 显示我的关注用户列表
     *
     * @param context
     * @param uid      用户id
     * @param userName 用户名
     */
    public static void showMyFollowers(Context context, String uid, String userName, int tabIdx) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        args.putString(Constants.BUNDLE_KEY_USERNAME, userName);
        showSimpleBack(context, SimpleBackPage.MSG_MY_FOLLOWERS, args);
    }

    /**
     * 显示我的粉丝用户列表
     *
     * @param context
     * @param uid      用户id
     * @param userName 用户名
     */
    public static void showMyFans(Context context, String uid, String userName, int tabIdx) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        args.putString(Constants.BUNDLE_KEY_USERNAME, userName);
        showSimpleBack(context, SimpleBackPage.MSG_MY_FANS, args);
    }

    /**
     * 显示我的评论列表
     *
     * @param context
     * @param uid     用户id
     */
    public static void showMyComments(Context context, String uid) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        showSimpleBack(context, SimpleBackPage.SHOW_MY_COMMENTS, args);
    }

    /**
     * 显示我的赞列表
     *
     * @param context
     * @param uid     用户id
     */
    public static void showMyLike(Context context, String uid) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        showSimpleBack(context, SimpleBackPage.SHOW_MY_LIKE, args);
    }

    /**
     * 显示我的消息页面
     *
     * @param context
     */
    public static void showMyMessage(Context context) {
        Bundle args = new Bundle();
        showSimpleBack(context, SimpleBackPage.SHOW_MY_MESSAGE, args);
    }

    /**
     * 显示消息/我的
     *
     * @param context
     * @param tabIdx  默认进入哪个TAB（0：消息  1：我的）
     */
    public static void showMine(Context context, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, tabIdx);
        showSimpleBack(context, SimpleBackPage.MSG_MINE, args);
    }

    /**
     * 显示我的空间图片
     *
     * @param context
     * @param userid
     */
    public static void showMyCosplay(Context context, String userid) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, userid);
        showSimpleBack(context, SimpleBackPage.MY_COSPLAY, args);
    }

    /**
     * 显示聊天对话页面
     *
     * @param context
     * @param uid
     * @param username
     */
    public static void showChat(Context context, String uid,
                                String username) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        args.putString(Constants.BUNDLE_KEY_USERNAME, username);
        showSimpleBack(context, SimpleBackPage.CHAT, args);
    }

    /**
     * 搜索的页面
     *
     * @param context
     */
    public static void showSearch(Context context) {
        Bundle args = new Bundle();
        showSimpleBack(context, SimpleBackPage.SEARCH, args);
    }

    /**
     * 搜索用户的页面
     *
     * @param context
     */
    public static void showUserSearch(Context context,boolean showInviteFriend) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, 2);
        args.putBoolean(Constants.BUNDLE_KEY_SHOW_INVITE, showInviteFriend);
        showSimpleBack(context, SimpleBackPage.SEARCH, args);
    }

    /**
     * 搜索话题的页面
     *
     * @param context
     */
    public static void showTopicSearch(Context context) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TABINDEX, 1);
        showSimpleBack(context, SimpleBackPage.SEARCH, args);
    }

    /**
     * 显示设置界面
     *
     * @param context
     */
    public static void showSetting(Context context) {
        showSimpleBack(context, SimpleBackPage.MY_SETTINGS);
    }

    /**
     * 显示账户安全界面
     *
     * @param context
     */
    public static void showSettingSecurity(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_SECURITY);
    }

    /**
     * 显示通知设置界面
     *
     * @param context
     */
    public static void showSettingNotification(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_NOTIFICATION);
    }

    /**
     * 显示意见反馈界面
     *
     * @param context
     */
    public static void showSettingFeedback(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_FEEDBACK);
    }

    /**
     * 显示意见反馈吐槽界面
     *
     * @param context
     */
    public static void showSettingFeedbackLament(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_FEEDBACK_LAMENT);
    }

    /**
     * 显示新消息通知界面
     *
     * @param context
     */
    public static void showNewMsgNotifyStyle(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_NEW_MSG_NOTIFY_STYLE);
    }

    /**
     * 显示关于界面
     *
     * @param context
     */
    public static void showAbout(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_ABOUT);
    }

    /**
     * 显示debug页面
     *
     * @param context
     */
    public static void showDebugFragment(Context context) {
        showSimpleBack(context, SimpleBackPage.SHOW_DEBUG);
    }

    /**
     * 显示关于/服务协议界面
     *
     * @param context
     */
    public static void showAboutProtocal(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_ABOUT_PROTOCAL);
    }

    /**
     * 显示关于/官方微信界面
     *
     * @param context
     */
    public static void showAboutWechat(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTINGS_ABOUT_WECHAT);
    }

    /**
     * 热门用户的界面
     *
     * @param context
     * @param uid:    用户id
     * @param sex:    用户性别
     * @param jump:   是否需要跳转到关注或发现
     */
    public static void showHotUser(Context context, String uid, String username, String sex,
                                   String avatarUrl, ThirdInfo info, int jump) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        args.putString(Constants.BUNDLE_KEY_SEX, sex);
        args.putString(Constants.BUNDLE_KEY_USERNAME, username);
        args.putString(Constants.BUNDLE_KEY_AVATAR, avatarUrl);
        args.putInt(Constants.BUNDLE_KEY_JUMP, jump);
        args.putSerializable(Constants.BUNDLE_KEY_TOKEN, info);
        showSimpleBack(context, SimpleBackPage.SHOW_HOT_USER, args);
    }

    /**
     * 三方登陆用户第一次进入时填写电话的界面 (3.2.5以后不再用了)
     *
     * @param context
     * @param uid:    用户id
     * @param sex:    用户性别
     * @param jump:   是否需要跳转到关注或发现
     */
//    public static void showFillPhone(Context context, String uid, String sex, ThirdInfo info, int jump) {
//        Bundle args = new Bundle();
//        args.putString(HotUserFragment.BUNDLE_KEY_UID, uid);
//        args.putString(HotUserFragment.BUNDLE_KEY_SEX, sex);
//        args.putInt(HotUserFragment.BUNDLE_KEY_JUMP, jump);
//        args.putSerializable(HotUserFragment.BUNDLE_KEY_TOKEN, info);
//        showSimpleBack(context, SimpleBackPage.SHOW_FILL_PHONE, args);
//    }

    /**
     * 展示用户列表
     *
     * @param context
     * @param ucid    : 1:话题id; 2:图片的id
     * @param title   : 标题名称:1,参与用户.2,浏览用户
     */
    public static void showUserList(Context context, String ucid, int title) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_ID, ucid);
        args.putInt(Constants.BUNDLE_KEY_TYPE, title);
        showSimpleBack(context, SimpleBackPage.USER_LIST, args);
    }

    /**
     * 展示话题列表
     *
     * @param context
     */
    public static void showTopicList(Context context) {
        Bundle args = new Bundle();
        showSimpleBack(context, SimpleBackPage.TOPIC_LIST, args);
    }

    /**
     * 展示邀请好友列表
     *
     * @param context
     * @param inviteType: 邀请类型 0:通讯录 1:新浪微博
     * @param phonelist:  通讯录电话列表
     * @param sinaToken:  新浪授权后的token
     */
    public static void showInviteList(Context context, int inviteType, String phonelist, ThirdInfo sinaToken) {
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_TYPE, inviteType);
        args.putString(Constants.BUNDLE_KEY_CONTACTS, phonelist);
        args.putSerializable(Constants.BUNDLE_KEY_TOKEN, sinaToken);
        showSimpleBack(context, SimpleBackPage.INVITE_LIST, args);
    }

    /**
     * 展示聊天列表
     *
     * @param context
     */
    public static void showChatList(Context context) {
        showSimpleBack(context, SimpleBackPage.CHAT_LIST);
    }

    /**
     * 显示帖子专区
     *
     * @param context
     * @param topicName: 话题名称
     * @param type:      话题类型: ip op tp等
     * @param topicID:   话题ID
     */
    public static void showArticleList(Context context, String topicName, String type, String topicID) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_TAG, topicName);
        args.putString(Constants.BUNDLE_KEY_TYPE, type);
        args.putString(Constants.BUNDLE_KEY_ID, topicID);
        showSimpleBack(context, SimpleBackPage.ARTICLE_LIST, args);
    }

    /**
     * 显示帖子的图片专区
     *
     * @param context
     * @param topicName: 话题名称
     * @param type:      话题类型: ip op tp等
     * @param topicID:   话题ID
     */
    public static void showTopicPhotoList(Context context, StickerPackage stickerPackage, String topicName, String type, String topicID) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.BUNDLE_KEY_STICKER, stickerPackage);
        args.putString(Constants.BUNDLE_KEY_TAG, topicName);
        args.putString(Constants.BUNDLE_KEY_TAG, topicName);
        args.putString(Constants.BUNDLE_KEY_TYPE, type);
        args.putString(Constants.BUNDLE_KEY_ID, topicID);
        showSimpleBack(context, SimpleBackPage.TOPIC_PHOTO_LIST, args);
    }

    /**
     * 展示活动列表
     *
     * @param context
     */
    public static void showEventsList(Context context) {
        showSimpleBack(context, SimpleBackPage.EVENTS_LIST);
    }

    /**
     * 展示活动详情
     *
     * @param context
     */
    public static void showEventsDetail(Context context, EventsInfo events) {
        Intent intent = new Intent(context, EventsDetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_EVENTINFO, events);
        context.startActivity(intent);

    }

    /**
     * 展示活动详情
     *
     * @param context
     */
    public static void showEventsDetail(Context context, EventsInfo events, int fromMission) {
        Intent intent = new Intent(context, EventsDetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_EVENTINFO, events);
        intent.putExtra(Constants.BUNDLE_KEY_FROM_MISSION, fromMission);
        context.startActivity(intent);

    }
    /**
     * 显示搜索话题结果页
     *
     * @param context
     * @param topicName: 话题关键字
     * @param category: 话题名称
     */
    public static void searchTopic(Context context, String topicName, String category) {
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_TAG, topicName);
        args.putString(Constants.BUNDLE_KEY_TYPE, category);
        showSimpleBack(context, SimpleBackPage.TOPIC_SEARCH_RESULT, args);
    }

    /**
     * 显示每日任务的分享页
     *
     * @param context
     */
    public static void showShareMission(Context context) {
        showSimpleBack(context, SimpleBackPage.SHARE_MISSION, null);
    }

    /**
     * 显示完成任务的分享引导页
     */
    public static Dialog showShareMissionGuide(Activity activity) {
        Dialog mDialog = new Dialog(activity, R.style.Dialog_FullScreen);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_share_guide, null);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.6f;
        lp.width = activity.getWindowManager().getDefaultDisplay().getWidth();
        lp.height = activity.getWindowManager().getDefaultDisplay().getHeight();
        window.setAttributes(lp);
        view.setOnClickListener(v -> {
            mDialog.dismiss();
        });
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        return  mDialog;
    }

    /**
     * 显示获得魔豆的奖励页
     * @param bean: 魔豆对象
     * @param mission: 任务id: 1分享2发图3点赞4评论
     */
    public static void showMoinBeanActivity(MoinBean bean, int mission) {
        Bundle args = new Bundle();
        args.putSerializable(GetMoinBeanActivity.KEY_MOIN_BEAN, bean);
        args.putInt(GetMoinBeanActivity.KEY_MISSION, mission);
        Intent intent = new Intent(BaseApplication.context(), GetMoinBeanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(args);
        BaseApplication.context().startActivity(intent);
    }

    /**
     * 显示获得魔豆页
     *
     * @param context
     */
    public static void showMissionActivity(Context context) {
        Intent intent = new Intent(context, MissionActivity.class);
        context.startActivity(intent);
    }

    /**
     * 显示魔豆PK榜
     *
     * @param context
     * @param userInfo
     */
    public static void showMoinBeanPk(Context context, UserInfo userInfo) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.BUNDLE_KEY_USERINFO,userInfo);
        showSimpleBack(context, SimpleBackPage.MOIN_BEAN_PK, args);
    }


    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AppContext.showToastShort("缓存清除成功");
                } else {
                    AppContext.showToastShort("缓存清除失败");
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    AppContext.getInstance().clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public static void openDownLoadService(Context context, String downurl,
                                           String tilte) {
        final ICallbackResult callback = new ICallbackResult() {

            @Override
            public void OnBackResult(Object s) {
            }
        };
        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadBinder binder = (DownloadBinder) service;
                binder.addCallback(callback);
                binder.start();

            }
        };
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constants.BUNDLE_KEY_DOWNLOAD_URL, downurl);
        intent.putExtra(Constants.BUNDLE_KEY_TITLE, tilte);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 发送广播告知评论发生变化
     *
     * @param context
     * @param isBlog
     * @param id
     * @param catalog
     * @param operation
     * @param replyComment
     */
    public static void sendBroadCastCommentChanged(Context context,
                                                   boolean isBlog, int id, int catalog, int operation,
                                                   Comment replyComment) {
        Intent intent = new Intent(Constants.INTENT_ACTION_COMMENT_CHANGED);
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_KEY_ID, id);
        args.putInt(Constants.BUNDLE_KEY_CATALOG, catalog);
        args.putBoolean(Constants.BUNDLE_KEY_BLOG, isBlog);
        args.putInt(Constants.BUNDLE_KEY_OPERATION, operation);
        args.putParcelable(Constants.BUNDLE_KEY_COMMENT, replyComment);
        intent.putExtras(args);
        context.sendBroadcast(intent);
    }

    /**
     * 改图/转发大咖秀
     */
    public static void editCosplay(Context context, CosplayInfo mCosplayInfo, long clickTime) {
        if (context == null || mCosplayInfo == null) {
            return;
        }
        try {
            if (mCosplayInfo.getIsWrite() == 0) {
                Intent newIntent = new Intent(context, PhotoReleaseActivity.class);
                newIntent.putExtra(PhotoReleaseActivity.KEY_XGT_URL, mCosplayInfo.getPicture().getUri());
                newIntent.putExtra(PhotoReleaseActivity.KEY_PARENT_WRITE_AUTH, 1);
                newIntent.putExtra(PhotoReleaseActivity.KEY_PARENT_UCID, mCosplayInfo.getUcid());
                context.startActivity(newIntent);
            } else {
                Intent intent = new Intent(context, PhotoGlorifyActivity.class);
                intent.putExtra(PhotoProcessActivity.KEY_PARENT_UCID, mCosplayInfo.getUcid());
                intent.putExtra(PhotoProcessActivity.KEY_PARENT_WRITE_AUTH, mCosplayInfo.getWriteAuth());
                intent.putExtra(PhotoProcessActivity.KEY_PARENT_CLICK_TIME, clickTime);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置大咖秀Path界面
     *
     * @param context
     * @param container 放置path的容器
     * @param info      大咖秀的对象
     *                  author Jerry
     */
    public static void setCosplayPath(Context context, LinearLayout container, CosplayInfo info) {
        if (container == null || info == null) {
            return;
        }
        container.removeAllViews();
        NodeInfo node = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;
        int total = 5;

        if (info.getPath().hasRoot()) {
            count++;
            node = info.getPath().getRoot();
            View rootView = inflater.inflate(R.layout.item_forward_user, null);
            RoundAngleImageView portrait = (RoundAngleImageView) rootView.findViewById(R.id.cos_img);
            if (node.getPicture() != null && StringUtils.isNotBlank(node.getPicture().getUri())) {
                ImageLoaderUtils.displayHttpImage(node.getPicture().getUri(), portrait, null);
            } else {
                portrait.setImageResource(R.drawable.default_img);
            }

            portrait.setOnClickListener(v -> {
                CosplayEvolutionActivity.showCosplayEvolution(context, info.getUcid(),
                        FollowAdapter.buildBundle(CosplayEvolutionActivity.KEY_FROM_ROOT));
            });

            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(R.string.cosplaypath_root);
            title.setVisibility(View.VISIBLE);
            TextView author = (TextView) rootView.findViewById(R.id.tv_author_name);
            author.setText(node.getAuthor() != null ? node.getAuthor().getUsername() : "");
            View right = rootView.findViewById(R.id.iv_right);
            if (info.getPath().hasPrevious()) {
                // 有前作时:横线
                right.setBackgroundResource(R.drawable.icon_path_full);
            } else if (info.getPath().hasChildren()) {
                // 没前作时:子横线>第二栏的
                right.setBackgroundResource(R.drawable.icon_path_half);
            } else {
                // 前作和子节点都没有则隐藏
                right.setVisibility(View.INVISIBLE);
            }
            container.addView(rootView);
        }

        if (info.getPath().hasPrevious()) {
            count++;
            node = info.getPath().getPrevious();
            View preView = inflater.inflate(R.layout.item_forward_user, null);
            RoundAngleImageView portrait = (RoundAngleImageView) preView.findViewById(R.id.cos_img);
            if (node.getPicture() != null && StringUtils.isNotBlank(node.getPicture().getUri())) {
                ImageLoaderUtils.displayHttpImage(node.getPicture().getUri(), portrait, null);
            } else {
                portrait.setImageResource(R.drawable.default_img);
            }

            portrait.setOnClickListener(v -> {
                CosplayEvolutionActivity.showCosplayEvolution(context, info.getUcid(),
                        FollowAdapter.buildBundle(CosplayEvolutionActivity.KEY_FROM_PREVIOUS));
            });

            TextView title = (TextView) preView.findViewById(R.id.title);
            title.setText(R.string.cosplaypath_previous);
            title.setVisibility(View.VISIBLE);
            TextView author = (TextView) preView.findViewById(R.id.tv_author_name);
            author.setText(node.getAuthor() != null ? node.getAuthor().getUsername() : "");
            View right = preView.findViewById(R.id.iv_right);
            if (info.getPath().hasChildren()) {
                // 有子节点时:半横线
                right.setBackgroundResource(R.drawable.icon_path_half);
            } else {
                // 子节点没有则隐藏
                right.setVisibility(View.INVISIBLE);
            }
            container.addView(preView);
        }

        if (info.getPath().hasChildren()) {
            int size = info.getPath().getChildren().size();
            int showSize = size > total - count ? total - count : size;

            for (int i = 0; i < showSize; i++) {
                node = info.getPath().getChildren().get(i);
                View childView = inflater.inflate(R.layout.item_forward_user, null);
                RoundAngleImageView portrait = (RoundAngleImageView) childView.findViewById(R.id.cos_img);
                if (node.getPicture() != null && StringUtils.isNotBlank(node.getPicture().getUri())) {
                    ImageLoaderUtils.displayHttpImage(node.getPicture().getUri(), portrait, null);
                } else {
                    portrait.setImageResource(R.drawable.default_img);
                }

                final int index = i;
                portrait.setOnClickListener(v -> {
                    CosplayEvolutionActivity.showCosplayEvolution(context, info.getUcid(),
                            FollowAdapter.buildBundle(CosplayEvolutionActivity.KEY_FROM_CHILDREN, index + 1));
                });

                TextView author = (TextView) childView.findViewById(R.id.tv_author_name);
                author.setText(node.getAuthor() != null ? node.getAuthor().getUsername() : "");
                View right = childView.findViewById(R.id.iv_right);
                if (i == showSize - 1) {
                    right.setVisibility(View.INVISIBLE);
                } else {
                    right.setBackgroundResource(R.drawable.icon_path_grey);
                }
                container.addView(childView);
            }

//            if(size > showSize){
            View lastView = inflater.inflate(R.layout.item_forward_lasticon, null);
            TextView portrait = (TextView) lastView.findViewById(R.id.last_forward);
            portrait.setText("转发\r\n(" + info.getChildrenNum() + ")");

            portrait.setOnClickListener(v -> {
                CosplayEvolutionActivity.showCosplayEvolution(context, info.getUcid(),
                        FollowAdapter.buildBundle(CosplayEvolutionActivity.KEY_FROM_CHILDREN, 1));
            });

            container.addView(lastView);
//            }
        }
    }

}


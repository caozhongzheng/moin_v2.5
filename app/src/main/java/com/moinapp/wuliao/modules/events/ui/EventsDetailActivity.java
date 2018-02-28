package com.moinapp.wuliao.modules.events.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryPreference;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.mission.MissionPreference;
import com.moinapp.wuliao.ui.ShareDialog;
import com.moinapp.wuliao.ui.WebViewActivity;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 活动详情的页面,因为详情主要是网页内容展示所以继承了WebViewActivity
 * Created by liujiancheng on 16/6/21.
 */
public class EventsDetailActivity extends WebViewActivity {
    private ILogger MyLog = LoggerFactory.getLogger(EventsDetailActivity.class.getSimpleName());

    private EventsInfo eventsInfo;
    private int fromIdx;//1表示从分享任务进来

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    @Override
    protected String getUrl() {
        if (eventsInfo == null) return null;
        return eventsInfo.getLink();
    }

    @Override
    protected void handleShare() {
        if (eventsInfo == null) return;
        final EventsShareDialog dialog = new EventsShareDialog(EventsDetailActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setShareInfo(getWebTitle(), eventsInfo.getDesc(),
                eventsInfo.getIcon().getUri(), eventsInfo.getLink());
        dialog.setEventId(eventsInfo.getEventId());
        dialog.hideSaveLayout();
        dialog.show();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("");
    }

    @Override
    protected void initData() {
        eventsInfo = (EventsInfo)getIntent().getSerializableExtra(Constants.BUNDLE_KEY_EVENTINFO);
        fromIdx = (int)getIntent().getIntExtra(Constants.BUNDLE_KEY_FROM_MISSION, 0);

        //如果是从任务跳转而来且不是第一次进入,弹出引导图
        if (fromIdx == MissionConstants.MISSION_SHARE
                && MissionPreference.getInstance().getShareGuide() == 0) {
            showGuide();
        }
    }

    /**
     * 页面加载完成的回调
     * @return
     */
    @Override
    protected WebViewClient getLoadUrlFinishCallBack() {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //开始
                super.onPageFinished(view, url);
                MyLog.i("onPageFinished....");
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                //结束
                super.onPageStarted(view, url, favicon);

            }
        };
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
        if (DiscoveryPreference.getInstance().getShareToWXFlag() == 1) {
            DiscoveryPreference.getInstance().setShareToWXFlag(0);
            MineManager.getInstance().callShareServer(null, eventsInfo.getEventId());
        }
    }

    /**
     * 分享活动的对话框
     */
    private class EventsShareDialog extends ShareDialog {

        public EventsShareDialog(Context context) {
            super(context);
        }

        @Override
        protected void getUMImage() {
            mUMImage = new UMImage(EventsDetailActivity.this, this.image);
            mIsShareImgReady = true;
        }

        @Override
        protected QQShareContent getQQShareContent() {
            QQShareContent qqShareContent = new QQShareContent();

            if (!TextUtils.isEmpty(this.link)) {
                qqShareContent.setTargetUrl(this.link);
            }
            if (!TextUtils.isEmpty(this.title)) {
                qqShareContent.setTitle(this.title);
            }
            if (!TextUtils.isEmpty(this.content)) {
                qqShareContent.setShareContent(this.content);
            }
            if (this.image != null) {
                qqShareContent.setShareImage(mUMImage);
            }
            return qqShareContent;
        }

        @Override
        protected QZoneShareContent getQZoneShareContent() {
            QZoneShareContent qZoneShareContent = new QZoneShareContent();
            if (!TextUtils.isEmpty(this.link)) {
                qZoneShareContent.setTargetUrl(this.link);
            }
            if (!TextUtils.isEmpty(this.title)) {
                qZoneShareContent.setTitle(this.title);
            }
            if (!TextUtils.isEmpty(this.content)) {
                qZoneShareContent.setShareContent(this.content);
            }
            if (this.image != null) {
                qZoneShareContent.setShareImage(mUMImage);
            }
            return qZoneShareContent;
        }
    }

    /**
     * 首次完成任务进入活动详情页面时需要弹出引导图
     */
    private void showGuide() {
        Dialog dialog = UIHelper.showShareMissionGuide(this);
        //延时3秒钟关闭
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
        MissionPreference.getInstance().setShareGuide(1);
    }
}

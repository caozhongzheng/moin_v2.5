package com.moinapp.wuliao.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.model.ThirdInfo;
import com.moinapp.wuliao.ui.dialog.CommonDialog;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 邀请好友dialog
 */
public class InviteFriendDialog extends CommonDialog implements
        View.OnClickListener {
    private static ILogger MyLog = LoggerFactory.getLogger("ShareDialog");

    private Context context;
    private String title;
    private String content;
    private String image;
    private String link;

    UMWXHandler wxHandler;
    SinaSsoHandler sinaSsoHandler;
    UMQQSsoHandler qqSsoHandler;

    UMImage mUMImage;

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");
    private LinearLayout mInviteContacts, mInviteWeibo, mInviteWeichat, mInviteQQ;

    //新浪微博的token,可以由外部传入
    private ThirdInfo mSinaToken;

    private InviteFriendDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @SuppressLint("InflateParams")
    private InviteFriendDialog(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View inviteView = getLayoutInflater().inflate(
                R.layout.dialog_cotent_invite, null);
        mInviteContacts = (LinearLayout) inviteView.findViewById(R.id.ly_contacts_invite);
        mInviteContacts.setOnClickListener(this);
        mInviteWeibo = (LinearLayout) inviteView.findViewById(R.id.ly_sina_weibo_invite);
        mInviteWeibo.setOnClickListener(this);
        mInviteWeichat = (LinearLayout) inviteView.findViewById(R.id.ly_weichat_invite);
        mInviteWeichat.setOnClickListener(this);
        mInviteQQ = (LinearLayout) inviteView.findViewById(R.id.ly_qq_invite);
        mInviteQQ.setOnClickListener(this);
        inviteView.findViewById(R.id.ly_cancel).setOnClickListener(this);

        mController.getConfig().setPlatforms(SHARE_MEDIA.SMS, SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ);

        wxHandler = new UMWXHandler(this.context,
                Constants.WEICHAT_APPID);
        sinaSsoHandler = new SinaSsoHandler();
        qqSsoHandler = new UMQQSsoHandler((Activity) this.context,
                Constants.QQ_APPID, Constants.QQ_APPKEY);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setIcon();

        setContent(inviteView, 0);
    }

    private void setIcon() {
        if (!wxHandler.isClientInstalled()) {
            mInviteWeichat.setVisibility(View.GONE);
        }

        if (!qqSsoHandler.isClientInstalled()) {
            mInviteQQ.setVisibility(View.GONE);
        }
    }

    public InviteFriendDialog(Context context) {
        this(context, R.style.dialog_share_bottom);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_contacts_invite:
                inviteContactFriends();
                break;
            case R.id.ly_weichat_invite:
                shareToWeiChat();
                break;
            case R.id.ly_sina_weibo_invite:
                inviteSinaFriends();
                break;
            case R.id.ly_qq_invite:
                shareToQQ();
                break;
            case R.id.ly_cancel:
                break;
            default:
                break;
        }
        this.dismiss();
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    // 设置新浪微博的token
    public void setSinaToken(ThirdInfo token) {
        mSinaToken = token;
    }

    // 设置需要分享的内容
    public void setInfo(String title, String content, String image, String link) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.link = link;

    }

    @SuppressWarnings("deprecation")
    private void shareToWeiChat() {
        mController.getConfig().cleanListeners();
        // 添加微信平台
        wxHandler.addToSocialSDK();
        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
//         设置分享文字
        weixinContent.setShareContent(context.getResources().getString(R.string.invite_friend_weichat));
//         设置分享title
        weixinContent.setTitle(context.getResources().getString(R.string.invite_friend_QQ_title));
//         设置分享图片
        weixinContent.setShareImage(new UMImage(context, R.drawable.icon_invite_weichat));
//         设置点击分享内容的跳转链接
        weixinContent.setTargetUrl("http://prd.mo-image.com/view/moin");

        mController.setShareMedia(weixinContent);
        mController.postShare(this.context, SHARE_MEDIA.WEIXIN, null);
    }

    private void shareToQQ() {
        qqSsoHandler.addToSocialSDK();
        QQShareContent qqShareContent = new QQShareContent();
//         设置分享文字
        qqShareContent.setShareContent(context.getResources().getString(R.string.invite_friend_QQ));
//         设置分享title
        qqShareContent.setTitle(context.getResources().getString(R.string.invite_friend_QQ_title));
//         设置分享图片
        qqShareContent.setShareImage(new UMImage(context, R.drawable.icon_invite));
//         设置点击分享内容的跳转链接
        qqShareContent.setTargetUrl("http://prd.mo-image.com/view/moin");
        mController.setShareMedia(qqShareContent);
        mController.postShare(this.context, SHARE_MEDIA.QQ, null);
    }

    private void inviteContactFriends() {
        UIHelper.showInviteList(context, 0, null, null);
    }

    private void inviteSinaFriends() {
        if (needDoAuthVerify()) {
            mController.doOauthVerify(context, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {

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
//                    AppContext.showToastShort("授权完成" + value.toString());
                    mSinaToken = convertBundle2Token(value);
//                    AppContext.showToastShort("授权完成 mSinaToken.token=" + mSinaToken.access_token + ",uid=" + mSinaToken.uid);
                    UIHelper.showInviteList(context, 1, null, mSinaToken);
                }

                @Override
                public void onCancel(SHARE_MEDIA platform) {
                    MyLog.i("授权取消");
                    AppContext.showToastShort("授权取消...");
                }
            });
        } else {
//            test
//            mSinaToken = new ThirdInfo();
//            mSinaToken.uid = "1456064343";
//            mSinaToken.access_token = "2.00lqUXaBVOEziB72bf46e53dWw3BFD";
            UIHelper.showInviteList(context, 1, null, mSinaToken);
        }
    }

    // 邀请微博好友时是否需要重新授权
    private boolean needDoAuthVerify() {
        //test
        if (mSinaToken == null) return true;

        // expires_in是秒为单位
        if ((System.currentTimeMillis() - mSinaToken.refresh_token_expires) > mSinaToken.expires_in * 1000) {
            return true;
        } else {
            return false;
        }
    }

    private ThirdInfo convertBundle2Token(Bundle b) {
        ThirdInfo info = new ThirdInfo();
        info.access_token = b.getString(LoginConstants.ACCESS_TOKEN);
        //有时候网页授权bundle key不一样
        if (TextUtils.isEmpty(info.access_token)) {
            info.access_token = b.getString(LoginConstants.ACCESS_KEY);
        }
        info.refresh_token = b.getString(LoginConstants.REFRESH_TOKEN);
        info.openid = b.getString(LoginConstants.OPEN_ID);
        info.expires_in = Long.parseLong(b.getString(LoginConstants.EXPIRE_IN));
        info.refresh_token_expires = System.currentTimeMillis();
        info.unionid = b.getString(LoginConstants.UNION_ID);
        info.uid = b.getString(LoginConstants.UID);
        return info;
    }
}

package com.moinapp.wuliao.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.DeleteOnClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryPreference;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.dialog.CommonDialog;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.DisplayUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.io.File;
import java.util.Date;

/**
 * 分享界面dialog
 *
 * @author liujiancheng modified
 */
public class ShareDialog extends CommonDialog implements
        android.view.View.OnClickListener {
    private static ILogger MyLog = LoggerFactory.getLogger("ShareDialog");

    private Context context;
    public String title;
    public String content;
    public String image;
    public String link;
    public String ucid;//cosplay id
    public String eventid;//活动id

    UMWXHandler wxHandler;
    UMWXHandler wxCircleHandler;
    SinaSsoHandler sinaSsoHandler;
    UMQQSsoHandler qqSsoHandler;
    QZoneSsoHandler qZoneSsoHandler;

    public ImageView saveImageView;
    public TextView saveTextView;
    LinearLayout deleteLayout;
    LinearLayout saveLayout;
    DeleteOnClickListener deleteOnClickListener;

    Bitmap mNewBitmap;
    public UMImage mUMImage;
    public boolean mIsShareImgReady = false;

    final UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");

    private ShareDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @SuppressLint("InflateParams")
    private ShareDialog(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View shareView = getLayoutInflater().inflate(
                R.layout.dialog_cotent_share, null);
        shareView.findViewById(R.id.ly_share_qq).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_qzone).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_weichat).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_weichat_friends).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_sina_weibo).setOnClickListener(this);
        shareView.findViewById(R.id.ly_cancel).setOnClickListener(this);
        shareView.findViewById(R.id.ly_report).setOnClickListener(this);
        deleteLayout = (LinearLayout) shareView.findViewById(R.id.ly_delete);
        deleteLayout.setOnClickListener(this);
        saveLayout = (LinearLayout) shareView.findViewById(R.id.ly_save);
        saveLayout.setOnClickListener(this);
        saveImageView = (ImageView) shareView.findViewById(R.id.iv_save);
        saveTextView = (TextView) shareView.findViewById(R.id.tv_save);

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA);
        wxHandler = new UMWXHandler(this.context,
                Constants.WEICHAT_APPID);
        wxCircleHandler = new UMWXHandler(this.context,
                Constants.WEICHAT_APPID);
        sinaSsoHandler = new SinaSsoHandler();
        qqSsoHandler = new UMQQSsoHandler((Activity) this.context,
                Constants.QQ_APPID, Constants.QQ_APPKEY);
        qZoneSsoHandler = new QZoneSsoHandler((Activity) this.context,
                Constants.QQ_APPID, Constants.QQ_APPKEY);

//        if (!wxHandler.isClientInstalled()) {
//            shareView.findViewById(R.id.ly_share_weichat).setVisibility(View.GONE);
//            shareView.findViewById(R.id.ly_share_weichat_friends).setVisibility(View.GONE);
//        }
//        if (!qqSsoHandler.isClientInstalled()) {
//            shareView.findViewById(R.id.ly_share_qq).setVisibility(View.GONE);
//            if (!qZoneSsoHandler.isClientInstalled()) {
//                shareView.findViewById(R.id.ly_share_qzone).setVisibility(View.GONE);
//            }
//        }
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.6f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContent(shareView, 0);
    }

    public ShareDialog(Context context) {
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

    // 设置需要分享的内容
    public void setShareInfo(String title, String content, String image, String link) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.link = link;

        getUMImage();
    }

    public void setCosplayUcid(String ucid) {
        this.ucid = ucid;
    }

    public void setEventId(String id) {
        this.eventid = id;
    }

    // 设置删除选项为可见
    public void setDeleteEnable() {
        deleteLayout.setVisibility(View.VISIBLE);
    }

    public void setDeleteOnClick(DeleteOnClickListener listener) {
        deleteOnClickListener = listener;
    }

    public void hideSaveLayout() {
        saveLayout.setVisibility(View.INVISIBLE);
    }

    protected void getUMImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mUMImage = getShareImg();
                mIsShareImgReady = true;
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (!mIsShareImgReady) {
            AppContext.showToast(R.string.tip_share_not_ready);
            return;
        }
        switch (v.getId()) {
            case R.id.ly_share_weichat:
                if (!wxHandler.isClientInstalled()) {
                    AppContext.toast(context, "请先安装微信");
                } else {
                    shareToWeiChat();
                }
                break;
            case R.id.ly_share_weichat_friends:
                if (!wxHandler.isClientInstalled()) {
                    AppContext.toast(context, "请先安装微信");
                } else {
                    shareToWeiChatFriends();
                }
                break;
            case R.id.ly_share_sina_weibo:
                shareToSinaWeibo();
                break;
            case R.id.ly_share_qq:
                if (!qqSsoHandler.isClientInstalled()) {
                    AppContext.toast(context, "请先安装QQ");
                } else {
                    shareToQQ();
                }
                break;
            case R.id.ly_share_qzone:
                if (!qqSsoHandler.isClientInstalled()) {
                    AppContext.toast(context, "请先安装QQ");
                } else {
                    shareToQZone();
                }
                break;
            case R.id.ly_cancel:
                break;
            case R.id.ly_delete:
                processDeleteClick();
                break;
            case R.id.ly_save:
                processSaveClick();
                break;
            case R.id.ly_report:
                if (ucid != null) {
                    MineManager.getInstance().report(null, ucid, null, new IListener2() {
                        @Override
                        public void onSuccess(Object obj) {
                            AppContext.showToastShort(R.string.report_text);
                        }

                        @Override
                        public void onErr(Object obj) {

                        }

                        @Override
                        public void onNoNetwork() {

                        }
                    });
                }
                break;
            default:
                break;
        }
        this.dismiss();
    }

    private void processDeleteClick() {
        DialogHelper dialogHelper = new DialogHelper((Activity) context);
        dialogHelper.alert4M(null, "您确定要删除这张图片吗？",
                "删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deleteOnClickListener != null) {
                            deleteOnClickListener.onClick(null);
                        }
                        dialogHelper.dialogDismiss();
                    }
                }, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogHelper.dialogDismiss();
                    }
                }, false);
    }

    View.OnClickListener clickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (deleteOnClickListener != null) {
                deleteOnClickListener.onClick(null);
            }
        }
    };


    /**
     * 分享结果的回调, 状态200表示分享成功
     */
    private SocializeListeners.SnsPostListener shareListener = new SocializeListeners.SnsPostListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
            //分享成功后需要调用分享接口看是否获得了魔豆
            if (i == 200) {
                MineManager.getInstance().callShareServer(ucid, eventid);
            }
        }
    };

    @SuppressWarnings("deprecation")
    private void shareToWeiChat() {
        // 添加微信平台
        wxHandler.addToSocialSDK();
        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = getWeiXinShareContent();
        mController.setShareMedia(weixinContent);
        mController.postShare(this.context, SHARE_MEDIA.WEIXIN, null);
        DiscoveryPreference.getInstance().setShareToWXFlag(1);
    }

    protected WeiXinShareContent getWeiXinShareContent() {
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置分享文字
        if (!TextUtils.isEmpty(this.content)) {
            weixinContent.setShareContent(this.content);
        }
        // 设置title
        if (!TextUtils.isEmpty(this.title)) {
            weixinContent.setTitle(this.title);
        }
        // 设置分享内容跳转URL
        if (!TextUtils.isEmpty(this.link)) {
            weixinContent.setTargetUrl(this.link);
        }
        // 设置分享图片
        if (mUMImage != null) {
            weixinContent.setShareImage(mUMImage);
        }
        return weixinContent;
    }

    private void shareToWeiChatFriends() {
        // 支持微信朋友圈
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        // 设置微信朋友圈分享内容
        CircleShareContent circleMedia = getCircleShareContent();
        mController.setShareMedia(circleMedia);
        mController.postShare(this.context, SHARE_MEDIA.WEIXIN_CIRCLE, shareListener);
    }

    protected CircleShareContent getCircleShareContent() {
        CircleShareContent circleMedia = new CircleShareContent();
        // 设置分享文字
        if (!TextUtils.isEmpty(this.content)) {
            circleMedia.setShareContent(this.content);
        }
        // 设置朋友圈title
        // 设置title
        if (!TextUtils.isEmpty(this.title)) {
            circleMedia.setTitle(this.title);
        }
        // 设置分享图片
        if (mUMImage != null) {
            circleMedia.setShareImage(mUMImage);
        }
        // 设置分享内容跳转URL
        if (!TextUtils.isEmpty(this.link)) {
            circleMedia.setTargetUrl(this.link);
        }

        return circleMedia;
    }

    private void shareToSinaWeibo() {
        if (this.title == null) {
            this.title = "";
        }

        if (this.content == null) {
            this.content = "";
        }

        String content = this.title + this.content;

        //如果没有标题文字,微博分享填上默认广告语
        if (TextUtils.isEmpty(content)) {
            content = context.getString(R.string.moin_default_share_string);
        }
        if (!TextUtils.isEmpty(this.link)) {
            sinaSsoHandler.setTargetUrl(this.link);
            mController.setShareContent(content + " " + this.link);
        } else {
            mController.setShareContent(content);
        }
        mController.setShareType(ShareType.SHAKE);

        if (mUMImage != null) {
            mController.setShareImage(mUMImage);
        }

        // 设置新浪微博SSO handler
        mController.getConfig().setSsoHandler(sinaSsoHandler);
        sinaSsoHandler.addToSocialSDK();
        mController.postShare(this.context, SHARE_MEDIA.SINA, shareListener);
    }

    private void shareToQQ() {
        qqSsoHandler.addToSocialSDK();
        QQShareContent qqShareContent = getQQShareContent();
        mController.setShareMedia(qqShareContent);
        mController.postShare(this.context, SHARE_MEDIA.QQ, shareListener);
    }

    protected QQShareContent getQQShareContent() {
        QQShareContent qqShareContent = new QQShareContent();

        if (!TextUtils.isEmpty(this.link)) {
            qqShareContent.setTargetUrl(this.link);
        }
        if (!TextUtils.isEmpty(this.title)) {
            qqShareContent.setTitle(this.title);
        }
        if (!TextUtils.isEmpty(this.content)) {
            mController.setShareContent(this.content);
        }
        if (mUMImage != null) {
            qqShareContent.setShareImage(mUMImage);
        }
        return qqShareContent;
    }

    private void shareToQZone() {
        qZoneSsoHandler.addToSocialSDK();
        QZoneShareContent qZoneShareContent = getQZoneShareContent();
        mController.setShareMedia(qZoneShareContent);
        mController.postShare(this.context, SHARE_MEDIA.QZONE, shareListener);
    }

    protected QZoneShareContent getQZoneShareContent() {
        QZoneShareContent qZoneShareContent = new QZoneShareContent();
        if (!TextUtils.isEmpty(this.ucid)) {
            qZoneShareContent.setTargetUrl(getShareLink(this.ucid));
        } else if (!TextUtils.isEmpty(this.link)) {
            qZoneShareContent.setTargetUrl(this.link);
        }
        if (!TextUtils.isEmpty(this.title)) {
            qZoneShareContent.setTitle(this.title);
        }
        if (!TextUtils.isEmpty(this.content)) {
            mController.setShareContent(this.content);
        }
        if (this.image != null) {
            qZoneShareContent.setShareImage(new UMImage(this.context, this.image));
        }
        qZoneShareContent.setTitle(context.getString(R.string.moin_default_share_title));
        qZoneShareContent.setShareContent(context.getString(R.string.moin_default_share_string));

        return qZoneShareContent;
    }

    protected UMImage getShareImg() {
        UMImage img;

        if (TextUtils.isEmpty(image)) return null;

        MyLog.i("image url=" + image);
        Bitmap bitmap = ImageLoaderUtils.getImageFromCache(this.image);
        if (bitmap == null) {
            String fileName = BitmapUtil.getTmpShareImagePath();
            if (HttpUtil.download(image, fileName)) {
                bitmap = BitmapFactory.decodeFile(fileName);
                if (bitmap != null) {
                    MyLog.i("fileName wh=" + bitmap.getWidth() + "*" + bitmap.getHeight());
                    img = getWatermarkImage(bitmap);
                } else {
                    img = new UMImage(this.context, this.image);
                    MyLog.i("fileName NULL 1~~~~");
                }
            } else {
                img = new UMImage(this.context, this.image);
                MyLog.i("fileName NULL 2~~~~");
            }
        } else {
            MyLog.i("image wh=" + bitmap.getWidth() + "*" + bitmap.getHeight());
            img = getWatermarkImage(bitmap);
        }
        return img;
    }

    private UMImage getWatermarkImage(Bitmap bitmap) {
        UMImage img;
        int SW = DisplayUtil.getDisplayWidth(this.context);
        int SH = SW * 932 / 750;//DisplayUtil.getDisplayHeight(this.context);
//        Bitmap baseboard = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_bg);
//        if (baseboard.getWidth() != SW) {
//            baseboard = BitmapUtil.zoomBitmap(baseboard, SW, SH);
//        }
        Bitmap watermark = null;
        if (MinePreference.getInstance().isSaveWatermark()) {
            watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.watermark);

            int ww = watermark.getWidth();
            int W = DisplayUtil.dip2px(context, 50);
            if (ww != W) {
                float scale = (float) W / (float) ww;
                watermark = BitmapUtil.zoomBitmap(watermark, scale);
            }
        }

        if (bitmap.getWidth() != SW) {
            bitmap = BitmapUtil.zoomBitmap(bitmap, SW, SW);
        }
        String authorName = "TA";
        Bitmap authorAvatar = null;
        int aw = DisplayUtil.dip2px(this.context, 61);
        int boardWidth = DisplayUtil.dip2px(this.context, 6);

        if (AppContext.getInstance().isLogin()) {
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            authorName = ClientInfo.getUserName();
            MyLog.i("avatar url=" + userInfo.getAvatar().getUri());
            authorAvatar = ImageLoaderUtils.getImageFromCache(userInfo.getAvatar().getUri());
            if (authorAvatar == null) {
                int wp30 = DisplayUtil.dip2px(this.context, 30);
                String leftTopUrl = ImageLoaderUtils.buildNewUrl(userInfo.getAvatar().getUri(), new ImageSize(wp30, wp30));
                authorAvatar = ImageLoaderUtils.getImageFromCache(leftTopUrl);
            }
            if (authorAvatar == null) {
                String fileName = BitmapUtil.getTmpShareAvatarImagePath();
                String newUrl = ImageLoaderUtils.buildNewUrl(userInfo.getAvatar().getUri(), new ImageSize(aw, aw));
                if (HttpUtil.download(newUrl, fileName, true)) {
                    authorAvatar = BitmapFactory.decodeFile(fileName);
                    MyLog.i("avatar wh=" + authorAvatar.getWidth() + "*" + authorAvatar.getHeight());
                } else {
                    MyLog.i("avatar download failed. " + newUrl);
                }
            }
            if (authorAvatar != null) {
                MyLog.i("avatar cache ok   wh=" + authorAvatar.getWidth() + "*" + authorAvatar.getHeight());
            }
        }
        if (authorAvatar == null) {
            authorAvatar = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.share_author_default);
        }
        if (authorAvatar.getWidth() != aw) {
            authorAvatar = BitmapUtil.zoomBitmap(authorAvatar, aw, aw);
        }
        if (StringUtil.isNullOrEmpty(authorName)) {
            authorName = "TA";
        }
        mNewBitmap = BitmapUtil.setWaterMark(null, bitmap, watermark, authorAvatar, authorName, SW, SH, aw, boardWidth);
        img = new UMImage(this.context, mNewBitmap);
        return img;
    }

//    private Bitmap getImageFromCache() {
//        if (TextUtils.isEmpty(this.image)) return null;
//
//        imageLoader = ImageLoader.getInstance();
//        if(!imageLoader.isInited()) {
//            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
//        }
//        return imageLoader.loadImageSync(this.image, BitmapUtil.getImageLoaderOption());
//    }

    private boolean checkCanShare() {
        boolean canShare = true;
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || TextUtils.isEmpty(link)) {
            canShare = false;
        }
        return canShare;
    }

    protected void processSaveClick() {
        String picName;
        if (TextUtils.isEmpty(this.ucid)) {
            picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");
        } else {
            picName = this.ucid;
        }
        String fileName = FileUtil.getInst().getSystemPhotoPath() + "/" + picName + ".jpg";
        File file = new File(fileName);
        if (file.exists()) file.delete();

        Bitmap bitmap = ImageLoaderUtils.getImageFromCache(this.image);
        if (bitmap != null) {
            StickerUtils.saveWaterMarkFile(context, bitmap, fileName);
//            BitmapUtil.saveBitmap2file(bitmap, file, Bitmap.CompressFormat.JPEG, 100);
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                    Uri.parse("file://" + fileName)));
            AppContext.toast(context, context.getString(R.string.cosplay_save_to_album_ok));
        } else {
            if (TextUtils.isEmpty(this.image)) {
                Toast.makeText(context, context.getString(R.string.cosplay_save_error),
                        Toast.LENGTH_SHORT);
            } else {
                //临时下载
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpUtil.download(image, fileName);
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.parse("file://" + fileName)));
                    }
                }).start();

                AppContext.toast(context, context.getString(R.string.cosplay_save_to_album_ok));
            }
        }
    }

    protected String getShareLink(String ucid) {
        // http://domain/view/cosplay/ucid
        if (AppContext.getInstance().isLogin()) {
            return AppConfig.getBaseShareUrl() + "view/cosplay/" + ucid + "/" + ClientInfo.getUID();
        }
        return AppConfig.getBaseShareUrl() + "view/cosplay/" + ucid;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mNewBitmap != null) {
            mNewBitmap.recycle();
        }
    }
}

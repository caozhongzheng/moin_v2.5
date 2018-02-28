package com.moinapp.wuliao.modules.discovery.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.EmojiInfo;
import com.moinapp.wuliao.modules.discovery.model.EmojiSet;
import com.moinapp.wuliao.ui.ShareDialog;
import com.moinapp.wuliao.ui.imageselect.CommonAdapter;
import com.moinapp.wuliao.ui.imageselect.ViewHolder;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.EmojiUtils;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.BitmapUtils;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.File;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

/**
 * 贴纸详情界面
 *
 * @author liujiancheng
 */
public class EmojiDetailActivity extends BaseActivity {
    private static final ILogger MyLog = LoggerFactory.getLogger("EmojiDetail");
    protected static final String TAG = EmojiDetailActivity.class.getSimpleName();
    public static final String EMOJI_SHARE_URL = AppConfig.getBaseShareUrl() + "view/emoji/";

    public static final String EMOJI_ID = "emoji_id";
    public static final String PLATFORM_WX = "weixin";
    public static final String PLATFORM_QQ = "qq";
    public static final int FLAG_WX = 1;
    public static final int FLAG_QQ = 2;

    private List<EmojiInfo> mEmojiInfoList;
    private EmojiSet mEmojiSet;

    private String mEmojiId;
    private String mEmojiPath;
    private EmojiInfo mSelectedEmoji;
    private int mCurrentIndex;
    private int[] mShareWXCount;
    private int[] mShareQQCount;
    private int mShareFlag;
    SocializeListeners.SnsPostListener mSnsPostListener;

    @InjectView(R.id.tv_left_img)
    RelativeLayout back;

    @InjectView(R.id.tv_title)
    TextView title;

    @InjectView(R.id.tv_right)
    RelativeLayout share;

    @InjectView(R.id.emoji_gif)
    GifImageView gifImageView;

//    @InjectView(R.id.emoji_name)
//    TextView name;

    @InjectView(R.id.emoji_count)
    TextView count;

    @InjectView(R.id.emoji_update)
    TextView update;

    @InjectView(R.id.emoji_grid)
    GridView grid;

    @InjectView(R.id.btn_shareWX)
    Button share2WX;

    @InjectView(R.id.btn_shareQQ)
    Button share2QQ;

    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    @Override
    protected int getLayoutId() {
        return R.layout.emoji_detail;
    }

    @Override
    public void initView() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        share.setVisibility(View.INVISIBLE);
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareEmojiSet();
//            }
//        });

        share2WX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TDevice.hasInternet()) {
                    mShareFlag = FLAG_WX;
                    sendEmoji2WX();
                } else {
                    Toast.makeText(EmojiDetailActivity.this, getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        share2QQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TDevice.hasInternet()) {
                    mShareFlag = FLAG_QQ;
                    sendEmoji2QQ();
                } else {
                    Toast.makeText(EmojiDetailActivity.this, getString(R.string.no_network),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void initData() {
        mEmojiId = getIntent().getStringExtra(EMOJI_ID);

        if (TextUtils.isEmpty(mEmojiId)) {
            finish();
            return;
        }

        getEmojiDetail();

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, Constants.WEICHAT_APPID, Constants.WEICHAT_SECRET);
        wxHandler.addToSocialSDK();
        if (!wxHandler.isClientInstalled()) {
            share2WX.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
            share2WX.setEnabled(false);
        }
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, Constants.QQ_APPID, Constants.QQ_APPKEY);
        qqSsoHandler.addToSocialSDK();
        if (!qqSsoHandler.isClientInstalled()) {
            share2QQ.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
            share2QQ.setEnabled(false);
        }
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.stick_detail;
    }

    @Override
    @OnClick({R.id.tv_left_img})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_left_img:
                finish();
                break;
            default:
                break;
        }
    }

    private void getEmojiDetail() {
//        LoginManager.getInstance().getEmojiDetail(mEmojiId, new IListener() {
//            @Override
//            public void onSuccess(Object obj) {
//                mEmojiSet = (EmojiSet) obj;
//                if (mEmojiSet != null) {
//                    mEmojiInfoList = mEmojiSet.getEmojis();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            updateView(mEmojiSet);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onErr(Object obj) {
//
//            }
//
//            @Override
//            public void onNoNetwork() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(EmojiDetailActivity.this, getString(R.string.no_network),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
    }

    // 初始化页面
    private void updateView(EmojiSet emojiSet) {
        if (emojiSet == null) return;
        title.setText(emojiSet.getName());
//        name.setText(emojiSet.getName());
        count.setText(getString(R.string.stick_count) + emojiSet.getEmojiNum() + "张");
        update.setText(getString(R.string.stick_update) + StringUtil.humanDate(emojiSet.getUpdateAt(), StringUtil.TIME_PATTERN));

        EmojiAdapter mAdaptor = new EmojiAdapter(BaseApplication.context(), mEmojiInfoList, R.layout.emoji_grid_item);
        grid.setAdapter(mAdaptor);

        //默认选中第一个
        if (emojiSet.getEmojis() != null && emojiSet.getEmojis().size() > 0) {
            List<EmojiInfo> emojiInfoList = emojiSet.getEmojis();
            mShareWXCount = new int[emojiInfoList.size()];
            mShareQQCount = new int[emojiInfoList.size()];
            for (int i = 0; i < emojiInfoList.size(); i++) {
                mShareWXCount[i] = emojiInfoList.get(i).getWeixin();
                mShareQQCount[i] = emojiInfoList.get(i).getQq();
            }

            mCurrentIndex = 0;
            mSelectedEmoji = emojiInfoList.get(mCurrentIndex);
            updateEmojiView();
        }
    }

    // 选中子表情后刷新表情gif和分享数量
    private void updateEmojiView() {
        updateShareCount();
        showBoder();
        mEmojiPath = EmojiUtils.getEmjPath(mSelectedEmoji);
        displayEmoji();
    }

    //刷新分享数量
    private void updateShareCount() {
        try {
            //有时候会崩溃,暂时防御下,没找到固定重现的原因
            if (share2WX != null && share2QQ != null) {
                share2WX.setText(getString(R.string.platform_weichat) + " (" + mShareWXCount[mCurrentIndex] + ")");
                share2QQ.setText(getString(R.string.platform_qq) + " (" + mShareQQCount[mCurrentIndex] + ")");
            }
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    private void showBoder() {

    }

    private void displayEmoji() {
        if (TextUtils.isEmpty(mEmojiPath)) return;
        final File file = new File(mEmojiPath);
        if (file.exists()) {
//            MyLog.i("ljc...file exist");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            GifUtils.displayGif(gifImageView, file);
                            if (GifUtils.isGifFormat(mEmojiPath)) {
                                GifUtils.getInstance(2, GifUtils.Type.FIFO).loadImage(mEmojiPath, gifImageView);
                            } else {
                                file.delete();
                                displayEmoji();
                            }
                        }
                    });
                }
            }).start();

        } else {
            //download gif first
            if (mSelectedEmoji != null) {
                downloadEmoji(mSelectedEmoji.getPicture().getUri(), mEmojiPath, new EmojiDownloadListener() {
                    @Override
                    public void onEmojiDownloadSucc(File gif) {
                        MyLog.i("onEmojiDownloadSucc: download gif file:" + gif.getAbsolutePath());
                        final File f = gif;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                GifUtils.displayGif(gifImageView, f);
                                GifUtils.getInstance(2, GifUtils.Type.FIFO).loadImage(mEmojiPath, gifImageView);
                            }
                        });
                    }
                });
            }
        }
    }

    private void downloadEmoji(final String url, final String path, final EmojiDownloadListener listener) {
        MyLog.i("downloadEmoji:url=" + url + ",path=" + path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (HttpUtil.download(url, path)) {
                    listener.onEmojiDownloadSucc(new File(path));
                }
            }
        }).start();
    }

    private interface EmojiDownloadListener {
        void onEmojiDownloadSucc(File gifFile);
    }

    private void sendEmoji2WX() {
        if (TextUtils.isEmpty(mEmojiPath)) {
            Toast.makeText(EmojiDetailActivity.this,
                    "分享失败", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        //删除旧的回调
        if (mSnsPostListener != null) {
            mController.unregisterListener(mSnsPostListener);
        }
        //微信分享的回调
        mSnsPostListener = new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int stCode,
                                   SocializeEntity entity) {
                if (stCode == 200) {
                    Toast.makeText(EmojiDetailActivity.this, "分享成功", Toast.LENGTH_SHORT)
                            .show();
                    if (mShareFlag == FLAG_WX) {
                        statisticShareCount(FLAG_WX);
                    }
                } else {
                    Toast.makeText(EmojiDetailActivity.this,
                            "分享失败 : error code: " + stCode, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        mController.registerListener(mSnsPostListener);

        UMWXHandler handler = (UMWXHandler) mController.getConfig().getSsoHandler(SHARE_MEDIA.WEIXIN.getReqCode());
        WXEmojiObject emojiObject = new WXEmojiObject();
        emojiObject.emojiPath = mEmojiPath;


        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(emojiObject);
        msg.title = "";
        msg.description = "";

        // 先从imageloader的缓存读
        Bitmap bitmap = null;
        if (mSelectedEmoji != null) {
            bitmap = ImageLoaderUtils.getImageFromCache(mSelectedEmoji.getIcon().getUri());
        }
        if (bitmap == null) {
            //再从表情文件下载的本读路径读
            String thumb = EmojiUtils.getThumbPath(mSelectedEmoji);
            MyLog.i("thumb =" + thumb);
            File thumbFIle = new File(thumb);
            if (thumbFIle.exists()) {
                bitmap = BitmapFactory.decodeFile(thumb);
            }
            if (bitmap == null) {
                MyLog.i("bitmap = null");
                // 都获取不到的话用默认的图标
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_img);
            }
        }
        //开始压缩质量，从60开始压，这个是经验值测试得出，不保险，最终需要服务器上传的表情缩略图
        //要小于32k来解决
        if (BitmapUtils.bitmap2Bytes(bitmap).length > 32 * 1024) {
            bitmap = BitmapUtil.compressImage(bitmap, 60, 32);
            //压缩后仍让大于32k，用默认缩略图
            if (BitmapUtils.bitmap2Bytes(bitmap).length > 32 * 1024) {
                bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_img);
            }
        }

        msg.thumbData = BitmapUtils.bitmap2Bytes(bitmap);
        MyLog.i("msg.thumbData.size=" + msg.thumbData.length);
        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis()); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        handler.getWXApi().sendReq(req);
    }

    private void sendEmoji2QQ() {
        if (TextUtils.isEmpty(mEmojiPath)) {
            Toast.makeText(EmojiDetailActivity.this,
                    "分享失败", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setTitle("");
        qqShareContent.setShareMedia(new UMImage(this, mEmojiPath));
        mController.setShareMedia(qqShareContent);
        mController.postShare(this, SHARE_MEDIA.QQ,
                new SocializeListeners.SnsPostListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(EmojiDetailActivity.this, "开始分享.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                        if (eCode == 200) {
                            Toast.makeText(EmojiDetailActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
                            statisticShareCount(FLAG_QQ);
                        } else {
                            String eMsg = "";
                            if (eCode == -101) {
                                eMsg = "没有授权";
                            }
                            Toast.makeText(EmojiDetailActivity.this, "分享失败[" + eCode + "] " +
                                    eMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //分享成功后连服务器统计数据
    private void statisticShareCount(final int flag) {
//        String type = flag == FLAG_WX ? PLATFORM_WX : PLATFORM_QQ;
//        LoginManager.getInstance().sendEmoji(mSelectedEmoji.getId(), mSelectedEmoji.getParentid(),
//                type, new IListener() {
//                    @Override
//                    public void onSuccess(Object obj) {
//                        if (flag == FLAG_WX) {
//                            mShareWXCount[mCurrentIndex]++;
//                        } else {
//                            mShareQQCount[mCurrentIndex]++;
//                        }
//                        EmojiDetailActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateShareCount();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onErr(Object obj) {
//
//                    }
//
//                    @Override
//                    public void onNoNetwork() {
//
//                    }
//                });
    }

    private void shareEmojiSet() {
        String shareUrl = EMOJI_SHARE_URL + mEmojiSet.getEmojiId();
        ShareDialog dialog = new ShareDialog(EmojiDetailActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setShareInfo(mEmojiSet.getName(), mEmojiSet.getName(), mSelectedEmoji.getIcon().getUri(), shareUrl);
        dialog.show();
    }

    private class EmojiAdapter extends CommonAdapter<EmojiInfo> {
        int lastClickItem = -1;
        RoundedImageView mLastImageView;

        public EmojiAdapter(Context context, List<EmojiInfo> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(final ViewHolder helper, final int position) {
            final EmojiInfo item = getItem(position);
            //设置图片
            final RoundedImageView mImageView = helper.getView(R.id.id_item_image);
            ImageLoaderUtils.displayHttpImage(item.getIcon().getUri(), mImageView, null);

            if (lastClickItem == -1 && position == 0) {
//                MyLog.i("默认第一个 ");
                lastClickItem = 0;
                mImageView.setBorderWidth(5f);
                mImageView.setBorderColor(getResources().getColor(R.color.moin));
                mLastImageView = mImageView;
            }

            mImageView.setOnClickListener(v -> {
                mSelectedEmoji = item;
                mCurrentIndex = item.getId() - 1;
                mImageView.setBorderWidth(5f);
                mImageView.setBorderColor(getResources().getColor(R.color.moin));

                if (lastClickItem != position && lastClickItem >= 0 && lastClickItem < getCount()) {
                    mLastImageView.setBorderWidth(0f);
//                    MyLog.i("恢复了第 " + lastClickItem + " 个 ");
                }
//                MyLog.i("点了第 " + position + " 个 ");
                lastClickItem = position;
                mLastImageView = mImageView;
                updateEmojiView();
            });
        }
    }

    //微博分享的回调
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        /**使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
//        if(ssoHandler != null){
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.EMOJI_DETAIL_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.EMOJI_DETAIL_ACTIVITY); //
        MobclickAgent.onPause(this);
    }

}

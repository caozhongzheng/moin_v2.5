package com.moinapp.wuliao.modules.discovery.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.melnykov.fab.ObservableScrollView;
import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.CommonDetailFragment;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.bean.FavoriteList;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.emoji.Emojicon;
import com.moinapp.wuliao.emoji.OnEmojiClickListener;
import com.moinapp.wuliao.emoji.OnSendClickListener;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.listener.CommentOnClickListener;
import com.moinapp.wuliao.listener.DeleteOnClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.DiscoveryPreference;
import com.moinapp.wuliao.modules.discovery.model.CommentInfo;
import com.moinapp.wuliao.modules.discovery.model.CommentInfoList;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.result.GetCosplayResult;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.modules.mission.MissionPreference;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.DetailActivity;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.ui.ShareDialog;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.MyAudioPlayer;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;
import com.moinapp.wuliao.widget.LikeLayout;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * 图片详情
 */
public class CosplayDetailFragment extends CommonDetailFragment<CosplayInfo> implements ObservableScrollView.OnScrollChangedListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(CosplayDetailFragment.class.getSimpleName());
    @InjectView(R.id.title_layout)
    CommonTitleBar titleBar;

    @InjectView(R.id.sv_news_container)
    ObservableScrollView scrollView;

    @InjectView(R.id.ly_image)
    FrameLayout mFlImage;

    @InjectView(R.id.ll_author)
    LinearLayout mLlAuthor;
    @InjectView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    @InjectView(R.id.tv_name)
    TextView mTvName;
    @InjectView(R.id.tv_time)
    TextView mTvTime;
    @InjectView(R.id.btn_follow)
    FollowView follow;

    @InjectView(R.id.tv_content)
    TextView mTvContent;
    @InjectView(R.id.iv_image)
    ImageView mIvCosplay;

    @InjectView(R.id.ll_audio_play)
    LinearLayout mLlAudioPlay;
    @InjectView(R.id.iv_audio_play)
    ImageView mIvAudioPlay;
    @InjectView(R.id.iv_circle)
    FrameLayout mIvAudioCircle;
    @InjectView(R.id.iv_circle_anim)
    ImageView mIvAudioCircleAnim;

    @InjectView(R.id.like_layout)
    LikeLayout mLlLikeBtn;

    @InjectView(R.id.ll_viewuser)
    LinearLayout mLlViewUserBtn;
    @InjectView(R.id.iv_viewuser)
    ImageView mIvViewer;
    @InjectView(R.id.tv_viewnum)
    TextView mTvViewerNum;

    @InjectView(R.id.ll_comment)
    LinearLayout mLlCommentBtn;
    @InjectView(R.id.iv_comment)
    ImageView mIvComment;
    @InjectView(R.id.tv_commentnum)
    TextView mTvCommentNum;

    @InjectView(R.id.ll_forward)
    LinearLayout mLlForwardBtn;
    @InjectView(R.id.iv_forward)
    ImageView mIvForward;
    @InjectView(R.id.tv_forwardnum)
    TextView mTvForward;

    @InjectView(R.id.ll_tag)
    LinearLayout mLlTags;
    @InjectView(R.id.fl_tag_container)
    FlowLayout mFlTags;

    @InjectView(R.id.ll_like)
    LinearLayout mLlLike;
    //    @InjectView(R.id.ll_likeusers)
//    LinearLayout mLlLikeUsers;
//    @InjectView(R.id.fl_likeuser_container)
//    FlowLayout mFlLikeUsers;
    @InjectView(R.id.avatar0)
    LinearLayout mAvatar0;
    @InjectView(R.id.avatar1)
    LinearLayout mAvatar1;
    @InjectView(R.id.avatar2)
    LinearLayout mAvatar2;
    @InjectView(R.id.avatar3)
    LinearLayout mAvatar3;
    @InjectView(R.id.avatar4)
    LinearLayout mAvatar4;
    @InjectView(R.id.avatar5)
    LinearLayout mAvatar5;
    @InjectView(R.id.avatar6)
    LinearLayout mAvatar6;
    @InjectView(R.id.avatar7)
    LinearLayout mAvatar7;
    @InjectView(R.id.avatar8)
    LinearLayout mAvatar8;
    @InjectView(R.id.avatar9)
    LinearLayout mAvatar9;
    @InjectView(R.id.tv_more_like_user)
    TextView mTvLikeCount;
    @InjectView(R.id.iv_more_user)
    ImageView mIvMoreUser;

    @InjectView(R.id.ll_forward_more)
    LinearLayout mLlForward;
    @InjectView(R.id.tv_forward)
    TextView mTvForwardInfo;

    @InjectView(R.id.ll_comments)
    LinearLayout mLlComments;
    @InjectView(R.id.tv_comment_info)
    TextView mTvCommentInfo;

    @InjectView(R.id.rl_comment0)
    RelativeLayout mRlComment0;
    @InjectView(R.id.rl_comment1)
    RelativeLayout mRlComment1;
    @InjectView(R.id.rl_comment2)
    RelativeLayout mRlComment2;
    @InjectView(R.id.rl_comment3)
    RelativeLayout mRlComment3;
    @InjectView(R.id.rl_comment4)
    RelativeLayout mRlComment4;
    @InjectView(R.id.rl_comment5)
    RelativeLayout mRlComment5;
    @InjectView(R.id.rl_comment6)
    RelativeLayout mRlComment6;
    @InjectView(R.id.rl_comment7)
    RelativeLayout mRlComment7;
    @InjectView(R.id.rl_comment8)
    RelativeLayout mRlComment8;
    @InjectView(R.id.rl_comment9)
    RelativeLayout mRlComment9;
    @InjectView(R.id.rl_comment10)
    RelativeLayout mRlComment10;
    @InjectView(R.id.rl_comment11)
    RelativeLayout mRlComment11;
    @InjectView(R.id.rl_comment12)
    RelativeLayout mRlComment12;
    @InjectView(R.id.rl_comment13)
    RelativeLayout mRlComment13;
    @InjectView(R.id.rl_comment14)
    RelativeLayout mRlComment14;
    @InjectView(R.id.rl_comment15)
    RelativeLayout mRlComment15;
    @InjectView(R.id.rl_comment16)
    RelativeLayout mRlComment16;
    @InjectView(R.id.rl_comment17)
    RelativeLayout mRlComment17;
    @InjectView(R.id.rl_comment18)
    RelativeLayout mRlComment18;
    @InjectView(R.id.rl_comment19)
    RelativeLayout mRlComment19;

    @InjectView(R.id.btn_show_all_comments)
    TextView mBtnShowAllComments;

    @InjectView(R.id.like_img0)
    RoundAngleImageView mLikeImg0;
    @InjectView(R.id.like_img1)
    RoundAngleImageView mLikeImg1;
    @InjectView(R.id.like_img2)
    RoundAngleImageView mLikeImg2;
    @InjectView(R.id.like_img3)
    RoundAngleImageView mLikeImg3;

    private String mUcid;
    private CosplayInfo mCosplayInfo;
    private String mFrom;
    private int mStatus;

    private DetailActivity outAty;
    private long startTime, displayStartTime, getDataStartTime, mClickTime;
    private int showLikeUserCount = 10;
    private int SW = (int) TDevice.getScreenWidth() / (showLikeUserCount + 4);
    private int IMG_WIDTH = (int) ((TDevice.getScreenWidth() - TDevice.dpToPixel(38f)) / 4);
    private int showCommentCount = 20;
    private int showGuessLikeCount = 4;
    private static String RECEIVE_MSG = "";

    private boolean registed = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RECEIVE_MSG = intent.getAction();
        }
    };

    public CosplayDetailFragment() {
    }

    public CosplayDetailFragment(long clickTime) {
        this.mClickTime = clickTime;
    }

    private List<LinearLayout> mAvatarListView = new ArrayList<LinearLayout>();
    private List<RelativeLayout> mCommentListView = new ArrayList<RelativeLayout>();
    private List<RoundAngleImageView> mGuessLikeImgView = new ArrayList<RoundAngleImageView>();
    private List<CosplayInfo> mGuessLikeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = TimeUtils.getCurrentTimeInLong();
        long duration = startTime - mClickTime;
        HashMap<String, String> map_value = new HashMap<String, String>();
        map_value.put("type", UmengConstants.T_COSPLAY_DETAIL_JUMP);
        EventBus.getDefault().register(this);

        onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_JUMP, map_value, (int) duration);
    }

    @Override
    protected String getCacheKey() {
        return "cosplay_detail_" + mUcid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        outAty = (DetailActivity) getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);

        getActivity().registerReceiver(mReceiver, filter);
        registed = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null && registed) {
            try {
                getActivity().unregisterReceiver(mReceiver);
                registed = false;
            } catch (Exception e) {
                MyLog.e(e);
            }
        }
        stopPlayAudio();

        EventBus.getDefault().unregist(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dc_cosplay_detail;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.COSPLAY_DETAIL_FRAGMENT); //统计页面
        DiscoveryManager.getInstance().setCommentFlag(DiscoveryManager.COMMENT_FROM_COSPLAY_DETAIL);

        if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
            if (Constants.INTENT_ACTION_USER_CHANGE.equals(RECEIVE_MSG) ||
                    Constants.INTENT_ACTION_LOGOUT.equals(RECEIVE_MSG)) {
                sendRequestDataForNet();
            }
            RECEIVE_MSG = "";
        }

        if (DiscoveryPreference.getInstance().getShareToWXFlag() == 1) {
            DiscoveryPreference.getInstance().setShareToWXFlag(0);
            MineManager.getInstance().callShareServer(mUcid, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.COSPLAY_DETAIL_FRAGMENT);

        MyLog.i("onPause is coming....");
        stopPlayAudio();
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        titleBar.setLeftBtnOnclickListener(v -> {
            outAty.finish();
        });

        titleBar.setRightBtnOnclickListener(v -> {
            handleShare();
        });
        FrameLayout.LayoutParams cosImageParams = (FrameLayout.LayoutParams) mIvCosplay.getLayoutParams();
        LinearLayout.LayoutParams cosImageContainerParams = (LinearLayout.LayoutParams) mFlImage.getLayoutParams();
        cosImageParams.height = cosImageParams.width =
                cosImageContainerParams.height = cosImageContainerParams.width
                        = (int) TDevice.getScreenWidth();

        initCommentWindow();

        scrollView.setOnScrollChangedListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mUcid = getActivity().getIntent().getStringExtra(DiscoveryConstants.UCID);
        mFrom = getActivity().getIntent().getStringExtra(DiscoveryConstants.FROM);
        MyLog.i("ucid = " + mUcid + ", from = " + mFrom);
        if (TextUtils.isEmpty(mUcid)) {
            return;
        }
        mCosplayInfo = (CosplayInfo) getActivity().getIntent().getSerializableExtra(DiscoveryConstants.COSPLAY_INFO);
        if (mCosplayInfo != null && mCosplayInfo.getPicture() != null) {
            ImageLoaderUtils.displayHttpImage(mCosplayInfo.getPicture().getUri(), mIvCosplay, null, true, new Callback() {
                @Override
                public void onStart() {
                    displayStartTime = TimeUtils.getCurrentTimeInLong();
                }

                @Override
                public void onFinish(int result) {
                    switch (result) {
                        case -1:
                            long displayCancel = TimeUtils.getCurrentTimeInLong() - displayStartTime;
                            HashMap<String, String> displayCancelMap = new HashMap<String, String>();
                            displayCancelMap.put("type", UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_CANCEL);
                            onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_CANCEL, displayCancelMap, (int) displayCancel);
                            break;
                        case 0:
                            long displayFailed = TimeUtils.getCurrentTimeInLong() - displayStartTime;
                            HashMap<String, String> displayFailedTime = new HashMap<String, String>();
                            displayFailedTime.put("type", UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_FAILED);
                            onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_FAILED, displayFailedTime, (int) displayFailed);
                            break;
                        case 1:
                            long displayComplete = TimeUtils.getCurrentTimeInLong() - displayStartTime;
                            HashMap<String, String> displayCompleteTime = new HashMap<String, String>();
                            displayCompleteTime.put("type", UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_COMPLETE);
                            onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_COMPLETE, displayCompleteTime, (int) displayComplete);
                            break;
                    }
                }
            });
        }

        if (StringUtil.isFromMission(mFrom)
                && MissionPreference.getInstance().getShareGuide() == 0) {
            showGuide();
        }
    }

    private void initCommentWindow() {

        if (outAty.emojiFragment.getEmjlistener() == null) {
            outAty.emojiFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
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
//                    currentFragment.onClickSendButton(v.getRemote());
                }
            });
        }
        if (outAty.emojiFragment.getListener() == null) {
            outAty.emojiFragment.setListener(new OnSendClickListener() {
                @Override
                public void onClickSendButton(String str) {
                    MyLog.i("MoinEmoji 你点击了发送按钮:" + str.toString());
//                    prepareSendMessage(1, str.toString());
                }

                @Override
                public void onClickFlagButton() {

                }
            });
        }
    }


    @Override
    protected void sendRequestDataForNet() {
        MyLog.i("sendRequestDataForNet mUcid=" + mUcid);
        getDataStartTime = TimeUtils.getCurrentTimeInLong();
        DiscoveryApi.getCosplay(mUcid, 1, mDetailHeandler);
    }

    @Override
    protected void onGetDataSuccess() {
        if (getDataStartTime >= 0) {
            long getDataTime = TimeUtils.getCurrentTimeInLong() - getDataStartTime;
            HashMap<String, String> displayCompleteTime = new HashMap<String, String>();
            displayCompleteTime.put("type", UmengConstants.T_COSPLAY_DETAIL_GETDATA);
            onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_GETDATA, displayCompleteTime, (int) getDataTime);
        }
    }

    @Override
    protected CosplayInfo parseData(InputStream is) {
        String s = null;
        try {
            s = XmlUtils.inputStream2String(is);
            if (s == null) return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        CosplayInfo cos = XmlUtils.JsontoBean(CosplayInfoDetail.class, s).getCosplay();
        GetCosplayResult result = XmlUtils.JsontoBean(GetCosplayResult.class, s);
        CosplayInfo cos = null;
//        MyLog.i("cos=" + cos.toString());
//        mCosplayInfo = cos;

        if (result != null) {
            if (result.getCosplay() != null) {
                cos = result.getCosplay();
                mCosplayInfo = cos;
                mGuessLikeList = result.getCosplayList();
            } else if (result.getError() == ErrorCode.ERROR_COSPLAY_DELETED) {
                //todo 如果图片已经被删除(-20),记下状态,返回一个new的对象,防止父类设位置网络错误的状态
                mStatus = ErrorCode.ERROR_COSPLAY_DELETED;
                return new CosplayInfo();
            }
        } else {
            return null;
        }

        return cos;
    }

    @Override
    protected String getWebViewBody(CosplayInfo detail) {
//        StringBuffer body = new StringBuffer();
//        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);
//        body.append(ThemeSwitchUtils.getWebViewBodyString());
//        // 添加title
//        body.append(String.format("<div class='title'>%s</div>", mDetail.getTitle()));
//        // 添加作者和时间
//        String time = StringUtils.friendly_time(mDetail.getPubDate());
//        String author = String.format("<a class='author' href='http://my.oschina.net/u/%s'>%s</a>", mDetail.getAuthorId(), mDetail.getAuthor());
//        body.append(String.format("<div class='authortime'>%s&nbsp;&nbsp;&nbsp;&nbsp;%s</div>", author, time));
//        // 添加图片点击放大支持
//        body.append(UIHelper.setHtmlCotentSupportImagePreview(mDetail.getBody()));
        // 封尾
//        body.append("</div></body>");
//        return body.toString();
        return "";
    }

    @Override
    protected void executeOnLoadDataSuccess(CosplayInfo detail) {
        super.executeOnLoadDataSuccess(detail);
        if (detail == null) {
            titleBar.hideRightBtn();
            return;
        }
        mCosplayInfo = detail;

        if (mStatus == ErrorCode.ERROR_COSPLAY_DELETED) {
            mEmptyLayout.setErrorType(EmptyLayout.IMG_DELETE);
            titleBar.hideRightBtn();
            return;
        }
        // -------------作者头像,名称和发布时间
        if (mCosplayInfo.getAuthor() != null) {
            if (mCosplayInfo.getAuthor().getAvatar() != null) {
                mIvAvatar.setAvatarUrl(mCosplayInfo.getAuthor().getAvatar().getUri());
            } else {
                mIvAvatar.setAvatarUrl(null);
            }
            mIvAvatar.setUserInfo(mCosplayInfo.getAuthor().getUId(), mCosplayInfo.getAuthor().getUsername());
            mTvName.setText(mCosplayInfo.getAuthor().getUsername());
            View.OnClickListener userCenterListener = v -> {
                //TODO 进入用户中心
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(mCosplayInfo.getAuthor().getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(outAty, 0);
                } else {
                    UIHelper.showUserCenter(outAty, mCosplayInfo.getAuthor().getUId());
                }
            };
            mLlAuthor.setOnClickListener(userCenterListener);
            mIvAvatar.setOnClickListener(userCenterListener);
            mTvName.setOnClickListener(userCenterListener);
            follow.init(mCosplayInfo.getAuthor(), mCosplayInfo.getAuthor().getRelation(), UserDefineConstants.FOLLOW_COSPLAY);
        }
        mTvTime.setText(StringUtil.humanDate(mCosplayInfo.getCreatedAt(), StringUtil.TIME_PATTERN));

        // -------------发布的说明
        mCosplayInfo.setContentView(getActivity(), mTvContent, false, false);

        // -------------发布的大咖秀
        if (!CameraManager.getInst().isCosplayUploaded(mCosplayInfo, false)) {
            MyLog.i("!isCosplayUploaded().....本地图片");
            File pic = new File(CameraManager.getInst().getCosplayElementPath(mCosplayInfo.getUcid(), 1));
            if (pic != null && pic.exists()) {
                ImageLoaderUtils.displayLocalImage(pic.getAbsolutePath(), mIvCosplay, null);
            }
        } else {
            if (mCosplayInfo.getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(mCosplayInfo.getPicture().getUri(), mIvCosplay, null, true, new Callback() {
                    @Override
                    public void onStart() {
                        displayStartTime = TimeUtils.getCurrentTimeInLong();
                    }

                    @Override
                    public void onFinish(int result) {
                        MyLog.i("displayHttpImage callback.onFinish... result="  + result);
                        switch (result) {
                            case -1:
                                long displayCancel = TimeUtils.getCurrentTimeInLong() - displayStartTime;
                                HashMap<String, String> displayCancelMap = new HashMap<String, String>();
                                displayCancelMap.put("type", UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_CANCEL);
                                onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_CANCEL, displayCancelMap, (int) displayCancel);
                                break;
                            case 0:
                                long displayFailed = TimeUtils.getCurrentTimeInLong() - displayStartTime;
                                HashMap<String, String> displayFailedTime = new HashMap<String, String>();
                                displayFailedTime.put("type", UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_FAILED);
                                onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_FAILED, displayFailedTime, (int) displayFailed);
                                break;
                            case 1:
                                long displayComplete = TimeUtils.getCurrentTimeInLong() - displayStartTime;
                                HashMap<String, String> displayCompleteTime = new HashMap<String, String>();
                                displayCompleteTime.put("type", UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_COMPLETE);
                                onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL_SHOWCOSPLAY_COMPLETE, displayCompleteTime, (int) displayComplete);
                                break;
                        }
                    }
                });
            }
        }

        //todo 声音设置要放到合适的地方,一般是在图片load完成后
        setAudio();

        mIvCosplay.setOnClickListener(v -> {
            //进入大图模式
            if (CameraManager.getInst().isFileUploadFailed(mCosplayInfo.getUcid()) != -1) {
                AppContext.showToast(R.string.cosplay_not_upload_faile);
                return;
            }

            if (mCosplayInfo.getPicture() != null) {
                MyLog.i("mCosplayInfo.getPicture().getUri()=" + mCosplayInfo.getPicture().getUri());
                UIHelper.showImagePreview(v.getContext(), new String[]{mCosplayInfo.getPicture().getUri()}, true);
            } else {
                MyLog.i("mCosplayInfo.getPicture().getUri()== null");
            }
        });

        // -------------三个按钮
        setLikeState(mCosplayInfo.getIsLike() == 1, false);

        //---------赞
        View.OnClickListener likeListener = v -> {
            handleLikeOrNot();
            //点赞评论分享等都要停止播放音频
            stopPlayAudio();
        };
        mLlLikeBtn.setOnClickListener(likeListener);
        mLlLikeBtn.setContent(mCosplayInfo);


        setCosplayInfo();

        if ((mCosplayInfo.getTags() == null || mCosplayInfo.getTags().isEmpty())) {
            mLlTags.setVisibility(View.GONE);
        } else {
            mLlTags.setVisibility(View.VISIBLE);

            mFlTags.removeAllViews();
            FlowLayout.LayoutParams tagParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tagParams.setMargins(0, 0, 14, 14);

            if (mCosplayInfo.getTags() != null && !mCosplayInfo.getTags().isEmpty()) {
                for (TagInfo tag : mCosplayInfo.getTags()) {
                    if (tag == null || TextUtils.isEmpty(tag.getType())) continue;
                    TextView text = new TextView(outAty);
                    text.setText(tag.getName());
                    text.setTextSize(12);
                    text.setTextColor(getResources().getColor(R.color.date_picker_text_normal));
                    text.setBackgroundResource(R.drawable.shape_cosplay_detail_topic);
                    text.setGravity(Gravity.CENTER);
                    text.setSingleLine();
                    text.setPadding(30, 10, 30, 10);
                    text.setOnClickListener(v -> {
                        // TODO 跳转到标签详情列表
                        MyLog.i("跳转到IP标签详情列表:" + text);
                        UIHelper.showTopicDetail(outAty, tag.getName(), tag.getType(), tag.getTagId(), 0);
                    });
                    mFlTags.addView(text, tagParams);
                }
            }
        }

        setLikeUser();
        setForward();
        setComments();
        setGuessLikeCosplay();
        long duration = TimeUtils.getCurrentTimeInLong() - mClickTime; //开发者需要自己计算时长
        HashMap<String, String> showTime = new HashMap<String, String>();
        showTime.put("type", UmengConstants.T_COSPLAY_DETAIL);
        onEvent(getActivity(), UmengConstants.T_COSPLAY_DETAIL, showTime, (int) duration);

        // todo
        //如果是由关注/发现的评论按钮跳转进来,暂时往下滚动半张图片的高度,以后需要优化
        if (("comment").equalsIgnoreCase(mFrom)) {
            scrollTo(0, (int) TDevice.getScreenWidth() / 2);
        }
    }

    // 将页面滚动到具体位置
    private void scrollTo(int x, int y) {
        scrollView.scrollTo(x, y);
    }

    //如果有声音贴纸,在贴纸的位置加上播放的动画
    private boolean mPlayingAudio = false;
    private AnimationDrawable animationDrawable;
    private StickerAudioInfo mAudioInfo;
    private void setAudio() {
        if (mCosplayInfo.getAudio() != null && mCosplayInfo.getAudio().size() > 0) {
            mAudioInfo = mCosplayInfo.getAudio().get(0);
            if (mAudioInfo != null) {
                //middlex,middley是中心点位置
                int middlex = (int)(((double)mAudioInfo.getX() / 10000) * TDevice.getScreenWidth());
                int middley = (int)(((double)mAudioInfo.getY() / 10000) * TDevice.getScreenWidth());
                int width = (int)(((double)mAudioInfo.getWidth() / 10000) * TDevice.getScreenWidth());
                int height = (int)(((double)mAudioInfo.getHeight() / 10000) * TDevice.getScreenWidth());

                int x = middlex - width/2;
                int y = middley- height/2;
                MyLog.i("mIvAudioPlay set postion: x=" + x + ", y =" + y + ", width=" + width + ", height=" + height);

                //设置闪烁的圆点位置
                mIvAudioCircle.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams paraCircle = (ViewGroup.MarginLayoutParams)mIvAudioCircle.getLayoutParams();
                int leftMargin = 0;
                if (mAudioInfo.getMirrorH()) {
                    leftMargin = width + x + (int)TDevice.dpToPixel(3f);
                } else {
                    leftMargin = x - (int)TDevice.dpToPixel(22f);
                }
                paraCircle.setMargins(leftMargin, y + (int) TDevice.dpToPixel(4.5f), 0, 0);
                mIvAudioCircle.setLayoutParams(paraCircle);
                AnimationDrawable animationDrawable = (AnimationDrawable)mIvAudioCircleAnim.getDrawable();
                animationDrawable.start();

                //设置贴纸上面的透明框位置,也就是贴纸区域
                mLlAudioPlay.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams para = (ViewGroup.MarginLayoutParams)mLlAudioPlay.getLayoutParams();
                para.setMargins(x, y, 0, 0);
                mLlAudioPlay.setLayoutParams(para);

                //设置播放喇叭的动画位置及图片
                ViewGroup.MarginLayoutParams paraImg = (ViewGroup.MarginLayoutParams)mIvAudioPlay.getLayoutParams();
                if (mAudioInfo.getMirrorH()) {
                    leftMargin = width - (int) TDevice.dpToPixel(10f) - mIvAudioPlay.getWidth();
                    mIvAudioPlay.setImageResource(R.drawable.horn_white_bar_6);
                } else {
                    leftMargin = (int)TDevice.dpToPixel(10f);
                    mIvAudioPlay.setImageResource(R.drawable.horn_white_bar_3);
                }
                int topMargin = (height-mIvAudioPlay.getHeight())/2;
                paraImg.setMargins(leftMargin, topMargin, 0, 0);
                mIvAudioPlay.setLayoutParams(paraImg);
                mLlAudioPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAudioPlayStatus();
                    }
                });
            }
        }
    }

    //设置播放的状态
    private void setAudioPlayStatus() {
//        mPlayingAudio = !mPlayingAudio;
        if (!mPlayingAudio) {
            mPlayingAudio = true;
            if (mAudioInfo.getMirrorH()) {
                mIvAudioPlay.setImageResource(R.drawable.sticker_audio_cosplay_right);
            } else {
                mIvAudioPlay.setImageResource(R.drawable.sticker_audio_cosplay_left);
            }
            animationDrawable = (AnimationDrawable) mIvAudioPlay.getDrawable();
            playAudio(mAudioInfo);
            if (animationDrawable != null) {
                animationDrawable.start();
            }
        } else {
            stopPlayAudio();
        }
    }

    private int mEmojiFragmentHeight;
    private long lastTime = 0;

    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        outAty.emojiFragment.hideAllKeyBoard();

        long current = System.currentTimeMillis();
        boolean delta = (current - lastTime) > 100;

        if (mEmojiFragmentHeight == 0) {
            mEmojiFragmentHeight = getResources().getDimensionPixelSize(R.dimen.pic_comment_fg_height) + 3;
        }
        if (delta) {
            lastTime = current;
            ViewPropertyAnimator.animate(outAty.emojiFragment.getRootView()).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200L).translationY((float) mEmojiFragmentHeight);
        }

        if (mTimer != null){
            if (mTimerTask != null){
                mTimerTask.cancel();
            }

            mTimerTask = new MyTimerTask();
            mTimer.schedule(mTimerTask, 100);
        }
    }

    Timer mTimer = new Timer(true);
    private MyTimerTask mTimerTask;
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewPropertyAnimator.animate(outAty.emojiFragment.getRootView()).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200L).translationY(0);
                }
            });
        }
    }

    interface OnCallback {
        void onSuccess(Object object);
        void onFailed(Object object);
    }
    private void playAudio(StickerAudioInfo audioInfo) {
        if (audioInfo == null) return;
        //todo 暂时把音频文件放到asserts,要考虑更好的方法
        if (!TextUtils.isEmpty(audioInfo.getUri())) {
            String input = audioInfo.getUri();
            String audio = BitmapUtil.BITMAP_AUDIO + input.substring(input.lastIndexOf("/") + 1);
            MyLog.i("audioInfo.setUri =" + audioInfo.getUri());
            File file = new File(audio);
            if (!file.exists()) {
                //本地找不到就先下载
                downloadAudio(input, audio, new OnCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playAudio(audio);
                            }
                        });
                    }

                    @Override
                    public void onFailed(Object object) {

                    }
                });
            } else {
                playAudio(audio);
            }
        }
    }

    private void downloadAudio(String url, String local, OnCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = HttpUtil.download(url, local);
                if (callback != null && result) {
                    callback.onSuccess(null);
                }
            }
        }).start();
    }

    private void playAudio(String path) {
        MyAudioPlayer.getInstance().playAudio(path, new MyAudioPlayer.PlayEndCallback() {
            @Override
            public void onCompleted() {
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
                mPlayingAudio = false;
                setStopPalyDrawable(mAudioInfo);
            }
        });
    }

    private void stopPlayAudio() {
        if (mAudioInfo == null) return;
        mPlayingAudio = false;
        MyAudioPlayer.getInstance().stopPlay();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        setStopPalyDrawable(mAudioInfo);
    }

    private void setStopPalyDrawable(StickerAudioInfo audioInfo) {
        if (audioInfo == null) return;
        if (audioInfo.getMirrorH()) {
            mIvAudioPlay.setImageResource(R.drawable.horn_white_bar_6);
        } else {
            mIvAudioPlay.setImageResource(R.drawable.horn_white_bar_3);
        }
    }

    private void setCosplayInfo() {
        //---------浏览
        mTvViewerNum.setText(String.valueOf(mCosplayInfo.getReadNum()));
        mLlViewUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showUserList(getActivity(), mCosplayInfo.getUcid(), 2);
            }
        });

        //---------评论
        View.OnClickListener cmtListener = v -> {
            //点赞评论分享等都要停止播放音频
            stopPlayAudio();
            prepareComment(null);
        };
        mLlCommentBtn.setOnClickListener(cmtListener);
        mIvComment.setOnClickListener(cmtListener);
        mTvCommentNum.setText(String.valueOf(mCosplayInfo.getCommentNum()));

        //---------转发
        mTvForward.setText(String.valueOf(mCosplayInfo.getChildrenNum()));
        View.OnClickListener forwardListener = v -> {
            // TODO 跳到改图转发界面(根据权限)
            boolean uploaded = CameraManager.getInst().isCosplayUploaded(mCosplayInfo, false);
            if (!uploaded) {
                AppContext.showToast(R.string.cosplay_not_upload_faile);
                return;
            }

            int writeAuth = mCosplayInfo.getWriteAuth();
            MyLog.i("跳到改图转发界面(根据权限)=" + writeAuth);
            UIHelper.editCosplay(outAty, mCosplayInfo, TimeUtils.getCurrentTimeInLong());
        };
        mLlForwardBtn.setOnClickListener(forwardListener);
        mIvForward.setOnClickListener(forwardListener);
    }

    UserInfo mReplyUser;
    private Handler mCallbackHandler = new Handler();
    private void prepareComment(UserInfo replyUser) {
        mReplyUser = replyUser;
        if (isHasNetAndLogin()) {
            initCommentWindow();

            mCallbackHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (replyUser == null) {
                        outAty.emojiFragment.setHint(R.string.comment_hint);
                    } else {
                        outAty.emojiFragment.setHint(String.format(getString(R.string.reply_referral_nickname), replyUser.getUsername()));
                    }
                    outAty.emojiFragment.showSoftKeyboard();
                }
            }, 200);

        }
    }

    // 点赞
    private void handleLikeOrNot() {
        if (AppContext.getInstance().isLogin() && mCosplayInfo != null) {
            boolean needRefresh = false;
            boolean hasNULLuser = false;

            List<UserInfo> likeList = mCosplayInfo.getLikeUsers();
            if (likeList == null) {
                likeList = new ArrayList<UserInfo>();
                likeList.add(0, AppContext.getInstance().getUserInfo());
                needRefresh = true;
            } else {
                boolean exist = false;
                for (int i = likeList.size() - 1; i >= 0 ; i--) {
                    UserInfo user = likeList.get(i);
                    if (user == null) {
                        likeList.remove(i);
                        hasNULLuser = true;
                        continue;
                    }
                    if (ClientInfo.getUID().equalsIgnoreCase(StringUtil.nullToEmpty(user.getUId()))) {
                        return;
                    }
                }

                if (!exist) {
                    likeList.add(0, AppContext.getInstance().getUserInfo());
                    needRefresh = true;
                }
            }

            if (needRefresh || hasNULLuser) {
                mCosplayInfo.setLikeUsers(likeList);
                setLikeUser();
            }
        } else{
//            AppContext.showToast("先登录再转改~");
        }
    }


    private void setLikeState(boolean liked, boolean anim) {


    }

    private void setLikeUser() {
        if (mCosplayInfo == null
                || mCosplayInfo.getLikeUsers() == null || mCosplayInfo.getLikeUsers().isEmpty()) {
            mLlLike.setVisibility(View.GONE);
            return;
        }
        mLlLike.setVisibility(View.VISIBLE);
        setLikeUserAvatar();
        setLikeUsersString();
    }

    private void setLikeUsersString() {
        if (mCosplayInfo.getLikeUsers() != null
                && mCosplayInfo.getLikeUsers().size() >= showLikeUserCount) {
//            mTvLikeCount.setVisibility(View.VISIBLE);
            mIvMoreUser.setVisibility(View.VISIBLE);
//            mTvLikeCount.setText(String.format(getString(R.string.cosplay_like_user), mCosplayInfo.getLikeUsers().size()));
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showCosplayLikeComment(outAty, mCosplayInfo.getUcid(), 0);
                }
            };
//            mTvLikeCount.setOnClickListener(listener);
            mIvMoreUser.setOnClickListener(listener);
        } else {
            mTvLikeCount.setVisibility(View.GONE);
            mIvMoreUser.setVisibility(View.GONE);
        }
    }

    private void setLikeUserAvatar() {
        // 构造多个超链接的html, 通过选中的位置来获取用户名
        if (mCosplayInfo.getLikeUsers() != null
                && mCosplayInfo.getLikeUsers().size() > 0) {
            mAvatarListView.add(mAvatar0);
            mAvatarListView.add(mAvatar1);
            mAvatarListView.add(mAvatar2);
            mAvatarListView.add(mAvatar3);
            mAvatarListView.add(mAvatar4);
            mAvatarListView.add(mAvatar5);
            mAvatarListView.add(mAvatar6);
            mAvatarListView.add(mAvatar7);
            mAvatarListView.add(mAvatar8);
            mAvatarListView.add(mAvatar9);

            int userCount = mCosplayInfo.getLikeUsers().size();
            if (userCount > 0) {
                int index = 0;
                for (int i = 0; i < mCosplayInfo.getLikeUsers().size(); i++) {
                    if (index >= showLikeUserCount) break;
                    UserInfo user = mCosplayInfo.getLikeUsers().get(i);
                    if (user == null || user.getAvatar() == null
                            || TextUtils.isEmpty(user.getAvatar().getUri())) {
                        MyLog.i("user is empty = " + i);
                        continue;
                    }

                    fillAvatar(index, user);
                    index++;
                }
            }

            ViewGroup.LayoutParams para = mIvMoreUser.getLayoutParams();
            para.width = SW;
            para.height = SW;
            mIvMoreUser.setLayoutParams(para);
        }
    }

    private void fillAvatar(int i, UserInfo user) {
        MyLog.i("fillAvatar = " + i);
        AvatarView avatarView;
        mAvatarListView.get(i).setVisibility(View.VISIBLE);
        avatarView = (AvatarView) mAvatarListView.get(i).findViewById(R.id.avatar);
        setAvatarView(avatarView, user);
    }

    private void setAvatarView(AvatarView avatarView, UserInfo user) {
        if (user == null) return;
        ViewGroup.LayoutParams para = avatarView.getLayoutParams();
        para.width = SW;
        para.height = SW;
        avatarView.setLayoutParams(para);
        avatarView.setUserInfo(user.getUId(), user.getUsername());
        avatarView.setAvatarUrl(user.getAvatar() == null ? null : user.getAvatar().getUri());

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(user.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(getActivity(), 0);
                } else {
                    UIHelper.showUserCenter(getActivity(), user.getUId());
                }
            }
        });
    }

    private void setForward() {
        if (mCosplayInfo.getChildrenNum() > 0) {
            mLlForward.setVisibility(View.VISIBLE);
            mTvForwardInfo.setText(String.format(getString(R.string.cosplay_forward_info), mCosplayInfo.getChildrenNum()));
            mLlForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CosplayEvolutionActivity.showCosplayEvolution(outAty, mCosplayInfo.getUcid());
                }
            });
        } else {
            mLlForward.setVisibility(View.GONE);
        }
    }

    private void setComments() {
        mTvCommentNum.setText(String.valueOf(mCosplayInfo.getCommentNum()));
        if (mCosplayInfo == null || mCosplayInfo.getComments() == null || mCosplayInfo.getComments().isEmpty()) {
            mLlComments.setVisibility(View.GONE);
            mBtnShowAllComments.setVisibility(View.GONE);
            return;
        } else {
            mLlComments.setVisibility(View.VISIBLE);

            int commentCount = mCosplayInfo.getCommentNum();
            if (commentCount > 0) {
                mCommentListView.clear();
                mCommentListView.add(mRlComment0);
                mCommentListView.add(mRlComment1);
                mCommentListView.add(mRlComment2);
                mCommentListView.add(mRlComment3);
                mCommentListView.add(mRlComment4);
                mCommentListView.add(mRlComment5);
                mCommentListView.add(mRlComment6);
                mCommentListView.add(mRlComment7);
                mCommentListView.add(mRlComment8);
                mCommentListView.add(mRlComment9);
                mCommentListView.add(mRlComment10);
                mCommentListView.add(mRlComment11);
                mCommentListView.add(mRlComment12);
                mCommentListView.add(mRlComment13);
                mCommentListView.add(mRlComment14);
                mCommentListView.add(mRlComment15);
                mCommentListView.add(mRlComment16);
                mCommentListView.add(mRlComment17);
                mCommentListView.add(mRlComment18);
                mCommentListView.add(mRlComment19);

                for (RelativeLayout commentView : mCommentListView) {
                    commentView.setVisibility(View.GONE);
                }
                mTvCommentInfo.setText(String.format(getString(R.string.cosplay_comment_info), commentCount));
                mLlComments.setVisibility(View.VISIBLE);
                int index = 0;
                for (int i = 0; i < mCosplayInfo.getComments().size(); i++) {
                    if (index >= showCommentCount) break;
                    CommentInfo comment = mCosplayInfo.getComments().get(i);
                    if (comment == null) {
                        continue;
                    }

                    setCommentInfo(index);
                    index++;
                }
                if (mCosplayInfo.getCommentNum() > showCommentCount) {
                    mBtnShowAllComments.setVisibility(View.VISIBLE);
                    mBtnShowAllComments.setVisibility(mCosplayInfo.getCommentNum() > showCommentCount ? View.VISIBLE : View.GONE);
                    mBtnShowAllComments.setText("查看全部" + mCosplayInfo.getCommentNum() + "条评论");
                    mBtnShowAllComments.setOnClickListener(v -> {
                        // TODO 跳到该图片的全部评论列表
                        MyLog.i("跳到该图片的全部评论列表");
                        UIHelper.showCosplayLikeComment(getActivity(), mCosplayInfo.getUcid(), 1);
                    });
                } else {
                    mBtnShowAllComments.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setGuessLikeCosplay() {
        if (mGuessLikeList == null || mGuessLikeList.size() == 0) {
            return;
        }
        mGuessLikeImgView.add(mLikeImg0);
        mGuessLikeImgView.add(mLikeImg1);
        mGuessLikeImgView.add(mLikeImg2);
        mGuessLikeImgView.add(mLikeImg3);
        int index = 0;
        MyLog.i("mGuessLikeList.size=" + mGuessLikeList.size());
        for (int i = 0; i < mGuessLikeList.size(); i++) {
            if (index >= showGuessLikeCount) break;
            CosplayInfo cosplay = mGuessLikeList.get(i);
            if (cosplay == null || cosplay.getPicture() == null) {
                continue;
            }

            ImageView imageView = mGuessLikeImgView.get(index);
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = IMG_WIDTH;
                params.height = IMG_WIDTH;
                imageView.setLayoutParams(params);
                ImageLoaderUtils.displayHttpImage(cosplay.getPicture().getUri(),
                        imageView, null);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIHelper.showDiscoveryCosplayDetail(outAty, null, cosplay.getUcid(),
                                CosplayDetailFragment.class.getSimpleName(), TimeUtils.getCurrentTimeInLong());
                    }
                });
                index++;
            }
        }
    }

    private void setCommentInfo(int i) {
        mCommentListView.get(i).setVisibility(View.VISIBLE);
        mCosplayInfo.getComments().get(i).setCommentUser(getActivity(), mCosplayInfo, mCommentListView.get(i), new CommentOnClickListener() {
            @Override
            public void onDeleteClick(Object object) {
                MyLog.i("ljc: 删除评论succeed!onDeleteClick..i=" + i + ", mCosplayInfo.getComments().size=" + mCosplayInfo.getComments().size());
                mCosplayInfo.getComments().remove(i);
                MyLog.i("ljc: 删除评论succeed! after remove, mCosplayInfo.getComments().size=" + mCosplayInfo.getComments().size());
                mCosplayInfo.setCommentNum(mCosplayInfo.getCommentNum() - 1);
                setComments();
            }

            @Override
            public void onReplyClick(Object object) {
                UserInfo replyUser = (UserInfo) object;
                prepareComment(replyUser);
            }

            @Override
            public void onCopyClick(Object object) {
                StringUtil.copyToClipboard((String) object);
            }
        });
    }

    @Override
    protected void showCommentView() {
        if (mDetail != null) {
            // 显示评论列表 本图片详情是没有评论的
//            UIHelper.showBlogComment(getActivity(), mId,
//                    mDetail.getAuthorId());
        }
    }

    @Override
    protected int getCommentType() {
        return CommentInfoList.CATALOG_COMMENT;
    }

    @Override
    protected String getShareTitle() {
        return null;
    }

    @Override
    protected String getShareContent() {
        return null;
    }

    @Override
    protected String getShareUrl() {
        return null;
    }

    protected String getShareImage() {
        if (mDetail == null || mDetail.getPicture() == null) return null;
        return ImageLoaderUtils.defineUrlByImageSize(mIvCosplay, mDetail.getPicture().getUri());
    }

    //分享
    public void handleShare() {
        if (mDetail == null) return;
        //点赞评论分享等都要停止播放音频
        stopPlayAudio();

        final ShareDialog dialog = new ShareDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setShareInfo("", null, getShareImage(), null);
        dialog.setCosplayUcid(mUcid);
        if (CameraManager.getInst().isMyCosplay(mCosplayInfo)) {
            dialog.setDeleteEnable();
            dialog.setDeleteOnClick(new DeleteOnClickListener() {
                @Override
                public void onClick(Object object) {
                    DiscoveryManager.getInstance().deleteCosplay(mUcid, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("Delete cosplay succeed!");
                            //发送删除大咖秀的事件
                            EventBus.getDefault().post(new DiscoveryManager.CosplayDeleteEvent(mUcid));

                            outAty.finish();
                        }

                        @Override
                        public void onErr(Object obj) {

                        }

                        @Override
                        public void onNoNetwork() {
                            AppContext.showToastShort(R.string.tip_network_error);
                        }
                    });
                }
            });
        }
        dialog.show();
    }


    @Override
    protected int getFavoriteTargetType() {
        return FavoriteList.TYPE_ALL;
    }

    @Override
    protected int getFavoriteState() {
        return mDetail.getIsLike();
    }

    @Override
    protected void updateFavoriteChanged(int newFavoritedState) {
        mDetail.setIsLike(newFavoritedState);
        saveCache(mDetail);
    }

    @Override
    protected int getCommentCount() {
        return mDetail.getCommentNum();
    }

    @Override
    public void onClickSendButton(String str) {
        MyLog.i("onClickSendButton [" + str.toString() + "]");
        sendComment(1, str);
    }

    /**
     * 发布评论
     *
     * @param type 发送类型 1:文本 2:图片 3:预置图片
     */
    public void sendComment(int type, final String content) {
        MyLog.i("MoinEmoji 你要发送的是:[" + content + "]");
        if (StringUtils.isBlank(content)) {
            if (type == 1) {
                AppContext.showToastShort(R.string.tip_comment_content_empty);
            }
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (!isNetOKetc()) {
            return;
        }

        outAty.emojiFragment.hideAllKeyBoard();
        // 发送文本
        if (type == 1) {
            outAty.emojiFragment.clean();
            sendContent(content, null);
        } else if (type == 3) {
            // 发送预制图片
            sendContent(null, content);
        } else if (type == 2) {
            // 发送图片
            MineManager.getInstance().uploadChatImage2Oss(content, new IListener() {
                @Override
                public void onSuccess(Object url) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendContent(null, (String) url);
                        }
                    });
                }

                @Override
                public void onErr(Object obj) {
//                    AppContext.showToastShort(getString(R.string.tip_message_public_faile) + "[" + obj + "]");
                }

                @Override
                public void onNoNetwork() {
                    AppContext.showToastShort(R.string.no_network);
                }
            });

        }
    }

    // TODO 发表评论
    public void sendContent(String content, String url) {
        UserInfo userInfo = AppContext.getInstance().getUserInfo();

        HashMap<String, String> map = new HashMap<String, String>();
//        map.put(UmengConstants.FROM, "图片详情页");
        String cosContent = StringUtil.nullToEmpty(mTvContent.getText().toString());
        if (cosContent.length() > 10) {
            cosContent = cosContent.substring(0, 10);
        }
        map.put(UmengConstants.ITEM_ID, mUcid + "_" + userInfo.getUsername() + "_" + cosContent);
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT_COSPLAY, map);
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT, map);


        showWaitDialog(R.string.progress_submit);

        DiscoveryManager.getInstance().commentCosplay(mUcid, null, content,
                mReplyUser == null ? null : mReplyUser.getUId(), url, new IListener2() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        hideWaitDialog();
                        AppContext.showToastShort(R.string.comment_publish_success);

                        List<CommentInfo> clist = mCosplayInfo.getComments();
                        if (clist == null) {
                            clist = new ArrayList<CommentInfo>();
                        }
                        CommentInfo myComment = new CommentInfo();
                        myComment.setCid((String) obj);
                        myComment.setContent(content);
                        BaseImage picture = null;
                        if (!TextUtils.isEmpty(url)) {
                            picture = new BaseImage();
                            String newUrl = url;
                            if (!url.startsWith("http") && url.startsWith("/")) {
                                newUrl = AppConfig.getBaseImageUrl() + url;
                            }
                            picture.setUri(newUrl);
                            myComment.setPicture(picture);
                        }
                        myComment.setCreatedAt(System.currentTimeMillis());
                        userInfo.setRelation(4);
                        myComment.setAuthor(userInfo);
                        myComment.setReply(mReplyUser);
                        mReplyUser = null;
                        clist.add(0, myComment);
                        mCosplayInfo.setComments(clist);
                        mCosplayInfo.setCommentNum(mCosplayInfo.getCommentNum() + 1);

                        setComments();

                        scrollTo(0, (int) TDevice.getScreenWidth());

//                        if (mCommentDialog == null) {
//                            initCommentWindow();
//                        }
//                        mCommentDialog.clean();
                    }

                    @Override
                    public void onErr(Object obj) {
                        hideWaitDialog();
                        AppContext.showToastShort(R.string.comment_publish_faile);
                    }

                    @Override
                    public void onNoNetwork() {
                        hideWaitDialog();
                        AppContext.showToastShort(R.string.tip_network_error);
                    }
                });
    }

    private boolean isNetOKetc() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return false;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return false;
        }
        return true;
    }

    /**
     * 首次完成任务进入活动详情页面时需要弹出引导图
     */
    private void showGuide() {
        Dialog dialog = UIHelper.showShareMissionGuide(getActivity());
        //延时3秒钟关闭
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
        MissionPreference.getInstance().setShareGuide(1);
    }

    public static void onEvent(Context context, String id, HashMap<String, String> m, long value) {
        m.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, m);
    }

    // TODO 图片评论
    public void onEvent(MineManager.SelectPhotoEvent event) {
        if (event == null || StringUtil.isNullOrEmpty(event.getImagePath())) return;
        if (DiscoveryManager.getInstance().getCommentFlag()
                != DiscoveryManager.COMMENT_FROM_COSPLAY_DETAIL) return;

        String path = event.getImagePath();
        MyLog.i("收到选择照片事件.....path=" + path);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendComment(2, path);
            }
        });
    }
}

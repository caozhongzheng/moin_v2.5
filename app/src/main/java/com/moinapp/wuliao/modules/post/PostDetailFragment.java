package com.moinapp.wuliao.modules.post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.moinapp.wuliao.listener.DeleteOnClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.CommentCosplayAdapter;
import com.moinapp.wuliao.modules.discovery.adapter.FollowAdapter;
import com.moinapp.wuliao.modules.discovery.model.CommentInfo;
import com.moinapp.wuliao.modules.discovery.model.CommentInfoList;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.result.GetCosplayResult;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.DetailActivity;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.ui.PostReportDialog;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.moinapp.wuliao.widget.AudioPlayLayout;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;
import com.moinapp.wuliao.widget.LikeLayout;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * 帖子详情
 */
public class PostDetailFragment extends CommonDetailFragment<CosplayInfo> implements AbsListView.OnScrollListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(PostDetailFragment.class.getSimpleName());

    @InjectView(R.id.title_layout)
    CommonTitleBar titleBar;

    LinearLayout mLlAuthor;
    AvatarView mIvAvatar;
    TextView mTvName;
    TextView mTvTime;
    FollowView follow;

    TextView mTvContent;
    TextView mTvLable;
    AudioPlayLayout mPlayer;
    LinearLayout mLlPicture;

    LinearLayout mLlViewUserBtn;
    ImageView mIvViewer;
    TextView mTvViewerNum;
    LinearLayout mLlCommentBtn;
    ImageView mIvComment;
    TextView mTvCommentNum;
    LikeLayout mLikeLayout;

    LinearLayout mLlTags;
    FlowLayout mFlTags;

    LinearLayout mLlLike;

    LinearLayout mAvatar0;
    LinearLayout mAvatar1;
    LinearLayout mAvatar2;
    LinearLayout mAvatar3;
    LinearLayout mAvatar4;
    LinearLayout mAvatar5;
    LinearLayout mAvatar6;
    LinearLayout mAvatar7;
    LinearLayout mAvatar8;
    LinearLayout mAvatar9;
    TextView mTvLikeCount;
    ImageView mIvMoreUser;

    LinearLayout mLlComment;
    TextView mTvCommentCount;

    @InjectView(R.id.listview)
    protected ListView mListView;

    private View mHeaderView;

    private String mUcid;
    private CosplayInfo mCosplayInfo;
    //发布帖子后跳转到帖子详情时会传入一个CosplayInfo的对象,由图片和声音都是本地路径
    private CosplayInfo mLocalCosplayInfo;
    private String mFrom;
    private int mStatus;

    private List<CommentInfo> mCommentList;
    protected CommentCosplayAdapter mAdapter;
    private List<Bitmap> mBitmapList = new ArrayList<Bitmap>();

    private DetailActivity outAty;
    private long startTime, displayStartTime, getDataStartTime, mClickTime;
    private int showLikeUserCount = 10;
    private int SW = (int) TDevice.getScreenWidth() / (showLikeUserCount + 4);
    private static String RECEIVE_MSG = "";

    private boolean registed = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RECEIVE_MSG = intent.getAction();
        }
    };

    public PostDetailFragment() {
    }

    public PostDetailFragment(long clickTime) {
        this.mClickTime = clickTime;
    }

    private List<LinearLayout> mAvatarListView = new ArrayList<LinearLayout>();
    private List<RelativeLayout> mCommentListView = new ArrayList<RelativeLayout>();

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
        return "post_detail_" + mUcid;
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        if (mLocalCosplayInfo == null) {
            return true;
        } else {
            return false;
        }
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

        if (mLocalCosplayInfo != null) {
            List<BaseImage> list= mLocalCosplayInfo.getPictureList();
            if (list != null) {
                for (BaseImage img:list) {
                    MyLog.i("发完贴后图是= " + img.toString());
                }
            }
            mUcid = mLocalCosplayInfo.getUcid();
            executeOnLoadDataSuccess(mLocalCosplayInfo);
        }
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

        if (mBitmapList != null && mBitmapList.size() > 0) {
            for (Bitmap bitmap : mBitmapList) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    MyLog.i("PostDetailFrament.onDestoryView: bitmap.recycle()....");
                    bitmap.recycle();
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dc_post_detail;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.POST_DETAIL_FRAGMENT); //统计页面
        DiscoveryManager.getInstance().setCommentFlag(DiscoveryManager.COMMENT_FROM_COSPLAY_DETAIL);

        if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
            if (Constants.INTENT_ACTION_USER_CHANGE.equals(RECEIVE_MSG) ||
                    Constants.INTENT_ACTION_LOGOUT.equals(RECEIVE_MSG)) {
                sendRequestDataForNet();
            }
            RECEIVE_MSG = "";
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.POST_DETAIL_FRAGMENT);

        MyLog.i("onPause is coming....");
        stopPlayAudio();
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        View headerView = View.inflate(getActivity(), R.layout.layout_title_down_grey_area, null);
        mListView.addHeaderView(headerView);

        titleBar.setLeftBtnOnclickListener(v -> {
            outAty.finish();
        });

        titleBar.setRightBtnOnclickListener(v -> {
            handleShare();
        });
        initCommentWindow();
        mListView.setOnScrollListener(this);
//        scrollView.setOnScrollChangedListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mUcid = getActivity().getIntent().getStringExtra(DiscoveryConstants.UCID);
        mFrom = getActivity().getIntent().getStringExtra(DiscoveryConstants.FROM);
        mLocalCosplayInfo = (CosplayInfo) getActivity().getIntent().getSerializableExtra(DiscoveryConstants.COSPLAY_INFO);
        MyLog.i("ucid = " + mUcid + ", from = " + mFrom);
        if (TextUtils.isEmpty(mUcid)) {
            return;
        }
    }

    private void initCommentWindow() {
        if (outAty.emojiFragment.getEmjlistener() == null) {
            outAty.emojiFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
                @Override
                public void onDeleteButtonClick(View v) {
                    MyLog.i("MoinEmoji 你点击了删除按钮");
                }

                @Override
                public void onEmojiClick(Emojicon v) {
                    MyLog.i("MoinEmoji 你点击了表情按钮:" + v.toString());
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
        DiscoveryApi.getCosplay(mUcid, 0, mDetailHeandler);
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
        GetCosplayResult result = XmlUtils.JsontoBean(GetCosplayResult.class, s);
        CosplayInfo cos = null;

        if (result != null) {
            if (result.getCosplay() != null) {
                cos = result.getCosplay();
                mCosplayInfo = cos;
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
        if (detail == null) return;
        mCosplayInfo = detail;
        super.executeOnLoadDataSuccess(detail);

        if (mStatus == ErrorCode.ERROR_COSPLAY_DELETED) {
            mEmptyLayout.setErrorType(EmptyLayout.POST_DELETE);
            titleBar.hideRightBtn();
            outAty.emojiFragment.getRootView().setVisibility(View.GONE);
            return;
        }

        if (mHeaderView == null) {
            initHeaderView();
            mListView.addHeaderView(mHeaderView);
        }
        //作者信息
        setAuthor();

        // 类型和发布的说明
        setPostType();

        //浏览数评论数和点赞数
        setCosplayInfo();

        //来自哪个话题
        setTopic();

        //点赞用户
        setLikeUser();
        //评论信息
        setCommentListAdaptor();
        setComment();

        // todo
        //如果是由关注/发现的评论按钮跳转进来,暂时往下滚动半张图片的高度,以后需要优化
        if (("comment").equalsIgnoreCase(mFrom)) {
            scrollTo(0, (int) TDevice.getScreenWidth() / 2);
        }
    }

    // 将页面滚动到具体位置
    private void scrollTo(int x, int y) {
//        scrollView.scrollTo(x, y);
    }

    private void initHeaderView() {
        if (mHeaderView != null) return;
        View headerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.item_post_detail, null);

        if (headerView != null) {
            mLlAuthor = (LinearLayout) headerView.findViewById(R.id.ll_author);
            mIvAvatar = (AvatarView) headerView.findViewById(R.id.iv_avatar);
            mTvName = (TextView) headerView.findViewById(R.id.tv_name);
            mTvTime = (TextView) headerView.findViewById(R.id.tv_time);
            follow = (FollowView) headerView.findViewById(R.id.btn_follow);

            mTvLable = (TextView) headerView.findViewById(R.id.post_lable);
            mTvContent = (TextView) headerView.findViewById(R.id.tv_content);
            mLlPicture = (LinearLayout) headerView.findViewById(R.id.ll_picture);
            mPlayer = (AudioPlayLayout)headerView.findViewById(R.id.audio_play);

            mLlViewUserBtn = (LinearLayout) headerView.findViewById(R.id.ll_viewuser);
            mIvViewer = (ImageView) headerView.findViewById(R.id.iv_viewuser);
            mTvViewerNum = (TextView) headerView.findViewById(R.id.tv_viewnum);

            mLlCommentBtn = (LinearLayout) headerView.findViewById(R.id.ll_comment);
            mIvComment = (ImageView) headerView.findViewById(R.id.iv_comment);
            mTvCommentNum = (TextView) headerView.findViewById(R.id.tv_commentnum);

            mLikeLayout = (LikeLayout) headerView.findViewById(R.id.like_layout);

            mLlTags = (LinearLayout) headerView.findViewById(R.id.ll_tag);
            mFlTags = (FlowLayout) headerView.findViewById(R.id.fl_tag_container);

            mLlLike = (LinearLayout) headerView.findViewById(R.id.ll_like);

            mAvatar0 = (LinearLayout) headerView.findViewById(R.id.avatar0);
            mAvatar1 = (LinearLayout) headerView.findViewById(R.id.avatar1);
            mAvatar2 = (LinearLayout) headerView.findViewById(R.id.avatar2);
            mAvatar3 = (LinearLayout) headerView.findViewById(R.id.avatar3);
            mAvatar4 = (LinearLayout) headerView.findViewById(R.id.avatar4);
            mAvatar5 = (LinearLayout) headerView.findViewById(R.id.avatar5);
            mAvatar6 = (LinearLayout) headerView.findViewById(R.id.avatar6);
            mAvatar7 = (LinearLayout) headerView.findViewById(R.id.avatar7);
            mAvatar8 = (LinearLayout) headerView.findViewById(R.id.avatar8);
            mAvatar9 = (LinearLayout) headerView.findViewById(R.id.avatar9);

            mTvLikeCount = (TextView) headerView.findViewById(R.id.tv_more_like_user);
            mIvMoreUser = (ImageView) headerView.findViewById(R.id.iv_more_user);

            mLlComment = (LinearLayout) headerView.findViewById(R.id.ll_comments);
            mTvCommentCount = (TextView) headerView.findViewById(R.id.tv_comment_info);
        }
        mHeaderView = headerView;
    }

    //帖子类型信息
    private void setPostType() {
        String bbsName = null;
        if (mCosplayInfo.getTags() != null && !mCosplayInfo.getTags().isEmpty()) {
            HashMap<String, String> map = new HashMap<String, String>();
            String postContent = mTvContent.getText().toString();
            if (postContent.length() > 10) {
                postContent = postContent.substring(0, 10);
            }
            for (TagInfo tag : mCosplayInfo.getTags()) {
                if (StringUtil.isNullOrEmpty(tag.getBbsName())) {
                    continue;
                }
                bbsName = tag.getBbsName();
                MyLog.i("你的帖子是 [" + tag.getName() + "]");
                map.put(UmengConstants.ITEM_ID, tag.getName() + "_" + mUcid + "_" + postContent);
                if (tag.getBbsName().startsWith("官")) {
                    MobclickAgent.onEvent(getActivity(), UmengConstants.POST_CLICK_OFFICIAL, map);
                } else if (tag.getBbsName().startsWith("精")) {
                    MobclickAgent.onEvent(getActivity(), UmengConstants.POST_CLICK_JP, map);
                } else if (tag.getBbsName().startsWith("达")) {
                    MobclickAgent.onEvent(getActivity(), UmengConstants.POST_CLICK_DR, map);
                } else if (tag.getBbsName().startsWith("置顶")) {
                    MobclickAgent.onEvent(getActivity(), UmengConstants.POST_CLICK_ZD, map);
                } else {
                    MobclickAgent.onEvent(getActivity(), UmengConstants.POST_CLICK_NORMAL, map);
                }
            }
        }

        //设置精华/官方等内容
        setContent(bbsName);
        MyLog.i("帖子类型是 " + mCosplayInfo.getType());

        switch (mCosplayInfo.getType()) {
            case CosplayInfo.TYPE_POST_TEXT:
                mLlPicture.setVisibility(View.GONE);
                mPlayer.setVisibility(View.GONE);
                break;
            case CosplayInfo.TYPE_POST_PICTURE:
                //-------------帖子的图片
                mLlPicture.setVisibility(View.VISIBLE);
                mPlayer.setVisibility(View.GONE);
                setPicture();
                break;
            case CosplayInfo.TYPE_POST_AUDIO:
                // -------------帖子的声音
                mLlPicture.setVisibility(View.GONE);
                mPlayer.setVisibility(View.VISIBLE);
                setAudio();
                break;
        }
    }

    private void setContent(String bbsName) {
        String content = mCosplayInfo.getContent();
        if (TextUtils.isEmpty(content)) {
            content = convertEmptyContent();
        }
        if (TextUtils.isEmpty(bbsName)) {
            mTvLable.setVisibility(View.GONE);
            mTvContent.setText(content);
        } else {
            mTvLable.setVisibility(View.VISIBLE);
            mTvLable.setText(bbsName);
            mTvContent.setText("           " + content);
        }
    }

    //当帖子文本内容为空时,加上分享音频和图片的文本串
    private String convertEmptyContent() {
        String content = null;
        switch (mCosplayInfo.getType()) {
            case CosplayInfo.TYPE_POST_PICTURE:
                if (mCosplayInfo.getPictureList() != null) {
                    content = getString(R.string.post_share_pic, mCosplayInfo.getPictureList().size());
                }
                break;
            case CosplayInfo.TYPE_POST_AUDIO:
                if (mCosplayInfo.getAudio() != null) {
                    content = getString(R.string.post_share_audio);
                }
                break;
        }
        return content;
    }

    private void setAuthor() {
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
    }

    private void setTopic() {
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
                    if (tag == null || TextUtils.isEmpty(tag.getType())
                            || !"tp".toLowerCase().equalsIgnoreCase(tag.getType().toLowerCase())) continue;
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
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(UmengConstants.ITEM_ID, tag.getName() + "_" + tag.getTagId() + "_" + mUcid);
//                map.put(UmengConstants.FROM, "帖子详情页");
                        MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK_POST_DETAIL, map);
                        MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK, map);
//                        MyLog.i("点击帖子详情页话题:" + map.get(UmengConstants.ITEM_ID));
                        UIHelper.showTopicDetail(outAty, tag.getName(), tag.getType(), tag.getTagId(), 0);
                    });
                    mFlTags.addView(text, tagParams);
                }
            }
        }
    }

    private void setPicture() {
        List<BaseImage> picList = mCosplayInfo.getPictureList();
        int count = 0;
        if (picList != null && picList.size() > 0) {
            mLlPicture.removeAllViews();
            String[] picArray = new String[picList.size()];
            for (BaseImage image : picList) {
                if (image != null && !TextUtils.isEmpty(image.getUri())) {
                    final int index = count;
                    ImageView imageView = new ImageView(outAty);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setImageResource(R.drawable.default_img);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    if (image.getUri().startsWith("http")) {
                        ImageLoaderUtils.displayScreenWidthImage(image.getUri(), imageView, null, new ImageLoaderUtils.LoadImageCallback() {
                            @Override
                            public void onLoadCompleted(Bitmap bitmap) {
                                mBitmapList.add(bitmap);
                            }
                        });
                    } else {
                        ImageLoaderUtils.displayLocalImage(image.getUri(), imageView, null);
                    }
                    MyLog.i("pictureList add view: image.getUri=" + image.getUri());
                    imageView.setPadding(0, 15, 0, 15);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIHelper.showImagePreview(getActivity(), index, picArray);
                        }
                    });
                    mLlPicture.addView(imageView);
                    picArray[count] = image.getUri();
                    count++;
                }
            }
        }
    }

    private Handler mCallbackHandler = new Handler();
    private void setCommentListAdaptor() {
        mCommentList = mCosplayInfo.getComments();
        if (mCommentList == null || mCommentList.size() == 0) {
            mCommentList = new ArrayList<CommentInfo>();
        }
        if (mAdapter == null) {
            mAdapter = new CommentCosplayAdapter(getActivity(), true);
            mAdapter.setCommentCallback(new FollowAdapter.CommentCallback() {
                @Override
                public void onComment(int position, UserInfo replyUser) {
                    if(replyUser == null) {
                        return;
                    }
                    mReplyUser = replyUser;

                    mCallbackHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            outAty.emojiFragment.setHint("回复" + replyUser.getUsername());
                            outAty.emojiFragment.showSoftKeyboard();
                        }
                    }, 200);
                }
            });
        }
        mListView.setAdapter(mAdapter);
        mAdapter.clear();
        mAdapter.addData(mCommentList);
    }

    private void setComment() {
        if (mCommentList == null || mCommentList.size() == 0) {
            mLlComment.setVisibility(View.GONE);
        } else {
            mLlComment.setGravity(View.VISIBLE);
            mTvCommentCount.setText(String.format(getString(R.string.cosplay_comment_info), mCommentList.size()));
        }
    }

    private StickerAudioInfo mAudioInfo;
    private void setAudio() {
        List<StickerAudioInfo> audioInfoList = mCosplayInfo.getAudio();

        if (audioInfoList != null && audioInfoList.size() > 0) {
            mAudioInfo = audioInfoList.get(0);
            MyLog.i("帖子audio " + mAudioInfo.toString());

            if (mAudioInfo != null) {
                mPlayer.setAudioInfo(mAudioInfo);
            }
        }
    }

    private int mEmojiFragmentHeight;
    private long lastTime = 0;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
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
            mTimer.schedule(mTimerTask, 200);
        }
    }

    Timer mTimer = new Timer(true);
    private MyTimerTask mTimerTask;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

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

    private void stopPlayAudio() {
        if (mPlayer != null) {
            mPlayer.stopPlayAudio();
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
        mLikeLayout.setContent(mCosplayInfo);
        mLikeLayout.setBackgroundImage(R.drawable.transparent);
        mLikeLayout.setLikeImage(R.drawable.big_like_black);
        mLikeLayout.setTextColor(getResources().getColor(R.color.gray));
    }

    UserInfo mReplyUser;

    private void prepareComment(UserInfo replyUser) {
        mReplyUser = replyUser;
        if (isHasNetAndLogin()) {
            initCommentWindow();
            if (replyUser == null) {
                outAty.emojiFragment.setHint(R.string.comment_hint);
            } else {
                outAty.emojiFragment.setHint(String.format(getString(R.string.reply_referral_nickname), replyUser.getUsername()));
            }
            outAty.emojiFragment.showSoftKeyboard();
        }
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
                    UIHelper.showCosplayLikeList(outAty, mCosplayInfo.getUcid());
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
        ViewGroup.LayoutParams para = avatarView.getLayoutParams();
        para.width = SW;
        para.height = SW;
        avatarView.setLayoutParams(para);
        avatarView.setUserInfo(user.getUId(), user.getUsername());
        avatarView.setAvatarUrl(user.getAvatar().getUri());

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
        return null;
    }

    //分享
    public void handleShare() {
        if (mDetail == null) return;
        //停止播放音频
        stopPlayAudio();

        final PostReportDialog dialog = new PostReportDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCosplayUcid(mUcid);
        if (CameraManager.getInst().isMyCosplay(mCosplayInfo)) {
            dialog.setDeleteEnable(true);
            dialog.setDeleteOnClick(new DeleteOnClickListener() {
                @Override
                public void onClick(Object object) {
                    DiscoveryManager.getInstance().deleteCosplay(mUcid, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("Delete cosplay succeed!");
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
        String postContent = StringUtil.nullToEmpty(mCosplayInfo.getContent());
        if (postContent.length() > 10) {
            postContent = postContent.substring(0, 10);
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengConstants.ITEM_ID, mUcid + "_" + mCosplayInfo.getAuthorName() + "_" + postContent);
//        map.put(UmengConstants.FROM, "帖子详情页");
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT_POST, map);
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
                        UserInfo userInfo = AppContext.getInstance().getUserInfo();
                        userInfo.setRelation(4);
                        myComment.setAuthor(userInfo);
                        myComment.setReply(mReplyUser);
                        mReplyUser = null;
                        mCommentList.add(0, myComment);
                        mCosplayInfo.setComments(mCommentList);
                        mCosplayInfo.setCommentNum(mCosplayInfo.getCommentNum() + 1);

                        setComment();
                        mAdapter.addItem(0, myComment);
                        mAdapter.notifyDataSetChanged();
                        scrollTo(0, (int) TDevice.getScreenWidth());
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

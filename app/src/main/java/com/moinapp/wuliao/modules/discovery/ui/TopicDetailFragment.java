package com.moinapp.wuliao.modules.discovery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BeseMultiHaveHeaderListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.ATopicDetailAdapter;
import com.moinapp.wuliao.modules.discovery.adapter.TopicDetailAllAdaptor;
import com.moinapp.wuliao.modules.discovery.adapter.TopicDetailHotAdaptor;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagCosplayList;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.ExpandableTextView;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.LikeLayout;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 话题详情界面
 *
 * @author caozz
 */
public class TopicDetailFragment extends
        BeseMultiHaveHeaderListFragment<CosplayInfo, TagPop> implements OnTabReselectListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(TopicDetailFragment.class.getSimpleName());
    private static String RECEIVE_MSG = "";

    RoundAngleImageView mIvCover;
    TextView mTvName;
    TextView mTvType;

    TextView mTvViewNum;
    TextView mTvPicNum;
    TextView mTvCmtNum;
    TextView mTvJoin;
    LinearLayout mLyJoin;
    LikeLayout mLikeView;

    TextView mTvMoreUser;

    TextView mTvCatHot;
    View mTvCatIndicatorHot;
    TextView mTvCatAll;
    View mTvCatIndicatorAll;

    ExpandableTextView mTvIntro;

    private LinearLayout mLyJoin2Cosplay;

    private LinearLayout mLyUsers;
    private int USER_COUNT = 8;
    AvatarView[] mUsers = new AvatarView[USER_COUNT];
    int[] user_ids = {
            R.id.avatar0,
            R.id.avatar1,
            R.id.avatar2,
            R.id.avatar3,
            R.id.avatar4,
            R.id.avatar5,
            R.id.avatar6,
            R.id.avatar7,
    };

    private String mTopicName;
    private String mTopicType;
    private String mTopicId;

    private boolean registed = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RECEIVE_MSG = intent.getAction();
        }
    };
    private View headerView;

    @Override
    protected void sendRequestData() {
        if (StringUtil.isNullOrEmpty(mTopicId) && StringUtil.isNullOrEmpty(mTopicName)) {
            return;
        }
        String lastid = null;
        int type;
        if (mCurrentIndex == 0) {
            // 获取最新的话题列表
            type = 1;
        } else {
            // 获取所有的话题列表
            type = 2;
        }
        if (mCurrentPage[mCurrentIndex] != 0 && mAdapter[mCurrentIndex].getData().size() > 0) {
            lastid = mAdapter[mCurrentIndex].getItem(
                    mAdapter[mCurrentIndex].getData().size() - 1) != null ?
                    mAdapter[mCurrentIndex].getItem(mAdapter[mCurrentIndex].getData().size() - 1).getUcid()
                    : null;
        }
        MyLog.i("sendRequestData type=" + type + ", lastid=" + lastid + ", mCurrentPage=" + mCurrentPage[mCurrentIndex]);

        DiscoveryApi.getTopicDetail(type, mTopicId, mTopicName, lastid, mHandler);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 默认进入最新列表
        setMode(0);
        Bundle args = getArguments();
        if (args != null) {
            mTopicName = args.getString(Constants.BUNDLE_KEY_TAG);
            mTopicType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTopicId = args.getString(Constants.BUNDLE_KEY_ID);
        }
        MyLog.i("args mTopicName=" + mTopicName + ", mTopicType=" + mTopicType + ", mTopicId=" + mTopicId);

        if (StringUtil.isNullOrEmpty(mTopicId) && StringUtil.isNullOrEmpty(mTopicName)) {
            AppContext.showToast(R.string.invalid_topic_id);
            getActivity().finish();
        }
    }

    private void setMode(int mode) {
        mCurrentIndex = mode;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic_detail_new;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyLog.i("onViewCreated mCurrentIndex = " + mCurrentIndex + ", mTopicId=" + mTopicId);

        CommonTitleBar mTitleBar = (CommonTitleBar) view.findViewById(R.id.title_layout);
        mTitleBar.setLeftBtnOnclickListener(v -> {
            getActivity().finish();
        });

        mLyJoin2Cosplay = (LinearLayout) view.findViewById(R.id.ly_topic_join_cosplay);
        mLyJoin2Cosplay.setOnClickListener(v -> {
            makeCosplayFromTopic();
        });

        for (int i = 0; i < LIST_COUNT; i++) {
            if (i == mCurrentIndex) {
                mLayout[i].setVisibility(View.VISIBLE);
            } else {
                mLayout[i].setVisibility(View.GONE);
            }

            final int finalI = i;
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int headerHeight = headerView.getMeasuredHeight();
                    mErrorLayout[finalI].layout(0, headerHeight - 100, (int) TDevice.getScreenWidth(),
                            mErrorLayout[finalI].getMeasuredHeight() + headerHeight - 100);
                }
            });

            mErrorLayout[i].setEmptyImage(R.drawable.activity_empty_image);
            mErrorLayout[i].setBackgroundColor(0);
            if (i == 1) {
                mErrorLayout[i].setNoDataContent(String.format(getString(R.string.topic_detail_all_no_data), mTvName.getText()));
                mErrorLayout[i].setBtnVisibility(View.GONE);
                mErrorLayout[i].setBtnText(getString(R.string.go_to_discovery_self));
                mErrorLayout[i].setBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO 进入拍照界面
                            CameraManager.getInst().openCamera(getActivity() == null ? AppContext.context() : getActivity(), null);
                            StickPreference.getInstance().setDefaultUseSticker(null);
                        }
                    });
            }
        }

        switch (mCurrentIndex) {
            // 最新列表模式
            case 0:
                mExchangeBt.setImageResource(R.drawable.ac_linemode);
                break;
            // 全部图片模式
            case 1:
            default:
                mExchangeBt.setImageResource(R.drawable.ac_gridmode);
                break;
        }

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);

        getActivity().registerReceiver(mReceiver, filter);
        registed = true;

        setupUser();
        mOutsideErrorLayout.setOnLayoutClickListener(v -> {
            UIHelper.showLoginActivity(getActivity());
        });
    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
        setupUser();

        MyLog.i("requestDetailData mCurrentIndex = " + mCurrentIndex + ", mTopicId=" + mTopicId + ", isRefresh=" + isRefresh);
        // 获取话题信息详情
        DiscoveryApi.getTopicDetail(mCurrentIndex + 1, mTopicId, mTopicName, null, mDetailHandler);
    }

    @Override
    protected void onGetDetailSuccess() {
        super.onGetDetailSuccess();
        setupUser();
    }

    @Override
    protected boolean onGetDetailFailed(byte[] arg2) {
        super.onGetDetailFailed(arg2);
        setupUser();
        return true;
    }

    /**
     * 设置话题信息详情UI
     */
    private void setupUser() {
        mOutsideErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        /*// 未登录时进入我的,应该是提示登录界面
        if (!AppContext.getInstance().isLogin() && StringUtil.isNullOrEmpty(mUid)) {
            mOutsideErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
        } else
        // 登录用户看自己
        if (ClientInfo.isLoginUser(mUid)) {
//            MyLog.i("is login user and setupUser mIsWatingLogin=" + mIsWatingLogin);
//            if (mIsWatingLogin && StringUtil.isNullOrEmpty(AppContext.getInstance().getTagPop().getUId())) {
//                mUserContainer.setVisibility(View.GONE);
//                mUserUnLogin.setVisibility(View.VISIBLE);
//            } else {
//            }
            mUserContainer.setVisibility(View.VISIBLE);
            mUserUnLogin.setVisibility(View.GONE);
            mIvBack.setVisibility(View.GONE);
            mBtnLikeUser.setVisibility(View.GONE);
            // TODO 新版动态中没有此设置按钮了
            mIvSettings.setVisibility(View.GONE);
        } else {
            // 登录用户看别的用户
//            MyLog.i("is NOT login user and setupUser");
            mUserContainer.setVisibility(View.VISIBLE);
            mUserUnLogin.setVisibility(View.GONE);
            mIvBack.setVisibility(View.VISIBLE);
            mBtnLikeUser.setVisibility(View.VISIBLE);
            mIvSettings.setVisibility(View.GONE);
            if (detailBean != null && isAdded()) {
                mErrorLayout[mCurrentIndex].setNoDataContent(String.format(getString(R.string.ac_grid_no_data_somebody), detailBean.getUsername()));
            }
        }*/
        if (detailBean != null && isAdded()) {
            mErrorLayout[mCurrentIndex].setNoDataContent(String.format(getString(R.string.ac_grid_no_data_somebody), detailBean.getName()));
        }
        if (isAdded()) {
            mErrorLayout[mCurrentIndex].setBackgroundColor(getResources().getColor(R.color.user_activity_light_grey));
        }
    }


    @Override
    protected View initHeaderView() {

        MyLog.i("initHeaderView mCurrentIndex = " + mCurrentIndex);
        headerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_topicdetail_header, null);

//        mIvCover = (RoundAngleImageView) headerView.findViewById(R.id.topic_cover);
//        mTvName = (TextView) headerView.findViewById(R.id.topic_name);
//        mTvType = (TextView) headerView.findViewById(R.id.topic_type);
//
//        mTvViewNum = (TextView) headerView.findViewById(R.id.tv_topic_view_num);
//        mTvPicNum = (TextView) headerView.findViewById(R.id.tv_topic_picnum);
//        mTvCmtNum = (TextView) headerView.findViewById(R.id.tv_topic_comment_num);
//        mTvIntro = (ExpandableTextView) headerView.findViewById(R.id.tv_introduce);
//
//        mLyJoin = (LinearLayout) headerView.findViewById(R.id.ly_join_topic);
//        mTvJoin = (TextView) headerView.findViewById(R.id.tv_join_topic);
//        mLikeView= (LikeLayout) headerView.findViewById(R.id.like_layout);
//        mLikeView.setBackgroundImage(0);
//        mLikeView.setLikeImage(R.drawable.big_like_pink);
//
//        mLyUsers = (LinearLayout) headerView.findViewById(R.id.ly_users);
//        for (int i = 0; i < USER_COUNT; i++) {
//            mUsers[i] = (AvatarView) headerView.findViewById(user_ids[i]);
//        }
//        mTvMoreUser = (TextView) headerView.findViewById(R.id.tv_more_user);
//
//        mTvCatHot = (TextView) headerView.findViewById(R.id.tv_topic_category_hot);
//        mTvCatAll = (TextView) headerView.findViewById(R.id.tv_topic_category_all);
//        mTvCatIndicatorHot = headerView.findViewById(R.id.tv_topic_category_hot_indicator);
//        mTvCatIndicatorAll = headerView.findViewById(R.id.tv_topic_category_all_indicator);

        return headerView;
    }

    private int mTvJoin2Cosplayheight;
    private boolean mTvJoin2Cosplayvisible = true;
    @Override
    protected void onScrolling(int scrollState) {
        boolean visible = scrollState == SCROLL_STATE_IDLE;
        if (mTvJoin2Cosplayvisible != visible) {
            mTvJoin2Cosplayvisible = visible;

            if (!visible && mTvJoin2Cosplayheight == 0) {
                mTvJoin2Cosplayheight = getResources().getDimensionPixelSize(R.dimen.topic_join_cosplay_height);
            }

            ViewPropertyAnimator.animate(mLyJoin2Cosplay).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200L).translationY(visible ? 0 : (float) mTvJoin2Cosplayheight);
        }
    }

    @Override
    protected boolean needShowEmptyNoData() {
        return true;
    }

    @Override
    public void initData() {
        super.initData();

        for (int i = 0; i < LIST_COUNT; i++) {
            mErrorLayout[i].setErrorType(EmptyLayout.HIDE_LAYOUT);
            mErrorLayout[i].setOnLayoutClickListener(v -> {
                requestData(true);
            });
        }
    }


    @Override
    public void onClick(View v) {
//        final int id = v.getId();
//        switch (id) {
//            case R.id.topic_join_cosplay:
//
//                break;
//            default:
//                break;
//        }
    }

    private void makeCosplayFromTopic() {
        if (detailBean == null) {
            AppContext.showToast(R.string.no_network);
            return;
        }
        if (detailBean.getSticker() == null ||
                TextUtils.isEmpty(detailBean.getSticker().getStickerPackageId())) {
            AppContext.showToast(R.string.get_topic_cosplay_failed);
            return;
        }

        if (Tools.isFastDoubleClick()) {
            return;
        }
        // 提示登录
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengConstants.ITEM_ID, detailBean.getTagPopId() + "");
        map.put(UmengConstants.FROM, "话题详情页面");
        MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_JOIN, map);

        StickPreference.getInstance().setJoinTopicName(mTopicName);
        StickPreference.getInstance().setJoinTopicID(mTopicId);

        MyLog.i("join topic " + mTopicName + ", id=" + mTopicId);
        // 3.2.6不管贴纸包是否被下载或更新等,都去联网获取最新的贴纸包
        StickerManager.getInstance().getStickerDetail(detailBean.getSticker().getStickerPackageId(), new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    onTopicStickGot(obj);

                    // 进入拍照页面
                    CameraManager.getInst().openCamera(getActivity(), null);
//                    StickPreference.getInstance().setDefaultUseSticker(/*((StickerPackage) obj).getStickerPackageId()*/detailBean.getSticker().getStickerPackageId());
                } else {
                    AppContext.getInstance().showToast(R.string.get_topic_cosplay_failed);
                }
            }

            @Override
            public void onErr(Object obj) {
                MyLog.i("join topic spid ERR= ");
                AppContext.getInstance().showToast(R.string.get_topic_cosplay_failed);
            }

            @Override
            public void onNoNetwork() {
                AppContext.getInstance().showToast(R.string.no_network);
            }
        });

/*
        int download = detailBean.getSticker().isDownload();
        // 未下载并且不是推荐贴纸包时进详情去下载
        // TODO [应该增加判断本地DB有无此贴纸包的逻辑]
        if (download == 0 && !getString(R.string.intime_sticker_pacakge_id).equals(detailBean.getSticker().getStickerPackageId())) {
            // 进入贴纸包详情
            Intent intent = new Intent(BaseApplication.context(), StickerDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(StickerDetailActivity.STICKER_ID, detailBean.getSticker().getStickerPackageId());
            intent.putExtra(StickerDetailActivity.STICKER_FROM_JOIN, 1);
            getActivity().startActivity(intent);
        } else {
            // 进入拍照页面
            CameraManager.getInst().openCamera(getActivity(), null);
            StickPreference.getInstance().setDefaultUseSticker(detailBean.getSticker().getStickerPackageId());
        }*/
    }

    private void onTopicStickGot(Object obj) {
        if (obj == null) return;
        StickerPackage mStickerPackage = (StickerPackage) obj;
        CacheManager.saveObject(getActivity(), mStickerPackage, StickerConstants.STICKER_DETAIL_CACHE_PREFIX + mStickerPackage.getStickerPackageId());

        StickPreference.getInstance().setJoinTopicStickerpackageId(mStickerPackage.getStickerPackageId());
        MyLog.i("join topic spid= " + mStickerPackage.getStickerPackageId());
        StickerManager.getInstance().downloadTopicSticker(mStickerPackage, new Callback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish(int result) {
                if (result != 1) {
                    MyLog.i("join topic sp download failed");
                } else {
                    MyLog.i("join topic sp download success");
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.TOPIC_DETAIL_FRAGMENT);

        if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
            if (Constants.INTENT_ACTION_USER_CHANGE.equals(RECEIVE_MSG) ||
                    Constants.INTENT_ACTION_LOGOUT.equals(RECEIVE_MSG)) {
                mOutsideErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestDetailData(true);
            }
            RECEIVE_MSG = "";
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.TOPIC_DETAIL_FRAGMENT);
    }

    // 更新header
    @Override
    protected void executeOnLoadDetailSuccess(TagPop detailBean) {
        // 填充信息详情
        MyLog.i("executeOnLoadDetailSuccess " + detailBean);
        if (detailBean == null) {
            return;
        }
        if (detailBean.getIcon() == null || StringUtil.isNullOrEmpty(detailBean.getIcon().getUri())) {
            MyLog.i("getIcon()=null" + detailBean.toString());
        } else {
            ImageLoaderUtils.displayHttpImage(detailBean.getIcon().getUri(), mIvCover, null);
        }
        mTvName.setText(detailBean.getName());
        mTvType.setText(detailBean.getCategoryName());

        mTvViewNum.setText(String.valueOf(StringUtil.humanNumber(detailBean.getReadNum())));
        mTvPicNum.setText(String.valueOf(StringUtil.humanNumber(detailBean.getCosplayNum())));
        mTvCmtNum.setText(String.valueOf(StringUtil.humanNumber(detailBean.getCommentNum())));
        setFollowTopicStatus(detailBean);
        mLikeView.setContent(detailBean);
        if (StringUtil.isNullOrEmpty(detailBean.getDesc())) {
            mTvIntro.setVisibility(View.GONE);
        } else {
            mTvIntro.setVisibility(View.VISIBLE);
            mTvIntro.setText(StringUtil.nullToEmpty(detailBean.getDesc()));
        }

        int userCount = detailBean.getUserNum() < USER_COUNT ? detailBean.getUserNum() : USER_COUNT;
        mLyUsers.setVisibility(userCount <= 0 ? View.GONE : View.VISIBLE);
        for (int i = userCount; i < USER_COUNT; i++) {
            AvatarView mUser = mUsers[i];
            mUser.setVisibility(View.GONE);
        }
        for (int i = 0; i < userCount; i++) {
            AvatarView mUser = mUsers[i];
            mUser.setAvatarUrl(detailBean.getUsers().get(i).getAvatarUri());
            mUser.setVisibility(View.VISIBLE);
            final int j = i;
            mUser.setOnClickListener(v -> {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(detailBean.getUsers().get(j).getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(getActivity(), 0);
                } else {
                    UIHelper.showUserCenter(getActivity(), detailBean.getUsers().get(j).getUId());
                }
            });
        }

//        if (detailBean.getUserNum() < USER_COUNT) {
//            mTvMoreUser.setVisibility(View.GONE);
//        } else {
            mTvMoreUser.setVisibility(View.VISIBLE);
            mTvMoreUser.setText(String.format(getString(R.string.topic_join_users), StringUtil.humanNumber(detailBean.getUserNum())));
            mTvMoreUser.setText("");
            mTvMoreUser.setOnClickListener(v -> {
                UIHelper.showUserList(getActivity(), detailBean.getTagPopId(), 1);
            });
//        }

        setCategoryHeader();
        mTvCatHot.setOnClickListener(view -> {
            if (mCurrentIndex == 0) {
                return;
            }

            doExchange();
        });
        mTvCatAll.setOnClickListener(view -> {
            if (mCurrentIndex == 1) {
                return;
            }

            doExchange();
        });
    }

    /**
     * 设置话题最新,全部列表名称
     */
    private void setCategoryHeader() {
        MyLog.i("setCategoryHeader mCuINdex = " + mCurrentIndex);
        if (mCurrentIndex == 0) {
            mTvCatIndicatorHot.setVisibility(View.VISIBLE);
            mTvCatIndicatorAll.setVisibility(View.INVISIBLE);
            mTvCatHot.setTextColor(getResources().getColor(R.color.moin));
            mTvCatAll.setTextColor(getResources().getColor(R.color.calendar_header));
        } else {
            mTvCatIndicatorHot.setVisibility(View.INVISIBLE);
            mTvCatIndicatorAll.setVisibility(View.VISIBLE);
            mTvCatHot.setTextColor(getResources().getColor(R.color.calendar_header));
            mTvCatAll.setTextColor(getResources().getColor(R.color.moin));
        }
    }

    /**
     * 切换话题最新,全部列表布局
     */
    @Override
    protected void doExchange() {
        int last = mCurrentIndex;

        mCurrentIndex = ++mCurrentIndex % LIST_COUNT;
        mLayout[mCurrentIndex].setVisibility(View.VISIBLE);
        mLayout[last].setVisibility(View.GONE);
        if (mCurrentIndex == 0) {
            mExchangeBt.setImageResource(R.drawable.ac_gridmode);
        } else {
            mExchangeBt.setImageResource(R.drawable.ac_linemode);
        }
        for (int i = 0; i < LIST_COUNT; i++) {
            if (i == mCurrentIndex) {
                mLayout[i].setVisibility(View.VISIBLE);
                fillData();
            } else {
                mLayout[i].setVisibility(View.GONE);
            }
        }
        setCategoryHeader();
    }

    /**
     * 订阅话题
     */
    private void followTopic(TagPop detailBean) {
        DiscoveryManager.getInstance().followTag(mTopicName, mTopicType, 1, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                AppContext.showToast(R.string.topic_follow_succ);
                detailBean.setIsIdol(1);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFollowTopicStatus(detailBean);
                    }
                });
            }

            @Override
            public void onErr(Object obj) {
                AppContext.showToast(R.string.topic_follow_fail);
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToast(R.string.no_network);
            }
        });
    }

    /**
     * 设置话题订阅状态
     */
    private void setFollowTopicStatus(TagPop detailBean) {
        MyLog.i("setFollowTopicStatus " + detailBean.getIsIdol() + ", followNum=" + detailBean.getFollowNum());
        if (detailBean.getIsIdol() == 1) {
            mTvJoin.setTextColor(getResources().getColor(R.color.join_grey));
            mTvJoin.setText(R.string.topic_followed);
            mLyJoin.setBackgroundResource(R.drawable.subscribe_gray_button);
            mTvJoin.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mTvJoin.setOnClickListener(null);
        } else {
            mTvJoin.setTextColor(Color.WHITE);
//            mTvJoin.setText(String.format(getString(R.string.topic_follow_num), StringUtil.humanNumber(detailBean.getFollowNum())));
            SpannableString styledText = new SpannableString(String.format(getString(R.string.topic_follow_num), StringUtil.humanNumber(detailBean.getFollowNum())));
            styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.topic_detail_header_join_btn), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.topic_detail_header_join_btn_num), 2, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvJoin.setText(styledText, TextView.BufferType.SPANNABLE);

            mLyJoin.setBackgroundResource(R.drawable.subscribe_pink_button);
            mTvJoin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.subscribe_white_add, 0, 0, 0);
            mTvJoin.setOnClickListener(view -> {
                if (!Tools.isFastDoubleClick()) {
                    // 登录才能订阅
                    if (!AppContext.getInstance().isLogin()) {
                        UIHelper.showLoginActivity(getActivity());
                    } else {
                        if (detailBean == null) return;
                        MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_FOLLOW);
                        followTopic(detailBean);
                    }
                }
            });
        }

    }

    @Override
    protected String getDetailCacheKey() {
        return "topic_detail_header_" + mTopicName.hashCode();
    }

    @Override
    protected TagPop getDetailBean(ByteArrayInputStream is) {
        TagPop us = XmlUtils.JsontoBean(TagCosplayList.class, is).getTagPop();
        MyLog.i("get detail=" + us.toString());
        mTopicId = us != null ? us.getTagPopId() : "";
        return us;
//        return XmlUtils.JsontoBean(TagCosplayList.class, is).getUser();
    }

    @Override
    protected ListBaseAdapter<CosplayInfo> getListAdapter() {
        ATopicDetailAdapter adapter = null;
        if (mCurrentIndex == 0) {
            adapter = new TopicDetailHotAdaptor(getActivity());
        } else if (mCurrentIndex == 1) {
            adapter = new TopicDetailAllAdaptor();
        }
        return adapter;
    }

    @Override
    protected TagCosplayList parseList(InputStream is) throws Exception {

        TagCosplayList list = XmlUtils.JsontoBean(TagCosplayList.class, is);
        if(list != null && list.getList() != null && !list.getList().isEmpty()) {
            MyLog.i("parseList index[" + mCurrentIndex + "] list size=" + list.getList().size());
            for (int i = 0; i < list.getList().size(); i++) {
                CosplayInfo inf = list.getList().get(i);
                MyLog.i((i+1) + "个大咖秀 " + inf.toString());
            }
        }
        return list;

//        return XmlUtils.JsontoBean(CosplayInfoList.class, is);
    }

    @Override
    protected TagCosplayList readList(Serializable seri) {
        return (TagCosplayList) seri;
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((CosplayInfo) enity).getUcid().equals(((CosplayInfo) data.get(i)).getUcid())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected String getCacheKeyPrefix(int mCurrentIndex) {
        return "topic_detail_index_" + mCurrentIndex + "_" + mTopicName.hashCode();
    }

    /**
     * 话题详情缓存时间为半分钟
     */
    @Override
    protected long getAutoRefreshTime() {
        return 60 * 10;
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
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

}

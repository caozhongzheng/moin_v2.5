package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.base.SystemBarTintManager;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.TopicDetailMultipleAdapter;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.discovery.result.GetTopicDetailResult;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.mission.MissionPreference;
import com.moinapp.wuliao.ui.ExpandableTextView;
import com.moinapp.wuliao.ui.ParallaxListView;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.LikeLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 话题详情页[from 3.2.7]:
 * 顶部下拉放大
 * Created by guyunfei on 16/6/12.16:36.
 */
public class TopicDetailNewFragment extends BaseFragment implements View.OnClickListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(TopicDetailNewFragment.class.getSimpleName());

    private int USER_COUNT = 10;

    @InjectView(R.id.title_layout)
    protected LinearLayout mTitleLayout;
    @InjectView(R.id.title_down_grey_line)
    protected View mTitleDownLine;
    @InjectView(R.id.listView)
    protected ParallaxListView mListView;
    @InjectView(R.id.btn_float)
    protected FloatingActionButton floatingBtn;
    @InjectView(R.id.ly_empty)
    protected EmptyLayout emptyLayout;
    @InjectView(R.id.back)
    protected ImageView back;
    @InjectView(R.id.progress)
    protected ProgressBar mProgress;
    @InjectView(R.id.tv_topic_name)
    protected TextView mTvTopicName;

    @InjectView(R.id.guide_cover)
    protected FrameLayout mFlGuide;
    @InjectView(R.id.comment_cover)
    protected RelativeLayout mRlGuideCmt;
    @InjectView(R.id.like_cover)
    protected RelativeLayout mRlGuideLike;

    private ImageView mIvCover;

    private TextView mTvViewNum;
    private TextView mTvContentNum;
    private TextView mTvCmtNum;

    private LinearLayout mLyJoin;
    private TextView mTvJoin;
    private ImageView mIvJoin;

    private LikeLayout mLikeView;

    private ImageView mIvMoreUser;

    private TextView mIvTopicCategory;
    private LinearLayout mLyHotArticleContainer;

    private RelativeLayout mRlUsers;
    private LinearLayout mLyUsers;

    ExpandableTextView mTvIntro;
    int[] user_ids = {
            R.id.avatar0,
            R.id.avatar1,
            R.id.avatar2,
            R.id.avatar3,
            R.id.avatar4,
            R.id.avatar5,
            R.id.avatar6,
            R.id.avatar7,
            R.id.avatar8,
            R.id.avatar9,
    };

    AvatarView[] mUsers = new AvatarView[USER_COUNT];
    private LayoutInflater inflater;

    private String mTopicName;
    private String mTopicType;
    private String mTopicId;
    private int mFromIdx;//从哪个任务跳转而来 1分享 2发图 3点赞 4评论(MissionConstants)
    private boolean isRefreshing = false;
    private TopicDetailMultipleAdapter mAdapter;
    private ProgressBar footerProgressBar;
    private TextView footerText;
    private boolean isNeedLoadMore = true;
    private View headerPart1, headerPart2;
    private float part2LocationYOriginal, moveArea;
    private TagPop detailBean;
    private boolean registed = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if((intent.getAction()).equals(Constants.INTENT_ACTION_USER_CHANGE)) {
                TopicDetailNewFragment.this.onRefresh();
            }
        }
    };

    public int getLayoutId() {
        return R.layout.fragment_topic_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        Bundle args = getArguments();
        if (args != null) {
            mTopicName = args.getString(Constants.BUNDLE_KEY_TAG);
            mTopicType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTopicId = args.getString(Constants.BUNDLE_KEY_ID);
            mFromIdx = args.getInt(Constants.BUNDLE_KEY_FROM_MISSION);
        }

        MyLog.i("mTopicName=" + mTopicName
                        + ", mTopicType=" + mTopicType
                        + ", mTopicID=" + mTopicId
                        + ", mFromIdx=" + mFromIdx
        );
        if (StringUtil.isNullOrEmpty(mTopicId) && StringUtil.isNullOrEmpty(mTopicName)) {
            AppContext.showToast(R.string.invalid_topic_id);
            getActivity().finish();
        }
    }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getActivity().getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(0);  //状态栏无背景
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    public static final int SCROLL_DOWN = 1;
    public static final int ROLLBACK = 2;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!needShowTaskGuide()) {
                return;
            }
            switch (msg.what) {
                case SCROLL_DOWN:
                    rollback();
                    break;
                case ROLLBACK:
                    showTaskGuideCover();
                    break;
                default:
                    break;
            }
        }
    };

    static final int FRI = 30;
    static final int DURATION = 1000;
    private void rollback() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    mListView.setFriction(ViewConfiguration.getScrollFriction() * FRI);

                    Thread.sleep(50);
                    int p = 2;
                    if (mAdapter.getDataSize() > p) {
                        mListView.smoothScrollToPositionFromTop(p, 0, DURATION);
                    } else {
                        mListView.smoothScrollToPositionFromTop(mAdapter.getDataSize() - 1, 0, DURATION);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    MyLog.e(e);
                }
            }

        });

    }

    private void showTaskGuideCover() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                View child1 = mListView.getChildAt(0);
                if (child1 == null) return;

                int contentHeight = 0;
                View viewContent = child1.findViewById(R.id.ly_content);
                if (viewContent != null) {
                    Rect rect = new Rect();
                    viewContent.getGlobalVisibleRect(rect);
                    contentHeight = rect.height();
                }
                if (mFromIdx == MissionConstants.MISSION_COMMENT) {
                    MissionPreference.getInstance().setFirstCommentGuide(false);

                    mFlGuide.setVisibility(View.VISIBLE);
                    mRlGuideCmt.setVisibility(View.VISIBLE);
                    mRlGuideLike.setVisibility(View.GONE);
                    if (contentHeight != 0) {
                        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mRlGuideCmt.getLayoutParams();
                        marginLayoutParams.topMargin = contentHeight + (int) TDevice.dpToPixel(50);
                    }
                } else if (mFromIdx == MissionConstants.MISSION_LIKE) {
                    MissionPreference.getInstance().setFirstLikeGuide(false);

                    mFlGuide.setVisibility(View.VISIBLE);
                    mRlGuideCmt.setVisibility(View.GONE);
                    mRlGuideLike.setVisibility(View.VISIBLE);
                    if (contentHeight != 0) {
                        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mRlGuideLike.getLayoutParams();
                        marginLayoutParams.topMargin = contentHeight + (int) TDevice.dpToPixel(50);
                    }
                } else {
                    return;
                }


                mFlGuide.setOnClickListener(v -> {
                    mFlGuide.setVisibility(View.GONE);
                });

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFlGuide.setVisibility(View.GONE);
                    }
                }, 3000);

                mListView.setFriction(ViewConfiguration.getScrollFriction());
            }

        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
        registed = true;
    }

    int[] titleLocation = new int[2];
    int[] part2Location = new int[2];

    @Override
    public void initView(View view) {

        back.setOnClickListener(this);

        emptyLayout.setVisibility(View.VISIBLE);
        if (!TDevice.hasInternet()) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TDevice.hasInternet()) {
                        AppContext.showToast(R.string.no_network);
                    } else if (!isRefreshing) {
                        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                        onRefresh();
                    }
                }
            });
        } else {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                mTitleLayout.getLocationInWindow(titleLocation);
                float marginScreenTop = titleLocation[1];//系统顶部栏的高度
                float titleHeight = mTitleLayout.getMeasuredHeight();// 悬浮的Layout的高度

                headerPart2.getLocationInWindow(part2Location);
                part2LocationYOriginal = part2Location[1];

                moveArea = part2LocationYOriginal - titleHeight;// 在这个区域内滑动会改变悬浮Layout的透明度
            }
        });

        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        initHeaderView();
        View footerView = initFooterView();

        mListView.addHeaderView(headerPart1);
        mListView.addHeaderView(headerPart2);
        mListView.addFooterView(footerView);
        mListView.setParallaxImageView(mIvCover);
        mAdapter = new TopicDetailMultipleAdapter(getActivity(), mHandler);
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 暂时设为当下拉到距离底部最后一个元素还有2个时开始加载下一页的数据
                if (mAdapter != null) {
                    // -1是因为有headview
                    if (view.getLastVisiblePosition() >= mAdapter.getCount() - 1 - 2 && isNeedLoadMore) {
                        TopicDetailNewFragment.this.onLoadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                headerPart2.getLocationInWindow(part2Location);
                float locationY = part2Location[1];
                float moveY = part2LocationYOriginal - locationY;
                if (moveY > 0) {
                    float i = moveY / moveArea;//移动的比例
                    if (i < 0.5) {
                        mTitleLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                        mTitleLayout.setAlpha(1f);
                        mTvTopicName.setTextColor(getResources().getColor(R.color.white));
                        back.setImageResource(R.drawable.back_white);
                    } else {
                        mTitleLayout.setBackgroundColor(getResources().getColor(R.color.white));
                        mTitleLayout.setAlpha(i - 0.1f);
                        mTvTopicName.setTextColor(getResources().getColor(R.color.black));
                        back.setImageResource(R.drawable.return_key_black);
                    }

                    if (i > 1.0) {
                        mTitleDownLine.setVisibility(View.VISIBLE);
                    } else {
                        mTitleDownLine.setVisibility(View.GONE);
                    }
                }
            }
        });

        mListView.setOnRefreshListener(new ParallaxListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TopicDetailNewFragment.this.onRefresh();
            }

            @Override
            public void onLoadMore() {
                if (isNeedLoadMore) {
                    TopicDetailNewFragment.this.onLoadMore();
                }
            }

            @Override
            public void onTouchMove() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTouchUp() {
                mProgress.setVisibility(View.INVISIBLE);
            }
        });

        floatingBtn.setOnClickListener(this);
    }

    /**
     * 刷新数据
     */
    private void onRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            int childCount = mLyHotArticleContainer.getChildCount();
            if (childCount > 0) {
                mLyHotArticleContainer.removeAllViews();
            }
            initData();
        }
    }

    /**
     * 加载更多
     */
    private void onLoadMore() {
        if (StringUtil.isNullOrEmpty(mTopicId) && StringUtil.isNullOrEmpty(mTopicName)) {
            return;
        }
        footerProgressBar.setVisibility(View.VISIBLE);
        footerText.setText(R.string.loading);

        int dataSize = mAdapter.getDataSize();
        String lastid;
        if (dataSize > 0) {
            CosplayInfo item = mAdapter.getData().get(dataSize - 1);
            lastid = item == null ? null : item.getUcid();
            DiscoveryManager.getInstance().getTopicDetail(3, mTopicId, mTopicName, lastid, new IListener() {

                @Override
                public void onSuccess(Object obj) {
                    if (obj == null) return;
                    GetTopicDetailResult moreDatas = (GetTopicDetailResult) obj;

                    if (moreDatas.isLast()) {
                        isNeedLoadMore = false;
                        footerProgressBar.setVisibility(View.GONE);
                        footerText.setText(R.string.loading_no_data);
                    } else {
                        isNeedLoadMore = true;
                    }

                    if (moreDatas != null) {
                        MyLog.i("Add datas");
                        List<CosplayInfo> cosplayList = moreDatas.getCosplayList();
                        mAdapter.addData((List<CosplayInfo>) cosplayList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        footerProgressBar.setVisibility(View.GONE);
                        footerText.setText(R.string.loading_no_data);
                    }
                }

                @Override
                public void onErr(Object obj) {
                    footerProgressBar.setVisibility(View.GONE);
                    footerText.setText(R.string.loading_no_data);
                }

                @Override
                public void onNoNetwork() {
                    footerProgressBar.setVisibility(View.GONE);
                    footerText.setText(R.string.loading_no_data);
                }
            });
        }
    }

    private void initHeaderView() {
        headerPart1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_topicdetail_header, null);
        headerPart2 = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_topicdetail_header_part2, null);

        mIvCover = (ImageView) headerPart1.findViewById(R.id.topic_cover);
        mIvTopicCategory = (TextView) headerPart1.findViewById(R.id.iv_topic_category);

        mTvViewNum = (TextView) headerPart1.findViewById(R.id.tv_topic_view_num);
        mTvContentNum = (TextView) headerPart1.findViewById(R.id.tv_topic_content_num);
        mTvCmtNum = (TextView) headerPart1.findViewById(R.id.tv_topic_comment_num);


        mTvIntro = (ExpandableTextView) headerPart2.findViewById(R.id.tv_introduce);

        mLyJoin = (LinearLayout) headerPart2.findViewById(R.id.ly_join_topic);
        mTvJoin = (TextView) headerPart2.findViewById(R.id.tv_join_topic);
        mIvJoin = (ImageView) headerPart2.findViewById(R.id.iv_join_topic);

        mLikeView = (LikeLayout) headerPart2.findViewById(R.id.like_layout);
        mLikeView.setBackgroundImage(0);
        mLikeView.setLikeImage(R.drawable.like_topic_pink);

        mRlUsers = (RelativeLayout) headerPart2.findViewById(R.id.rl_users);
        for (int i = 0; i < USER_COUNT; i++) {
            mUsers[i] = (AvatarView) headerPart2.findViewById(user_ids[i]);
        }
        mIvMoreUser = (ImageView) headerPart2.findViewById(R.id.iv_more_user);

        headerPart2.findViewById(R.id.ly_article).setOnClickListener(this);
        headerPart2.findViewById(R.id.ly_pic).setOnClickListener(this);

        mLyHotArticleContainer = (LinearLayout) headerPart2.findViewById(R.id.ly_hot_article_container);

    }

    private View initFooterView() {
        View footerView = inflater.inflate(R.layout.list_cell_footer, null);
        footerView.setBackgroundColor(getResources().getColor(R.color.white));
        footerProgressBar = (ProgressBar) footerView
                .findViewById(R.id.progressbar);
        footerText = (TextView) footerView.findViewById(R.id.text);
        footerText.setTextSize(13);
        return footerView;
    }

    @Override
    public  void initData() {
        if (mProgress.getVisibility() != View.VISIBLE) {
            mProgress.setVisibility(View.VISIBLE);
        }
        // 获取话题详情
        DiscoveryManager.getInstance().getTopicDetail(3, mTopicId, mTopicName, null, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj == null) return;
                emptyLayout.setVisibility(View.GONE);
                isRefreshing = false;
                mProgress.setVisibility(View.INVISIBLE);

                GetTopicDetailResult mDatas = (GetTopicDetailResult) obj;
                if (mDatas.isLast()) {
                    isNeedLoadMore = false;
                    footerProgressBar.setVisibility(View.GONE);
                    footerText.setText(R.string.loading_no_data);
                } else {
                    isNeedLoadMore = true;
                }
                if (mDatas != null) {
                    updateView(mDatas);
                }
            }

            @Override
            public void onErr(Object obj) {
                isRefreshing = false;
                footerProgressBar.setVisibility(View.GONE);
                footerText.setText("话题详情获取失败");
            }

            @Override
            public void onNoNetwork() {
                isRefreshing = false;
                footerProgressBar.setVisibility(View.GONE);
                footerText.setText("无网络");
            }
        });

    }

    /**
     * 界面加载
     */
    private void updateView(GetTopicDetailResult result) {
        detailBean = result.getTopic();

        if (detailBean == null) {
            return;
        }

        if (detailBean.getIcon() == null || StringUtil.isNullOrEmpty(detailBean.getIcon().getUri())) {
            MyLog.i("getIcon() = null" + detailBean.toString());
        } else {
            ImageLoaderUtils.displayHttpImage(true, detailBean.getIcon().getUri(), mIvCover,
                    ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), true, null);
        }

        if (detailBean.getName() != null) {
            mTvTopicName.setText(detailBean.getName());
            if (TextUtils.isEmpty(mTopicName)) {
                mTopicName = detailBean.getName();
            }
        }

        if (!StringUtils.isEmpty(detailBean.getCategoryName())) {
            mIvTopicCategory.setText(detailBean.getCategoryName());
            MyLog.i("category color = " + detailBean.getCategoryColor());
            int color = StringUtil.parseColor(detailBean.getCategoryColor());
            if (color != 0) {
                MyLog.i("parsed color = " + color);
                GradientDrawable myGrad = (GradientDrawable)mIvTopicCategory.getBackground();
                myGrad.setColor(color);
            }
        }

        mTvViewNum.setText(String.valueOf(StringUtil.humanNumber(detailBean.getReadNum())));
        mTvContentNum.setText(String.valueOf(StringUtil.humanNumber(detailBean.getCosplayNum())));
        mTvCmtNum.setText(String.valueOf(StringUtil.humanNumber(detailBean.getCommentNum())));

        setFollowTopicStatus(detailBean);
        mLikeView.setContent(detailBean);

        if (StringUtil.isNullOrEmpty(detailBean.getDesc())) {
            mTvIntro.setVisibility(View.GONE);
        } else {
            mTvIntro.setVisibility(View.VISIBLE);
            mTvIntro.setText(StringUtil.nullToEmpty(detailBean.getDesc()));
        }

        int userCount = Math.min(detailBean.getUserNum(), USER_COUNT);
        userCount = Math.min(detailBean.getUsers() == null ? 0 : detailBean.getUsers().size(), userCount);
        mRlUsers.setVisibility(userCount <= 0 ? View.GONE : View.VISIBLE);
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

        mIvMoreUser.setOnClickListener(v -> {
            UIHelper.showUserList(getActivity(), detailBean.getTagPopId(), 1);
        });

        List<CosplayInfo> hotList = detailBean.getHotList();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = (int) TDevice.dpToPixel(50);
        if (hotList != null && hotList.size() > 0) {
            mLyHotArticleContainer.setVisibility(View.VISIBLE);
            for (int i = 0; i < hotList.size(); i++) {
                CosplayInfo cosplayInfo = hotList.get(i);
                LinearLayout hotArticle = (LinearLayout) inflater.inflate(R.layout.layout_hot_article_textview, null);
                TextView category = (TextView) hotArticle.findViewById(R.id.hot_article_category);
                TextView textView = (TextView) hotArticle.findViewById(R.id.hot_article);
                List<TagInfo> tags = cosplayInfo.getTags();
                if (tags != null) {
                    for (int j = 0; j < tags.size(); j++) {
                        TagInfo tagInfo = tags.get(j);
                        if (tagInfo.getType().equals("bbs")) {
                            String name = tagInfo.getName();
                            if (name != null) {
                                category.setText(name);
                            }
                        }
                    }
                }
                textView.setText(cosplayInfo.getContent());

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO 置顶帖掉转到帖子详情
                        UIHelper.showPostDetail(getActivity(), cosplayInfo.getUcid(), System.currentTimeMillis());
                    }
                });
                mLyHotArticleContainer.addView(hotArticle, layoutParams);
            }
        } else {
            mLyHotArticleContainer.setVisibility(View.GONE);
        }

        // 底部List内容
        List<CosplayInfo> cosplayList = result.getCosplayList();
//        MyLog.i("cosplayList.size():" + cosplayList.size());
        if (cosplayList != null && cosplayList.size() > 0) {
            mAdapter.setData((ArrayList<CosplayInfo>) cosplayList);
            mAdapter.notifyDataSetChanged();

            // 滚动到第五条(如果有5条的话,然后回滚回来) 如果是从每日任务来的话,并且是第一次,则显示引导页
            if (needShowTaskGuide()) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mListView.setFriction(ViewConfiguration.getScrollFriction() * FRI);

                            Thread.sleep(50);
                            int p = 7;
                            if (mAdapter.getDataSize() > p) {
                                mListView.smoothScrollToPositionFromTop(p, 0, DURATION);
                            } else {
                                mListView.smoothScrollToPositionFromTop(mAdapter.getDataSize() - 1, 0, DURATION);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        } else {
            isNeedLoadMore = false;
            footerProgressBar.setVisibility(View.GONE);
            footerText.setText(R.string.loading_no_data);
        }
    }

    private boolean needShowTaskGuide() {
        // TODO fromDayTask:是否从每日任务过来,而且是第一次
        return (mFromIdx == MissionConstants.MISSION_COMMENT && MissionPreference.getInstance().isFirstCommentGuide())
                || (mFromIdx == MissionConstants.MISSION_LIKE && MissionPreference.getInstance().isFirstLikeGuide());
    }

    /**
     * 设置话题订阅状态
     */
    private void setFollowTopicStatus(TagPop detailBean) {
        MyLog.i("setFollowTopicStatus " + detailBean.getIsIdol() + ", followNum=" + detailBean.getFollowNum());
        if (detailBean.getIsIdol() == 1) {
            mLyJoin.setBackgroundResource(R.drawable.topic_gray_button);
            mIvJoin.setVisibility(View.GONE);

            mTvJoin.setText(R.string.topic_followed);

            mTvJoin.setTextColor(AppContext.getInstance().getResources().getColor(R.color.common_sticker_grey));
            mTvJoin.setOnClickListener(null);
        } else {
            mLyJoin.setBackgroundResource(R.drawable.topic_pink_button);
            mIvJoin.setVisibility(View.VISIBLE);

            // TODO error
            mTvJoin.setText(String.format(AppContext.getInstance().getResources().getString(R.string.topic_follow_num), StringUtil.humanNumber(detailBean.getFollowNum())));
            mTvJoin.setTextColor(AppContext.getInstance().getResources().getColor(R.color.moin));
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

    @Override
    public void onClick(View v) {
        MyLog.i("话题[" + mTopicName + "] 对应的贴纸包是 " + (detailBean == null ? null : detailBean.getSticker()));
        switch (v.getId()) {
            case R.id.ly_article:
                // 进入帖子专区

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, mTopicName + "_" + mTopicId);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_POST_LIST_CLICK, map);

                UIHelper.showArticleList(getActivity(), mTopicName, mTopicType, mTopicId);
                break;
            case R.id.ly_pic:
                // 进入图片专区

                HashMap<String, String> map2 = new HashMap<String, String>();
                map2.put(UmengConstants.ITEM_ID, mTopicName + "_" + mTopicId);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_COSPLAY_LIST_CLICK, map2);

                UIHelper.showTopicPhotoList(getActivity(), detailBean == null ? null : detailBean.getSticker(), mTopicName, mTopicType, mTopicId);
                break;
            case R.id.btn_float:
                Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_STICKER, detailBean == null ? null : detailBean.getSticker());
                intent.putExtra(Constants.BUNDLE_KEY_ID, mTopicId);
                intent.putExtra(Constants.BUNDLE_KEY_TAG, mTopicName);
                intent.putExtra(Constants.BUNDLE_KEY_TYPE, mTopicType);
                startActivity(intent);
                break;
            case R.id.back:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null && registed) {
            getActivity().unregisterReceiver(mReceiver);
            registed = false;
        }
    }
}

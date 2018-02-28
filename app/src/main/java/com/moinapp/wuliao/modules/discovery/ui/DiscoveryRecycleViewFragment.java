package com.moinapp.wuliao.modules.discovery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.DiscoveryRecycleViewAdapter;
import com.moinapp.wuliao.modules.discovery.model.BannerInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.discovery.result.GetDiscoveryItemResult;
import com.moinapp.wuliao.modules.events.EventsManager;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 瀑布流模式首页
 * Created by guyunfei on 16/6/29.16:24.
 */
public class DiscoveryRecycleViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnTabReselectListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(DiscoveryRecycleViewFragment.class.getSimpleName());

    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    private static final String CACHE_KEY_PREFIX = "discoverylist_";
    private static final String CACHE_KEY_PREFIX_BANNER = "BANNER";
    private static final String CACHE_KEY_PREFIX_HOT_TOPIC = "HotTopic";
    private static final String CACHE_KEY_PREFIX_HOT_EVNET = "HotEvent";

    @InjectView(R.id.swiperefreshlayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar mTitleLayout;

    @InjectView(R.id.recycleView)
    protected RefreshLoadMoreRecyclerView mRecycleView;

    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;

    @InjectView(R.id.mission)
    protected Button mMissionBtn;

    protected int mCatalog = 1;

    protected int mCurrentPage = 0;
    private LayoutInflater inflater;

    //首页加载时间统计用到的变量
    private long mStartTime;//开始联网获取数据的时刻
    private long mGotDataTime;//联网获取到数据的时刻

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onUserChange();
        }
    };
    private DiscoveryRecycleViewAdapter discoveryRecycleViewAdapter;
    private boolean isLast;


    private int mMissionBtnWidth;   //每日任务按钮宽度
    private boolean mMissionBtnVisible = true;  //每日任务按钮是否显示

    private void onUserChange() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mCurrentPage = 0;
        initData(true);
    }

    public int getLayoutId() {
        return R.layout.fragment_discovery_recycleview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(Constants.BUNDLE_KEY_CATALOG, 0);
        }
        if (mCatalog > 0) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        initData(false);
    }

    private void initView(View view) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mTitleLayout.setVisibility(View.VISIBLE);
        mTitleLayout.setTitleTxt(getString(R.string.main_tab_name_discovery));
        mTitleLayout.hideLeftBtn();
        mTitleLayout.setRightBtnIcon(R.drawable.actionbar_search_icon);
        mTitleLayout.setRightBtnClickAble(true);
        mTitleLayout.setRightBtnOnclickListener(v -> {
            UIHelper.showSearch(getActivity());
        });

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCatalog > 0) {
                    mCurrentPage = 0;
                    initData(true);
                } else {
                    initData(true);
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }
        });

        mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        if (discoveryRecycleViewAdapter == null) {
            discoveryRecycleViewAdapter = new DiscoveryRecycleViewAdapter(getActivity(), mRecycleView);
        }
        mRecycleView.setAdapter(discoveryRecycleViewAdapter);

        mRecycleView.setOnLoadMoreListener(new RefreshLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                DiscoveryRecycleViewFragment.this.onLoadMore();
            }
        });

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean visible = newState == RecyclerView.SCROLL_STATE_IDLE;
                if (mMissionBtnVisible != visible) {
                    mMissionBtnVisible = visible;

                    if (!visible && mMissionBtnWidth == 0) {
                        mMissionBtnWidth = getResources().getDimensionPixelSize(R.dimen.mission_byn_width);
                    }

                    ViewPropertyAnimator.animate(mMissionBtn).setInterpolator(
                            new AccelerateDecelerateInterpolator()).setDuration(200L).translationX(visible ? 0 : (float) mMissionBtnWidth);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mMissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!AppContext.getInstance().isLogin()) {
//                    UIHelper.showLoginActivity(getActivity());
//                } else {
                UIHelper.showMissionActivity(getActivity());
//                }
            }
        });
    }

    /**
     * 加载更多
     */
    private void onLoadMore() {
        String lastId = discoveryRecycleViewAdapter.getLastId();
        if (!isLast) {
            if (discoveryRecycleViewAdapter != null) {
                discoveryRecycleViewAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_LOADING);
            }
            DiscoveryManager.getInstance().getHotList(lastId, 50, new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    GetDiscoveryItemResult result = (GetDiscoveryItemResult) obj;

                    if (result != null) {
                        isLast = result.isLast();
                        if (isLast) {
                            if (discoveryRecycleViewAdapter != null) {
                                discoveryRecycleViewAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_NO_MORE);
                            }
                        }
                        List<CosplayInfo> hotList = result.getList();
                        if (hotList != null) {
                            if (discoveryRecycleViewAdapter.getDatas() != null) {
                                // 去除重复数据
                                for (int i = 0; i < hotList.size(); i++) {
                                    if (compareTo(discoveryRecycleViewAdapter.getDatas(), hotList.get(i))) {
                                        hotList.remove(i);
                                        i--;
                                    }
                                }
                            }
                            discoveryRecycleViewAdapter.addHotListData(hotList);
                            discoveryRecycleViewAdapter.notifyItemInserted(discoveryRecycleViewAdapter.getItemCount() - 2);
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            MyLog.i("hotList == null");
                        }
                    }
                }

                @Override
                public void onErr(Object obj) {
                    if (discoveryRecycleViewAdapter != null) {
                        discoveryRecycleViewAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_ERROR);
                    }
                }

                @Override
                public void onNoNetwork() {
                    if (discoveryRecycleViewAdapter != null) {
                        discoveryRecycleViewAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_ERROR);
                    }
                }
            });
        } else {
            if (discoveryRecycleViewAdapter != null) {
                discoveryRecycleViewAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_NO_MORE);
            }
        }
        mCurrentPage++;
    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        if (mState == STATE_REFRESH) {
            return;
        }

        // 设置顶部正在刷新
        setSwipeRefreshLoadingState();
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            setSwipeRefreshLoadedState();
            return;
        }
        mState = STATE_REFRESH;
        initData(true);
    }

    /**
     * 完成刷新
     */
    protected void executeOnLoadFinish() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
    }

    /**
     * 设置顶部正在加载的状态
     */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    private void initData(boolean refresh) {
        String lastUcid = null;

        long current = System.currentTimeMillis();
        mStartTime = current;
        mGotDataTime = current;
        DiscoveryManager.getInstance().writeDiscoveryLoadInfo2File(mStartTime +
                ": start to get discovery data from server...\n");

        if (discoveryRecycleViewAdapter != null) {
            if (discoveryRecycleViewAdapter.getDatas() == null || refresh) {
                //获取热门列表信息
                DiscoveryManager.getInstance().getHotList(lastUcid, 50, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        GetDiscoveryItemResult result = (GetDiscoveryItemResult) obj;
                        if (result != null) {
                            setHotListDatas(result);
                            CacheManager.saveObject(getActivity(), result, getCacheKey());
                        }
                    }

                    @Override
                    public void onErr(Object obj) {
                        GetDiscoveryItemResult result = (GetDiscoveryItemResult) CacheManager.readObject(getActivity(), getCacheKey());
                        if (result != null) {
                            setHotListDatas(result);
                        }
                    }

                    @Override
                    public void onNoNetwork() {
                        if (refresh) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            GetDiscoveryItemResult result = (GetDiscoveryItemResult) CacheManager.readObject(getActivity(), getCacheKey());
                            if (result != null) {
                                setHotListDatas(result);
                            } else {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        }
                    }
                });

                //获取首页banner信息
                DiscoveryManager.getInstance().getBanner(new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        recordGotDataTime();
                        List<BannerInfo> bannerInfoList = (List<BannerInfo>) obj;
                        if (bannerInfoList != null) {
                            setBannerDatas(bannerInfoList);
                            CacheManager.saveObject(getActivity(), (Serializable) bannerInfoList, CACHE_KEY_PREFIX_BANNER);
                        }
                    }

                    @Override
                    public void onErr(Object obj) {
                        List<BannerInfo> bannerInfoList = (List<BannerInfo>) CacheManager.readObject(getActivity(), CACHE_KEY_PREFIX_BANNER);
                        if (bannerInfoList != null) {
                            setBannerDatas(bannerInfoList);
                        }
                    }

                    @Override
                    public void onNoNetwork() {
                        if (refresh) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            List<BannerInfo> bannerInfoList = (List<BannerInfo>) CacheManager.readObject(getActivity(), CACHE_KEY_PREFIX_BANNER);
                            if (bannerInfoList != null) {
                                setBannerDatas(bannerInfoList);
                            } else {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        }
                    }
                });

                //获取热门话题列表
                DiscoveryManager.getInstance().getTopicList(0, null, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        recordGotDataTime();
                        List<TagPop> topicList = (List<TagPop>) obj;
                        if (topicList != null) {
                            setHotTopicData(topicList);
                            CacheManager.saveObject(getActivity(), (Serializable) topicList, CACHE_KEY_PREFIX_HOT_TOPIC);
                        }
                    }

                    @Override
                    public void onErr(Object obj) {
                        List<TagPop> topicList = (List<TagPop>) CacheManager.readObject(getActivity(), CACHE_KEY_PREFIX_HOT_TOPIC);
                        setHotTopicData(topicList);
                    }

                    @Override
                    public void onNoNetwork() {
                        if (refresh) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            List<TagPop> topicList = (List<TagPop>) CacheManager.readObject(getActivity(), CACHE_KEY_PREFIX_HOT_TOPIC);
                            if (topicList != null) {
                                setHotTopicData(topicList);
                            } else {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        }
                    }
                });

                //获取热门活动
                EventsManager.getInstance().getEvents(null, 20, 1, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        recordGotDataTime();
                        List<EventsInfo> events = (List<EventsInfo>) obj;
                        if (events != null) {
                            setHotEventData(events);
                            CacheManager.saveObject(getActivity(), (Serializable) events, CACHE_KEY_PREFIX_HOT_EVNET);
                        }
                    }

                    @Override
                    public void onErr(Object obj) {
                        List<EventsInfo> events = (List<EventsInfo>) CacheManager.readObject(getActivity(), CACHE_KEY_PREFIX_HOT_EVNET);
                        setHotEventData(events);
                    }

                    @Override
                    public void onNoNetwork() {
                        if (refresh) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            List<EventsInfo> events = (List<EventsInfo>) CacheManager.readObject(getActivity(), CACHE_KEY_PREFIX_HOT_EVNET);
                            if (events != null) {
                                setHotEventData(events);
                            } else {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        }
                    }
                });
            } else {
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }

        executeOnLoadFinish();
    }

    private void recordGotDataTime() {
        long current = System.currentTimeMillis();
        if (current > mGotDataTime) {
            mGotDataTime = current;
            DiscoveryManager.getInstance().writeDiscoveryLoadInfo2File(current +
                    ": got discovery data from server...\n");
        }
    }

    /**
     * 给adapter设置热门图片数据
     *
     * @param result 请求得到的结果
     */
    private void setHotListDatas(GetDiscoveryItemResult result) {
        isLast = result.isLast();
        List<CosplayInfo> hotList = result.getList();
        if (hotList != null && discoveryRecycleViewAdapter != null) {
            discoveryRecycleViewAdapter.setHotListData(hotList);
            discoveryRecycleViewAdapter.notifyItemInserted(discoveryRecycleViewAdapter.getItemCount() - 2);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            MyLog.i("hotList == null");
        }
    }

    /**
     * 给adapter设置Banner数据
     *
     * @param bannerInfoList
     */
    private void setBannerDatas(List<BannerInfo> bannerInfoList) {
        if (bannerInfoList != null) {
            discoveryRecycleViewAdapter.setBannerData(bannerInfoList);
            discoveryRecycleViewAdapter.notifyDataSetChanged();
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            MyLog.i("BannerList == null");
        }
    }

    /**
     * 给adapter设置热门话题数据
     *
     * @param topicList 请求到的热门话题列表
     */
    private void setHotTopicData(List<TagPop> topicList) {
        if (topicList != null) {
            discoveryRecycleViewAdapter.setHotTopicData(topicList);
            discoveryRecycleViewAdapter.notifyDataSetChanged();
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            MyLog.i("topicList == null");
        }
    }

    /**
     * 给adapter设置热门活动数据
     *
     * @param events 请求到的活动数据
     */
    private void setHotEventData(List<EventsInfo> events) {
        if (events != null) {
            discoveryRecycleViewAdapter.setHotEventData(events);
            discoveryRecycleViewAdapter.notifyDataSetChanged();
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            MyLog.i("EventList == null");
        }
    }

    @Override
    public void onDestroy() {
        if (mCatalog > 0 && mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregist(this);
        if (discoveryRecycleViewAdapter != null && discoveryRecycleViewAdapter.mBannerHandler != null) {
            discoveryRecycleViewAdapter.mBannerHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.DISCOVERY_RECYCLE_VIEW_FRAGMENT); //统计页面，
        if (discoveryRecycleViewAdapter != null && discoveryRecycleViewAdapter.mBannerHandler != null) {
            discoveryRecycleViewAdapter.mBannerHandler.removeCallbacksAndMessages(null);
            discoveryRecycleViewAdapter.mBannerHandler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.DISCOVERY_RECYCLE_VIEW_FRAGMENT);
        if (discoveryRecycleViewAdapter != null && discoveryRecycleViewAdapter.mBannerHandler != null) {
            discoveryRecycleViewAdapter.mBannerHandler.removeCallbacksAndMessages(null);
        }
    }


    private String getCacheKey() {
        return CACHE_KEY_PREFIX + ClientInfo.getUID() + "_" + mCatalog;
    }

    @Override
    public void onTabReselect() {
        mRecycleView.scrollToPosition(0);
    }

    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            // 防止子类忘记复写这个方法,尤其是有关大咖秀的子类,由父类在在这里防御一下
            if (enity instanceof CosplayInfo) {
                for (int i = 0; i < s; i++) {
                    if (((CosplayInfo) enity).getUcid().equals(((CosplayInfo) data.get(i)).getUcid())) {
                        return true;
                    }
                }
            } else {
                for (int i = 0; i < s; i++) {
                    if (enity.getId() == data.get(i).getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

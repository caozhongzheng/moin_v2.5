package com.moinapp.wuliao.modules.discovery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BeseHaveHeaderListFragment;
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
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.DiscoverySimpleAdapter;
import com.moinapp.wuliao.modules.discovery.adapter.RecommendTopicAdapter;
import com.moinapp.wuliao.modules.discovery.model.BannerInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.DiscoveryList;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.discovery.result.GetBannerResult;
import com.moinapp.wuliao.modules.events.EventsManager;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.HorizontalListView;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.DisplayUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;

/**
 * 发现列表(首页)
 */
public class DiscoveryFragment extends BeseHaveHeaderListFragment<CosplayInfo, GetBannerResult> implements
        OnTabReselectListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(DiscoveryFragment.class.getSimpleName());

    private static final String CACHE_KEY_PREFIX = "discoverylist_";
    private static final String CACHE_KEY_PREFIX_HOT_TOPIC = "HotTopic";

    @InjectView(R.id.title_layout)
    protected CommonTitleBar mTitleLayout;

    private DiscoveryBannerViewPager vp_discovery_banner;
    private List<BannerInfo> mBannerInfoList;
    private LinearLayout ll_discovery_banner_point;
    private Handler mBannerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            currentPosition = vp_discovery_banner.getCurrentItem();
            vp_discovery_banner.setCurrentItem(currentPosition + 1);
            mBannerHandler.sendEmptyMessageDelayed(0, 3000);
        }
    };

    //首页最多有三个活动
    private RoundAngleImageView mIvEvents1;
    private RoundAngleImageView mIvEvents2;
    private RoundAngleImageView mIvEvents3;
    private List<RoundAngleImageView> mEventsImageViewList = new ArrayList<RoundAngleImageView>();

    private int currentPosition;
    private RelativeLayout rl_discovery_hot_topic;
    private HorizontalListView hor_listview;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCatalog > 0) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        if (mCatalog > 0 && mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregist(this);
        mBannerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        mBannerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected DiscoverySimpleAdapter getListAdapter() {
        return new DiscoverySimpleAdapter(getActivity());
    }

    @Override
    protected String getCacheKeyPrefix() {
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String str = bundle.getString("topic");
//            if (str != null) {
//                return str;
//            }
//        }
        return CACHE_KEY_PREFIX + ClientInfo.getUID() + "_" + mCatalog;
    }

    @Override
    protected DiscoveryList parseList(InputStream is) throws Exception {
        DiscoveryList list = XmlUtils.JsontoBean(DiscoveryList.class, is);
        return list;
    }

    @Override
    protected DiscoveryList readList(Serializable seri) {
        return ((DiscoveryList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((CosplayInfo) enity).getUcid().equals(((CosplayInfo) data.get(i))
                        .getUcid())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void sendRequestData() {
        int size = mAdapter.getData().size();
        String lastUcid = null;
        if (size > 0 && mCurrentPage != 0) {
            lastUcid = mAdapter.getData().get(mAdapter.getData().size() - 1).getUcid();
        }

        DiscoveryApi.getHotList(lastUcid, 50, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onUserChange();
        }
    };

    private void onUserChange() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mCurrentPage = 0;
        requestData(true);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mTitleLayout.setVisibility(View.VISIBLE);
        mTitleLayout.setTitleTxt(getString(R.string.main_tab_name_discovery));
        mTitleLayout.hideLeftBtn();
        mTitleLayout.setRightBtnIcon(R.drawable.actionbar_search_icon);
        mTitleLayout.setRightBtnClickAble(true);
        mTitleLayout.setRightBtnOnclickListener(v -> {
            UIHelper.showSearch(getActivity());
        });

        // warn:BaseListFragment 已经设置点击事件.此处不需要,否则影响下拉刷新
//        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (mCatalog > 0) {
////                    if (AppContext.getInstance().isLogin()) {
////                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//                    requestData(true);
////                    } else {
////                        UIHelper.showLoginActivity(getActivity());C
////                    }
//                } else {
//                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//                    requestData(true);
//                }
//            }
//        });
    }

    @Override
    public void onTabReselect() {
//        onRefresh();
        scrollToTop();
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新发现10分钟刷新一次
        if (TDevice.getNetworkType() == TDevice.NETTYPE_WIFI) {
            return 10 * 60;
        } else {
            return 10 * 60;
        }
    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
        DiscoveryApi.getBanner(mDetailHandler);
        DiscoveryManager.getInstance().getTopicList(0, null, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                List<TagPop> topicList = (List<TagPop>) obj;
                if (topicList != null) {
                    new SaveCacheTask(getActivity(), (Serializable) topicList,
                            CACHE_KEY_PREFIX_HOT_TOPIC).execute();

                    updateHotTopic(topicList);
                }
            }

            @Override
            public void onErr(Object obj) {
                new ReadCacheTask(getActivity()).execute(CACHE_KEY_PREFIX_HOT_TOPIC);
            }

            @Override
            public void onNoNetwork() {
                new ReadCacheTask(getActivity()).execute(CACHE_KEY_PREFIX_HOT_TOPIC);

            }
        });

        //获取热门活动
        EventsManager.getInstance().getEvents(null, 20, 1, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    List<EventsInfo> events = (List<EventsInfo>) obj;
                    updateEventsInfo(events);
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    private void updateEventsInfo(List<EventsInfo> events) {
        if (events != null && events.size() > 0) {
            mEventsImageViewList.clear();
            mIvEvents1.setVisibility(View.GONE);
            mIvEvents2.setVisibility(View.GONE);
            mIvEvents3.setVisibility(View.GONE);
            mEventsImageViewList.add(mIvEvents1);
            mEventsImageViewList.add(mIvEvents2);
            mEventsImageViewList.add(mIvEvents3);
            for (int i = 0; i < 3; i++) {
                if (i == events.size()) break;
                if (events.get(i).getIcon() != null) {
                    mEventsImageViewList.get(i).setVisibility(View.VISIBLE);
                    ImageLoaderUtils.displayHttpImage(events.get(i).getIcon().getUri(),
                            mEventsImageViewList.get(i), null, true, null);
                    final int index = i;
                    mEventsImageViewList.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIHelper.showEventsDetail(getActivity(), events.get(index));
                        }
                    });
                }
            }
        }
    }

    private void updateHotTopic(List<TagPop> topicList) {
        RecommendTopicAdapter recommendTopicAdapter = new RecommendTopicAdapter(getActivity());
        recommendTopicAdapter.setData((ArrayList<TagPop>) topicList);
        hor_listview.setAdapter(recommendTopicAdapter);
        hor_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 点击跳转到话题详情
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, topicList.get(position).getName() + "_" + topicList.get(position).getTagPopId());
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK_DISCOVERY, map);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK, map);
                UIHelper.showTopicDetail(getActivity(), topicList.get(position).getName(),
                        topicList.get(position).getType(), topicList.get(position).getTagPopId(), 0);
            }
        });
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    private class ReadCacheTask extends AsyncTask<String, Void, List<TagPop>> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected List<TagPop> doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return (List<TagPop>) seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TagPop> topicList) {
            super.onPostExecute(topicList);
            if (topicList != null) {
                for (int i = 0; i < topicList.size(); i++) {
                    updateHotTopic(topicList);
                }
            } else {
                requestDetailData(false);
            }
        }
    }

    @Override
    public void onRefresh() {
        mBannerHandler.removeCallbacksAndMessages(null);
        super.onRefresh();
    }

    @Override
    protected View initHeaderView() {
        View headerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_discovery_banner, null);
        vp_discovery_banner = (DiscoveryBannerViewPager) headerView.findViewById(R.id.vp_discovery_banner);
        ViewGroup.LayoutParams params = vp_discovery_banner.getLayoutParams();
        params.width = (int) TDevice.getScreenWidth();
        params.height = params.width / 2;

        ll_discovery_banner_point = (LinearLayout) headerView.findViewById(R.id.ll_discovery_banner_point);
        hor_listview = (HorizontalListView) headerView.findViewById(R.id.hor_listview);
        rl_discovery_hot_topic = (RelativeLayout) headerView.findViewById(R.id.rl_discovery_hot_topic);
        rl_discovery_hot_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到话题列表
                UIHelper.showTopicList(getActivity());
            }
        });

        mIvEvents1 = (RoundAngleImageView)headerView.findViewById(R.id.iv_hot_activity1);
        mIvEvents2 = (RoundAngleImageView)headerView.findViewById(R.id.iv_hot_activity2);
        mIvEvents3 = (RoundAngleImageView)headerView.findViewById(R.id.iv_hot_activity3);
        return headerView;
    }

    @Override
    protected String getDetailCacheKey() {
        return "discovery_banner";
    }

    @Override
    protected void executeOnLoadDetailSuccess(GetBannerResult detailBean) {
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (detailBean == null || detailBean.getBanners() == null || detailBean.getBanners().isEmpty()) {
            mBannerInfoList = new ArrayList<BannerInfo>(1);
            mBannerInfoList.add(new BannerInfo());
        } else {
            mBannerInfoList = detailBean.getBanners();
            addBannerPoint(0);

//            iv_discovery_banner_black_point.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                @Override
//                public void onGlobalLayout() {
//                    iv_discovery_banner_black_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    mPointDis = ll_discovery_banner_point.getChildAt(1).getLeft() -
//                            ll_discovery_banner_point.getChildAt(0).getLeft();
//                }
//            });
            vp_discovery_banner.setAdapter(new DiscoveryHeaderAdapter());
            vp_discovery_banner.setCurrentItem(100000 * mBannerInfoList.size());
            mBannerHandler.sendEmptyMessageDelayed(0, 3000);

            vp_discovery_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {

                    addBannerPoint(position);
//                    int dis = newPosition * mPointDis;
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_discovery_banner_black_point.getLayoutParams();
//                    params.leftMargin = dis;
//                    iv_discovery_banner_black_point.setLayoutParams(params);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == SCROLL_STATE_TOUCH_SCROLL || state == SCROLL_STATE_FLING) {
                        mBannerHandler.removeCallbacksAndMessages(null);
                    } else {
                        mBannerHandler.sendEmptyMessageDelayed(0, 3000);
                    }
                }
            });
        }
    }

    /**
     * 添加banner个数的小白点
     */
    private void addBannerPoint(int position) {
        int currPosition = position % mBannerInfoList.size();
        ll_discovery_banner_point.removeAllViews();
        for (int i = 0; i < mBannerInfoList.size(); i++) {
            ImageView point = new ImageView(AppContext.context());
            if (currPosition == i) {
                point.setImageResource(R.drawable.shape_discovery_banner_point_white);
            } else {
                point.setImageResource(R.drawable.shape_discovery_banner_point_gray);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.leftMargin = DisplayUtil.dip2px(AppContext.context(), 4);
            }
            point.setLayoutParams(params);
            ll_discovery_banner_point.addView(point);
        }
    }

    @Override
    protected GetBannerResult getDetailBean(ByteArrayInputStream is) {
        GetBannerResult bannerInfo = XmlUtils.JsontoBean(GetBannerResult.class, is);
        try {
            MyLog.i("发现banner" + bannerInfo.toString());
        } catch (Exception e) {
            MyLog.e(e);
        }
        return bannerInfo;
    }

    class DiscoveryHeaderAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            try {
                int newPosition = position % mBannerInfoList.size();
                if (getActivity() == null) {
                    view = LayoutInflater.from(AppContext.context()).inflate(R.layout.banner_discovery_item, null);
                } else {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.banner_discovery_item, null);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.width = (int) TDevice.getScreenWidth();
                params.height = params.width / 2;

                ImageView imageView = (ImageView) view.findViewById(R.id.iv_discovery_banner_pic);
                imageView.setLayoutParams(params);

                BannerInfo bannerInfo = mBannerInfoList.get(newPosition);
                if (bannerInfo == null) {
                    ImageLoaderUtils.displayHttpImage(null, imageView, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), false, null);
                    container.addView(view);
                    return view;
                } else {
                    ImageLoaderUtils.displayHttpImage(bannerInfo.getPicture().getUri(), imageView, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), false, null);
                    container.addView(view);
                }
                if (imageView != null) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("TP".equalsIgnoreCase(bannerInfo.getType()) && bannerInfo.getTagPop() != null) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put(UmengConstants.ITEM_ID, bannerInfo.getTagPop().getName() + "_" + bannerInfo.getTagPop().getTagPopId());
                                map.put(UmengConstants.FROM, "发现页");
                                MobclickAgent.onEvent(getActivity(), UmengConstants.BANNER_CLICK, map);
                                UIHelper.showTopicDetail(getActivity(), bannerInfo.getTagPop().getName(),
                                        bannerInfo.getTagPop().getType(), bannerInfo.getTagPop().getTagPopId(), 0);
                            }
//                            else if ("WEB".equalsIgnoreCase(bannerInfo.getType()) && bannerInfo.getUrl() != null) {
//                                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                                intent.putExtra(WebViewActivity.URL, bannerInfo.getUrl());
//                                startActivity(intent);
//                            }
                        }
                    });
                }
                return view;
            } catch (Exception e) {
                MyLog.e(e);
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.DISCOVERY_FRAGMENT); //统计页面，
        mBannerHandler.removeCallbacksAndMessages(null);
        mBannerHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.DISCOVERY_FRAGMENT);
        mBannerHandler.removeCallbacksAndMessages(null);
    }
}
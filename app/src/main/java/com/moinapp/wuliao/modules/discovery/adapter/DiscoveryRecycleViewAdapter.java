package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.BannerInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.discovery.ui.DiscoveryBannerViewPager;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.ui.HorizontalListView;
import com.moinapp.wuliao.util.DisplayUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 首页瀑布流适配器
 * Created by guyunfei on 16/6/29.16:48.
 */
public class DiscoveryRecycleViewAdapter extends RecyclerViewHeaderFotterAdapter {

    private static final ILogger MyLog = LoggerFactory.getLogger(DiscoveryRecycleViewAdapter.class.getSimpleName());

    private Activity mActivity;

    //热门推荐
    private List<CosplayInfo> mHotListDatas;
    //banner数据
    private List<BannerInfo> mBannerDatas;
    //热门话题
    private List<TagPop> mHotTopicDatas;
    //热门活动
    private List<EventsInfo> mHotEventDatas;

    private RecyclerView mRecyclerView;
    private HeaderViewHolder headerViewHolder;

    private int currentPosition;
    public Handler mBannerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (vp_discovery_banner != null) {

                currentPosition = vp_discovery_banner.getCurrentItem();
                vp_discovery_banner.setCurrentItem(currentPosition + 1);
                mBannerHandler.sendEmptyMessageDelayed(0, 3000);
            }
        }
    };

    public DiscoveryRecycleViewAdapter(Activity activity, RecyclerView recyclerView) {
        mActivity = activity;
        mRecyclerView = recyclerView;
        setSpanCount(recyclerView);
    }

    public void setHotListData(List<CosplayInfo> mDatas) {
        this.mHotListDatas = mDatas;
    }

    public void addHotListData(List<CosplayInfo> mDatas) {
        mHotListDatas.addAll(mDatas);
    }

    public void setBannerData(List<BannerInfo> mDatas) {
        this.mBannerDatas = mDatas;
    }

    public void setHotTopicData(List<TagPop> mDatas) {
        this.mHotTopicDatas = mDatas;
    }

    public void setHotEventData(List<EventsInfo> mDatas) {
        this.mHotEventDatas = mDatas;
    }

    public String getLastId() {
        if (mHotListDatas != null && mHotListDatas.size() > 0) {
            CosplayInfo cosplayInfo = mHotListDatas.get(mHotListDatas.size() - 1);
            if (cosplayInfo != null) {
                return cosplayInfo.getUcid();
            }
        }
        return null;
    }

    public List<CosplayInfo> getDatas() {
        if (mHotListDatas != null) {
            return mHotListDatas;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mHotListDatas != null) {
            return mHotListDatas.size() + 2;
        }
        return 2;
    }

    /**
     * 创建正常布局viewHolder
     *
     * @param parent viewGroup
     * @return viewHolder
     */
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent) {
        DiscoveryRecycleViewHolder holder = new DiscoveryRecycleViewHolder(
                LayoutInflater.from(mActivity).inflate(R.layout.item_discovery_simple,
                        parent, false));
        return holder;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_discovery_banner, parent, false);
        headerViewHolder = new HeaderViewHolder(view);
        return headerViewHolder;
    }

    @Override
    protected void setHeaderLayoutParams(StaggeredGridLayoutManager.LayoutParams layoutParams) {
        headerViewHolder.llyLoading.setLayoutParams(layoutParams);
    }


    private int mPosition = 0;
    private boolean animShow = true;

    /**
     * 绑定正常布局viewHolder
     *
     * @param holder   viewHolder
     * @param position position
     */
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < 0) {
            return;
        }
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        DiscoveryRecycleViewHolder viewHolder = (DiscoveryRecycleViewHolder) holder;
        if (position >= mHotListDatas.size()) {
            return;
        }
        CosplayInfo cosplayInfo = mHotListDatas.get(position);
        if (cosplayInfo != null) {
            if (cosplayInfo.getPicture() != null) {
                if (cosplayInfo.getPicture().getUri() != null) {
                    ImageLoaderUtils.displayHttpImage(false, cosplayInfo.getPicture().getUri(), viewHolder.image,
                            null, animShow, null);
                }
            }

            if (cosplayInfo.getContent() != null && !cosplayInfo.getContent().equals("")) {
                viewHolder.desc.setVisibility(View.VISIBLE);
                viewHolder.desc.setText(cosplayInfo.getContent());
            } else {
                viewHolder.desc.setVisibility(View.GONE);
            }
        }

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= 0 && position < 6) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(UmengConstants.ITEM_ID, cosplayInfo.getUcid() + "");
                    map.put(UmengConstants.FROM, "发现页");
                    MobclickAgent.onEvent(mActivity, UmengConstants.HOTPIC_CLICK, map);
                }
                UIHelper.showDiscoveryCosplayDetail(mActivity, cosplayInfo, cosplayInfo.getUcid(), TimeUtils.getCurrentTimeInLong());

            }
        });
    }

    /**
     * 绑定头布局ViewHolder
     *
     * @param holder
     */
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder vh = (HeaderViewHolder) holder;
        if (mBannerDatas != null) {
            updateBanner(vh, mBannerDatas);
        } else {
            mBannerDatas = new ArrayList<BannerInfo>(1);
            mBannerDatas.add(new BannerInfo());
        }

        if (mHotTopicDatas != null) {
            updateHotTopic(vh, mHotTopicDatas);
        }
        if (mHotEventDatas != null) {
            updateEventsInfo(vh, mHotEventDatas);
        }
    }

    /**
     * 更新顶部banner
     *
     * @param vh
     * @param detailBean
     */
    private void updateBanner(HeaderViewHolder vh, List<BannerInfo> detailBean) {
        if (detailBean == null || detailBean.isEmpty()) {
            return;
        } else {
            addBannerPoint(0);

            vp_discovery_banner.setAdapter(new DiscoveryHeaderAdapter());
            vp_discovery_banner.setCurrentItem(100000 * mBannerDatas.size());
            mBannerHandler.sendEmptyMessageDelayed(0, 3000);

            vp_discovery_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    addBannerPoint(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    if (state == 1 || state == 2) {
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
        int currPosition = getBannerPosition(position);
        ll_discovery_banner_point.removeAllViews();
        for (int i = 0; i < mBannerDatas.size(); i++) {
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

    /**
     * 获取banner的位置
     *
     * @param position
     * @return
     */
    private int getBannerPosition(int position) {
        if (mBannerDatas == null || mBannerDatas.isEmpty()) {
            return -1;
        }
        return position % mBannerDatas.size();
    }

    /**
     * 更新热门话题列表
     *
     * @param vh
     * @param topicList
     */
    private void updateHotTopic(HeaderViewHolder vh, List<TagPop> topicList) {
        RecommendTopicAdapter recommendTopicAdapter = new RecommendTopicAdapter(mActivity);
        recommendTopicAdapter.setData((ArrayList<TagPop>) topicList);
        vh.hor_listview.setAdapter(recommendTopicAdapter);
        vh.hor_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 点击跳转到话题详情
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, topicList.get(position).getName() + "_" + topicList.get(position).getTagPopId());
                MobclickAgent.onEvent(mActivity, UmengConstants.TOPIC_CLICK_DISCOVERY, map);
                MobclickAgent.onEvent(mActivity, UmengConstants.TOPIC_CLICK, map);
                UIHelper.showTopicDetail(mActivity, topicList.get(position).getName(),
                        topicList.get(position).getType(), topicList.get(position).getTagPopId(), 0);
            }
        });
    }


    private List<RoundAngleImageView> mEventsImageViewList = new ArrayList<RoundAngleImageView>();

    /**
     * 更新热门活动
     *
     * @param vh
     * @param events
     */
    private void updateEventsInfo(HeaderViewHolder vh, List<EventsInfo> events) {
        if (events != null && events.size() > 0) {
            mEventsImageViewList.clear();
            vh.mIvEvents1.setVisibility(View.GONE);
            vh.mIvEvents2.setVisibility(View.GONE);
            vh.mIvEvents3.setVisibility(View.GONE);
            mEventsImageViewList.add(vh.mIvEvents1);
            mEventsImageViewList.add(vh.mIvEvents2);
            mEventsImageViewList.add(vh.mIvEvents3);
            for (int i = 0; i < 3; i++) {
                if (i == events.size()) break;
                if (events.get(i).getIcon() != null) {
                    mEventsImageViewList.get(i).setVisibility(View.VISIBLE);
                    final int index = i;
                    ImageLoaderUtils.displayHttpImage(events.get(i).getIcon().getUri(),
                            mEventsImageViewList.get(i), null, true, new Callback() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinish(int result) {
                                    DiscoveryManager.getInstance().writeDiscoveryLoadInfo2File(System.currentTimeMillis() +
                                            ": ACTIVITY->loaded activity" + index  + " data finished...\n");
                                }
                            });
                    mEventsImageViewList.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIHelper.showEventsDetail(mActivity, events.get(index));
                        }
                    });
                }
            }
        }
    }


    public DiscoveryBannerViewPager vp_discovery_banner;
    public LinearLayout ll_discovery_banner_point;

    /**
     * 头布局ViewHolder
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final HorizontalListView hor_listview;
        private final RelativeLayout rl_discovery_hot_topic;
        private final RoundAngleImageView mIvEvents1;
        private final RoundAngleImageView mIvEvents2;
        private final RoundAngleImageView mIvEvents3;
        public LinearLayout llyLoading;

        public HeaderViewHolder(View view) {
            super(view);
            vp_discovery_banner = (DiscoveryBannerViewPager) view.findViewById(R.id.vp_discovery_banner);
            ViewGroup.LayoutParams params = vp_discovery_banner.getLayoutParams();
            params.width = (int) TDevice.getScreenWidth();
            params.height = params.width / 2;

            ll_discovery_banner_point = (LinearLayout) view.findViewById(R.id.ll_discovery_banner_point);
            hor_listview = (HorizontalListView) view.findViewById(R.id.hor_listview);
            rl_discovery_hot_topic = (RelativeLayout) view.findViewById(R.id.rl_discovery_hot_topic);
            rl_discovery_hot_topic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 跳转到话题列表
                    UIHelper.showTopicList(mActivity);
                }
            });

            mIvEvents1 = (RoundAngleImageView) view.findViewById(R.id.iv_hot_activity1);
            mIvEvents2 = (RoundAngleImageView) view.findViewById(R.id.iv_hot_activity2);
            mIvEvents3 = (RoundAngleImageView) view.findViewById(R.id.iv_hot_activity3);
            llyLoading = (LinearLayout) view.findViewById(R.id.ll_discovery_header);
        }
    }

    public ProgressBar progressBar;
    public TextView tvLoading;


    /**
     * 底部加载更多布局ViewHolder
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llyLoading;


        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view
                    .findViewById(R.id.progressbar);
            tvLoading = (TextView) view.findViewById(R.id.text);
            tvLoading.setTextSize(13);
            llyLoading = (LinearLayout) view.findViewById(R.id.ll_loading_more);
            llyLoading.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }


    /**
     * 正常布局ViewHolder
     */
    class DiscoveryRecycleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        RoundAngleImageView image;
        TextView desc;

        public DiscoveryRecycleViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView.findViewById(R.id.ll_container);
            image = (RoundAngleImageView) itemView.findViewById(R.id.image);
            desc = (TextView) itemView.findViewById(R.id.desc);
        }
    }

    /**
     * 头布局banner适配器
     */
    class DiscoveryHeaderAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            try {
                int newPosition = getBannerPosition(position);
                if (mActivity == null) {
                    view = LayoutInflater.from(AppContext.context()).inflate(R.layout.banner_discovery_item, null);
                } else {
                    view = LayoutInflater.from(mActivity).inflate(R.layout.banner_discovery_item, null);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.width = (int) TDevice.getScreenWidth();
                params.height = params.width / 2;

                ImageView imageView = (ImageView) view.findViewById(R.id.iv_discovery_banner_pic);
                imageView.setLayoutParams(params);

                BannerInfo bannerInfo = newPosition == -1 ? null : mBannerDatas.get(newPosition);
                if (imageView != null && bannerInfo != null) {
                    if (bannerInfo.getPicture() != null) {
                        if (bannerInfo.getPicture().getUri() != null) {
                            ImageLoaderUtils.displayHttpImage(bannerInfo.getPicture().getUri(), imageView, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), false, new Callback() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinish(int result) {
                                    DiscoveryManager.getInstance().writeDiscoveryLoadInfo2File(System.currentTimeMillis() +
                                            ": BANNER->loaded banner data finished...\n");
                                }
                            });
                            container.addView(view);
                        }
                    }
                } else {
                    ImageLoaderUtils.displayHttpImage(null, imageView, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), false, null);
                    container.addView(view);
                    return view;
                }
                if (imageView != null) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("TP".equalsIgnoreCase(bannerInfo.getType()) && bannerInfo.getTagPop() != null) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put(UmengConstants.ITEM_ID, bannerInfo.getTagPop().getName() + "_" + bannerInfo.getTagPop().getTagPopId());
                                map.put(UmengConstants.FROM, "发现页");
                                MobclickAgent.onEvent(mActivity, UmengConstants.BANNER_CLICK, map);
                                UIHelper.showTopicDetail(mActivity, bannerInfo.getTagPop().getName(),
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

}

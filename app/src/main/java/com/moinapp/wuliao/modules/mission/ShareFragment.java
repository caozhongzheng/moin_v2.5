package com.moinapp.wuliao.modules.mission;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.DiscoveryRecycleViewAdapter;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.result.GetDiscoveryItemResult;
import com.moinapp.wuliao.modules.discovery.ui.RefreshLoadMoreRecyclerView;
import com.moinapp.wuliao.modules.events.EventsManager;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by guyunfei on 16/7/25.11:20.
 */
public class ShareFragment extends Fragment {

    private static final ILogger MyLog = LoggerFactory.getLogger(ShareFragment.class.getSimpleName());

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

    private ShareAdapter mAdapter;

    private boolean isLast;

    public int getLayoutId() {
        return R.layout.fragment_discovery_recycleview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mSwipeRefreshLayout.setEnabled(false);
        mMissionBtn.setVisibility(View.GONE);

        mTitleLayout.setTitleTxt("分享");
        mTitleLayout.hideRightBtn();
        mTitleLayout.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(true);
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        });

        mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        if (mAdapter == null) {
            mAdapter = new ShareAdapter(getActivity(), mRecycleView);
        }
        mRecycleView.setAdapter(mAdapter);

        mRecycleView.setOnLoadMoreListener(new RefreshLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ShareFragment.this.onLoadMore();
            }
        });
    }

    private void initData(boolean b) {
        EventsManager.getInstance().getShareEvents(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    EventsInfo eventsInfo = (EventsInfo) obj;
                    setHotEventData(eventsInfo);
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });


        //获取热门列表信息
        DiscoveryManager.getInstance().getHotList(null, 50, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                GetDiscoveryItemResult result = (GetDiscoveryItemResult) obj;
                if (result != null) {
                    setHotListDatas(result);
                }
            }

            @Override
            public void onErr(Object obj) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            }

            @Override
            public void onNoNetwork() {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
    }


    /**
     * 加载更多
     */
    private void onLoadMore() {
        String lastId = mAdapter.getLastId();
        if (!isLast) {
            if (mAdapter != null) {
                mAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_LOADING);
            }
            DiscoveryManager.getInstance().getHotList(lastId, 50, new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    GetDiscoveryItemResult result = (GetDiscoveryItemResult) obj;

                    if (result != null) {
                        isLast = result.isLast();
                        if (isLast) {
                            if (mAdapter != null) {
                                mAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_NO_MORE);
                            }
                        }
                        List<CosplayInfo> hotList = result.getList();
                        if (hotList != null) {
                            if (mAdapter.getDatas() != null) {
                                // 去除重复数据
                                for (int i = 0; i < hotList.size(); i++) {
                                    if (compareTo(mAdapter.getDatas(), hotList.get(i))) {
                                        hotList.remove(i);
                                        i--;
                                    }
                                }
                            }
                            mAdapter.addHotListData(hotList);
                            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 2);
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            MyLog.i("hotList == null");
                        }
                    }
                }

                @Override
                public void onErr(Object obj) {
                    if (mAdapter != null) {
                        mAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_ERROR);
                    }
                }

                @Override
                public void onNoNetwork() {
                    if (mAdapter != null) {
                        mAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_ERROR);
                    }
                }
            });
        } else {
            if (mAdapter != null) {
                mAdapter.setFooterMode(DiscoveryRecycleViewAdapter.FOOTER_MODE_NO_MORE);
            }
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
        if (hotList != null && mAdapter != null) {

            if (mAdapter.getDatas() != null) {
                // 去除重复数据
                for (int i = 0; i < hotList.size(); i++) {
                    if (compareTo(mAdapter.getDatas(), hotList.get(i))) {
                        hotList.remove(i);
                        i--;
                    }
                }
            }
            mAdapter.setHotListData(hotList);
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 2);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            MyLog.i("hotList == null");
        }
    }

    /**
     * 给adapter设置热门活动数据
     *
     * @param event 请求到的活动数据
     */
    private void setHotEventData(EventsInfo event) {
        if (event != null) {
            mAdapter.setHotEventData(event);
            mAdapter.notifyDataSetChanged();
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            MyLog.i("Event == null");
        }
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.DISCOVERY_RECYCLE_VIEW_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.DISCOVERY_RECYCLE_VIEW_FRAGMENT);
    }
}

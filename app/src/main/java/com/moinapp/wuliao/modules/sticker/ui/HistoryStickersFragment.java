package com.moinapp.wuliao.modules.sticker.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.sticker.StickerApi;
import com.moinapp.wuliao.modules.sticker.adapter.MyHistoryStickerAdapter;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerInfoList;
import com.moinapp.wuliao.modules.sticker.ui.mall.StickerCenterViewPagerFragment;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的历史贴纸列表
 */
public class HistoryStickersFragment extends BaseListFragment<StickerInfo> implements OnTabReselectListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(HistoryStickersFragment.class.getSimpleName());

    protected static final String TAG = HistoryStickersFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "history_list_";
    private static final int PAGE_SIZE = 30;//每次请求服务器下发多少个
    private String mUid = ClientInfo.getUID();

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_title;
    }

    @Override
    protected void addListHeader() {
        if (mListView.getHeaderViewsCount() == 0) {
            View headerView = View.inflate(getActivity(), R.layout.layout_title_down_grey_area, null);
            mListView.addHeaderView(headerView);
            View headerViewWhite = View.inflate(getActivity(), R.layout.layout_title_down_white_area, null);
            mListView.addHeaderView(headerViewWhite);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                getActivity().finish();
            }
        });
        title.setLeftBtnIcon(R.drawable.close_black);
        title.hideRightBtn();

        mListView.setBackgroundColor(Color.parseColor("#F0EFF4"));

        initEemptyLayout();
    }

    protected void initEemptyLayout() {
        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.setEmptyImage(R.drawable.no_date_history_sticker);
            mErrorLayout.setNoDataContent(getString(R.string.no_history_use_sticker));
            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setOnLayoutClickListener(v -> {
                List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragments.size(); i++) {
                    if ((fragments.get(i).getClass()).equals(StickerCenterViewPagerFragment.class)) {
                        StickerCenterViewPagerFragment stickerCenterViewPagerFragment = (StickerCenterViewPagerFragment) fragments.get(i);
                        stickerCenterViewPagerFragment.setGroupCheck(0);
                    }
                }
            });
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            requestData(true);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            mErrorLayout.setOnLayoutClickListener(v -> {
                if (AppContext.getInstance().isLogin()) {
                    requestData(true);
                } else {
                    UIHelper.showLoginActivity(getActivity(), 0);
                }
            });
            return;
        }
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(
                Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected MyHistoryStickerAdapter getListAdapter() {
        return new MyHistoryStickerAdapter(getActivity(), getString(R.string.my_history));
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUid;
    }

    @Override
    protected StickerInfoList parseList(InputStream is) throws Exception {
        StickerInfoList list = XmlUtils.JsontoBean(StickerInfoList.class, is);
        if (list == null) {
            MyLog.i("history list is null");
        } else if (list.getList() != null){
            MyLog.i("history list  size =" + list.getList().size());

        }
        return list;
    }

    @Override
    protected StickerInfoList readList(Serializable seri) {
        return ((StickerInfoList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((StickerInfo) enity).getStickerId() == ((StickerInfo) data.get(i))
                        .getStickerId()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void sendRequestData() {
        int size = mAdapter.getData().size();
        if (size > 0 && mCurrentPage != 0) {
            String lastid = mAdapter.getData().get(mAdapter.getData().size() - 1).getStickerId();
            StickerApi.getHistoryStickers(PAGE_SIZE, lastid, mHandler);
        } else {
            StickerApi.getHistoryStickers(PAGE_SIZE, null, mHandler);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupContent();
        }
    };

    private void setupContent() {
        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.hideNoLogin();
            initEemptyLayout();
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            requestData(true);
        } else {
            if (isAdded()) {
                mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
                //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            }
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mCatalog = AppContext.getInstance().getLoginUid();
            super.requestData(refresh);
        } else {
            if (isAdded()) {
                mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            }
        }
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.HISTORY_STICKERS_FRAGMENT); //统计页面，
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.HISTORY_STICKERS_FRAGMENT);
    }


    @Override
    protected int getPageSize() {
        return 30;
    }
}
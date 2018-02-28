package com.moinapp.wuliao.modules.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.modules.discovery.adapter.LikeCosplayAdapter;
import com.moinapp.wuliao.modules.discovery.ui.LikeCosplayFragment;
import com.moinapp.wuliao.modules.mine.MineApi;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * 搜索用户的界面
 * Created by liujiancheng on 15/11/25.
 */
public class SearchUserFragment extends LikeCosplayFragment {
    private String mKeyword;
    private SearchTextChangedListener mSearchListener;
    @InjectView(R.id.tv_hot_search)
    public TextView mText;
    @InjectView(R.id.title_down_area)
    protected View title_down_area;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.INTENT_ACTION_USER_CHANGE)) {
                requestData(true);
            }
        }
    };

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(
                Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    protected void addListHeader() {
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setBackgroundColor(Color.parseColor("#f0eff4"));

        mSearchListener = new SearchTextChangedListener() {
            @Override
            public void onSearchTextChanged(String searchText) {
                mKeyword = searchText;
                hideHotUser();
                doSearch();
            }

            @Override
            public void onSearchTextInvalid() {
//                if (mAdapter != null) {
//                    mAdapter.clear();
//                    mAdapter.setState(-1);
//                    mListView.setBackgroundColor(Color.parseColor("#f0eff4"));
//                    title_down_area.setVisibility(View.GONE);
//                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//                }
                Log.i("SearchUserFragment", "onSearchTextInvalid...");
                mKeyword = "";
                showHotUser();
                doSearch();
            }
        };
        SearchViewPagerFragment.setUserListener(mSearchListener);
        mErrorLayout.setEmptyImage(R.drawable.no_content_black);
        mErrorLayout.setNoDataContent(getString(R.string.error_view_no_data));

        mKeyword = "";
        doSearch();
    }

    private void doSearch() {
        if (TDevice.hasInternet()) {
            handleSearch();
        } else {
            hideHotUser();
            mAdapter.clear();
            mCurrentPage = 0;
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    }

    private void handleSearch() {
        mListView.setBackgroundColor(Color.parseColor("#ffffff"));
        mAdapter.clear();
        requestData(true);
    }

    @Override
    protected LikeCosplayAdapter getListAdapter() {
        return new LikeCosplayAdapter(getActivity(), UserDefineConstants.FOLLOW_SEARCH);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_user;
    }


    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            MineApi.searchUser(mKeyword, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getUId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        MineApi.searchUser(mKeyword, lastid, mHandler);
    }

    @Override
    public void onRefresh() {
        //没有下拉刷新, 下拉时显式调用父类方法表示加载完毕
        super.executeOnLoadFinish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SEARCH_USER_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SEARCH_USER_FRAGMENT);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<UserInfo> data, boolean isLast) {
        if (data == null) {
            data = new ArrayList<UserInfo>();
        }

//        if (mResult != null && mResult.getResult() == 0) {
//            AppContext.showToast(R.string.error_view_load_error_click_to_refresh);
//        }

        if (mAdapter == null) {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);
        }

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (mCurrentPage == 0) {
            mAdapter.clear();
        }

        // 去除重复数据
        for (int i = 0; i < data.size(); i++) {
            if (compareTo(mAdapter.getData(), data.get(i))) {
                data.remove(i);
                i--;
            }
        }
        int adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
        if (isLast) {
            if ((mAdapter.getDataSize() + data.size()) == 0) {
                adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
            } else {
                adapterState = ListBaseAdapter.STATE_NO_MORE;
                mAdapter.notifyDataSetChanged();
            }
        } else {
            adapterState = ListBaseAdapter.STATE_LOAD_MORE;
        }
        mAdapter.setState(adapterState);
        mAdapter.addData(data);
        // 判断等于是因为最后有一项是listview的状态
        if (mAdapter.getCount() == 1) {

            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            } else {
                mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void hideHotUser() {
        mText.setVisibility(View.GONE);
        title_down_area.setVisibility(View.VISIBLE);
    }

    private void showHotUser() {
        mText.setVisibility(View.VISIBLE);
        title_down_area.setVisibility(View.GONE);
    }
}

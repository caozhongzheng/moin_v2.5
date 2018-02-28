package com.moinapp.wuliao.modules.mine.message;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.mine.MineApi;
import com.moinapp.wuliao.modules.mine.adapter.MyMessagesAdapter;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.modules.mine.model.UserActivityList;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 所有消息列表
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AllMessagesFragment extends BaseListFragment<UserActivity> implements OnTabReselectListener {

    public final static int PAGE_SIZE = 10;
    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "my_all_messages_list";

    protected String mUid = ClientInfo.getUID();

    @Override
    protected void addListHeader() {
        if (mListView.getHeaderViewsCount() == 0) {
            View headerView = View.inflate(getActivity(), R.layout.layout_title_down_grey_area, null);
            mListView.addHeaderView(headerView);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        if (ClientInfo.isLoginUser(mUid)) {
            mErrorLayout.setNoDataContent(getString(R.string.no_message));
        }
        mErrorLayout.hideTitleDownArea();
        mErrorLayout.setEmptyImage(R.drawable.no_data_message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }

    @Override
    protected MyMessagesAdapter getListAdapter() {
        return new MyMessagesAdapter(getActivity());
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUid;
    }

    @Override
    protected UserActivityList parseList(InputStream is) throws Exception {
        UserActivityList result = XmlUtils.JsontoBean(UserActivityList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        Log.i("ljc", "UserActivityList.size = " + result.getList().size());
        return result;
    }

    @Override
    protected UserActivityList readList(Serializable seri) {
        return ((UserActivityList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((UserActivity) enity).getActivityId() != null) {
            for (int i = 0; i < s; i++) {
                if (((UserActivity) enity).getActivityId().equalsIgnoreCase(((UserActivity) data.get(i)).getActivityId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            MineApi.getMyMessages(getAction(), null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getActivityId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        MineApi.getMyMessages(getAction(), lastid, mHandler);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            ((MyMessagesAdapter)mAdapter).unregisterEventBus();
        }
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    protected int getAction() {
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.ALL_MESSAGES_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.ALL_MESSAGES_FRAGMENT);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<UserActivity> data, boolean isLast) {
        if (data == null) {
            data = new ArrayList<UserActivity>();
        }

        if (mResult != null && mResult.getResult() == 0) {
            AppContext.showToast(R.string.error_view_load_error_click_to_refresh);
        }

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
}

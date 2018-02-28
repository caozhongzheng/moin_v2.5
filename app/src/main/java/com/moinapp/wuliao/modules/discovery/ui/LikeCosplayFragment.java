package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.LikeCosplayAdapter;
import com.moinapp.wuliao.modules.discovery.model.LikeList;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 大咖秀图片的赞列表
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LikeCosplayFragment extends BaseListFragment<UserInfo>
        implements OnTabReselectListener {

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "like_cosplay_list";

    private String mUcid;

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
        mErrorLayout.hideTitleDownArea();
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUcid = args.getString(Constants.BUNDLE_KEY_ID);
        }

        IntentFilter filter = new IntentFilter(
                Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected LikeCosplayAdapter getListAdapter() {
        return new LikeCosplayAdapter(getActivity(), UserDefineConstants.FOLLOW_LIKE_LIST);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUcid;
    }

    @Override
    protected LikeList parseList(InputStream is) throws Exception {
        LikeList result = XmlUtils.JsontoBean(LikeList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        return result;
    }

    @Override
    protected LikeList readList(Serializable seri) {
        return ((LikeList) seri);
    }

    @Override
    protected int getPageSize() {
        return 10;
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((UserInfo) enity).getUId() != null) {
            for (int i = 0; i < s; i++) {
                if (((UserInfo) enity).getUId().equalsIgnoreCase(((UserInfo) data.get(i)).getUId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            DiscoveryApi.getLikeList(mUcid, null, mHandler);
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
        DiscoveryApi.getLikeList(mUcid, lastid, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        UserInfo item = (UserInfo) mAdapter.getItem(position);
        if (item != null) {
            UIHelper.showFriends(getActivity(), item.getUId(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.INTENT_ACTION_USER_CHANGE)) {
                requestData(true);
            }
        }
    };
}

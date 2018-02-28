package com.moinapp.wuliao.modules.events.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.modules.events.EventsApi;
import com.moinapp.wuliao.modules.events.adapter.EventsListAdapter;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.modules.events.model.EventsInfoList;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import butterknife.InjectView;

/**
 * 首页活动列表
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EventsFragment extends BaseListFragment<EventsInfo> {

    public final static int PAGE_SIZE = 20;

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "event_list";

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

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

        mAdapter.setFinishText(R.string.no_more_events);

        title.hideRightBtn();
        title.hideLeftBtn();
        title.setTitleTxt(getString(R.string.events_title));

        title.setTitleOnclickListener(v -> {
            scrollToTop();
        });
        mErrorLayout.setEmptyImage(R.drawable.no_data_fan_follow);
        mErrorLayout.setClickable(true);
        mErrorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(true);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_title;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData(false);
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected EventsListAdapter getListAdapter() {
        return new EventsListAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog;
    }

    @Override
    protected EventsInfoList parseList(InputStream is) throws Exception {
        EventsInfoList result = XmlUtils.JsontoBean(EventsInfoList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        return result;
    }

    @Override
    protected EventsInfoList readList(Serializable seri) {
        return ((EventsInfoList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((EventsInfo) enity).getEventId().equalsIgnoreCase(((EventsInfo) data.get(i)).getEventId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            EventsApi.getEvents(null, PAGE_SIZE, 0, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getEventId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        EventsApi.getEvents(lastid, PAGE_SIZE, 0, mHandler);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }
}

package com.moinapp.wuliao.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.empty.EmptyLayout;

@SuppressLint("NewApi")
public abstract class BaseLazyListFragment<T extends Entity> extends BaseListFragment {

    private static final ILogger MyLog = LoggerFactory.getLogger(BaseLazyListFragment.class.getSimpleName());
    protected boolean isVisible;
    protected boolean hasData = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasData = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onRefresh() {
        hasData = false;
        super.onRefresh();
    }

    protected void onInvisible() {
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void lazyLoad() {
        if (!hasData) {
            initData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hasData = false;
    }

    @Override
    public void initView(View view) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentPage = 0;
                mState = STATE_REFRESH;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);


        if (mStoreEmptyState != -1) {
            mErrorLayout.setErrorType(mStoreEmptyState);
        }
    }


    @Override
    public void initData() {
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);

            if (requestDataIfViewCreated()) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                mState = STATE_NONE;
                requestData(false);
            } else {
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }
        hasData = true;
    }
}

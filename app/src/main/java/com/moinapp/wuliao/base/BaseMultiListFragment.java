package com.moinapp.wuliao.base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseMultiListFragment<T extends Entity> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
        OnScrollListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(BaseMultiListFragment.class.getSimpleName());

    protected FloatingActionButton mExchangeBt;
    protected EmptyLayout mOutsideErrorLayout;

    protected static final int LIST_COUNT = 2;

    protected SwipeRefreshLayout[] mSwipeRefreshLayout = new SwipeRefreshLayout[LIST_COUNT];
    protected FrameLayout[] mLayout = new FrameLayout[LIST_COUNT];
    protected ListView[] mListView = new ListView[LIST_COUNT];
    protected EmptyLayout[] mErrorLayout = new EmptyLayout[LIST_COUNT];
    protected ListBaseAdapter<T>[] mAdapter = new ListBaseAdapter[LIST_COUNT];
    protected int[] mStoreEmptyState = new int[LIST_COUNT];// {-1, -1};

    protected int[] mCurrentPage = new int[LIST_COUNT]; //{0, 0};

    protected int mCatalog = 1;
    // 第几个布局
    protected int mCurrentIndex = 0;
    // 错误信息
    protected BaseHttpResponse[] mResult = new BaseHttpResponse[LIST_COUNT];

    private AsyncTask<String, Void, ListEntity<T>> mCacheTask;
    private ParserTask mParserTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_multi_pull_refresh_listview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mExchangeBt = (FloatingActionButton) view.findViewById(R.id.exchange);
        mOutsideErrorLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        // 第一个布局
        FrameLayout mLayout1 = (FrameLayout) view.findViewById(R.id.layout_1);
        SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout1);
        ListView mListView1 = (ListView) view.findViewById(R.id.listview1);
        EmptyLayout mErrorLayout1 = (EmptyLayout) view.findViewById(R.id.list_error_layout1);
//        mErrorLayout1.setNoDataContent("没有动态列表数据了");
        // 第二个布局
        FrameLayout mLayout2 = (FrameLayout) view.findViewById(R.id.layout_2);
        SwipeRefreshLayout mSwipeRefreshLayout2 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout2);
        ListView mListView2 = (ListView) view.findViewById(R.id.listview2);
        EmptyLayout mErrorLayout2 = (EmptyLayout) view.findViewById(R.id.list_error_layout2);
//        mErrorLayout2.setNoDataContent("没有九宫格");

        for (int i = 0; i < LIST_COUNT; i++) {
            mStoreEmptyState[i] = -1;
            mCurrentPage[i] = 0;
        }
        mSwipeRefreshLayout[0] = mSwipeRefreshLayout1;
        mSwipeRefreshLayout[1] = mSwipeRefreshLayout2;
        mLayout[0] = mLayout1;
        mLayout[1] = mLayout2;
        mListView[0] = mListView1;
        mListView[1] = mListView2;
        mErrorLayout[0] = mErrorLayout1;
        mErrorLayout[1] = mErrorLayout2;

        initView(view);
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(Constants.BUNDLE_KEY_CATALOG, 0);
            mCurrentIndex = args.getInt(Constants.BUNDLE_KEY_INDEX, 0);
        }
    }

    protected View.OnClickListener exchangeListener;
    @Override
    public void initView(View view) {
        for (int i = 0; i < LIST_COUNT; i++) {
            mSwipeRefreshLayout[i].setOnRefreshListener(this);
            mSwipeRefreshLayout[i].setColorSchemeResources(
                    R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                    R.color.swiperefresh_color3, R.color.swiperefresh_color4);

            final int index = i;
            mErrorLayout[i].setOnLayoutClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCurrentPage[index] = 0;
                    mState = STATE_REFRESH;
                    mErrorLayout[index].setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData(true);
                }
            });

            mListView[i].setOnItemClickListener(this);
            mListView[i].setOnScrollListener(this);

//            mySetAdapter(i);
            if (mStoreEmptyState[i] != -1) {
                mErrorLayout[i].setErrorType(mStoreEmptyState[i]);
            }
        }
        /*
        mSwipeRefreshLayout[mCurrentIndex].setOnRefreshListener(this);
        mSwipeRefreshLayout[mCurrentIndex].setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mErrorLayout[mCurrentIndex].setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentPage[mCurrentIndex] = 0;
                mState = STATE_REFRESH;
                mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });

        mListView[mCurrentIndex].setOnItemClickListener(this);
        mListView[mCurrentIndex].setOnScrollListener(this);

        fillData();
*/
        exchangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doExchange();
            }
        };
        mExchangeBt.setOnClickListener(exchangeListener);
    }

    protected void doExchange() {
        int last = mCurrentIndex;

        mCurrentIndex = ++mCurrentIndex % LIST_COUNT;
        mLayout[mCurrentIndex].setVisibility(View.VISIBLE);
        mLayout[last].setVisibility(View.GONE);
        if (mCurrentIndex == 0) {
            mExchangeBt.setImageResource(R.drawable.ac_gridmode);
        } else {
            mExchangeBt.setImageResource(R.drawable.ac_linemode);
        }
        for (int i = 0; i < LIST_COUNT; i++) {
            if (i == mCurrentIndex) {
                mLayout[i].setVisibility(View.VISIBLE);
                fillData();
            } else {
                mLayout[i].setVisibility(View.GONE);
            }
        }
    }

    protected void mySetAdapter(int i) {
        if (i == mCurrentIndex) {
//                MyLog.i("jgg--BASE 显示viewpage " + i);
            mLayout[i].setVisibility(View.VISIBLE);

//                MyLog.i(i+" 你要看页面adapte=: " + mAdapter[i]);
            if (mAdapter[i] != null) {
//                    MyLog.i("fillData setadapte=:");
                mListView[mCurrentIndex].setAdapter(mAdapter[mCurrentIndex]);
                mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.HIDE_LAYOUT);
            } else {
//                    MyLog.i("fillData getListAdapter=:");
                mAdapter[mCurrentIndex] = getListAdapter();
                mListView[mCurrentIndex].setAdapter(mAdapter[mCurrentIndex]);

                if (requestDataIfViewCreated()) {
                    mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NETWORK_LOADING);
                    mState = STATE_NONE;
                    requestData(false);
                } else {
                    mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.HIDE_LAYOUT);
                }

            }
        } else {

//                MyLog.i("jgg--BASE 隐藏viewpage " + i);
            mLayout[i].setVisibility(View.GONE);
        }
    }

    protected void fillData() {
//        MyLog.i(mCurrentIndex+" 你要看页面adapte=: " + mAdapter[mCurrentIndex]);
        if (mAdapter[mCurrentIndex] == null || mListView[mCurrentIndex].getAdapter() == null) {
//            MyLog.i("fillData setadapte=:" + mListView[mCurrentIndex].getAdapter());
//            mListView[mCurrentIndex].setAdapter(mAdapter[mCurrentIndex]);
//            mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.HIDE_LAYOUT);
//        } else {
//            MyLog.i("fillData getListAdapter=:");
            mAdapter[mCurrentIndex] = getListAdapter();
            mListView[mCurrentIndex].setAdapter(mAdapter[mCurrentIndex]);

            if (mState == STATE_REFRESH ) {
                mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NETWORK_LOADING);
            } else if (true || requestDataIfViewCreated()) {
                mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NETWORK_LOADING);
                mState = STATE_NONE;
                requestData(false);
            } else {
                mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

        }
        if (mStoreEmptyState[mCurrentIndex] != -1) {
            mErrorLayout[mCurrentIndex].setErrorType(mStoreEmptyState[mCurrentIndex]);
        }
    }

    @Override
    public void onDestroyView() {
        mStoreEmptyState[mCurrentIndex] = mErrorLayout[mCurrentIndex].getErrorState();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        cancelReadCacheTask();
        cancelParserTask();
        super.onDestroy();
    }

    protected abstract ListBaseAdapter<T> getListAdapter();

    // 下拉刷新数据
    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        // 设置顶部正在刷新
        mListView[mCurrentIndex].setSelection(0);
        setSwipeRefreshLoadingState();
        mCurrentPage[mCurrentIndex] = 0;
        mState = STATE_REFRESH;
        doRefresh();
    }

    protected void doRefresh() {
        requestData(true);
    }

    public void scrollToTop() {
        mListView[mCurrentIndex].setSelection(0);
    }

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    protected String getCacheKeyPrefix(int mCurrentIndex) {
        return null;
    }

    protected ListEntity<T> parseList(InputStream is) throws Exception {
        return null;
    }

    protected ListEntity<T> readList(Serializable seri) {
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {}

    private String getCacheKey() {
        return new StringBuilder(getCacheKeyPrefix(mCurrentIndex)).append("_")
                .append(mCurrentPage[mCurrentIndex]).toString();
    }

    // 是否需要自动刷新
    protected boolean needAutoRefresh() {
        return true;
    }

    /***
     * 获取列表数据
     *
     * @param refresh
     */
    protected void requestData(boolean refresh) {
        String key = getCacheKey();
//        MyLog.i("获取列表数据 cacheKey="+key);
        if (isReadCacheData(refresh)) {
//            MyLog.i("获取列表数据 has cacheKey="+key);
            readCacheData(key);
        } else {
            // 取新的数据
//            MyLog.i("获取列表数据 has NO cache 取新的数据 cacheKey="+key);
            sendRequestData();
        }
    }

    /***
     * 判断是否需要读取缓存的数据
     *
     * @return boolean
     * @param refresh
     * @return
     */
    protected boolean isReadCacheData(boolean refresh) {
        String key = getCacheKey();
        if (!TDevice.hasInternet()) {
            return true;
        }
        // 第一页若不是主动刷新，缓存存在，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key) && !refresh
                && mCurrentPage[mCurrentIndex] == 0) {
            return true;
        }
        // 其他页数的，缓存存在以及还没有失效，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key)
                && !CacheManager.isCacheDataFailure(getActivity(), key)
                && mCurrentPage[mCurrentIndex] != 0) {
            return true;
        }

        return false;
    }

    // 是否到时间去刷新数据了
    private boolean onTimeRefresh() {
        String lastRefreshTime = AppContext.getLastRefreshTime(getCacheKey());
        String currTime = StringUtils.getCurTimeStr();
        long diff = StringUtils.calDateDifferent(lastRefreshTime, currTime);
        return needAutoRefresh() && diff > getAutoRefreshTime();
    }

    /***
     * 自动刷新的时间
     *
     * 默认：自动刷新的时间为半天时间
     *
     * @author 火蚁 2015-2-9 下午5:55:11
     *
     * @return long
     * @return
     */
    protected long getAutoRefreshTime() {
        return 12 * 60 * 60;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onTimeRefresh()) {
            onRefresh();
        }
    }

    protected void sendRequestData() {}

    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private class CacheTask extends AsyncTask<String, Void, ListEntity<T>> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected ListEntity<T> doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return readList(seri);
            }
        }

        @Override
        protected void onPostExecute(ListEntity<T> list) {
            super.onPostExecute(list);
            if (list != null) {
                executeOnLoadDataSuccess(list.getList());
            } else {
                executeOnLoadDataError(null);
            }
            executeOnLoadFinish();
        }
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

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] responseBytes) {
            // TODO 为什么是page=0时才保存上次刷新时间呢？
            Log.i("ljc","response =" + new String(responseBytes));
            if (mCurrentPage[mCurrentIndex] == 0 && needAutoRefresh()) {
                AppContext.putToLastRefreshTime(getCacheKey(),
                        StringUtils.getCurTimeStr());
            }
            if (isAdded()) {
                if (mState == STATE_REFRESH) {
                    onRefreshNetworkSuccess();
                }
                executeParserTask(responseBytes);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            if (isAdded()) {
                readCacheData(getCacheKey());
            }
        }
    };

    protected void executeOnLoadDataSuccess(List<T> data) {
        if (data == null) {
            data = new ArrayList<T>();
        }

        if (AppContext.getInstance().isLogin()) {
            if (mResult[mCurrentIndex] != null && mResult[mCurrentIndex].getResult() == 0) {
                AppContext.showToast(R.string.error_view_load_error_click_to_refresh);
                // 注销登陆，密码已经修改，cookie，失效了
//            AppContext.getInstance().Logout();
            }
        }

        mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (mCurrentPage[mCurrentIndex] == 0) {
            mAdapter[mCurrentIndex].clear();
        }

        // 去除重复数据
        for (int i = 0; i < data.size(); i++) {
            if (compareTo(mAdapter[mCurrentIndex].getData(), data.get(i))) {
                data.remove(i);
                i--;
            }
        }
        int adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
        if ((mAdapter[mCurrentIndex].getDataSize() + data.size()) == 0) {
            adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
        } else if (data.size() == 0
                || (data.size() < getPageSize() && mCurrentPage[mCurrentIndex] == 0)) {
            adapterState = ListBaseAdapter.STATE_NO_MORE;
            mAdapter[mCurrentIndex].notifyDataSetChanged();
        } else {
            adapterState = ListBaseAdapter.STATE_LOAD_MORE;
        }
        mAdapter[mCurrentIndex].setState(adapterState);
        mAdapter[mCurrentIndex].addData(data);
        // 判断等于是因为最后有一项是listview的状态条
        if (mAdapter[mCurrentIndex].getCount() == 1) {

            if (needShowEmptyNoData()) {
                mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NODATA);
            } else {
                mAdapter[mCurrentIndex].setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                mAdapter[mCurrentIndex].notifyDataSetChanged();
            }
        }
    }

    protected boolean needShowEmptyNoData() {
        return true;
    }

    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (enity.getId() == data.get(i).getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected int getPageSize() {
        return AppContext.PAGE_SIZE;
    }

    protected void onRefreshNetworkSuccess() {}

    protected void executeOnLoadDataError(String error) {
        if (mCurrentPage[mCurrentIndex] == 0
                && !CacheManager.isExistDataCache(getActivity(), getCacheKey())) {
            mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.HIDE_LAYOUT);
            mAdapter[mCurrentIndex].setState(ListBaseAdapter.STATE_NETWORK_ERROR);
            mAdapter[mCurrentIndex].notifyDataSetChanged();
        }
    }

    // 完成刷新
    protected void executeOnLoadFinish() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
    }

    /** 设置顶部正在加载的状态 */
    private void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout[mCurrentIndex] != null) {
            mSwipeRefreshLayout[mCurrentIndex].setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout[mCurrentIndex].setEnabled(false);
        }
    }

    /** 设置顶部加载完毕的状态 */
    private void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout[mCurrentIndex] != null) {
            mSwipeRefreshLayout[mCurrentIndex].setRefreshing(false);
            mSwipeRefreshLayout[mCurrentIndex].setEnabled(true);
        }
    }

    private void executeParserTask(byte[] data) {
        cancelParserTask();
        mParserTask = new ParserTask(data);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    class ParserTask extends AsyncTask<Void, Void, String> {

        private final byte[] reponseData;
        private boolean parserError;
        private List<T> list;

        public ParserTask(byte[] data) {
            this.reponseData = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ListEntity<T> data = parseList(new ByteArrayInputStream(
                        reponseData));
                new SaveCacheTask(getActivity(), data, getCacheKey()).execute();
                list = data.getList();
                if (list == null) {
                    BaseHttpResponse resultBean = XmlUtils.JsontoBean(BaseHttpResponse.class,
                            reponseData);
                    if (resultBean != null) {
                        mResult[mCurrentIndex] = resultBean;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (parserError) {
                readCacheData(getCacheKey());
            } else {
                executeOnLoadDataSuccess(list);
                executeOnLoadFinish();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mAdapter[mCurrentIndex] == null || mAdapter[mCurrentIndex].getCount() == 0) {
            return;
        }

        // 当滑动时如果有键盘,收起来
        TDevice.hideSoftKeyboard(view);

        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
//        MyLog.i(mState + " =mState 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件 scrollState=" + scrollState);
        if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
            return;
        }
        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {
            if (view.getPositionForView(mAdapter[mCurrentIndex].getFooterView()) == view
                    .getLastVisiblePosition())
                scrollEnd = true;
        } catch (Exception e) {
            scrollEnd = false;
        }

        if (mState == STATE_NONE && scrollEnd) {
            if (mAdapter[mCurrentIndex].getState() == ListBaseAdapter.STATE_LOAD_MORE
                    || mAdapter[mCurrentIndex].getState() == ListBaseAdapter.STATE_NETWORK_ERROR) {
                mCurrentPage[mCurrentIndex]++;
                mState = STATE_LOADMORE;
                requestData(false);
                mAdapter[mCurrentIndex].setFooterViewLoading();
            }
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            mExchangeBt.show(true);
        } else {
            mExchangeBt.hide(true);
        }
        onScrolling(scrollState);
    }

    protected abstract void onScrolling(int scrollState);

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        // if (mState == STATE_NOMORE || mState == STATE_LOADMORE
        // || mState == STATE_REFRESH) {
        // return;
        // }
        // if (mAdapter != null
        // && mAdapter.getDataSize() > 0
        // && mListView.getLastVisiblePosition() == (mListView.getCount() - 1))
        // {
        // if (mState == STATE_NONE
        // && mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
        // mState = STATE_LOADMORE;
        // mCurrentPage++;
        // requestData(true);
        // }
        // }
    }
}

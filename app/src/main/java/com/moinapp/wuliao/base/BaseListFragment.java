package com.moinapp.wuliao.base;

import android.annotation.SuppressLint;
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
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

@SuppressLint("NewApi")
public abstract class BaseListFragment<T extends Entity> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
        OnScrollListener {

    @InjectView(R.id.swiperefreshlayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.listview)
    protected ListView mListView;

    protected ListBaseAdapter<T> mAdapter;

    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;

    protected int mStoreEmptyState = -1;

    protected int mCurrentPage = 0;

    protected int mCatalog = 1;
    // 错误信息
    protected BaseHttpResponse mResult;

    private AsyncTask<String, Void, ListEntity<T>> mCacheTask;
    private ParserTask mParserTask;

    protected int SCROLL_STATE = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCatalog = args.getInt(Constants.BUNDLE_KEY_CATALOG, 0);
        }
    }

    @Override
    public void initView(View view) {
        addListHeader();
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
        if (mStoreEmptyState != -1) {
            mErrorLayout.setErrorType(mStoreEmptyState);
        }
    }

    protected void addListHeader() {}

    @Override
    public void onDestroyView() {
        mStoreEmptyState = mErrorLayout.getErrorState();
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
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            setSwipeRefreshLoadedState();
            return;
        }
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        mCurrentPage = 0;
        mState = STATE_REFRESH;
        requestData(true);
    }

    public void scrollToTop() {
        mListView.setSelection(0);
    }

    public void scrollToBottom() {
        mListView.setSelection(mAdapter.getDataSize() - 1);
    }

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    protected String getCacheKeyPrefix() {
        return null;
    }

    //定义为抽象类
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
        return new StringBuilder(getCacheKeyPrefix()).append("_")
                .append(mCurrentPage).toString();
    }

    // 是否需要自动刷新
    protected boolean needAutoRefresh() {
        return true;
    }

    /***
     * 获取列表数据
     * @return void
     * @param refresh
     */
    protected void requestData(boolean refresh) {
        String key = getCacheKey();
        if (isReadCacheData(refresh)) {
            readCacheData(key);
        } else {
            // 取新的数据
            sendRequestData();
        }
    }

    /***
     * 判断是否需要读取缓存的数据
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
                && mCurrentPage == 0) {
            return true;
        }
        // 其他页数的，缓存存在以及还没有失效，优先取缓存的
        if (CacheManager.isExistDataCache(getActivity(), key)
                && !CacheManager.isCacheDataFailure(getActivity(), key)
                && mCurrentPage != 0) {
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

    // 定义为抽象方法
    protected void sendRequestData() {}

    protected void deleteCacheData() {
        new DeleteCacheTask(getActivity(), getCacheKey()).execute();
    }

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
                executeOnLoadDataSuccess(list.getList(), isLast);
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

    private class DeleteCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final String key;

        private DeleteCacheTask(Context context, String key) {
            mContext = new WeakReference<Context>(context);
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.deleteObject(mContext.get(), key);
            return null;
        }
    }

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                byte[] responseBytes) {
            // TODO 为什么是page=0时才保存上次刷新时间呢？
            Log.i("ljc",getCacheKey()+"'s response =" + new String(responseBytes));
            if (mCurrentPage == 0 && needAutoRefresh()) {
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
            if (isAdded() && isReadCacheData(false)) {
                readCacheData(getCacheKey());
            }
        }
    };

    protected void executeOnLoadDataSuccess(List<T> data, boolean isLast) {
        if (data == null) {
            data = new ArrayList<T>();
        }

        if (mResult != null && mResult.getResult() == 0) {
            AppContext.showToast(R.string.error_view_load_error_click_to_refresh);
            // 注销登陆，密码已经修改，cookie，失效了
//            AppContext.getInstance().Logout();
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
//        if ((mAdapter.getDataSize() + data.size()) == 0) {
//            adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
//        } else if (data.size() == 0
//                || (data.size() < getPageSize()/* && mCurrentPage == 0*/)) {
//            adapterState = ListBaseAdapter.STATE_NO_MORE;
//            mAdapter.notifyDataSetChanged();
//        } else {
//            adapterState = ListBaseAdapter.STATE_LOAD_MORE;
//        }
        mAdapter.setState(adapterState);
        mAdapter.addData(data);
        // 判断等于是因为最后有一项是listview的状态
        if (mAdapter.getCount() == 1) {

            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            } else {
                mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                mAdapter.notifyDataSetChanged();
            }
        }
        android.util.Log.i("bl", "mAdapter.getState= " + mAdapter.getState());
    }

    /**
     * 是否需要隐藏listview，显示无数据状态
     * 
     */
    protected boolean needShowEmptyNoData() {
        return true;
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

    protected int getPageSize() {
        return AppContext.PAGE_SIZE;
    }

    protected void onRefreshNetworkSuccess() {}

    protected void executeOnLoadDataError(String error) {
        if (mCurrentPage == 0
                && !CacheManager.isExistDataCache(getActivity(), getCacheKey())) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mAdapter.setState(ListBaseAdapter.STATE_NETWORK_ERROR);
            mAdapter.notifyDataSetChanged();
        }
    }

    // 完成刷新
    protected void executeOnLoadFinish() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
    }

    /** 设置顶部正在加载的状态 */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /** 设置顶部加载完毕的状态 */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
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

    private boolean isLast;

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
                BaseHttpResponse response = XmlUtils.JsontoBean(BaseHttpResponse.class, reponseData);
                if (response != null) {
                    isLast = response.isLast();
                }
                ListEntity<T> data = parseList(new ByteArrayInputStream(
                        reponseData));
                new SaveCacheTask(getActivity(), data, getCacheKey()).execute();
                list = data.getList();
                if (list == null) {
                    BaseHttpResponse resultBean = XmlUtils.JsontoBean(BaseHttpResponse.class,
                            reponseData);
                    if (resultBean != null) {
                        mResult = resultBean;
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
                executeOnLoadDataSuccess(list, isLast);
                executeOnLoadFinish();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        SCROLL_STATE = scrollState;
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }

        // 当滑动时如果有键盘,收起来
        TDevice.hideSoftKeyboard(view);

        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
            return;
        }
        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {
            if (view.getPositionForView(mAdapter.getFooterView()) == view
                    .getLastVisiblePosition())
                scrollEnd = true;
        } catch (Exception e) {
            scrollEnd = false;
        }

        // 暂时设为当下拉到距离底部最后一个元素还有2个时开始加载下一页的数据
        if (view.getLastVisiblePosition() == mAdapter.getDataSize() - 1 - 2) {
            Log.i("ljc","mAdapter.getDataSize() =" + mAdapter.getDataSize() +
                    ", current position = " + view.getLastVisiblePosition());
            loadMoreData();
        }

        if (mState == STATE_NONE && scrollEnd) {
            if (mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE
                    || mAdapter.getState() == ListBaseAdapter.STATE_NETWORK_ERROR) {
                loadMoreData();
                mAdapter.setFooterViewLoading();
            }
        }
    }

    private void loadMoreData() {
        if (mAdapter.getState() == ListBaseAdapter.STATE_NO_MORE
                || mAdapter.getState() == ListBaseAdapter.STATE_EMPTY_ITEM) {
            return;
        }

        mCurrentPage++;
        mState = STATE_LOADMORE;
        requestData(false);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
    }

    /**
     * @param view
     * @param prefFileName
     * @param key
     */
    protected void saveToReadedList(final View view, final String prefFileName,
            final String key) {
    }
}

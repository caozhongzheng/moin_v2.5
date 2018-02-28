package com.moinapp.wuliao.base;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.empty.EmptyLayout;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * 需要加入header的BaseMultiListFragment
 *
 * @desc 应用场景：如用户动态， 即是头部显示详情，然后下面显示多个类别的列表的
 *
 * @author caozz
 *
 * @data 2016-1-14 下午3:02:42
 */
public abstract class BeseMultiHaveHeaderListFragment<T1 extends Entity, T2 extends Serializable>
        extends BaseMultiListFragment<T1> {
    private static final ILogger MyLog = LoggerFactory.getLogger(BeseMultiHaveHeaderListFragment.class.getSimpleName());

    protected T2 detailBean;// list 头部的详情实体类

    protected Activity aty;

    protected View mHeaderView;


    protected final AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                if (arg2 != null) {
                    T2 detail = getDetailBean(new ByteArrayInputStream(arg2));
                    detailBean = detail;
                    if (detail != null) {
                        onGetDetailSuccess();
                        requstListData();
                        executeOnLoadDetailSuccess(detail);
                        new SaveCacheTask(getActivity(), detail,
                                getDetailCacheKey()).execute();
                    } else {
                        MyLog.w("detail==null");
                        onFailure(arg0, arg1, arg2, null);
                    }
                } else {
                    throw new RuntimeException("load detail error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.w("detail=Exception");
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            MyLog.w("onFailure arg0=" + arg0);
            boolean isGoON = onGetDetailFailed(arg2);
            if (isGoON) {
                readDetailCacheData(getDetailCacheKey());
                requstListData();
            }
        }
    };

    protected void onGetDetailSuccess() {
    }
    protected boolean onGetDetailFailed(byte[] arg2) {
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 通过注解绑定控件
//        ButterKnife.inject(this, view);

        mHeaderView = initHeaderView();
        if (mHeaderView != null) {
            for (int i = 0; i < LIST_COUNT; i++) {
                mListView[i].removeHeaderView(mHeaderView);
                mListView[i].addHeaderView(mHeaderView);
                mySetAdapter(i);
            }
//            LinearLayout headerView = (LinearLayout) view.findViewById(R.id.header);
//            headerView.addView(mHeaderView);
        }
        aty = getActivity();

        requestDetailData(isRefresh());
    }

    @Override
    public void doRefresh() {
        requestDetailData(true);
    }

    protected boolean isRefresh() {
        return false;
    }

    protected abstract void requestDetailData(boolean isRefresh);

    protected abstract View initHeaderView();

    protected abstract String getDetailCacheKey();

    protected abstract void executeOnLoadDetailSuccess(T2 detailBean);

    protected abstract T2 getDetailBean(ByteArrayInputStream is);

    protected void saveCache(T2 detailBean) {
        new SaveCacheTask(getActivity(), detailBean,
                getDetailCacheKey()).execute();
    }

    /***
     * 获取header数据
     *
     * @return void
     * @param refresh
     */
    @Override
    protected void requestData(boolean refresh) {
//        requestDetailData(refresh);
        requstListData();
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    private void requstListData() {
        mState = STATE_REFRESH;
        mAdapter[mCurrentIndex].setState(ListBaseAdapter.STATE_LOAD_MORE);
        sendRequestData();
    }

    /***
     * 带有header view的listfragment不需要显示是否数据为空
     */
    @Override
    protected boolean needShowEmptyNoData() {
        return false;
    }

    protected void readDetailCacheData(String cacheKey) {
        new ReadCacheTask(getActivity()).execute(cacheKey);
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

    private class ReadCacheTask extends AsyncTask<String, Void, T2> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected T2 doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return (T2) seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(T2 t) {
            super.onPostExecute(t);
            if (t != null) {
                executeOnLoadDetailSuccess(t);
            } else {
                executeOnLoadDetailSuccess(null);
            }
        }
    }

    @Override
    protected void executeOnLoadDataError(String error) {
//        if (mHeaderView != null) {
//            mListView.removeHeaderView(mHeaderView);
//        }
        mErrorLayout[mCurrentIndex].setErrorType(EmptyLayout.NETWORK_ERROR);
        mAdapter[mCurrentIndex].setState(ListBaseAdapter.STATE_NETWORK_ERROR);
        mAdapter[mCurrentIndex].notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findHeaderView(View headerView, int viewId) {
        return (T) headerView.findViewById(viewId);
    }
}

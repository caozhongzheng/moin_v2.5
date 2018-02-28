package com.moinapp.wuliao.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ActiveAdapter;
import com.moinapp.wuliao.api.remote.OSChinaApi;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Active;
import com.moinapp.wuliao.bean.ActiveList;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Notice;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.DialogHelp;
import com.moinapp.wuliao.util.HTMLUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 动态fragment
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @author kymjs (https://github.com/kymjs)
 * @created 2014年10月22日 下午3:35:43
 * 
 */
public class ActiveFragment extends BaseListFragment<Active> implements
        OnItemLongClickListener {

    protected static final String TAG = ActiveFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "active_list";
    private boolean mIsWatingLogin; // 还没登陆

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mErrorLayout != null) {
                mIsWatingLogin = true;
                mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
                //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (mIsWatingLogin) {
            mCurrentPage = 0;
            mState = STATE_REFRESH;
            requestData(false);
        }
        refreshNotice();
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.ACTIVE_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.ACTIVE_FRAGMENT);
    }

    /**
     * 开始刷新请求
     */
    private void refreshNotice() {
        Notice notice = MainActivity.mNotice;
        if (notice == null) {
            return;
        }
        if (notice.getAtmeCount() > 0 && mCatalog == ActiveList.CATALOG_ATME) {
            onRefresh();
        } else if (notice.getReviewCount() > 0
                && mCatalog == ActiveList.CATALOG_COMMENT) {
            onRefresh();
        }
    }

    @Override
    protected ActiveAdapter getListAdapter() {
        return new ActiveAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return new StringBuffer(CACHE_KEY_PREFIX + mCatalog).append(
                AppContext.getInstance().getLoginUid()).toString();
    }

    @Override
    protected ActiveList parseList(InputStream is) {
        ActiveList list = XmlUtils.toBean(ActiveList.class, is);
        return list;
    }

    @Override
    protected ActiveList readList(Serializable seri) {
        return ((ActiveList) seri);
    }

    @Override
    public void initView(View view) {
        if (mCatalog == ActiveList.CATALOG_LASTEST) {
            setHasOptionsMenu(true);
        }
        super.initView(view);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData(false);
                } else {
                    UIHelper.showLoginActivity(getActivity());
                }
            }
        });
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mIsWatingLogin = false;
            super.requestData(refresh);
        } else {
            mIsWatingLogin = true;
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getActiveList(AppContext.getInstance().getLoginUid(),
                mCatalog, mCurrentPage, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Active active = mAdapter.getItem(position);
        if (active != null)
            UIHelper.showActiveRedirect(view.getContext(), active);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        final Active active = mAdapter.getItem(position);
        if (active == null)
            return false;
        String[] items = new String[] { getResources().getString(R.string.copy) };
        DialogHelp.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(active.getMessage()));
            }
        }).show();
        return true;
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新动态，即是好友圈
        if (mCatalog == ActiveList.CATALOG_LASTEST) {
            return 5 * 60;
        }
        return super.getAutoRefreshTime();
    }
}

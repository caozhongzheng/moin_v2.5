package com.moinapp.wuliao.modules.mine;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.FansList;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.modules.mine.adapter.FollowerAdapter;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import butterknife.InjectView;

/**
 * 我的粉丝
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FansFragment extends BaseListFragment<UserInfo> {

    public final static int PAGE_SIZE = 10;

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "fans_list";

    private String mUid;
    private String mUserName;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @Override
    public void initView(View view) {
        super.initView(view);

        mAdapter.setFinishText(R.string.no_more_fans);

        title.hideRightBtn();
        title.setTitleTxt(ClientInfo.isLoginUser(mUid) ? "我的粉丝" : "粉丝");
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.setTitleOnclickListener(v -> {
            scrollToTop();
        });
        mErrorLayout.setEmptyImage(R.drawable.no_data_fan_follow);
        if (ClientInfo.isLoginUser(mUid)) {
            mErrorLayout.setNoDataContent(getString(R.string.no_following_user));
            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setOnLayoutClickListener(v -> {
                // TODO 我没有粉丝时,跳转到发现频道
                UIHelper.gotoMain(getActivity(), MainActivity.KEY_TAB_DISCOVERY, true);
            });
        }else {
            mErrorLayout.setNoDataContent(String.format(getString(R.string.other_no_fans), mUserName));
            mErrorLayout.setClickable(false);
        }
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(Constants.BUNDLE_KEY_UID);
            mUserName = args.getString(Constants.BUNDLE_KEY_USERNAME);
        }
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.clear();
            requestData(true);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_title;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.FANS_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.FANS_FRAGMENT);
    }

    @Override
    protected FollowerAdapter getListAdapter() {
        return new FollowerAdapter(mUid);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog + "_" + mUid;
    }

    @Override
    protected FansList parseList(InputStream is) throws Exception {
        FansList result = XmlUtils.JsontoBean(FansList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        return result;
    }

    @Override
    protected FansList readList(Serializable seri) {
        return ((FansList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
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
            MineApi.getMyFans(mUid, null, null, mHandler);
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
        MineApi.getMyFans(mUid, null,lastid, mHandler);
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
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }
}

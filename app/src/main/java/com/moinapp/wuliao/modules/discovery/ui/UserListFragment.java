package com.moinapp.wuliao.modules.discovery.ui;

import android.os.Bundle;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.LikeCosplayAdapter;
import com.moinapp.wuliao.modules.discovery.adapter.UserListAdapter;
import com.moinapp.wuliao.modules.discovery.model.UserInfoList;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 参与用户/浏览用户
 * Created by guyunfei on 16/2/24.14:04.
 */
public class UserListFragment extends BaseListFragment<UserInfo> {
    private static final ILogger MyLog = LoggerFactory.getLogger(UserListFragment.class.getSimpleName());

    private static final int PAGE_SIZE = 30;//每次请求服务器下发多少个
    private static final String CACHE_KEY_PREFIX = "user_list";

    private String mTitle = "用户列表";
    private int mType = 1;
    private String mUCID = "";

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mType = args.getInt(Constants.BUNDLE_KEY_TYPE);
            if (mType == 1) {
                mTitle = "参与用户";
            } else if (mType == 2) {
                mTitle = "浏览用户";
            }
            mUCID = args.getString(Constants.BUNDLE_KEY_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                getActivity().finish();
            }
        });
        title.setTitleTxt(mTitle);
        initEmptyLayout();
    }

    protected void initEmptyLayout() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        requestData(true);
    }

    @Override
    protected void requestData(boolean refresh) {
        super.requestData(refresh);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_USER_LIST_" + mUCID;
    }

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    @Override
    protected void sendRequestData() {
        int size = mAdapter.getData().size();
        String lastId = null;
        if (size > 0 && mCurrentPage != 0) {
            lastId = mAdapter.getData().get(mAdapter.getData().size() - 1).getUId();
        }
        if (mType == 1) {
            DiscoveryApi.getTopicUserList(PAGE_SIZE, lastId, mUCID, mHandler);
        } else if (mType == 2) {
            DiscoveryApi.getViewUserList(PAGE_SIZE, lastId, mUCID, mHandler);
        }
    }

    @Override
    protected UserInfoList parseList(InputStream is) throws Exception {
        UserInfoList list = XmlUtils.JsontoBean(UserInfoList.class, is);
        return list;
    }

    @Override
    protected UserInfoList readList(Serializable seri) {
        return ((UserInfoList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((UserInfo) enity).getUId() == ((UserInfo) data.get(i))
                        .getUId()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected ListBaseAdapter<UserInfo> getListAdapter() {
        if (mType == 1) {
            return new LikeCosplayAdapter(getActivity(), UserDefineConstants.FOLLOW_LIKE_LIST);
        }
        return new UserListAdapter(getActivity());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_list;
    }

    @Override
    protected int getPageSize() {
        return 30;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData(true);
        MobclickAgent.onPageStart(UmengConstants.USERLIST_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.USERLIST_FRAGMENT);
    }
}

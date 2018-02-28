package com.moinapp.wuliao.modules.mine;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.modules.mine.adapter.MyLikeAdapter;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.modules.mine.model.UserActivityList;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的赞
 *
 * @author liujiancheng
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyLikeFragment extends BaseListFragment<UserActivity> {

    public final static int PAGE_SIZE = 10;

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "my_like_list";

    private String mUid;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_title;
    }

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

        mAdapter.setFinishText(R.string.no_more_like);

        title.setTitleTxt(getString(R.string.my_like));
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.hideRightBtn();
        title.setTitleOnclickListener(v -> {
            scrollToTop();
        });

        mErrorLayout.hideTitleDownArea();
        mErrorLayout.setEmptyImage(R.drawable.no_data_like);
        if (ClientInfo.isLoginUser(mUid)) {
            mErrorLayout.setNoDataContent(getString(R.string.no_like));
            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setOnLayoutClickListener(v -> {
                // TODO 我没有关注任何人时,跳转到发现频道
                UIHelper.gotoMain(getActivity(), MainActivity.KEY_TAB_DISCOVERY, true);
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(Constants.BUNDLE_KEY_UID);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
//        initView(view);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.MY_LIKE_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.MY_LIKE_FRAGMENT);
    }

    @Override
    protected MyLikeAdapter getListAdapter() {
        return new MyLikeAdapter(getActivity(), mUid);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog + "_" + mUid;
    }

    @Override
    protected UserActivityList parseList(InputStream is) throws Exception {
        UserActivityList result = XmlUtils.JsontoBean(UserActivityList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
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
            MineApi.getMyActivity(mUid, 1, Messages.ACTION_LIKE_COSPLAY, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ?
                    mAdapter.getItem(mAdapter.getData().size() - 1).getActivityId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        MineApi.getMyActivity(mUid, 1, Messages.ACTION_LIKE_COSPLAY, lastid, mHandler);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }

}

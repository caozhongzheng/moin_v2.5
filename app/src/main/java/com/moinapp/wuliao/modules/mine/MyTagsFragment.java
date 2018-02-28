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
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.result.GetFollowTagResult;
import com.moinapp.wuliao.modules.mine.adapter.MyTagsAdapter;
import com.moinapp.wuliao.modules.mine.model.TagInfoList;
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
 * 我关注的标签列表
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyTagsFragment extends BaseListFragment<TagInfo> implements OnTabReselectListener {

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "my_tags_list";

    private String mUid;
    private String mUserName;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;
    @InjectView(R.id.title_down_grey_line)
    protected View title_down_line;

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

        title.hideRightBtn();
        title.setTitleTxt("订阅话题");
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.setTitleOnclickListener(v -> {
            scrollToTop();
        });
        mErrorLayout.setEmptyImage(R.drawable.no_data_tag);
        if (ClientInfo.isLoginUser(mUid)) {
            mErrorLayout.setNoDataContent(getString(R.string.no_tag));
            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setOnLayoutClickListener(v -> {
                // TODO 我没有关注的标签时,跳转到发现频道
                UIHelper.gotoMain(getActivity(), MainActivity.KEY_TAB_DISCOVERY, true);
            });
        }else{
            mErrorLayout.setNoDataContent(String.format(getString(R.string.other_no_follow_tag),mUserName));
            mErrorLayout.setClickable(false);
            mErrorLayout.setBtnVisibility(View.GONE);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(Constants.BUNDLE_KEY_UID);
            mUserName = args.getString(Constants.BUNDLE_KEY_USERNAME);
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
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_title;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.MY_TAGS_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.MY_TAGS_FRAGMENT);
    }

    @Override
    protected MyTagsAdapter getListAdapter() {
        return new MyTagsAdapter(getActivity(), allowCancelFollow(),false);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUid;
    }

    @Override
    protected TagInfoList parseList(InputStream is) throws Exception {
        TagInfoList list = null;
        GetFollowTagResult result = XmlUtils.JsontoBean(GetFollowTagResult.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        list = new TagInfoList();
        list.setTagInfos(result.getTags());
        return list;
    }

    @Override
    protected TagInfoList readList(Serializable seri) {
        return ((TagInfoList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((TagInfo) enity).getName() != null) {
            for (int i = 0; i < s; i++) {
                if (((TagInfo) enity).getName().equalsIgnoreCase(((TagInfo) data.get(i)).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        DiscoveryApi.getFollowTags(mUid, mHandler);
    }

    @Override
    protected void sendRequestData() {
        DiscoveryApi.getFollowTags(mUid, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected boolean allowCancelFollow() {
        return mUid.equalsIgnoreCase(ClientInfo.getUID());
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }
}

package com.moinapp.wuliao.modules.search;

import android.os.Bundle;
import android.view.View;

import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.ui.TopicListFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by guyunfei on 16/7/22.18:59.
 */
public class TopicSearchResultFragment extends TopicListFragment {

    private static final ILogger MyLog = LoggerFactory.getLogger(TopicSearchResultFragment.class.getSimpleName());
    private String mTag;
    private String mCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTag = args.getString(Constants.BUNDLE_KEY_TAG);
            mCategory = args.getString(Constants.BUNDLE_KEY_TYPE);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        if (mTag != null) {
            title.setTitleTxt(mTag);
        } else {
            if (mCategory != null) {
                title.setTitleTxt(mCategory);

            }
        }
        title.hideRightBtn();
    }

    @Override
    protected void sendRequestData() {
        int size = mAdapter.getData().size();
        if (size > 0 && mCurrentPage != 0) {
            String lastID = mAdapter.getData().get(mAdapter.getData().size() - 1).getTagPopId();
            DiscoveryApi.searchTopic(mTag, mCategory, lastID, mHandler);
        } else {
            DiscoveryApi.searchTopic(mTag, mCategory, null, mHandler);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.TOPIC_SEARCH_RESULT_FRAGMENT); //统计页面，
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.TOPIC_SEARCH_RESULT_FRAGMENT);
    }
}

package com.moinapp.wuliao.modules.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.TopicDetailMultipleAdapter;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TopicArticleList;
import com.moinapp.wuliao.modules.post.PostActivity;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;

/** 话题>帖子专区
 * Created by guyunfei on 16/6/16.15:13.
 */
public class ArticleListFragment extends BaseListFragment<CosplayInfo> {

    private static final ILogger MyLog = LoggerFactory.getLogger(ArticleListFragment.class.getSimpleName());

    private static final String CACHE_KEY_PREFIX = "articleList_";

    private String mTopicName;
    private String mTopicType;
    private String mTopicId;

    @InjectView(R.id.btn_float)
    protected FloatingActionButton floatingBtn;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;
    private String POST_LIST = "2,3,4";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_article_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTopicName = args.getString(Constants.BUNDLE_KEY_TAG);
            mTopicType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTopicId = args.getString(Constants.BUNDLE_KEY_ID);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        EventBus.getDefault().register(this);

        title.setTitleTxt(mTopicName + getResources().getString(R.string.article_list));
        title.hideRightBtn();

        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 我要发帖-帖子专区

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, mTopicName + "_" + mTopicId);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_POST_CLICK_POSTS, map);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_POST_CLICK, map);

                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_ID, mTopicId);
                intent.putExtra(Constants.BUNDLE_KEY_TAG, mTopicName);
                intent.putExtra(Constants.BUNDLE_KEY_TYPE, mTopicType);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void sendRequestData() {
        String lastUcid = null;
        if (!mAdapter.getData().isEmpty() && mCurrentPage != 0) {
            lastUcid = mAdapter.getData().get(mAdapter.getData().size() - 1).getUcid();
        }
        MyLog.i("sendRequestData lastUcid=" + lastUcid);
        DiscoveryApi.getTopicCosplay(POST_LIST, mTopicId, mTopicName, null, lastUcid, mHandler);
    }

    @Override
    protected TopicArticleList parseList(InputStream is) throws Exception {
        TopicArticleList list = XmlUtils.JsontoBean(TopicArticleList.class, is);
        
        return list;
    }

    @Override
    protected TopicArticleList readList(Serializable seri) {
        return ((TopicArticleList) seri);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTopicId;
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((CosplayInfo) enity).getUcid().equals(((CosplayInfo) data.get(i)).getUcid())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新关注10分钟刷新一次
        return 10 * 60;
    }

    @Override
    protected ListBaseAdapter<CosplayInfo> getListAdapter() {
        return new TopicDetailMultipleAdapter(getActivity(), null);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.ARTICLE_LIST_FRAGMENT); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.ARTICLE_LIST_FRAGMENT);
    }
}

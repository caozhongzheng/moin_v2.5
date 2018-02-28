package com.moinapp.wuliao.modules.discovery.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.TagListAdapter;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.mine.model.TopicList;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 话题列表
 * Created by guyunfei on 16/2/25.17:45.
 */
public class TopicListFragment extends BaseListFragment<TagPop> {
    private static final ILogger MyLog = LoggerFactory.getLogger(TopicListFragment.class.getSimpleName());
    protected static final int PAGE_SIZE = 10;//每次请求服务器下发多少个
    private static final String CACHE_KEY_PREFIX = "tag_list";

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

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

        title.setLeftBtnIcon(R.drawable.return_key_black);
        title.setRightBtnIcon(R.drawable.actionbar_search_icon);
        title.setTitleTxt("话题");
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                getActivity().finish();
            }
        });
        title.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索话题
                UIHelper.showTopicSearch(getActivity());
            }
        });

        initEmptyLayout();
        int headerViewsCount = mListView.getHeaderViewsCount();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 点击跳转到话题详情
                int newPos = position - headerViewsCount;
                if (newPos < 0 || newPos >= mAdapter.getDataSize()) {
                    return;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, mAdapter.getItem(newPos).getName() + "_" + mAdapter.getItem(newPos).getTagPopId());
//                map.put(UmengConstants.FROM, "话题列表页");
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK_TOPIC_LIST, map);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK, map);
//                MyLog.i("点击话题列表页话题:" + map.get(UmengConstants.ITEM_ID));
                UIHelper.showTopicDetail(getActivity(), mAdapter.getItem(newPos).getName(),
                        mAdapter.getItem(newPos).getType(), mAdapter.getItem(newPos).getTagPopId(), 0);
            }
        });
    }

    protected void initEmptyLayout() {
        mErrorLayout.setBtnVisibility(View.GONE);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        requestData(true);
    }

    @Override
    protected TopicList parseList(InputStream is) throws Exception {
        TopicList list = XmlUtils.JsontoBean(TopicList.class, is);
        return list;
    }

    @Override
    protected TopicList readList(Serializable seri) {
        return ((TopicList) seri);
    }

    @Override
    protected void sendRequestData() {
        int size = mAdapter.getData().size();
        if (size > 0 && mCurrentPage != 0) {
            String lastID = mAdapter.getData().get(mAdapter.getData().size() - 1).getTagPopId();
            DiscoveryApi.getTopicList(PAGE_SIZE, lastID, mHandler);
        } else {
            DiscoveryApi.getTopicList(PAGE_SIZE, null, mHandler);
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        super.requestData(refresh);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((TagPop) enity).getTagPopId() == ((TagPop) data.get(i))
                        .getTagPopId()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected TagListAdapter getListAdapter() {
        return new TagListAdapter(getActivity());
    }


    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX;
    }

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.TOPICLIST_FRAGMENT); //统计页面，
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.TOPICLIST_FRAGMENT);
    }
}

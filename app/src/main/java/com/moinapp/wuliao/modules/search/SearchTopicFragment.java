package com.moinapp.wuliao.modules.search;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.discovery.result.SearchTopicResult;
import com.moinapp.wuliao.modules.mine.model.TopicList;
import com.moinapp.wuliao.modules.sticker.model.FolderInfo;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by guyunfei on 16/7/21.14:57.
 */
public class SearchTopicFragment extends BaseListFragment<TagPop> {

    private ILogger MyLog = LoggerFactory.getLogger(SearchTopicFragment.class.getSimpleName());

    private String mKeyword;
    private SearchTextChangedListener mSearchListener;
    private SearchKeyBoardListener mCosplaySearchListener;

    private static final String CACHE_KEY_PREFIX = "search_topic_list";

    private List<FolderInfo> mHotWordList;

    @InjectView(R.id.tv_hot_search)
    public TextView mText;

    @InjectView(R.id.fl_hot_container)
    public FlowLayout mHotWord;

    @InjectView(R.id.ll_hotword_part)
    public LinearLayout mLlHotWord;

    @InjectView(R.id.title_down_area)
    public View mHeadView;
    private TopicSearchAdapter topicSearchAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_tag_cosplay;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        if (mAdapter != null) {
            topicSearchAdapter = (TopicSearchAdapter) mAdapter;
        }

        mText.setText("话题分类");

        mListView.setBackgroundColor(Color.parseColor("#f0eff4"));

        mSearchListener = new SearchTextChangedListener() {
            @Override
            public void onSearchTextChanged(String searchText) {
                mKeyword = searchText;
                mListView.setBackgroundColor(Color.parseColor("#ffffff"));
                doSearch();
            }

            @Override
            public void onSearchTextInvalid() {
                if (mAdapter != null) {
                    mAdapter.clear();
                    mAdapter.setState(-1);
                    mListView.setBackgroundColor(Color.parseColor("#f0eff4"));

                    if (TDevice.hasInternet()) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        if (mHotWordList != null && mHotWordList.size() > 0) {
                            updateHotWord();
                        } else {
                            getHotWord();
                        }
                    } else {
                        hideHotword();
                        mAdapter.clear();
                        mCurrentPage = 0;
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                }
            }
        };

        mCosplaySearchListener = new SearchKeyBoardListener() {
            @Override
            public void onSearchKeyClicked(String searchText) {
                // TODO 搜索话题结果页
                UIHelper.searchTopic(getActivity(), mKeyword, null);
            }
        };

        mErrorLayout.setEmptyImage(R.drawable.no_content_black);
        mErrorLayout.setNoDataContent(getString(R.string.error_view_no_data));
        mErrorLayout.setClickable(true);
        SearchViewPagerFragment.setTopicListener(mSearchListener);
        SearchViewPagerFragment.setSearchTopicListener(mCosplaySearchListener);

        getHotWord();
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
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK_TOPIC_LIST, map);
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_CLICK, map);
                UIHelper.showTopicDetail(getActivity(), mAdapter.getItem(newPos).getName(),
                        mAdapter.getItem(newPos).getType(), mAdapter.getItem(newPos).getTagPopId(), 0);
            }
        });
    }

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    @Override
    protected TopicList parseList(InputStream is) throws Exception {
        TopicList list = new TopicList();
        SearchTopicResult result = XmlUtils.JsontoBean(SearchTopicResult.class, is);
        if (result == null || result.getTopicList() == null || result.getTopicList().size() == 0) {
            Log.i("ljc", "search tags is empty");
            return list;
        }
        list.setTagInfos(result.getTopicList());
        return list;
    }

    @Override
    protected TopicList readList(Serializable seri) {
        return ((TopicList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((TagPop) enity).getName() != null) {
            for (int i = 0; i < s; i++) {
                if (((TagPop) enity).getName().equalsIgnoreCase(((TagPop) data.get(i)).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            DiscoveryApi.searchTopic(mKeyword, null, null, mHandler);
        } else {
            sendRequestData();
        }
        topicSearchAdapter.setKeyword(mKeyword);
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ?
                    mAdapter.getItem(mAdapter.getData().size() - 1).getTagPopId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        DiscoveryApi.searchTopic(mKeyword, null, lastid, mHandler);
    }

    @Override
    protected TopicSearchAdapter getListAdapter() {
        return new TopicSearchAdapter(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        //没有下拉刷新, 下拉时显式调用父类方法表示加载完毕
        super.executeOnLoadFinish();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<TagPop> data, boolean isLast) {
        if (data == null) {
            data = new ArrayList<TagPop>();
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
        mAdapter.setState(adapterState);
        mAdapter.addData(data);
        // 判断等于是因为最后有一项是listview的状态
        if (mAdapter.getCount() == 1) {

            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            } else {
                mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                mAdapter.notifyDataSetChanged();
            }
        }
        hideHotword();
    }

    private void getHotWord() {
        DiscoveryManager.getInstance().getTopicCategory(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    mHotWordList = (List<FolderInfo>) obj;
                    if (mHotWordList.size() > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateHotWord();
                            }
                        });

                    }
                }

            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideHotword();
                        mAdapter.clear();
                        mCurrentPage = 0;
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                });
            }
        });
    }

    /**
     *  热词
     */
    private void updateHotWord() {
        if (mHotWordList == null || mHotWordList.size() == 0) return;

        showHotword();
        mHotWord.removeAllViews();
        FlowLayout.LayoutParams tagParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tagParams.setMargins(0, 0, 25, 25);
        MyLog.i("mHotWordList.size=" + mHotWordList.size());
        for (FolderInfo tag : mHotWordList) {
            if (tag == null || TextUtils.isEmpty(tag.getName())) continue;

            //防止下发的热词没有type,防御下
//            if (tag.getType() == null) tag.setType("hot");

            TextView text = new TextView(BaseApplication.context());
            text.setText(tag.getName());
            text.setTextSize(11f);
            text.setTextColor(getResources().getColor(R.color.search_tag_text_color));
            text.setPadding(20, 10, 20, 10);
            text.setGravity(Gravity.CENTER);
            text.setBackgroundResource(R.drawable.long_boreder_gray);
            text.setSingleLine();

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  搜索
                    UIHelper.searchTopic(getActivity(), null, tag.getName());
                }
            });
            mHotWord.addView(text, tagParams);
        }
    }

    private void hideHotword() {
        mLlHotWord.setVisibility(View.GONE);
        mHeadView.setVisibility(View.VISIBLE);
    }

    private void doSearch() {
        if (TDevice.hasInternet()) {
            requestData(true);
        } else {
            hideHotword();
            mAdapter.clear();
            mCurrentPage = 0;
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    }

    private void showHotword() {
        mLlHotWord.setVisibility(View.VISIBLE);
        mHeadView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SEARCH_TOPIC_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SEARCH_TOPIC_FRAGMENT);
    }

}

package com.moinapp.wuliao.modules.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.result.SearchTagResult;
import com.moinapp.wuliao.modules.mine.adapter.MyTagsAdapter;
import com.moinapp.wuliao.modules.mine.model.TagInfoList;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 搜索标签的页面
 * Created by liujiancheng on 15/11/26.
 */
public class SearchTagFragment extends BaseListFragment<TagInfo> {
    private ILogger MyLog = LoggerFactory.getLogger(SearchTagFragment.class.getSimpleName());

    private String mKeyword;
    private SearchTextChangedListener mSearchListener;
    private SearchKeyBoardListener mCosplaySearchListener;

    private static final String CACHE_KEY_PREFIX = "search_tags_list";

    private List<TagInfo> mHotWordList;

    @InjectView(R.id.tv_hot_search)
    public TextView mText;

    @InjectView(R.id.fl_hot_container)
    public FlowLayout mHotWord;

    @InjectView(R.id.ll_hotword_part)
    public LinearLayout mLlHotWord;

    @InjectView(R.id.title_down_area)
    public View mHeadView;

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.INTENT_ACTION_USER_CHANGE)) {
                doSearch();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(
                Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
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
                UIHelper.showTagDetail(getActivity(), searchText, "hot", 0);
            }
        };

        mErrorLayout.setEmptyImage(R.drawable.no_content_black);
        mErrorLayout.setNoDataContent(getString(R.string.error_view_no_data));
        mErrorLayout.setClickable(true);
        SearchViewPagerFragment.setTagListener(mSearchListener);
        SearchViewPagerFragment.setCosplayListener(mCosplaySearchListener);

        getHotWord();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_tag_cosplay;
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
    protected TagInfoList parseList(InputStream is) throws Exception {
        TagInfoList list = new TagInfoList();
        SearchTagResult result = XmlUtils.JsontoBean(SearchTagResult.class, is);
        if (result == null || result.getTags() == null || result.getTags().size() == 0) {
            Log.i("ljc", "search tags is empty");
            return list;
        }
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
        if (refresh) {
            DiscoveryApi.searchTag(mKeyword, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getTagId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        DiscoveryApi.searchTag(mKeyword, lastid, mHandler);
    }

    @Override
    protected MyTagsAdapter getListAdapter() {
        return new MyTagsAdapter(getActivity(), false, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onRefresh() {
        //没有下拉刷新, 下拉时显式调用父类方法表示加载完毕
        super.executeOnLoadFinish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SEARCH_TAG_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SEARCH_TAG_FRAGMENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<TagInfo> data, boolean isLast) {
        if (data == null) {
            data = new ArrayList<TagInfo>();
        }

//        if (mResult != null && mResult.getResult() == 0) {
//            AppContext.showToast(R.string.error_view_load_error_click_to_refresh);
//        }

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
        StickerManager.getInstance().getHotList(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    mHotWordList = (List<TagInfo>) obj;
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

    private void updateHotWord() {
        if (mHotWordList == null || mHotWordList.size() == 0) return;

        showHotword();
        mHotWord.removeAllViews();
        FlowLayout.LayoutParams tagParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tagParams.setMargins(0, 0, 25, 25);
        MyLog.i("mHotWordList.size=" + mHotWordList.size());
        for (TagInfo tag : mHotWordList) {
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
                    UIHelper.showTagDetail(getActivity(), tag.getName(), tag.getType(), 0);
                }
            });
            mHotWord.addView(text, tagParams);
        }
    }

    private void hideHotword() {
        mLlHotWord.setVisibility(View.GONE);
        mHeadView.setVisibility(View.VISIBLE);
    }

    private void showHotword() {
        mLlHotWord.setVisibility(View.VISIBLE);
        mHeadView.setVisibility(View.GONE);
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
}

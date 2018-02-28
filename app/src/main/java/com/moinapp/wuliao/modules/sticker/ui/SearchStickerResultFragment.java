package com.moinapp.wuliao.modules.sticker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.StickerApi;
import com.moinapp.wuliao.modules.sticker.adapter.MyHistoryStickerAdapter;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 搜索贴纸的结果列表
 */
public class SearchStickerResultFragment extends HistoryStickersFragment {
    private static final ILogger MyLog = LoggerFactory.getLogger(SearchStickerResultFragment.class.getSimpleName());

    protected static final String TAG = SearchStickerResultFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "search_sticker_list_";
    public static final String KEY_WORD = "key_word";
    public static final String KEY_TYPE = "type";

    private String mKeyword;
    private String mType;

    @Override
    public void initView(View view) {
        super.initView(view);

        title.setTitleTxt(mKeyword);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX;
    }

    @Override
    protected void initEemptyLayout() {
        mErrorLayout.setEmptyImage(R.drawable.no_content_black);
        mErrorLayout.setNoDataContent(getString(R.string.error_view_no_data));
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mKeyword = args.getString(KEY_WORD);
            mType = args.getString(KEY_TYPE);
        }
    }

    @Override
    protected MyHistoryStickerAdapter getListAdapter() {
        return new MyHistoryStickerAdapter(getActivity(), getString(R.string.search_sticke_result));
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            StickerApi.searchSticker(mKeyword, mType, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getStickerId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        StickerApi.searchSticker(mKeyword, mType, lastid, mHandler);
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SEARCH_STICKER_RESULT_FRAGMENT); //统计页面，
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SEARCH_STICKER_RESULT_FRAGMENT);
    }

    @Override
    protected int getPageSize() {
        return super.getPageSize();
    }
}
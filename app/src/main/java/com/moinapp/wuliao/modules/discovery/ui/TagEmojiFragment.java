package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;

/**
 * 表情页面
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TagEmojiFragment extends EmojiFragment {
    protected static final String TAG = TagEmojiFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "tag_emoji_list";

    private String mTag;
    private String mType;

    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTag = args.getString(Constants.BUNDLE_KEY_TAG);
            mType = args.getString(Constants.BUNDLE_KEY_TYPE);
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            DiscoveryApi.getTagDetail(mTag, mType, 2, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getEmojiId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        DiscoveryApi.getTagDetail(mTag, mType, 2, lastid, mHandler);
    }
}

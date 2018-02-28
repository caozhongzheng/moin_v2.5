package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.modules.discovery.adapter.EmojiListAdaptor;
import com.moinapp.wuliao.modules.discovery.model.EmojiSet;
import com.moinapp.wuliao.modules.discovery.model.EmojiSetList;
import com.moinapp.wuliao.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 表情页面
 *
 * @author liujiancheng
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EmojiFragment extends BaseListFragment<EmojiSet> {

    protected static final String TAG = EmojiFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "emoji_list";

    private String mUid;

    @Override
    public void initView(View view) {
        super.initView(view);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    protected EmojiListAdaptor getListAdapter() {
        return new EmojiListAdaptor();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog;
    }

    @Override
    protected EmojiSetList parseList(InputStream is) throws Exception {
        EmojiSetList result = XmlUtils.JsontoBean(EmojiSetList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        Log.i("ljc", "result.size = " + result.getList().size());
        return result;
    }

    @Override
    protected EmojiSetList readList(Serializable seri) {
        return ((EmojiSetList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((EmojiSet) enity).getEmojiId().equalsIgnoreCase(((EmojiSet) data.get(i)).getEmojiId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
//        if (refresh) {
//            LoginApi.getEmojiList(null, null, null, null, mHandler);
//        } else {
//            sendRequestData();
//        }
    }

    @Override
    protected void sendRequestData() {
//        String lastid = null;
//        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
//            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getEmojiId() : null;
//
//            Log.i("ljc", "lastid = " + lastid);
//        }
//        LoginApi.getEmojiList(null, null, null, lastid, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        EmojiSet item = (EmojiSet) mAdapter.getItem(position);
        if (item != null) {
//            UIHelper.showFriends(getActivity(), item.getUId(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            ((EmojiListAdaptor)mAdapter).unregisterEventBus();
        }
    }
}

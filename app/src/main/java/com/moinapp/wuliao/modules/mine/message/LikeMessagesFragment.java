package com.moinapp.wuliao.modules.mine.message;

import android.annotation.TargetApi;
import android.os.Build;

import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.UmengConstants;
import com.umeng.analytics.MobclickAgent;

/**
 * 赞消息列表
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LikeMessagesFragment extends AllMessagesFragment {
    protected static final String TAG = LikeMessagesFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "my_like_messages_list";

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUid;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.LIKE_MESSAGES_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.LIKE_MESSAGES_FRAGMENT);
    }

    @Override
    protected int getAction() {
        return Messages.ACTION_LIKE_COSPLAY;
    }
}

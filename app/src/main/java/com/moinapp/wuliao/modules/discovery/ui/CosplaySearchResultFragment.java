package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseMultiColumnListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.CosplaySearchResultAdaptor;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagCosplayList;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 搜索图片结果集页面[瀑布流形式]
 *
 * @author moin 2016/5/10
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CosplaySearchResultFragment extends BaseMultiColumnListFragment<CosplayInfo> {
    private static final ILogger MyLog = LoggerFactory.getLogger(CosplaySearchResultFragment.class.getSimpleName());

    private static final String CACHE_KEY_PREFIX = "tag_cosplay_list";

    private String mTag;
    private String mType;
    private String mTpId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTag = args.getString(Constants.BUNDLE_KEY_TAG);
            mType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTpId = args.getString(Constants.BUNDLE_KEY_ID);
        }

        MyLog.i(mTag + ", id=" + mTpId + ", type=" + mType);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        MyLog.i("initView");

        // 设置标题
        StringBuffer stringBuffer = new StringBuffer();

        String name = null;
        if (!TextUtils.isEmpty(mTag)) {
            if (mTag.length() > getResources().getInteger(R.integer.tag_max_len) - 4) {
                name = mTag.substring(0, getResources().getInteger(R.integer.tag_max_len) - 6) + "...";
            } else {
                name = mTag;
            }
        }

        if (mType.equalsIgnoreCase("IP")) {
            stringBuffer.append((name.startsWith("《") ? "" : "《") + name + (name.endsWith("》") ? "" : "》"));
        } else if (mType.equalsIgnoreCase("OP")) {
            stringBuffer.append("#").append(name).append("#");
        } else {
            stringBuffer.append(name);
        }

        mCommonTitleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mCommonTitleBar.setTitleTxt(stringBuffer.toString());
        mCommonTitleBar.hideRightBtn();
//
//        // 设置空数据布局
//        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyLog.i("mErrorLayout onClick requestData refresh="+true + ", ");
//                requestData(true);
//            }
//        });
        mErrorLayout.hideNoLogin();
        mErrorLayout.setBtnVisibility(View.GONE);

        MyLog.i("initView requestData refresh="+true + ", ");
        requestData(true);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.i("requestData refresh="+true + ", onReceive");
            requestData(true);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.COSPLOY_SEARCH_RESULT_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.COSPLOY_SEARCH_RESULT_FRAGMENT);
    }

    @Override
    protected CosplaySearchResultAdaptor getListAdapter() {
        return new CosplaySearchResultAdaptor();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mTag;
    }

    @Override
    protected TagCosplayList parseList(InputStream is) throws Exception {
        TagCosplayList result = XmlUtils.JsontoBean(TagCosplayList.class, is);
        if (result == null) {
            return null;
        }

        return result;
    }

    @Override
    protected TagCosplayList readList(Serializable seri) {
        return ((TagCosplayList) seri);
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getDataSize() > 0) {
            lastid = mAdapter.getItem(mAdapter.getDataSize() - 1) != null ? mAdapter.getItem(mAdapter.getDataSize() - 1).getUcid() : null;
        }
        MyLog.i("sendRequestData lastid = " + lastid);
        DiscoveryApi.getTagDetail(mTag, mType, 1, lastid, mHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

}

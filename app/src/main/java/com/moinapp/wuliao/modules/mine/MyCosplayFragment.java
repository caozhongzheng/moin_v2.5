package com.moinapp.wuliao.modules.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfoList;
import com.moinapp.wuliao.modules.mine.adapter.MyCosplayInfoAdapter;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 空间图片列表
 */
public class MyCosplayFragment extends BaseListFragment<CosplayInfo> {
    private static final ILogger MyLog = LoggerFactory.getLogger(MyCosplayFragment.class.getSimpleName());

    private static final String CACHE_KEY_PREFIX = "my_cosplay_list_";
    private String mUid;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview_with_head;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mAdapter.setFinishText(R.string.no_more_pics);
        FrameLayout header = (FrameLayout) view.findViewById(R.id.fl_header);
        header.removeAllViews();
        View header_view = getActivity().getLayoutInflater().inflate(R.layout.layout_common_title_bar, null);
        header.addView(header_view);
        CommonTitleBar titleBar = (CommonTitleBar) header_view.findViewById(R.id.title_bar);
        titleBar.setTitleTxt(getString(R.string.s_cosplay));
        titleBar.setLeftBtnOnclickListener(v -> {
            getActivity().finish();
        });

        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mErrorLayout.setEmptyImage(R.drawable.activity_empty_image);
            mErrorLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mErrorLayout.setNoDataContent(getString(R.string.ac_grid_no_data_self));
            mErrorLayout.setBtnText(getString(R.string.go_to_discovery_self));
            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO 进入拍照界面
                    CameraManager.getInst().openCamera(getActivity() == null ? AppContext.context() : getActivity(), null);
                    StickPreference.getInstance().setDefaultUseSticker(null);
                }
            });
            mErrorLayout.setOnLayoutClickListener(v -> {
                // TODO 我没有任何图片时,跳转到发现频道
                UIHelper.gotoMain(getActivity(), MainActivity.KEY_TAB_DISCOVERY, true);
            });
            requestData(true);
        } else {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        mUid = args.getString(Constants.BUNDLE_KEY_UID);
        if (mCatalog > 0) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        if (mCatalog > 0 && mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregist(this);
        super.onDestroy();
    }

    @Override
    protected MyCosplayInfoAdapter getListAdapter() {
        MyCosplayInfoAdapter adapter = new MyCosplayInfoAdapter();
        adapter.setFrom(2);
        return adapter;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mUid;
    }

    @Override
    protected CosplayInfoList parseList(InputStream is) throws Exception {
        CosplayInfoList list = XmlUtils.JsontoBean(CosplayInfoList.class, is);
        if (list != null && list.getList() != null) {
            for (CosplayInfo cos:list.getList()) {
                MyLog.i("第几个大咖秀是" + cos.toString());
            }
        }
        return list;
    }

    @Override
    protected CosplayInfoList readList(Serializable seri) {
        return (CosplayInfoList) seri;
    }

    // TODO 如果是CosplayInfo的list的时候,都要重写这个compare
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
    protected void sendRequestData() {
        // 获取所有的图片列表
        String lastid = null;
        if(mCurrentPage != 0 && mAdapter.getData().size() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getUcid() : null;
        }
        MyLog.i("sendRequestData uid=" + mUid + ", lastid=" + lastid + ", mCurrentPage=" + mCurrentPage);
        MineApi.getUserCosplay(mUid, lastid, mHandler);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupContent();
        }
    };

    private void setupContent() {
        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            requestData(true);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mCatalog = AppContext.getInstance().getLoginUid();
            super.requestData(refresh);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected long getAutoRefreshTime() {
        return super.getAutoRefreshTime();
    }

    public void onEvent(CropCosplayFragment.CropCosplay avatarPath) {
        getActivity().finish();
    }

    public void onEvent(DiscoveryManager.CosplayDeleteEvent event) {
        MyLog.i("ljc:receive event:ucid="+event.getUcid());
        for (CosplayInfo cosplayInfo : mAdapter.getData()) {
            if (event.getUcid().equalsIgnoreCase(cosplayInfo.getUcid())) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.removeItem(cosplayInfo);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.MY_COSPLAY_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.MY_COSPLAY_FRAGMENT);
    }

    //不取缓存数据, 因为需要删除图片
    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }
}
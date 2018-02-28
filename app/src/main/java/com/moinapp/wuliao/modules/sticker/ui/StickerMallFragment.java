package com.moinapp.wuliao.modules.sticker.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseLazyListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.sticker.StickerApi;
import com.moinapp.wuliao.modules.sticker.model.StickerGroupList;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.StickerListAdaptor;
import com.moinapp.wuliao.util.PinYinUtil;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 贴纸商城的具体某个分类的内容fragment
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StickerMallFragment extends BaseLazyListFragment<StickerPackage>
        implements OnTabReselectListener {

    protected static final String TAG = StickerMallFragment.class.getSimpleName();
    private static final ILogger MyLog = LoggerFactory.getLogger(TAG);
    private static final String CACHE_KEY_PREFIX = "allsticker_list";

    private String mName;//分类接口的名字
    private String mType;//分类接口的类型

    protected boolean isPrepared;
    protected int isTitleDownAreaVis = 0;

    private boolean isRegistered = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestData(true);
        }
    };

    @Override
    public void initView(View view) {
        super.initView(view);
        View headerView = View.inflate(getActivity(), R.layout.layout_title_down_grey_area, null);
        if (isTitleDownAreaVis==1){
            mListView.addHeaderView(headerView);
        }else if (isTitleDownAreaVis==0){
            mErrorLayout.hideTitleDownArea();
        }
        mErrorLayout.setEmptyImage(R.drawable.no_content_black);
    }

    @Override
    public void initData() {
        if (!isPrepared || !isVisible) {
            return;
        }
        super.initData();
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mName = args.getString(Constants.BUNDLE_KEY_NAME);
            mType = args.getString(Constants.BUNDLE_KEY_TYPE);
            isTitleDownAreaVis = args.getInt(Constants.BUNDLE_KEY_DOWN_AREA_VISIBLE);
        }

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);

        getActivity().registerReceiver(mReceiver, filter);
        isRegistered = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        isPrepared = true;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.STICKER_MALL_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.STICKER_MALL_FRAGMENT);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sticker_mall;
    }

    @Override
    protected StickerListAdaptor getListAdapter() {
        StickerListAdaptor listAdaptor = new StickerListAdaptor(getActivity());
        listAdaptor.setDownloadCallback(new Callback() {
            @Override
            public void onStart() {
                showWaitDialog(R.string.waitting);
            }

            @Override
            public void onFinish(int result) {
                hideWaitDialog();
                switch (result) {
                    case Constants.RESULT_OK:
                        MinePreference.getInstance().setNeedRefreshPhotoEdit(true);
                        AppContext.getInstance().showToast(R.string.download_success);
                        break;
                    case Constants.RESULT_NETWORK_ERROR:
                        AppContext.getInstance().showToast(R.string.download_fail);
                        break;
                    case Constants.RESULT_NO_NETWORK:
                        AppContext.getInstance().showToast(R.string.no_network);
                        break;
                }
            }
        });
        return listAdaptor;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog +"_"+ PinYinUtil.getPinyin(mName);
    }

    @Override
    protected StickerGroupList parseList(InputStream is) throws Exception {
        StickerGroupList result = XmlUtils.JsontoBean(StickerGroupList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        } else {
            // test code start
//            List<StickerPackage> resultList = result.getList();
//            int size = resultList.size();
//            if(size > 0) {
//                resultList.get(0).setUpdatedAt(System.currentTimeMillis());
//            }
//            if(size > 1) {
//                resultList.get(1).setUpdatedAt(System.currentTimeMillis() - StickerManager.STICKER_NEWST_INTERVAL + 50*1000);
//            }
//            if(size > 2) {
//                resultList.get(2).setUpdatedAt(System.currentTimeMillis() - StickerManager.STICKER_NEWST_INTERVAL - 50*1000);
//            }
            // test code end
//            DBHelper dbHelper = new DBHelper(BaseApplication.context());
//            for (int i = 0; i < resultList.size(); i++) {
//                ArrayList<EmoticonSetBean> x = dbHelper.queryEmoticonSetByID(ClientInfo.getUID(), resultList.get(i).getStickerPackageId());
//                if(x == null || x.isEmpty()) {
//                    android.util.Log.w("smf", resultList.get(i).getName() + "没有在本地DB");
//                    resultList.get(i).setIsDownload(0);
//                }
//            }
        }
        return result;
    }

    @Override
    protected StickerGroupList readList(Serializable seri) {
        return ((StickerGroupList) seri);
    }

    @Override
    protected boolean compareTo(List data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((StickerPackage) enity).getStickerPackageId().equalsIgnoreCase(((StickerPackage) data.get(i)).getStickerPackageId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
//            StickerApi.getStickerGroupList(mName, mType, null,mHandler);
            super.requestData(refresh);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? ((StickerPackage) mAdapter.getItem(mAdapter.getData().size() - 1)).getStickerPackageId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
//        keyWords = searchEdit.getText().toString().replaceAll(" ", "");
        StickerApi.getStickerGroupList(mName, mType, lastid, mHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            ((StickerListAdaptor)mAdapter).unregisterEventBus();
        }
        if (mReceiver != null && isRegistered) {
            try {
                getActivity().unregisterReceiver(mReceiver);
                isRegistered = false;
            } catch (Exception e) {
                MyLog.e(e);
            }
        }
    }
}

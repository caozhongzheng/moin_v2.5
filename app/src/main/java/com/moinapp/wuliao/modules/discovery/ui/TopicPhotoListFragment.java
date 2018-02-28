package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseMultiColumnListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.CosplaySearchResultAdaptor;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TopicArticleList;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

import butterknife.InjectView;

/**
 * 帖子的图片专区结果集页面[瀑布流形式]
 *
 * @author moin 2016/6/17
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TopicPhotoListFragment extends BaseMultiColumnListFragment<CosplayInfo> {
    private static final String CACHE_KEY_PREFIX = "topic_photo_list_";

    private StickerPackage mSticker;
    private String mTag;
    private String mType;
    private String mTpId;

    @InjectView(R.id.btn_float)
    protected FloatingActionButton floatingBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTag = args.getString(Constants.BUNDLE_KEY_TAG);
            mType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTpId = args.getString(Constants.BUNDLE_KEY_ID);
            mSticker = (StickerPackage) args.getSerializable(Constants.BUNDLE_KEY_STICKER);
            android.util.Log.i("TopicPhotoListFragment", "话题[" + mTag + "] 对应的贴纸包是 " + (mSticker == null ? null : mSticker));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic_photo_list;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        // 设置标题
        mCommonTitleBar.setTitleTxt(mTag + getResources().getString(R.string.topic_photo_list));
        mCommonTitleBar.setLeftBtnOnclickListener(v -> getActivity().finish());
        mCommonTitleBar.hideRightBtn();

        mErrorLayout.hideNoLogin();
        mErrorLayout.setBtnVisibility(View.GONE);

        requestData(true);

        floatingBtn.setOnClickListener(v -> {
            // 我要发图-图片专区
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(UmengConstants.ITEM_ID, mTag + "_" + mTpId);
            MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_COSPLAY_CLICK_COSPLAYS, map);
            MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_COSPLAY_CLICK, map);

            if (mSticker != null && !StringUtil.isNullOrEmpty(mSticker.getStickerPackageId())) {
                CameraManager.getInst().makeCosplay(getActivity(), mSticker.getStickerPackageId(), mTpId, mType, mTag);
            } else {
                StickPreference.getInstance().setJoinTopicName(mTag);
                CameraManager.getInst().openCamera(getActivity(), null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.TOPIC_PHOTO_LIST_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.TOPIC_PHOTO_LIST_FRAGMENT);
    }

    @Override
    protected CosplaySearchResultAdaptor getListAdapter() {
        return new CosplaySearchResultAdaptor();
    }


    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getDataSize() > 0) {
            lastid = mAdapter.getItem(mAdapter.getDataSize() - 1) != null ? mAdapter.getItem(mAdapter.getDataSize() - 1).getUcid() : null;
        }
        android.util.Log.i(TopicPhotoListFragment.class.getSimpleName(),
                "sendRequestData lastid = " + lastid
                        + ", tagName=" + mTag
                        + ", tagId=" + mTpId
                        + ", tagType=" + mType
        );
        DiscoveryApi.getTopicCosplay("1", mTpId, mTag, null, lastid, mHandler);
    }

    @Override
    protected TopicArticleList parseList(InputStream is) throws Exception {
        TopicArticleList list = XmlUtils.JsontoBean(TopicArticleList.class, is);
        return list;
    }

    @Override
    protected TopicArticleList readList(Serializable seri) {
        return ((TopicArticleList) seri);
    }


    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTpId;
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新关注10分钟刷新一次
        return 10 * 60;
    }

}

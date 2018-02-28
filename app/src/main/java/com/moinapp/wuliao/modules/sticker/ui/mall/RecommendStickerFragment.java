package com.moinapp.wuliao.modules.sticker.ui.mall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.StickerItemAdapter;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.ui.NoScrollGridView;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 商城的推荐贴纸包fragment
 * Created by guyunfei on 16/5/25.15:52.
 */
public class RecommendStickerFragment extends Fragment {
    private ILogger MyLog = LoggerFactory.getLogger(RecommendStickerFragment.class.getSimpleName());

    @InjectView(R.id.stickers)
    protected NoScrollGridView recommendStickers;

    @InjectView(R.id.fl_hot_container)
    public FlowLayout mHotWord;

    @InjectView(R.id.ly_empty)
    public EmptyLayout emptyLayout;


    private ArrayList<TagInfo> mHotWordList;
    private StickerPackage stickerPackage;

    public static final String RECOMMEND_STICKER_CACHE_KEY = "Recommend_sticker_list_cache";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_sticker_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        getRecommendSticker();
        getHotWord();
    }

    private void initView(View view) {

        if (!TDevice.hasInternet()) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            emptyLayout.setClickable(true);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getRecommendSticker();
                    getHotWord();
                }
            });
        } else {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
    }

    private String getStickerCacheKey() {
        return RECOMMEND_STICKER_CACHE_KEY + "_" + "stickers";
    }

    private String getHotWordCacheKey() {
        return RECOMMEND_STICKER_CACHE_KEY + "_" + "hot_words";
    }


    private void getRecommendSticker() {
        stickerPackage = (StickerPackage) CacheManager.readObject(getActivity(), getStickerCacheKey());
        if (stickerPackage == null) {
            StickerManager.getInstance().getStickerUpdate(2, 0, null, new IListener2() {
                @Override
                public void onSuccess(Object obj) {
                    if (obj != null) {
                        stickerPackage = (StickerPackage) obj;
                        if (stickerPackage != null) {
                            CacheManager.saveObject(getActivity(), stickerPackage, getStickerCacheKey());
                            updateRecommendSticker(stickerPackage);
                        }
                    }
                }

                @Override
                public void onErr(Object obj) {
                    MyLog.i("请求推荐贴纸失败");
                }

                @Override
                public void onNoNetwork() {
                    MyLog.i("请求推荐贴纸失败无网络");
                }
            });
        } else {
            updateRecommendSticker(stickerPackage);
        }
    }

    private void updateRecommendSticker(StickerPackage stickerPackage) {
        if (stickerPackage == null) return;

        emptyLayout.setVisibility(View.GONE);
        StickerItemAdapter stickerItemAdapter = new StickerItemAdapter(getActivity(), stickerPackage, false, true);
        recommendStickers.setAdapter(stickerItemAdapter);
    }

    private void getHotWord() {
        mHotWordList = (ArrayList<TagInfo>) CacheManager.readObject(getActivity(), getHotWordCacheKey());
        if (mHotWordList == null) {
            StickerManager.getInstance().getHotList(new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    if (obj != null) {
                        mHotWordList = (ArrayList<TagInfo>) obj;
                        if (mHotWordList != null && mHotWordList.size() > 0) {
                            CacheManager.saveObject(getActivity(), mHotWordList, getHotWordCacheKey());
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

                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateHotWord();
                }
            });
        }
    }

    private void updateHotWord() {
        emptyLayout.setVisibility(View.GONE);

        if (mHotWordList == null || mHotWordList.size() == 0) return;

        mHotWord.removeAllViews();
        FlowLayout.LayoutParams tagParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tagParams.setMargins(0, 0, 25, 25);
        MyLog.i("mHotWordList.size=" + mHotWordList.size());
        for (TagInfo tag : mHotWordList) {
            if (tag == null || TextUtils.isEmpty(tag.getName())) continue;
            TextView text = new TextView(getActivity());
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
                    UIHelper.showSearchStickerResult(getActivity(), tag.getName(), tag.getType());
                }
            });
            mHotWord.addView(text, tagParams);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.RECOMMEND_STICKER_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.RECOMMEND_STICKER_FRAGMENT);
    }

}

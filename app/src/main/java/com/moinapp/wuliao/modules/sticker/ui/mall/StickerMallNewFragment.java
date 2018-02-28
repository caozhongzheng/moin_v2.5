package com.moinapp.wuliao.modules.sticker.ui.mall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.FolderInfo;
import com.moinapp.wuliao.modules.sticker.model.GetFolderResult;
import com.moinapp.wuliao.modules.sticker.ui.StickerMallFragment;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 贴纸商城[包含一级二级分类] V3.1
 * Created by guyunfei on 16/2/2.16:25.
 */
public class StickerMallNewFragment extends BaseFragment {
    private static ILogger MyLog = LoggerFactory.getLogger(StickerMallNewFragment.class.getSimpleName());

    private ArrayList<String> mallTabName;
    @InjectView(R.id.pager_tabstrip)
    protected PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.pager)
    protected ViewPager mViewPager;
    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;
    @InjectView(R.id.left_layout)
    public View mLeftLayout;
    @InjectView(R.id.search_layout)
    public View mSearchLayout;
    protected ViewPageFragmentAdapter mTabsAdapter;
    private List<FolderInfo> folder;
    private long startTime;
    private long lastEndtme, endTime;
    private View view;
    private long duration;

    public StickerMallNewFragment setIsShowBack(boolean isShowBack) {
        this.isShowBack = isShowBack;
        return this;
    }

    private boolean isShowBack;
    private boolean isFirstTimeIn = true;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sticker_mall_new, null);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        if (!isFirstTimeIn) {
            startTime = TimeUtils.getCurrentTimeInLong();
        }
        return view;
    }

    private void initUI(List<FolderInfo> folder) {
        if (folder == null) {
            return;
        }
        mViewPager.setOffscreenPageLimit(folder.size());

        mallTabName = new ArrayList<String>();
        if (folder != null) {
            for (int i = 0; i < folder.size(); i++) {
                mallTabName.add(folder.get(i).getName());
            }
        }
        if (isAdded()) {
            mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(),
                    mTabStrip, mViewPager);
            onSetupTabAdapter(mTabsAdapter);
        }

        mTabStrip.setOnClickTabListener(new PagerSlidingTabStrip.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {
                if (index == mViewPager.getCurrentItem()) {
                    try {
                        Fragment currentFragment = getChildFragmentManager().getFragments()
                                .get(index);
                        if (currentFragment != null
                                && currentFragment instanceof OnTabReselectListener) {
                            OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                            listener.onTabReselect();
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }
        });
        mTabStrip.setOnPagerChange(new PagerSlidingTabStrip.OnPagerChangeLis() {
            @Override
            public void onChanged(int page) {
                if (page != 0) {
                    TDevice.hideSoftKeyboard(mTabStrip);
                }
            }
        });
    }

    @Override
    public void initView(View view) {
        mLeftLayout.setVisibility(isShowBack ? View.VISIBLE : View.INVISIBLE);
        mLeftLayout.setOnClickListener(v -> {
            getActivity().finish();
        });

        mSearchLayout.setOnClickListener(v -> {
            UIHelper.searchSticker(getActivity());
        });
    }

    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        for (int i = 0; i < folder.size(); i++) {
            List<TagInfo> list = folder.get(i).getList();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE_KEY_MARGIN, R.dimen.space_10);
            if (list == null || list.size() == 0) {
                if (mallTabName.get(i).equals("推荐")) {
                    adapter.addTab(mallTabName.get(i), mallTabName.get(i) + "", RecommendStickerFragment.class, bundle);
                } else {
                    bundle.putString(Constants.BUNDLE_KEY_NAME, folder.get(i).getName());
                    bundle.putString(Constants.BUNDLE_KEY_TYPE, folder.get(i).getType());
                    bundle.putInt(Constants.BUNDLE_KEY_DOWN_AREA_VISIBLE, 1);
                    adapter.addTab(mallTabName.get(i), mallTabName.get(i) + "", StickerMallFragment.class, bundle);
                }
            } else {
                bundle.putSerializable(StickerListWithTab.STICKER_LIST, (Serializable) list);
                adapter.addTab(mallTabName.get(i), mallTabName.get(i) + "", StickerListWithTab.class, bundle);
            }
        }
    }

    @Override
    public void initData() {
        if (StickerManager.getInstance().isFolderListExpired()) {
            MyLog.i("get Folderlist isFolderListExpired ");
            fetchFolderList();
        } else {
            GetFolderResult result = StickerManager.getInstance().getStickerMallFolderResult(getActivity());
            MyLog.i("get Folderlist from cache not Expired " + result);
            if (result != null && result.getFolderList() != null) {
                mErrorLayout.setVisibility(View.GONE);
                folder = result.getFolderList();
                onGetFolderSuccess();
            } else {
                fetchFolderList();
            }
        }
    }

    private void fetchFolderList() {
        mErrorLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        StickerManager.getInstance().getFolderList(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("get Folderlist succeed! from network");
                if (obj == null) {
                    return;
                }
                mErrorLayout.setVisibility(View.GONE);
                folder = ((GetFolderResult) obj).getFolderList();

                StickPreference.getInstance().setLastStickerMallFolders(System.currentTimeMillis());
                StickerManager.getInstance().setStickerMallFolderResult(getActivity(), ((GetFolderResult) obj));

                if (folder != null) {
//                        for (int i = 0; i < folder.size(); i++) {
//                            MyLog.i(folder.get(i).getList()+"===");
//                        }
                    onGetFolderSuccess();
                }
            }

            @Override
            public void onErr(Object obj) {
                MyLog.i("get Folderlist onErr ");
                mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
                mErrorLayout.setClickable(true);
                mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mState = STATE_REFRESH;
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                        initData();
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("get Folderlist onNONetwork ");
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                mErrorLayout.setClickable(true);
                mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mState = STATE_REFRESH;
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                        initData();
                    }
                });
            }
        });
    }

    private void onGetFolderSuccess() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initUI(folder);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFirstTimeIn) {
            startTime = TimeUtils.getCurrentTimeInLong();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.STICKER_MALL_NEW_FRAGMENT); //统计页面
        if (!isFirstTimeIn) {
            startTime = TimeUtils.getCurrentTimeInLong();
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                endTime = TimeUtils.getCurrentTimeInLong();
                if (lastEndtme == 0 || count < 6) {
                    lastEndtme = endTime;
                } else {
                    count = 0;
                    duration = endTime - startTime;
                    if (isFirstTimeIn) {
                        HashMap<String, String> forward = new HashMap<String, String>();
                        forward.put("type", UmengConstants.T_STICKER_STORE);
                        onEvent(getActivity(), UmengConstants.T_STICKER_STORE, forward, (int) duration);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
                count += 1;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstTimeIn = false;
        MobclickAgent.onPageEnd(UmengConstants.STICKER_MALL_NEW_FRAGMENT);
    }

    public static void onEvent(Context context, String id, HashMap<String, String> m, long value) {
        m.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, m);
    }
}

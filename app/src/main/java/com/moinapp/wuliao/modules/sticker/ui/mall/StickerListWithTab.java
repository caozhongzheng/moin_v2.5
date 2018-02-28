package com.moinapp.wuliao.modules.sticker.ui.mall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.sticker.ui.StickerMallFragment;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 贴纸商城有二级分类的fragment
 * Created by guyunfei on 16/2/4.10:22.
 */
public class StickerListWithTab extends BaseFragment {

    public static final String STICKER_LIST = "list";

    private String[] mTabNames;
    @InjectView(R.id.pager_tabstrip)
    protected PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.pager)
    protected ViewPager mViewPager;
    private List<TagInfo> tagList;
    private ViewPageFragmentAdapter mTabsAdapter;
    private int lastPosition = 0;
    private int currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tagList = (List<TagInfo>) bundle.getSerializable(STICKER_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_with_tab, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void initView(View view) {
        mViewPager.setOffscreenPageLimit(tagList.size());

        mTabNames = new String[tagList.size()];

        for (int i = 0; i < tagList.size(); i++) {
            mTabNames[i] = tagList.get(i).getName();
        }

        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(),
                mTabStrip, mViewPager);
        onSetupTabAdapter(mTabsAdapter);
        //设置初始选择的标题变大
        setTabTextSize(0);

        mTabStrip.setOnClickTabListener(new PagerSlidingTabStrip.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {
                setTabTextSize(index);
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
                setTabTextSize(page);
                if (page != 0) {
                    TDevice.hideSoftKeyboard(mTabStrip);
                }
            }
        });

    }

    /**
     *  设置标题的字体大小,选中状态为15号,未选中状态为13号
     * */
    private void setTabTextSize(int index) {
        currentPosition = index;
        LinearLayout linearLayout = (LinearLayout) mTabStrip.getChildAt(0);

        RelativeLayout relativeLayout = (RelativeLayout) linearLayout.getChildAt(currentPosition);
        TextView currentTab = (TextView) relativeLayout.getChildAt(0);
        if (currentTab != null) {
            currentTab.setTextSize(15);
            currentTab.setGravity(Gravity.CENTER_VERTICAL);
        }
        if (lastPosition != currentPosition) {
            RelativeLayout lastRelativeLayout = (RelativeLayout) linearLayout.getChildAt(lastPosition);
            TextView lastTab = (TextView) lastRelativeLayout.getChildAt(0);
            if (lastTab != null) {
                lastTab.setTextSize(13);
                lastTab.setGravity(Gravity.CENTER_VERTICAL);
            }
            lastPosition = currentPosition;
        }
    }

    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        for (int i = 0; i < tagList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE_KEY_NAME, tagList.get(i).getName());
            bundle.putString(Constants.BUNDLE_KEY_TYPE, tagList.get(i).getType());
            bundle.putInt(Constants.BUNDLE_KEY_DOWN_AREA_VISIBLE, 0);
            bundle.putInt(Constants.BUNDLE_KEY_STYLE, R.style.sticker_mall_three_tab);
            bundle.putInt(Constants.BUNDLE_KEY_MARGIN, R.dimen.space_10);
            adapter.addTab(mTabNames[i], mTabNames[i] + "", StickerMallFragment.class, bundle);
        }
    }
}

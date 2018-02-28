package com.moinapp.wuliao.modules.sticker.ui.mall;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.modules.sticker.ui.HistoryStickersFragment;
import com.moinapp.wuliao.modules.sticker.ui.MyStickerFragment;
import com.moinapp.wuliao.ui.NoScrollViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 贴纸中心[商城,我的历史,我的贴纸]
 * Created by guyunfei on 16/1/27.17:10.
 */
public class StickerCenterViewPagerFragment extends Fragment {

    public static final int CENTER_TAB_NUM = 3;

    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private NoScrollViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private int tabNum = 0;
    private ArrayList<Fragment> mFragments ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mFragmentManager = getFragmentManager();
        Bundle bundle = getArguments();
        tabNum = bundle.getInt(Constants.BUNDLE_KEY_TABINDEX);
        mFragments=new ArrayList<Fragment>();
        mFragments.add((new StickerMallNewFragment()).setIsShowBack(true));
        mFragments.add(new HistoryStickersFragment());
        mFragments.add(new MyStickerFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        setGroupCheck(tabNum);
        return view;
    }

    public void setGroupCheck(int tabNum) {
        switch (tabNum){
            case 0:
                mRadioGroup.check(R.id.rb_sticker_mall);
                break;
            case 1:
                mRadioGroup.check(R.id.rb_history);
                break;
            case 2:
                mRadioGroup.check(R.id.rb_my_sticker);
                break;
        }
    }

    private View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_sticker_mall_viewpager, null);

        mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content_sticker_center);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_group_sticker_center);

        mViewPager.setAdapter(new StickerCenterAdapter(getFragmentManager()));
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sticker_mall:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_history:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_my_sticker:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    private class StickerCenterAdapter extends FragmentPagerAdapter {

        public StickerCenterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return CENTER_TAB_NUM;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            getFragmentManager().beginTransaction().remove(getItem(position));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.STICKER_CENTER_VIEWPAGER_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.STICKER_CENTER_VIEWPAGER_FRAGMENT);
    }

}
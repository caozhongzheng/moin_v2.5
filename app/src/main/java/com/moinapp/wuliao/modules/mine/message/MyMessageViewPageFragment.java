package com.moinapp.wuliao.modules.mine.message;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的消息页面,里面包括全部消息和各个分类的消息
 * Created by liujiancheng on 16/1/18.
 */
public class MyMessageViewPageFragment extends BaseFragment {

    private int mInitTabIdx;
    private Handler mHandler = new Handler();

    @InjectView(R.id.pager_tabstrip)
    protected PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.pager)
    protected ViewPager mViewPager;
    protected ViewPageFragmentAdapter mTabsAdapter;
    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;
    @InjectView(R.id.left_layout)
    public View mLeftLayout;
    @InjectView(R.id.new_message_tip)
    public TextView mNewMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_viewpage_fragment, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(),
                mTabStrip, mViewPager);
        onSetupTabAdapter(mTabsAdapter);

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

        mLeftLayout.setOnClickListener(v -> {
            MinePreference.getInstance().setLastReadTime(System.currentTimeMillis());
            getActivity().finish();
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mInitTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);

        EventBus.getDefault().register(this);
   }

    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(R.array.my_message);
        adapter.addTab(title[0], "all_message", AllMessagesFragment.class, getBundle());
        adapter.addTab(title[1], "comment_message", CommentMessagesFragment.class, getBundle());
        adapter.addTab(title[2], "like_message", LikeMessagesFragment.class, getBundle());
        adapter.addTab(title[3], "foward_message", ForwardMessagesFragment.class, getBundle());
        adapter.addTab(title[4], "follow_message", FollowMessagesFragment.class, getBundle());

        mViewPager.setCurrentItem(mInitTabIdx);
        mViewPager.setOffscreenPageLimit(5);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.MY_MESSAGE_VIEWPAGE_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.MY_MESSAGE_VIEWPAGE_FRAGMENT);
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("STYLE", R.style.search_text_bg);
        return bundle;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView(View view) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initData() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onBackPressed() {
        MinePreference.getInstance().setLastReadTime(System.currentTimeMillis());
        return false;
    }

    public void onEvent(MineManager.ReceivedMessage message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNewMessage.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Animation anim = AnimationUtils.loadAnimation(AppContext.context(), R.anim.out_to_top);
//                        mNewMessage.startAnimation(anim);
//                        anim.setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationEnd(Animation arg0) {
//                                mNewMessage.setVisibility(View.GONE);
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation animation) {
//                            }
//
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//                            }
//                        });
                        mNewMessage.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        });
    }
}


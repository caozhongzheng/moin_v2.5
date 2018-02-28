package com.moinapp.wuliao.modules.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的viewpager页面
 */
public class MineViewPagerFragment extends BaseFragment {
	
	private static String RECEIVE_MSG = "";

	@InjectView(R.id.pager_tabstrip)
	protected PagerSlidingTabStrip mTabStrip;
	@InjectView(R.id.pager)
	protected ViewPager mViewPager;
	protected ViewPageFragmentAdapter mTabsAdapter;
//	@InjectView(R.id.error_layout)
//	protected EmptyLayout mErrorLayout;
	@InjectView(R.id.left_layout)
	public View mLeftLayout;

	private int mTabIdx;

	private boolean registed = false;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			RECEIVE_MSG = intent.getAction();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mine_viewpage_fragment, null);
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

		mLeftLayout.setVisibility(View.INVISIBLE);
		mLeftLayout.setOnClickListener(v -> {
			getActivity().finish();
		});

//		if (AppContext.getInstance().isLogin()) {
//			mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//		} else {
//			mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
//		}
//
//		mErrorLayout.setOnLayoutClickListener(v -> {
//			UIHelper.showLoginActivity(getActivity());
//		});
//
//		if (!AppContext.getInstance().isLogin()) {
//			UIHelper.showLoginActivity(getActivity());
//		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mTabIdx = args == null ? 1 : args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
		filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);
		getActivity().registerReceiver(mReceiver, filter);
		registed = true;
	}
	
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.my_tab);
		adapter.addTab(title[0], "useractivity", UserActivityFragment.class, null);
		adapter.addTab(title[1], "userspace", UserSpaceFragment.class, null);

		int unreadMsg = MineManager.getInstance().getUnreadMessages(MinePreference.getInstance().getLastReadTime());
		if (unreadMsg > 0 && AppContext.getInstance().isLogin()) {
			mTabIdx = 1;
		}

		mViewPager.setCurrentItem(mTabIdx);
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.MINEVIEWPAGER_FRAGMENT); //统计页面
//		if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
//			if (mErrorLayout != null) {
//				if (Constants.INTENT_ACTION_USER_CHANGE.equals(RECEIVE_MSG)) {
//					mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//				} else {
//					mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
//				}
//			}
//			RECEIVE_MSG = "";
//		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.MINEVIEWPAGER_FRAGMENT);
	}

	@Override
	public void onDestroy() {
		if (mReceiver != null && registed) {
			getActivity().unregisterReceiver(mReceiver);
			registed = false;
		}
		super.onDestroy();
	}
}

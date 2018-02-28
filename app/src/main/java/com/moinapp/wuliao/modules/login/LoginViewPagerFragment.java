package com.moinapp.wuliao.modules.login;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 登录/注册viewpager页面[old]
 */
public class LoginViewPagerFragment extends BaseFragment {
	
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
	private int gotoFollow;


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

		mLeftLayout.setOnClickListener(v -> {
			getActivity().finish();
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);
		gotoFollow = args.getInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, 0);
	}
	
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.login);
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.BUNDLE_KEY_GOTO_FOLLOW, gotoFollow);
		adapter.addTab(title[0], "login", LoginFragment.class, bundle);
		adapter.addTab(title[1], "regist", RegistFragment_step1.class, bundle);

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
}

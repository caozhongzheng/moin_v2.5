package com.moinapp.wuliao.modules.discovery.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 赞评论viewpager页面
 * @author liujiancheng
 */
public class LikeCommentViewPagerFragment extends BaseFragment {

	@InjectView(R.id.pager_tabstrip)
	protected PagerSlidingTabStrip mTabStrip;
	@InjectView(R.id.pager)
	protected ViewPager mViewPager;
	protected ViewPageFragmentAdapter mTabsAdapter;
	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;
	@InjectView(R.id.left_layout)
	public View mLeftLayout;

	private int mInitTabIdx;
	//0:默认赞列表和评论列表都显示 1:只显示赞列表 2:只显示评论列表
	private int mInitType;

	private String mUcid;

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

		mLeftLayout.setOnClickListener(v -> {
			getActivity().finish();
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mInitTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);
		mUcid = args.getString(Constants.BUNDLE_KEY_ID);
		mInitType = args.getInt(Constants.BUNDLE_KEY_TYPE, 0);
	}

	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.cosplay_like_comment);
		switch (mInitType) {
			case 0:
				adapter.addTab(title[0], "like", LikeCosplayFragment.class, getBundle());
				adapter.addTab(title[1], "comment", CommentListFragment.class, getBundle());
				break;
			case 1:
				adapter.addTab(title[0], "like", LikeCosplayFragment.class, getBundle());
				mTabStrip.setSlidingBlockDrawable(getResources().getDrawable(R.drawable.transparent));
				break;
			case 2:
				adapter.addTab(title[1], "comment", CommentListFragment.class, getBundle());
				mTabStrip.setSlidingBlockDrawable(getResources().getDrawable(R.drawable.transparent));
				break;
		}
		mViewPager.setCurrentItem(mInitTabIdx);
	}

	private Bundle getBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.BUNDLE_KEY_ID, mUcid);
		bundle.putInt(Constants.BUNDLE_KEY_STYLE, R.style.search_text_bg);
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.LIKE_COMMENTVIEWPAGER_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.LIKE_COMMENTVIEWPAGER_FRAGMENT);
	}
}

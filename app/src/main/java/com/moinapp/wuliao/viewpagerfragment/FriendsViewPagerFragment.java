package com.moinapp.wuliao.viewpagerfragment;

import android.os.Bundle;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseViewPagerFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.FriendsList;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.modules.mine.FansFragment;
import com.moinapp.wuliao.modules.mine.FollowersFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 关注、粉丝viewpager页面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 *
 */
public class FriendsViewPagerFragment extends BaseViewPagerFragment {
	
	private int mInitTabIdx;
	
	private String mUid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mInitTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);
		mUid = args.getString(Constants.BUNDLE_KEY_UID);
	}
	
	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.friends_viewpage_arrays);
		adapter.addTab(title[0], "follower", FollowersFragment.class, getBundle(FriendsList.TYPE_FOLLOWER));
		adapter.addTab(title[1], "fans", FansFragment.class, getBundle(FriendsList.TYPE_FANS));
		
		mViewPager.setCurrentItem(mInitTabIdx);
	}
	
	private Bundle getBundle(int catalog) {
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.BUNDLE_KEY_CATALOG, catalog);
		bundle.putString(Constants.BUNDLE_KEY_UID, mUid);
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
		MobclickAgent.onPageStart(UmengConstants.FRIENDS_VIEWPAGER_FRAGMENT); //统计页面，
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.FRIENDS_VIEWPAGER_FRAGMENT);
	}
}

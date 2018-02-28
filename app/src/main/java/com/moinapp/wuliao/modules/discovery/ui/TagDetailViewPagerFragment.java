package com.moinapp.wuliao.modules.discovery.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 标签详情viewpager页面,包含两个frangment:图片和表情
 * @author liujiancheng
 */
public class TagDetailViewPagerFragment extends BaseFragment {
	private ILogger MyLog = LoggerFactory.getLogger("TagDetailViewPagerFragment");

	private int mInitTabIdx;
	private String mTag;
	private String mType;
	private int mIsIdol;//是否已经关注此标签的标记

	@InjectView(R.id.pager_tabstrip)
	protected PagerSlidingTabStrip mTabStrip;
	@InjectView(R.id.pager)
	protected ViewPager mViewPager;
	protected ViewPageFragmentAdapter mTabsAdapter;
	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;
	@InjectView(R.id.title_left_area)
	public LinearLayout mLeftLayout;
	@InjectView(R.id.title_middle)
	public TextView mTitle;
	@InjectView(R.id.btn_follow)
	public TextView mFollow;
	@InjectView(R.id.title_right_area)
	public LinearLayout lyFollow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tag_viewpage_fragment, null);
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

		StringBuffer stringBuffer = new StringBuffer();

		String name = null;
		if (!TextUtils.isEmpty(mTag)) {
			if (mTag.length() > getResources().getInteger(R.integer.tag_max_len) - 4) {
				name = mTag.substring(0, getResources().getInteger(R.integer.tag_max_len) - 6) + "...";
			} else {
				name = mTag;
			}
		}

		if (mType.equalsIgnoreCase("IP")) {
			stringBuffer.append((name.startsWith("《") ? "" : "《") + name + (name.endsWith("》") ? "" : "》"));
		} else if (mType.equalsIgnoreCase("OP")) {
			stringBuffer.append("#").append(name).append("#");
		} else {
			stringBuffer.append(name);
		}
		mTitle.setText(stringBuffer.toString());

		lyFollow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//未登录时提示登陆
				if (!AppContext.getInstance().isLogin()) {
					UIHelper.showLoginActivity(getActivity());
					return;
				}
				if (Tools.isFastDoubleClick()) {
					return;
				}
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(UmengConstants.ITEM_ID, mTag);
				map.put(UmengConstants.FROM, "标签详情");
				MobclickAgent.onEvent(getActivity(), UmengConstants.TAG_FOLLOW,map);
				if (mIsIdol == 0) {
					DiscoveryManager.getInstance().followTag(mTag, mType, 1, new IListener() {
						@Override
						public void onSuccess(Object obj) {
							mIsIdol = 1;
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									setFollowUserStatus(mFollow, mIsIdol);
								}
							});
						}

						@Override
						public void onErr(Object obj) {

						}

						@Override
						public void onNoNetwork() {

						}
					});
				}
			}
		});
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mInitTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);
		mTag = args.getString(Constants.BUNDLE_KEY_TAG);
		mType = args.getString(Constants.BUNDLE_KEY_TYPE);

		EventBus.getDefault().register(this);
	}

	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.tag_detail);
		adapter.addTab(title[0], "cosplay",    TagCosplayFragment.class, getBundle());
		adapter.addTab(title[1], "emoji", TagEmojiFragment.class, getBundle());

		mViewPager.setCurrentItem(mInitTabIdx);
	}
	
	private Bundle getBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.BUNDLE_KEY_TAG, mTag);
		bundle.putString(Constants.BUNDLE_KEY_TYPE, mType);
		bundle.putInt(Constants.BUNDLE_KEY_COLOR, R.color.common_title_grey);
		return bundle;
	}

	private void setFollowUserStatus(TextView follow, int isIdol) {
		if (isIdol == 1) {
			follow.setText("已关注");
		} else  {
			follow.setText("+ 关注");
		}
	}

	//接收TagCosplayFragment类发送来的是否关注标签的结果
	public void onEvent(TagFollowResult event) {
		MyLog.i("onEvent: isIdol =" + event.getIsIdol());
		mIsIdol = event.getIsIdol();
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mFollow.setVisibility(View.VISIBLE);
				setFollowUserStatus(mFollow, mIsIdol);
			}
		});
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
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregist(this);
	}

	public static class TagFollowResult {
		private int isIdol;
		public TagFollowResult(int isIdol) {
			this.isIdol = isIdol;
		}

		public int getIsIdol() {
			return this.isIdol;
		}
	}
}

package com.moinapp.wuliao.modules.discovery.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TypefaceUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.BadgeView;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 首页viewpager页面 [关注,发现,表情]
 * @author liujiancheng
 */
public class MainViewPagerFragment extends BaseFragment {
	private static final ILogger MyLog = LoggerFactory.getLogger("mvp");

	private int mTabIdx;
	@InjectView(R.id.pager_tabstrip)
	protected PagerSlidingTabStrip mTabStrip;
	@InjectView(R.id.pager)
	protected ViewPager mViewPager;
	protected ViewPageFragmentAdapter mTabsAdapter;
	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;
	@InjectView(R.id.iv_avatar)
	public AvatarView mIvMe;
	@InjectView(R.id.iv_left)
	public ImageView mIvLeft;
	@InjectView(R.id.unread_mes)
	public TextView mUnreadMsg;
	@InjectView(R.id.unpop_mes)
	public TextView mUnpopMsg;
	@InjectView(R.id.search_layout)
	public RelativeLayout mSearchLy;

	private BadgeView mBvMsg;
	private HashMap<Integer, String> msgActionMap = new HashMap();
	private HashMap<Integer, Integer> msgCountMap = new HashMap();

	@Override
	public void onResume() {
		super.onResume();
		initAvatar();
		//如果有未读消息,显示
//		displayMessageTip();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_viewpage_fragment, null);
		ButterKnife.inject(this, view);
		EventBus.getDefault().register(this);
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
//				MyLog.i("主界面的回调 你点击了tab " + index + ", lastTab= " + mViewPager.getCurrentItem());
				if (index == mViewPager.getCurrentItem()) {
					try {
//						int currentIndex = mViewPager.getCurrentItem();
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
				if (page != 2) {
					MyLog.i("不在 商城 关闭键盘");
					TDevice.hideSoftKeyboard(mTabStrip);
				}
			}
		});

		initAvatar();

		mUnreadMsg.setVisibility(View.GONE);
		initBadgeView();

		mSearchLy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.showSearch(getActivity());
			}
		});

		mViewPager.setOffscreenPageLimit(3);
	}

	private void initAvatar() {
		if(AppContext.getInstance().isLogin()) {
			mIvMe.setVisibility(View.VISIBLE);
			mIvLeft.setVisibility(View.GONE);
			UserInfo loginUser = AppContext.getInstance().getUserInfo();
			MyLog.i("loginUser:" + loginUser);
			if(loginUser != null && loginUser.getAvatar() != null && !StringUtil.isNullOrEmpty(loginUser.getAvatar().getUri())) {
				mIvMe.setAvatarUrl(loginUser.getAvatar().getUri());
			} else {
				mIvMe.setImageResource(R.drawable.widget_dface);
			}
		} else {
			mIvMe.setVisibility(View.GONE);
			mIvLeft.setVisibility(View.VISIBLE);
		}
	}

	private void initBadgeView() {
		mBvMsg = new BadgeView(getActivity(), mUnpopMsg);
		mBvMsg.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
		mBvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		mBvMsg.setBackgroundResource(R.drawable.icon_msg);
		mBvMsg.setGravity(Gravity.CENTER);

		mBvMsg.setOnClickListener(v -> {
			if(mBvMsg.isShown()) {
				mBvMsg.hide();
			}
//			updateLastReadTime();
//			mUnreadMsg.setVisibility(View.GONE);
			UIHelper.showMine(getActivity(), 1);
		});
	}

	private void displayMessageTip() {
		if(AppContext.getInstance().isLogin()) {
			if (hasUnReadMsg()) {
				mUnreadMsg.setVisibility(View.VISIBLE);
				List< Messages > unpopMsgList = MineManager.getInstance().getUnpopMessageList(MinePreference.getInstance().getLastPopTime());
				MyLog.i("displayMessageTip unpopMsgList.size=" + unpopMsgList.size());

				if (unpopMsgList != null && !unpopMsgList.isEmpty()) {
					msgCountMap.clear();

					// TODO 根据Messages的type,action确定归类到哪种消息组
					StringBuffer lastText = new StringBuffer();
					for (Messages message: unpopMsgList) {
						int messageType = MineManager.getInstance().ConvertMessageType(message);
						if (!msgCountMap.containsKey(messageType)) {
							msgCountMap.put(messageType, 1);
						} else {
							int count = msgCountMap.get(messageType) + 1;
							msgCountMap.put(messageType, count);
						}
					}
					for (HashMap.Entry<Integer, Integer> entry : msgCountMap.entrySet()) {
						lastText.append(msgActionMap.get(entry.getKey()));
						lastText.append(entry.getValue()).append(" ");
					}
					TypefaceUtils.setTypeface(mBvMsg, lastText.toString().trim());

					mBvMsg.show();
					updateLastPopTime();

					hidePop();
				} else {
					mBvMsg.hide();
				}
			} else {
				if (mBvMsg.isShown()) mBvMsg.hide();
				mUnreadMsg.setVisibility(View.GONE);
			}

		}
	}

	private void hidePop() {
		Animation anim = AnimationUtils.loadAnimation(AppContext.context(), R.anim.anim_pop_msg);
		mBvMsg.startAnimation(anim);
		anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				mBvMsg.hide();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}

	private void updateLastReadTime() {
		MinePreference.getInstance().setLastReadTime(System.currentTimeMillis());
	}

	private void updateLastPopTime() {
		MinePreference.getInstance().setLastPopTime(System.currentTimeMillis());
	}

	private boolean hasUnReadMsg() {
		int unreadMsg = MineManager.getInstance().getUnreadMessages(MinePreference.getInstance().getLastReadTime());
		return unreadMsg > 0;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);

		buildMsgMap();
	}

	private void buildMsgMap() {
		Resources res = AppContext.getInstance().getResources();
		msgActionMap.put(Messages.MESSAGE_COMMENT, res.getString(R.string.fa_comment_o));
		msgActionMap.put(Messages.MESSAGE_FORWARD, res.getString(R.string.fa_share_square_o));
		msgActionMap.put(Messages.MESSAGE_LIKE, res.getString(R.string.fa_heart_o));
		msgActionMap.put(Messages.MESSAGE_AT, res.getString(R.string.fa_at));
		msgActionMap.put(Messages.MESSAGE_FOLLOW, res.getString(R.string.fa_user_md));
		msgActionMap.put(Messages.MESSAGE_EMOJI, res.getString(R.string.fa_smile_o));
		msgActionMap.put(Messages.MESSAGE_STICKER, res.getString(R.string.fa_photo));
		msgActionMap.put(Messages.MESSAGE_SYSTEM, res.getString(R.string.fa_bullhorn));
	}

	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.main_tab);
		adapter.addTab(title[MainActivity.KEY_TAB_DISCOVERY], "discovery", DiscoveryRecycleViewFragment.class, null);
		adapter.addTab(title[MainActivity.KEY_TAB_FOLLOW], "follow", FollowFragment.class, null);
//		adapter.addTab(title[2], "stickmall", StickerMallFragment.class, null);

		mViewPager.setCurrentItem(mTabIdx);
	}

	@Override
	@OnClick({R.id.mine_layout, R.id.iv_avatar, R.id.iv_left})
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
			case R.id.mine_layout:
			case R.id.iv_avatar:
			case R.id.iv_left:
				clickAvatar();
				break;
			default:
				break;
		}
	}

	private void clickAvatar() {
		MyLog.i("你点击了顶部头像");
		if (AppContext.getInstance().isLogin()) {
			if (hasUnReadMsg()) {
//				updateLastReadTime();
//				mUnreadMsg.setVisibility(View.GONE);
				if(mBvMsg.isShown()) {
					mBvMsg.hide();
				}
				UIHelper.showMine(getActivity(), 1);
			} else {
				UIHelper.showMine(getActivity(), 0);
			}
		} else {
			//设置参数,表示登陆完成后要回到关注页面
			UIHelper.showLoginActivity(getActivity(), 1);
		}
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

	public void onEvent(MineManager.ReceivedMessage message) {
		/*
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				displayMessageTip();
			}
		});
		*/
	}
}

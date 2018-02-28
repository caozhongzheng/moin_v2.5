package com.moinapp.wuliao.ui;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.discovery.ui.DiscoveryRecycleViewFragment;
import com.moinapp.wuliao.modules.discovery.ui.FollowFragment;
import com.moinapp.wuliao.modules.events.ui.EventsFragment;
import com.moinapp.wuliao.modules.mine.MineViewPagerFragment;

public enum MainTab {


	DISCOVERY(MainActivity.KEY_TAB_DISCOVERY, R.string.main_tab_name_discovery, R.drawable.tab_icon_explore,
			DiscoveryRecycleViewFragment.class),

	FOLLOW(MainActivity.KEY_TAB_FOLLOW, R.string.main_tab_name_focus, R.drawable.tab_icon_follow,
			FollowFragment.class),

	QUICK(2, R.string.tab_cosplay, R.drawable.tab_icon_sticker,
		  null),

	EVENTS(3, R.string.events_title, R.drawable.tab_icon_events,
			EventsFragment.class),

	ME(4, R.string.main_tab_name_me, R.drawable.tab_icon_me,
			MineViewPagerFragment.class);

	private int idx;
	private int resName;
	private int resIcon;
	private Class<?> clz;

	private MainTab(int idx, int resName, int resIcon, Class<?> clz) {
		this.idx = idx;
		this.resName = resName;
		this.resIcon = resIcon;
		this.clz = clz;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getResName() {
		return resName;
	}

	public void setResName(int resName) {
		this.resName = resName;
	}

	public int getResIcon() {
		return resIcon;
	}

	public void setResIcon(int resIcon) {
		this.resIcon = resIcon;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}
}

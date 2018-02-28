package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.widget.togglebutton.ToggleButton;
import com.moinapp.wuliao.widget.togglebutton.ToggleButton.OnToggleChanged;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 消息通知设置界面[oschina]
 * */
public class SettingsMyNotificationFragment extends BaseFragment {
	@InjectView(R.id.tb_accept) ToggleButton mTbAccept;
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_my_notifcation, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.mes_notice_setting));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});

		setToggleChanged(mTbAccept, AppConfig.KEY_NOTIFICATION_ACCEPT);

		view.findViewById(R.id.rl_accept).setOnClickListener(this);
	}

	public void initData() {
		setToggle(AppContext.get(AppConfig.KEY_NOTIFICATION_ACCEPT, true), mTbAccept);
	}
	
	private void setToggleChanged(ToggleButton tb, final String key) {
		tb.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				AppContext.set(key, on);
			}
		});
	}
	
	private void setToggle(boolean value, ToggleButton tb) {
		if (value)
			tb.setToggleOn();
		else
			tb.setToggleOff();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.rl_accept:
			mTbAccept.toggle();
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.SETTING_MY_NOTIFICATION_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.SETTING_MY_NOTIFICATION_FRAGMENT);
	}
}

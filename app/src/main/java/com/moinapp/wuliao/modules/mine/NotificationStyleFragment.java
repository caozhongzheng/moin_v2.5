package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 新消息提醒方式
 */
public class NotificationStyleFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;

	@InjectView(R.id.notify_style_group)
	RadioGroup mRadioGroup;
	@InjectView(R.id.notify_style_sound)
	RadioButton mRbSound;
	@InjectView(R.id.notify_style_vibrate)
	RadioButton mRbVibrate;
	@InjectView(R.id.notify_style_none)
	RadioButton mRbNone;
	@InjectView(R.id.rl_notify_style_sound)
	RelativeLayout mRlSound;
	@InjectView(R.id.rl_notify_style_vibrate)
	RelativeLayout mRlVibrate;
	@InjectView(R.id.rl_notify_style_none)
	RelativeLayout mRlNone;


	/**
	 * http://push.baidu.com/doc/restapi/msg_struct：可以设置通知的基本样式包括(响铃：0x04;振动：0x02;可清除：0x01;),这是一个flag整形，每一位代表一种样式,如果想选择任意两种或三种通知样式，notification_basic_style的值即为对应样式数值相加后的值。
	 */
	public static final int NOTIFY_STYLE_SOUND = 7;
	public static final int NOTIFY_STYLE_VIBRATE = 3;
	public static final int NOTIFY_STYLE_NONE = 0;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_notification_style, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});
		changeNotifyStyle(MinePreference.getInstance().getNotificationStyle());

		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				if (checkedId == mRbSound.getId()) {
					changeOK(NOTIFY_STYLE_SOUND);
				} else if (checkedId == mRbVibrate.getId()) {
					changeOK(NOTIFY_STYLE_VIBRATE);
				} else if (checkedId == mRbNone.getId()) {
					changeOK(NOTIFY_STYLE_NONE);
				}
			}
		});

		mRlSound.setOnClickListener(v -> changeNotifyStyle(NOTIFY_STYLE_SOUND));
		mRlVibrate.setOnClickListener(v -> changeNotifyStyle(NOTIFY_STYLE_VIBRATE));
		mRlNone.setOnClickListener(v -> changeNotifyStyle(NOTIFY_STYLE_NONE));
	}


	private void changeNotifyStyle(int style) {
		mRadioGroup.clearCheck();
		switch (style) {
			case NOTIFY_STYLE_SOUND:
				mRadioGroup.check(mRbSound.getId());
				break;
			case NOTIFY_STYLE_VIBRATE:
				mRadioGroup.check(mRbVibrate.getId());
				break;
			default:
				mRadioGroup.check(mRbNone.getId());
				break;
		}
	}

	private void changeOK(int style) {
		switch (style) {
			case NOTIFY_STYLE_SOUND:
				mRadioGroup.check(mRbSound.getId());
				break;
			case NOTIFY_STYLE_VIBRATE:
				mRadioGroup.check(mRbVibrate.getId());
				break;
			default:
				mRadioGroup.check(mRbNone.getId());
				break;
		}
		MinePreference.getInstance().setNotificationStyle(style);
		// TODO 接口更新消息提醒方式
		MineManager.getInstance().updateSettings(style, MinePreference.getInstance().isSaveWatermark() ? 1 : 0, new IListener() {
			@Override
			public void onSuccess(Object obj) {
				getActivity().finish();
			}

			@Override
			public void onErr(Object obj) {
				getActivity().finish();
			}

			@Override
			public void onNoNetwork() {
				getActivity().finish();
			}
		});

		getActivity().finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.NotificationStyleFragment); //统计页面，
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.NotificationStyleFragment);
	}
}

package com.moinapp.wuliao.modules.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 意见反馈
 */
public class MyFeedbackFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;
	@InjectView(R.id.rl_good_settings)
	RelativeLayout mRlGood;
	@InjectView(R.id.rl_bad_settings)
	RelativeLayout mRlBad;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_my_feedback, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.feedback));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});
		mRlGood.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(LoginConstants.MOIN_APP_URL));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		});
		mRlBad.setOnClickListener(v -> {
			UIHelper.showSettingFeedbackLament(getActivity());
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.MY_FEEDBACK_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.MY_FEEDBACK_FRAGMENT);
	}
}

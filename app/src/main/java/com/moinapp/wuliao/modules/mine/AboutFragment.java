package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 关于MOIN
 */
public class AboutFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;
	@InjectView(R.id.rl_service_protocal)
	RelativeLayout mRlProtocal;
	@InjectView(R.id.rl_official_wechat)
	RelativeLayout mRlContact;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_about, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.about));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});
		mRlProtocal.setOnClickListener(v -> {
			UIHelper.showAboutProtocal(getActivity());
		});
		mRlContact.setOnClickListener(v -> {
			UIHelper.showAboutWechat(getActivity());
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.ABOUT_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.ABOUT_FRAGMENT);
	}
}

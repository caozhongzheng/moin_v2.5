package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 服务协议
 */
public class AboutProtocalFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_about_protocal, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.service_agreement));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.ABOUT_PROTOCAL_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.ABOUT_PROTOCAL_FRAGMENT);
	}

}

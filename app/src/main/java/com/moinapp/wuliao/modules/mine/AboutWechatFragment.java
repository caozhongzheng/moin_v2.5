package com.moinapp.wuliao.modules.mine;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 官方微信
 */
public class AboutWechatFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;
	@InjectView(R.id.ll_moin_wechat)
	LinearLayout mLlContact;
	@InjectView(R.id.wechat_qrcode)
	ImageView mIvCode;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_about_wechat, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.contact));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});
		mLlContact.setOnClickListener(v -> {
			copyToClip();
		});
		mIvCode.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				copyToClip();
				return false;
			}
		});
	}

	private void copyToClip() {
		// 得到剪贴板管理器  API11后是android.content.ClipboardManager
		ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(getString(R.string.contact_wechat).trim());
		AppContext.showToast(R.string.wechat_copy_tip);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.ABOUT_WECHAT_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.ABOUT_WECHAT_FRAGMENT);
	}
}

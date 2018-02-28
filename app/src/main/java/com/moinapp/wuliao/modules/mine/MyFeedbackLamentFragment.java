package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.AppTools;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MyFeedbackLamentFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;
	@InjectView(R.id.feedback_content)
	EditText mContent;
	@InjectView(R.id.feedback_contact)
	EditText mContact;
	@InjectView(R.id.submit)
	Button btnSubmit;
	private static final int MAX_LENGTH = 600;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_my_feedback_lament, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.feedback_bad));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});
		commonTitleBar.setRightTxtBtn(getString(R.string.summit));


		mContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
				MAX_LENGTH) {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
									   Spanned dest, int dstart, int dend) {
				if (source.length() > 0 && dest.length() == MAX_LENGTH) {
					AppContext.getInstance().showToast(R.string.over_length_limit);
				}
				return super.filter(source, start, end, dest, dstart, dend);
			}
		}});

		commonTitleBar.setRightBtnOnclickListener(v -> {
			String content = mContent.getText().toString().trim();
			String contact = mContact.getText().toString().trim();

			if (TextUtils.isEmpty(content)) {
				AppContext.getInstance().showToast(R.string.feedback_empty);
				return;
			}

			if (!AppTools.isNetworkAvailable(getActivity())) {
				AppContext.getInstance().showToast(R.string.no_network);
				return;
			}
			LoginManager.getInstance().userFeedback(contact, content, new IListener2() {

				@Override
				public void onSuccess(Object obj) {
					AppContext.getInstance().showToast(R.string.feedback_succ);
					btnSubmit.setEnabled(false);
					getActivity().finish();
				}

				@Override
				public void onNoNetwork() {
					AppContext.getInstance().showToast(R.string.no_network);
				}


				@Override
				public void onErr(Object object) {
					android.util.Log.w("fb", "feedback_failed " + object.toString());
					AppContext.getInstance().showToast(R.string.feedback_failed);
				}
			});
		});
	}


	private boolean mShowConfirm;
	@Override
	public boolean onBackPressed() {
		String content = mContent.getText().toString().trim();
		if (!TextUtils.isEmpty(content) && !mShowConfirm) {
			AppContext.getInstance().showToast(R.string.feedback_confirm);
			mShowConfirm = true;
			return true;
		} else {
			getActivity().finish();
			return false;
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.MY_FEEDBACKLAMENT_FRAGMENT); //统计页面
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.MY_FEEDBACKLAMENT_FRAGMENT);
	}
}

package com.moinapp.wuliao.modules.sticker.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 贴纸商城的搜索页面
 * @author liujiancheng
 */
public class SearchStickerFragment extends BaseFragment {
	private ILogger MyLog = LoggerFactory.getLogger("SearchStickerFragment");

	private String mSearchText;
	private List<TagInfo> mHotWordList;

	@InjectView(R.id.search_et)
	public EditText mSearchEdit;
	@InjectView(R.id.clear_iv)
	public ImageView mClear_iv;
	@InjectView(R.id.cancel)
	public TextView mCancel;
	@InjectView(R.id.tv_hot_search)
	public TextView mText;

	@InjectView(R.id.fl_hot_container)
	public FlowLayout mHotWord;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_sticker_fragment, null);
		ButterKnife.inject(this, view);
		initViews();
		getHotWord();
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void initViews() {
		mSearchEdit.setFocusable(true);
		mSearchEdit.setFocusableInTouchMode(true);
		mSearchEdit.requestFocus();

		//默认弹出键盘
		showKeyboard();

		//设置搜索输入时的热词显示状态
		mSearchEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				mClear_iv.setVisibility(editable.toString().length() > 0 ? View.VISIBLE : View.GONE);
				mSearchText = editable.toString().trim();
				if (mSearchText != null && mSearchText.length() > 0) {
					mHotWord.setVisibility(View.INVISIBLE);
					mText.setVisibility(View.INVISIBLE);
				} else {
					mHotWord.setVisibility(View.VISIBLE);
					mText.setVisibility(View.VISIBLE);
				}
			}
		});

		//设置键盘上搜索键的响应
		mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					TDevice.hideSoftKeyboard(v);
					doSearch(mSearchText, null);
					return true;
				}
				return false;
			}
		});

		//清楚搜索输入框的文本
		mClear_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSearchEdit.setText(null);
			}
		});

		mCancel.setOnClickListener(v -> {
			getActivity().finish();
		});
	}

	private void getHotWord() {
		StickerManager.getInstance().getHotList(new IListener() {
			@Override
			public void onSuccess(Object obj) {
				mHotWordList = (List<TagInfo>) obj;
				if (mHotWordList != null && mHotWordList.size() > 0) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateHotWord();
						}
					});

				}

			}

			@Override
			public void onErr(Object obj) {

			}

			@Override
			public void onNoNetwork() {

			}
		});
	}

	private void updateHotWord() {
		if (mHotWordList == null || mHotWordList.size() == 0) return;

		mHotWord.removeAllViews();
		FlowLayout.LayoutParams tagParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		tagParams.setMargins(0, 0, 25, 25);
		MyLog.i("mHotWordList.size=" + mHotWordList.size());
		for (TagInfo tag : mHotWordList) {
			if (tag == null || TextUtils.isEmpty(tag.getName())) continue;
			TextView text = new TextView(getActivity());
			text.setText(tag.getName());
			text.setTextSize(11f);
			text.setTextColor(getResources().getColor(R.color.search_tag_text_color));
			text.setPadding(20, 10, 20, 10);
			text.setGravity(Gravity.CENTER);
			text.setBackgroundResource(R.drawable.long_boreder_gray);
			text.setSingleLine();

			text.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					doSearch(tag.getName(), tag.getType());
				}
			});
			mHotWord.addView(text, tagParams);
		}
	}

	private void doSearch(String keyword, String type) {
		UIHelper.showSearchStickerResult(getActivity(), keyword, type);
	}

	/**
	 * 为防止由于页面加载过程弹不出软键盘,延迟500毫秒再弹
	 */
	private void showKeyboard() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager =
						(InputMethodManager) mSearchEdit.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mSearchEdit, 0);
			}
		}, 500);
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
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UmengConstants.SEARCH_STICKER_FRAGMENT); //统计页面，
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UmengConstants.SEARCH_STICKER_FRAGMENT);
	}
}

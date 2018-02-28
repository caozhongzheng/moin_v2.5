package com.moinapp.wuliao.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.preference.CommonsPreference;


public class CommentPopWindow {

	private Activity activity;
	private View view;
	private EditText mEt;
	private Button mSend;
	private PopupWindow popwindow;

	public CommentPopWindow(Activity activity, int width, int height) {
		this.activity = activity;
		this.view = activity.getLayoutInflater().inflate(R.layout.emoji_title, null);
		
		view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
		mEt = (EditText) view.findViewById(R.id.emoji_titile_input);
		TextWatcher watcher = new TextWatcher() {
			private int editStart;
			private int added;
			private int maxlength = activity.getResources().getInteger(R.integer.comment_max_len);
			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
				editStart = mEt.getSelectionStart();
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				added = count - before;
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String finalStr = editable.toString();
				if(finalStr.length() > 0 ) {
					if (added > 0) {
						String inputChar = finalStr.substring(editStart, editStart + 1);
						if (finalStr.length() == 1 && inputChar.equals("@")) {
							mSend.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
						} else {
							mSend.setBackgroundResource(R.drawable.tag_btn_solid_bg);
						}
					} else {
						mSend.setBackgroundResource(R.drawable.tag_btn_solid_bg);
					}
				} else {
					mSend.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
				}

				if (added > 0) {
					if (finalStr.length() > maxlength) {
						mEt.removeTextChangedListener(this);
						mEt.getText().delete(editStart, editStart + added);
						mEt.setSelection(editStart);
						mEt.addTextChangedListener(this);
						return;
					}
					String inputChar = finalStr.substring(editStart, editStart + 1);
					if (inputChar.equals("@")) {
						mEt.removeTextChangedListener(this);
						mEt.getText().delete(editStart, editStart + added);
						mEt.setSelection(editStart);
						mEt.addTextChangedListener(this);
					}
				}
			}
		};
		mEt.addTextChangedListener(watcher);

		mSend = (Button) view.findViewById(R.id.emoji_title_send);
		mSend.setOnClickListener(v -> {
			dismiss();
			onClickSend();
		});

		popwindow = new PopupWindow(view, width, height);
		popwindow.setContentView(view);
		popwindow.setFocusable(true);	//该方法可以设定popupWindow获取焦点的能力。当设置为true时，系统会捕获到焦点给popupWindow上的组件。默认为false
		popwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popwindow.setOutsideTouchable(true);	//要让点击PopupWindow之外的地方PopupWindow消失你需要调用setBackgroundDrawable(new BitmapDrawable());
		popwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popwindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setBgDim_off();
				onDissmissCallback();
			}
		});
	}

	private void onClickSend() {
		if(callback != null) {
			callback.onComment(mEt.getText().toString());
		}
	}

	private void onDissmissCallback() {
		hideSoftKeyboard();
		if(callback != null) {
			callback.onFinish();
		}
	}

	private void onShowCallback() {
		showSoftKeyboard();
		if(callback != null) {
			callback.onStart();
		}
	}

	public void clean() {
		mEt.setText(null);
		mEt.setTag(null);
	}

	public void setHint(String hint) {
		mEt.setHint(hint);
	}

	public View getEditText() {
		return mEt;
	}

	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyboard() {
		((InputMethodManager) activity.getSystemService(
				Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				mEt.getWindowToken(), 0);
	}

	/**
	 * 显示软键盘
	 */
	public void showSoftKeyboard() {
		mEt.requestFocus();
		((InputMethodManager) activity.getSystemService(
				Context.INPUT_METHOD_SERVICE)).showSoftInput(mEt,
				InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 设置背景透明度，实现dialog弹出背景变暗效果
	 */
	public void setBgDim_on() {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = 1.0f;//0.3f
		activity.getWindow().setAttributes(lp);
	}
	
	public void setBgDim_off() {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = 1.0f;
		activity.getWindow().setAttributes(lp);
	}

	/**
	 * @param gravity	依靠父布局的位置如Gravity.CENTER 
	 * @param gravity		gravity
	 * @param xoffset		x偏移量
	 * @param yoffset		y偏移量
	 * 我发现showAtLocation的parent参数可以很随意，只要是activity中的view都可以。
	 */
	public void show(int gravity, int xoffset, int yoffset) {
		popwindow.showAtLocation(view, gravity, xoffset, xoffset);
		setBgDim_on();
		onShowCallback();
	}
	
	/**
	 * 从底部滑出
	 */
	public void showButtom() {
		popwindow.setAnimationStyle(R.style.popwin_anim_style);
		popwindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		setBgDim_on();
		onShowCallback();
	}
	
	
	/**
	 * 弹出对话框，位置在紧挨着view组件
	 * @param anchor
	 * @param xoffset	x偏移量
	 * @param yoffset	y偏移量
	 */
	public void showAsDropDown(View anchor, int xoffset, int yoffset) {
		popwindow.showAsDropDown(anchor, xoffset, yoffset);
		setBgDim_on();
	}
	
	public void dismiss() {
		setBgDim_off();
		popwindow.dismiss();
	}

	private CommentCallback callback = null;

	public interface CommentCallback {
		void onComment(String string);
		void onFinish();
		void onStart();
	}

	public void setCallback(CommentCallback callback) {
		this.callback = callback;
	}
}

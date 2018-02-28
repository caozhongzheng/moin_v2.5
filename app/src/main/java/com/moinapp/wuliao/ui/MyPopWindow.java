package com.moinapp.wuliao.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.moinapp.wuliao.R;


public class MyPopWindow {

	private Activity activity;
	private View view;
	private PopupWindow popwindow;
	private boolean isShowing;

	public MyPopWindow(Activity activity, View view, int width, int height) {
		this.activity = activity;
		this.view = view;
		popwindow = new PopupWindow(view, width, height);
		popwindow.setContentView(view);
		popwindow.setFocusable(true);	//该方法可以设定popupWindow获取焦点的能力。当设置为true时，系统会捕获到焦点给popupWindow上的组件。默认为false
		popwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popwindow.setOutsideTouchable(true);	//要让点击PopupWindow之外的地方PopupWindow消失你需要调用setBackgroundDrawable(new BitmapDrawable());
		popwindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setBgDim_off();
			}
		});
	}

	public void setOutsideTouchable(boolean enable) {
		popwindow.setOutsideTouchable(enable);
	}
	/**
	 * 设置背景透明度，实现dialog弹出背景变暗效果
	 */
	public void setBgDim_on() {
		isShowing = true;
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = 0.3f;
		activity.getWindow().setAttributes(lp);
	}
	
	public void setBgDim_off() {
		isShowing = false;
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
	}
	
	/**
	 * 无动画
	 */
	public void show(int gravity, boolean anim) {
		if(anim) {
			popwindow.setAnimationStyle(R.style.popwin_anim_style);
		} else {
			popwindow.setAnimationStyle(0);
		}
		popwindow.showAtLocation(view, gravity, 0, 0);
		setBgDim_on();
	}
	/**
	 * 从底部滑出
	 */
	public void show(int gravity) {
		show(gravity, false);
	}
	/**
	 * 从底部滑出
	 */
	public void showButtom() {
		show(Gravity.BOTTOM, true);
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
		try {
			setBgDim_off();
			popwindow.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isShowing() {
		return isShowing;
	}
}

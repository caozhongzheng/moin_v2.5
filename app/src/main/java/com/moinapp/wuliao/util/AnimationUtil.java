package com.moinapp.wuliao.util;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * 动画类
 */
public class AnimationUtil {
	
	/**
	 * 位移动画
	 * @param view
	 * @param durationMillis
	 * @param fromX
	 * @param toX
	 * @param fromY
	 * @param toY
	 */
	public static void moveTo(View view, long durationMillis, int fromX, int toX, int fromY, int toY) {
		TranslateAnimation ta = new TranslateAnimation(fromX, toX, fromY, toY);
		ta.setDuration(durationMillis);
		ta.setFillAfter(true);//设置终止填充,该方法用于设置一个动画效果执行完毕后，View对象保留在终止的位置。
		view.startAnimation(ta);
	}
}

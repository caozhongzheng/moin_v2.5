package com.moinapp.wuliao.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.commons.info.MobileInfo;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.util.DisplayUtil;

public class BaseFragmentActivity extends FragmentActivity {
	private LayoutInflater mCustomInflater;
	public int mVirtualKeyHeight;

	protected RelativeLayout rl_enter;
	protected LinearLayout lv_loading;
	protected LinearLayout lv_reload;

//	protected final static int MODE_LOADING = AppConstants.MODE_LOADING;
//	protected final static int MODE_RELOADING = AppConstants.MODE_RELOADING;
//	protected final static int MODE_OK = AppConstants.MODE_OK;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		//tintManager.setStatusBarTintEnabled(true);
		tintManager.setNavigationBarTintEnabled(true);

		setVirtualKeyHeight();

		super.onCreate(savedInstanceState);
	}

	@Override
    protected void onResume() {
        super.onResume();
        if(MobileInfo.getDeviceName().equals("ZTE__ZTE__ZTE G718C")){
            getWindow().getDecorView().setPadding(0,50,0,0);
        } else if(MobileInfo.isMeizuNote()) {
			CommonsPreference.getInstance().setVirtualKeyboardHeight(DisplayUtil.dip2px(getApplicationContext(), 48));
        }
    }

	@Override
	public Object getSystemService(final String name) {
	    if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
	        return getCustomLayoutInflater();
	    }
	    return super.getSystemService(name);
	}

	@Override
	public LayoutInflater getLayoutInflater() {
	    return getCustomLayoutInflater();
	}

	private LayoutInflater getCustomLayoutInflater() {
	    if (mCustomInflater == null) {
	    	LayoutInflater systemInflater = (LayoutInflater)super.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			if (FontFactory.hasSetDefaultTypeface()) {
//				mCustomInflater = new TypefaceLayoutInflater(systemInflater,
//						this);
//				mCustomInflater.setFactory(this);
//			} else {
				mCustomInflater = systemInflater;
//			}
	    }
	    
	    return mCustomInflater;
	}
	
 	@Override
 	public AssetManager getAssets() {  
 	    return this.getApplicationContext().getAssets(); 
 	}  

 	@Override
 	public Resources getResources() {  
 	    return this.getApplicationContext().getResources();  
 	}

	public int getVirtualKeyHeight() {
		return mVirtualKeyHeight;
	}
	private void setVirtualKeyHeight() {
		Rect r = new Rect();
		View rootview = this.getWindow().getDecorView(); // this = activity
		rootview.getWindowVisibleDisplayFrame(r);
		mVirtualKeyHeight = r.height();
	}
//
//	protected void initLoadingView() {
//		rl_enter = (RelativeLayout) this.findViewById(R.id.__enter);
//		lv_loading = (LinearLayout) this.findViewById(R.id.__loading);
//		lv_reload = (LinearLayout) this.findViewById(R.id.__reload);
//		lv_reload.setOnClickListener(onclick);
//	}
//
//	protected void setLoadingMode(int mode) {
//		rl_enter.setVisibility(mode == MODE_OK ? View.GONE : View.VISIBLE);
//		lv_loading.setVisibility(mode == MODE_LOADING ? View.VISIBLE : View.GONE);
//		lv_reload.setVisibility(mode == MODE_RELOADING ? View.VISIBLE : View.GONE);
//	}
//
//	private View.OnClickListener onclick = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//				case R.id.__reload:
//					reloadHandle();
//					break;
//			}
//		}
//	};
//
//	protected void reloadHandle() {}
}

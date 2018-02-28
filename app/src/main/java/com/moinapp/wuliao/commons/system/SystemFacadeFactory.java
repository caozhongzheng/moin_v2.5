package com.moinapp.wuliao.commons.system;

import android.content.Context;

import com.moinapp.wuliao.base.BaseApplication;

public class SystemFacadeFactory {
	private static SystemFacade sMock;
	private static SystemFacade sInstance;
	
	public static void setMock(SystemFacade mock){
		sMock = mock;
	}
	
	public static SystemFacade getSystem(){
		return getSystem(BaseApplication.context());
	}
	
	public static SystemFacade getSystem(Context context){
		if (sMock != null){
			return sMock;
		} 

		if (sInstance == null){
			sInstance = new AndroidSystemFacade(context);
		}
		
		return sInstance;		
	}
}

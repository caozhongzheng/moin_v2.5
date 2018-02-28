package com.moinapp.wuliao.commons.info;

import android.util.Log;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

public class ClientInfoFactory {
	private static ILogger MyLog = LoggerFactory.getLogger("ClientInfoFactory");
	private static IClientInfo sMock;

	public static void setMock(IClientInfo mock) {
		sMock = mock;
	}

	public static IClientInfo getInstance() {
		if (sMock != null) {
			return sMock;
		} else {
			return loadClientInfo();
		}
	}

	private static IClientInfo loadClientInfo() {
		IClientInfo clientInfo = null;
		Class<?> clz = null;
		try {
			clz = Class
					.forName("com.moinapp.wuliao.commons.info.CustomClientInfo");
			if (clz != null) {
				clientInfo = (IClientInfo) clz.newInstance();
			}
		} catch (ClassNotFoundException e) {
			MyLog.i("CustomClientInfo is not existing");
		} catch (Exception e) {
			MyLog.e(Log.getStackTraceString(e));
		}

		if (clientInfo == null) {
			clientInfo = new BaseClientInfo();
		}

		return clientInfo;
	}
}

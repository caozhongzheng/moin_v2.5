package com.moinapp.wuliao.commons.info;

import java.util.Map;


public interface IClientInfo {
	
	public int getBusinessId();

	public String getChannelId();
	
	public int getEditionId();
	
	public String getPackageName();	
	
	public boolean isGP();

	public boolean hasLocalTheme();

	public Map<String, Boolean> overrideModuleDefaults();
	
	public boolean isUseBingSearchUrl();

	public void onUpgrade(int lastVer);
}

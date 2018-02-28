package com.moinapp.wuliao.commons.moduleframework;


public interface IFeature {
	
	public int getFeatureId();

	public boolean isEnabled();

	public int getStatus();

	public String getVersionTag();
	
	public IUpdateActionHandler getHandler();

}

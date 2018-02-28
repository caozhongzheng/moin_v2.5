package com.moinapp.wuliao.commons.moduleframework;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.util.Map;


/**
 * 抽象开关Feature
 * 
 */
public abstract class AbsSwitchFeature extends AbsFeature implements
		IUpdateActionHandler {
	ILogger NqLog = LoggerFactory.getLogger("AbsSwitchFeature");
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public AbsSwitchFeature() {
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean isEnabled() {
		return getModule().canEnabled();
	}

	protected abstract IModule getModule();

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public IUpdateActionHandler getHandler() {
		return this;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	@Override
	public void enableFeature() {
		NqLog.d("enableFeature: "+getFeatureId());
		getModule().setEnabled(true);
	}

	@Override
	public void disableFeature() {
		NqLog.d("disableFeature: "+getFeatureId());
		getModule().setEnabled(false);
	}

	@Override
	public void upgradeAssets(Map<String, String> keyValues) {
	}

	@Override
	public void updatePreferences(Map<String, String> keyValues) {
	}

	@Override
	public void hasUpdate() {
	}
	
	@Override
	public void updateSearchConfig(Map<String, String> keyValues) {
	}

	@Override
	public void updateDomain(Map<String, String> keyValues) {

	}

	@Override
	public boolean supportModuleLevelAction() {
		return false;
	}

}

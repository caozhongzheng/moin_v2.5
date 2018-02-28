package com.moinapp.wuliao.modules.update;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.system.SystemFacade;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;

public abstract class AbsManager {
	protected SystemFacade getSystem(){
		return SystemFacadeFactory.getSystem();
	}
	
	public abstract void init();

	public void onDisable() {
		EventBus.getDefault().unregist(this);
	}
}

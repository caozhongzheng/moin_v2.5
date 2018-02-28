package com.moinapp.wuliao.modules.discovery.model;

import com.google.gson.annotations.SerializedName;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 关注频道大咖秀列表
 */
@SuppressWarnings("serial")
public class FollowCosplayList extends Entity implements ListEntity<CosplayInfo> {

	@SerializedName("cosplayList")
	private List<CosplayInfo> cosplayInfoList;

	@Override
	public List<CosplayInfo> getList() {
		return cosplayInfoList;
	}

	public List<CosplayInfo> getCosplayInfoList() {
		return cosplayInfoList;
	}

	public void setCosplayInfoList(List<CosplayInfo> cosplayInfoList) {
		this.cosplayInfoList = cosplayInfoList;
	}

}

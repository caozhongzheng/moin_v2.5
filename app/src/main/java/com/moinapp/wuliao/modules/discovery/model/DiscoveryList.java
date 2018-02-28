package com.moinapp.wuliao.modules.discovery.model;

import com.google.gson.annotations.SerializedName;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 发现列表
 */
@SuppressWarnings("serial")
public class DiscoveryList extends Entity implements ListEntity<CosplayInfo> {

	@SerializedName("list")
	private List<CosplayInfo> discoveryItemList;

	@Override
	public List<CosplayInfo> getList() {
		return discoveryItemList;
	}

	public List<CosplayInfo> getDiscoveryItemList() {
		return discoveryItemList;
	}

	public void setDiscoveryItemList(List<CosplayInfo> discoveryItemList) {
		this.discoveryItemList = discoveryItemList;
	}

}

package com.moinapp.wuliao.modules.discovery.model;

import com.google.gson.annotations.SerializedName;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 话题详情以及所含大咖秀列表对象(最早是给单个列表时用)
 */
@SuppressWarnings("serial")
public class TagCosplayList extends Entity implements ListEntity<CosplayInfo> {

	@SerializedName("cosplayList")
	private List<CosplayInfo> cosplayList;

	private int isIdol;

	@SerializedName("topic")
	private TagPop tagPop;

	@Override
	public List<CosplayInfo> getList() {
		return cosplayList;
	}

	public List<CosplayInfo> getCosplayList() {
		return cosplayList;
	}

	public void setCosplayList(List<CosplayInfo> cosplayInfoList) {
		this.cosplayList = cosplayInfoList;
	}

	public int getIsIdol() {
		return isIdol;
	}

	public TagPop getTagPop() {
		return tagPop;
	}

	public void setTagPop(TagPop tagPop) {
		this.tagPop = tagPop;
	}
}

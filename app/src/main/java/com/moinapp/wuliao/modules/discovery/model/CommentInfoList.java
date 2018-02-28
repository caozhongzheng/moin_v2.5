package com.moinapp.wuliao.modules.discovery.model;

import com.google.gson.annotations.SerializedName;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 */
@SuppressWarnings("serial")
public class CommentInfoList extends Entity implements ListEntity<CommentInfo> {

	public final static int CATALOG_COMMENT = 1;// 发现>图片详情>评论

	@SerializedName("list")
	private List<CommentInfo> commentInfoList;

	@Override
	public List<CommentInfo> getList() {
		return commentInfoList;
	}

	public List<CommentInfo> getCommentInfoList() {
		return commentInfoList;
	}

	public void setCommentInfoList(List<CommentInfo> commentInfoList) {
		this.commentInfoList = commentInfoList;
	}

}

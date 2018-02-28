package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/15.
 * 评论列表类, 给赞/评论列表用
 */
public class CommentList extends Entity implements ListEntity<CommentInfo> {
    private List<CommentInfo> comments;

    public List<CommentInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentInfo> comments) {
        this.comments = comments;
    }

    @Override
    public List<CommentInfo> getList() {
        return comments;
    }

}

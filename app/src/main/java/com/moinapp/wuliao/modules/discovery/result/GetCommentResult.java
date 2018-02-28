package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CommentInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class GetCommentResult extends BaseHttpResponse {
    private List<CommentInfo> comments;

    public List<CommentInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentInfo> comments) {
        this.comments = comments;
    }
}

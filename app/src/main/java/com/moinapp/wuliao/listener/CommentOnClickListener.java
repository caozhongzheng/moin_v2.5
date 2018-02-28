package com.moinapp.wuliao.listener;

/**
 * Created by moying on 15/10/14.
 * callback, 用于ui层和业务逻辑处理层Manager交互
 */
public interface CommentOnClickListener {
    public void onDeleteClick(Object object);//删除
    public void onReplyClick(Object object);//回复
    public void onCopyClick(Object object);//复制
}

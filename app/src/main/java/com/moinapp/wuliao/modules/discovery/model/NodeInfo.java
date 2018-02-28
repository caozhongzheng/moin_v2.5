package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;

/**
 * 大咖秀图片在转改路径钟的一个节点
 * Created by liujiancheng on 15/10/9.
 */
public class NodeInfo extends Entity {

    /**
     * 图片对象
     */
    private BaseImage picture;

    /**
     * 作者信息
     */
    private UserInfo author;

    /**
     *当前用户是否点赞 0 否 1 是
     */
    private int isLike;

    /**
     *当前图片被点赞个数
     */
    private int likeNum;

    /**
     * 前作作者信息，userInfo对象
     */
    private UserInfo parentAuthor;

    /**
     * 节点对应的图片ID
     */
    private String id;

    /**
     * 创建时间
     */
    private long createdAt;

    /**
     * 前作创建时间
     */
    private long parentCreatedAt;

    /**
     * 大咖秀图片的状态 1:正常 0:已经删除
     */
    private int status;

    /**
     * 大咖秀图片的说明
     */
    private String content;

    /**
     *当前图片被浏览个数
     */
    private int readNum;

    /**
     *当前图片评论个数
     */
    private int commentNum;

    /**
     *当前图片转改个数
     */
    private int childrenNum;

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public UserInfo getParentAuthor() {
        return parentAuthor;
    }

    public void setParentAuthor(UserInfo parentAuthor) {
        this.parentAuthor = parentAuthor;
    }

    public String getUcid() {
        return id;
    }

    public void setUcid(String ucid) {
        this.id = ucid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long updatedAt) {
        this.createdAt = updatedAt;
    }

    public long getParentCreatedAt() {
        return parentCreatedAt;
    }

    public void setParentCreatedAt(long parentCreatedAt) {
        this.parentCreatedAt = parentCreatedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(int childrenNum) {
        this.childrenNum = childrenNum;
    }
}

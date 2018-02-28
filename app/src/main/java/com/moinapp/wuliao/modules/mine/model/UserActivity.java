package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;

/**
 * 用户的动态模型类,从消息类继承
 * Created by liujiancheng on 16/1/14.
 */
public class UserActivity extends Messages {
    //******* 以下部分为从服务器接口获取消息需要的额外字段**************************************************
	/* 展示从服务器联网获取的消息和通知栏消息机制不同, 消息的整体内容需要根据action进行拼接，
	 如101，对应的消息正文内容应该是："userName 赞了你的图片."；
	 102，对应的消息正文内容应该是："userName 评论了你的图片：content";
	 106，对应的消息正文内容应该是："userName 回复了你：content"；
	 108，对应的消息正文内容应该是："userName 回复了 targetName：content"；
	 */

    /**
     * 动态(activity)的标示
     */
    private String aid;

    /**
     * 评论或者回复的正文内容
     */
    private String content;

    /**
     * 消息点击跳转的资源标识，如关注消息对应的是关注人的ID
     */
    private String resource;

    /**
     * 消息发起者的名称
     */
    private String userName;

    /**
     * 消息接收者的名称
     */
    private String targetName;

    /**
     * 用户发表的cosplay
     */
    private CosplayInfo cosplay;

    /**
     * 消息时间戳
     */
    private long createdAt;

    /**
     * action内容是202的时候，下发一个UserInfo对象，内容为用户的基本信息，包括{id, username, avatar, relation}
     */
    private UserInfo follow;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getActivityId() {
        return aid;
    }

    public void setActivitysId(String id) {
        aid = id;
    }

    public CosplayInfo getCosplay() {
        return cosplay;
    }

    public void setCosplay(CosplayInfo cosplay) {
        this.cosplay = cosplay;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public UserInfo getFollow() {
        return follow;
    }

    public void setFollow(UserInfo follow) {
        this.follow = follow;
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                ", title='" + getTitle() + '\'' +
                ", type=" + getType() +
                ", action=" + getAction() +
                ", id='" + getResId() + '\'' +
                ", content='" + content + '\'' +
                ", resource='" + resource + '\'' +
                ", userName='" + userName + '\'' +
                ", targetName='" + targetName + '\'' +
                ", cosplay=" + cosplay +
                '}';
    }
}

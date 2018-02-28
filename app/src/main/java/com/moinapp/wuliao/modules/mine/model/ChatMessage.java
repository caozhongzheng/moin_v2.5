package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;

/**
 * 聊天的消息类
 * Created by liujiancheng on 16/4/13.
 */
public class ChatMessage extends Entity {
    /**
     * 聊天对象
     */
    private UserInfo chatUser;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息类型://1发送 2接收
     */
    private int type;

    /**
     * 消息发送/接收的本地时间戳
     */
    private long localTime;

    /**
     * 消息接收/发送的服务器时间戳
     */
    private long serverTime;

    /**
     * 聊天的内容类型 1文本 2图片 3预置图片
     */
    private int contentType;

    /**
     * 聊天的内容
     */
    private String content;

     /**
     * 消息的发送状态,仅发送消息用到 -1失败 -2图片上传失败 0发送中 1成功
     */
    int sendStatus;

    public static class SendStatus {
        public static final int NORMAL = 1;
        public static final int SENDING = 0;
        public static final int ERROR = -1;
        public static final int IMG_ERROR = -2;
    }

    /**
     * 消息的已读状态, 聊天内容是否已读 0未读 1已读
     */
    private int readStatus;

    /**
     * 当前用户的uid
     */
    private String loginUid;

    //是否显示时间
    private boolean showDate;

    public UserInfo getChatUser() {
        return chatUser;
    }

    public void setChatUser(UserInfo chatUser) {
        this.chatUser = chatUser;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLocalTime() {
        return localTime;
    }

    public void setLocalTime(long localTime) {
        this.localTime = localTime;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public String getLoginUid() {
        return loginUid;
    }

    public void setLoginUid(String loginUid) {
        this.loginUid = loginUid;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +

                "chatUser=" + chatUser.getUId() +
                ",name=" + chatUser.getUsername() +
                ",alias=" + chatUser.getAlias() +

                ", messageId='" + messageId + '\'' +
                ", type=" + type +
                ", localTime=" + localTime +
                ", serverTime=" + serverTime +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                ", sendStatus=" + sendStatus +
                ", readStatus=" + readStatus +
                ", loginUid='" + loginUid + '\'' +
                ", showDate=" + showDate +
                '}';
    }
}

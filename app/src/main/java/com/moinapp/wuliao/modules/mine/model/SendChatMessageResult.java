package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 发送聊天消息后的返回结果
 */
public class SendChatMessageResult extends BaseHttpResponse {
    /**
     * 消息发送成功后的id
     */
    private String mid;

    /**
     * 发送消息的服务器时间
     */
    private long createdAt;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

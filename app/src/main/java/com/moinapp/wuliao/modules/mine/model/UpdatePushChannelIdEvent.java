package com.moinapp.wuliao.modules.mine.model;

/**
 * 绑定百度消息推送事件
 * Created by liujiancheng on 15/11/17.
 */
public class UpdatePushChannelIdEvent {
    private String mChannelId;

    public UpdatePushChannelIdEvent(String channel_id) {
        mChannelId = channel_id;
    }

    public String getChannelId() {
        return mChannelId;
    }
}

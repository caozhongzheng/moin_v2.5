package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.Messages;

import java.util.List;

/**
 * 系统消息的返回结果类
 */
public class GetSysMessageResult extends BaseHttpResponse {
    private List<Messages> messageList;

    public List<Messages> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Messages> messageList) {
        this.messageList = messageList;
    }
}

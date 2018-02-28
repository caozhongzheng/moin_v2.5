package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;

import java.util.List;

/**
 * 发现频道大咖秀图片的相关消息
 * Created by liujiancheng on 16/2/25.
 */
public class CosplayMsg extends Entity {
    /**
     * 类型
     */
    private int action;

    /**
     * 主语对应的用户userInfo对象列表，如 丁丁、小小鹿等赞了这张图中的丁丁、小小鹿
     */
    private List<UserInfo> fromUsers;

    /**
     * 宾语对应的用户userInfo对象列表，如 Mandagie 最近关注了阿仙、安哥中的阿仙、安哥
     */
    private List<UserInfo> targetUsers;

    /**
     * 宾语对应的标签tagInfo对象列表，如来自#美人鱼# #双鱼座#中的美人鱼、双鱼座
     */
    private List<TagInfo> targetTags;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public List<UserInfo> getFromUsers() {
        return fromUsers;
    }

    public void setFromUsers(List<UserInfo> fromUsers) {
        this.fromUsers = fromUsers;
    }

    public List<UserInfo> getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(List<UserInfo> targetUsers) {
        this.targetUsers = targetUsers;
    }

    public List<TagInfo> getTargetTags() {
        return targetTags;
    }

    public void setTargetTags(List<TagInfo> targetTags) {
        this.targetTags = targetTags;
    }
}

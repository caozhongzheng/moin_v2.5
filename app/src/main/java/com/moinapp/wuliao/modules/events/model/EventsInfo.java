package com.moinapp.wuliao.modules.events.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;

/**
 * 活动信息
 * Created by liujiancheng on 16/6/8.
 */
public class EventsInfo extends Entity {
    /**
     * 活动id
     */
    private String id;

    /**
     * 活动icon
     */
    private BaseImage icon;

    /**
     * 活动的详细描述信息
     */
    private String desc;

    /**
     * 活动的链接url
     */
    private String link;

    public String getEventId() {
        return id;
    }

    public void setEventId(String id) {
        this.id = id;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

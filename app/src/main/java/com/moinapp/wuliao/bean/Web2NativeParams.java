package com.moinapp.wuliao.bean;

import java.util.Map;

/**
 * Created by liujiancheng on 16/7/21.
 * web跳转native的协议参数
 */
public class Web2NativeParams {
    /**
     * 跳转的资源类型 比如tag topic cosplay sticker等等
     */
    private String resource;

    /**
     * 对相关resource的action,比如浏览view
     */
    private String action;

    /**
     * 参数,比如tpid话题id等
     */
    private Map params;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }
}

package com.moinapp.wuliao.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("serial")
public abstract class Entity extends Base {

    @SerializedName("baseid")
    protected int id;

    protected String cacheKey;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getCacheKey() {
	return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
	this.cacheKey = cacheKey;
    }

}

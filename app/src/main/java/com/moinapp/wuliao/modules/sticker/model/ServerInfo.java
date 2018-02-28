package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;

/**
 * 服务器对象
 */
public class ServerInfo extends Entity {
    /**
     * 域名，如oss-cn-shenzhen-internal.aliyuncs.com，oss-cn-hongkong-internal.aliyuncs.com
     */
    private String domain;

    /**
     * OSS 存储的容器名称，如 moon-shenzhen, moon-shanghai
     */
    private String bucket;

    /**
     * 平均响应时间 ms
     */
    private long response_time;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public long getResponse_time() {
        return response_time;
    }

    public void setResponse_time(long response_time) {
        this.response_time = response_time;
    }
}

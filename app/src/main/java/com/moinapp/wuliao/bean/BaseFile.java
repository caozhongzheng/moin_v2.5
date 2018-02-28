package com.moinapp.wuliao.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/8/11.
 */
public class BaseFile implements Serializable {
    @SerializedName("uri")
    private String url; //下载地址
    private long size; //大小
    private String md5; //文件包的md5

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "BaseFile{" +
                "url='" + url + '\'' +
                ", size=" + size +
                ", md5='" + md5 + '\'' +
                '}';
    }
}


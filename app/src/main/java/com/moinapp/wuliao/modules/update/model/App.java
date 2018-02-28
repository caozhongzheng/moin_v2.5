package com.moinapp.wuliao.modules.update.model;

import com.moinapp.wuliao.bean.BaseFile;

import java.io.Serializable;

/**
 * 版本更新中服务器下发的app对象
 * Created by liujiancheng on 15/8/11.
 */
public class App implements Serializable {
    private String versionName; //版本名称字符串
    private String versionCode; //版本号整数
    private String channel; //渠道名称
    private BaseFile apkFile; //更新的程序包对象apkInfo
    private String title; //客户端展示标题
    private String desc; //客户端展示描述内容

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BaseFile getApkFile() {
        return apkFile;
    }

    public void setApkFile(BaseFile apkFile) {
        this.apkFile = apkFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 用户上传头像的结果类，包含上传成功后服务器返回的图片url
 * Created by liujiancheng on 15/9/7.
 */
public class UploadAvatarResult extends BaseHttpResponse {
    /**
     * 上传完成的图片在服务器的地址
     */
    private String url;

    /**
     * 生成的图片id
     */
    private String avatar;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

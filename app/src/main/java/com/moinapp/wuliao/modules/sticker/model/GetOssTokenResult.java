package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 文件上传所用token的结果[从本地服务器获取和OSS连接的验证TOKEN]
 */
public class GetOssTokenResult extends BaseHttpResponse {
    private OssToken token;

    public OssToken getOssToken() {
        return token;
    }

    public void setOssToken(OssToken token) {
        this.token = token;
    }
}

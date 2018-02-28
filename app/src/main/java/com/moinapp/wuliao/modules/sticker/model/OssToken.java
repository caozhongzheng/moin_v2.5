package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.Entity;

/**
 * Oss token对象
 */
public class OssToken extends Entity {
    /**
     * key
     */
    private String AccessKeyId;

    /**
     * secret
     */
    private String AccessKeySecret;

    /**
     * Expiration
     */
    private String Expiration;

    /**
     * SecurityToken
     */
    private String SecurityToken;

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String expiration) {
        Expiration = expiration;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }
}

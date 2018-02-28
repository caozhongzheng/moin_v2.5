package com.moinapp.wuliao.bean;

/**
 * Created by liujiancheng on 15/12/7.
 */
public class ErrorCode {
    /**
     * 参数错误
     */
    public static final int ERROR_WRONG_ARGS = -1;

    /**
     * 请求的资源ID不存在
     */
    public static final int ERROR_NO_RES_ID = -2;

    /**
     * 系统错误
     */
    public static final int ERROR_SYSTEM = -100;

    /**
     * 用户名非法
     */
    public static final int ERROR_INVALID_USERNAME = -10;

    /**
     * 手机号未注册
     */
    public static final int ERROR_PHONE_NOT_REGISTER = -11;

    /**
     * 用户被禁用
     */
    public static final int ERROR_USER_FORBIDDEN = -12;

    /**
     * 验证码错误
     */
    public static final int ERROR_SMS_CODE = -13;

    /**
     * 用户版本过低无法接收聊天消息
     */
    public static final int ERROR_LOW_VERSION = -14;

    /**
     * 大咖秀图像被删除
     */
    public static final int ERROR_COSPLAY_DELETED = -20;

    /**
     * 图像评论不存在
     */
    public static final int ERROR_COMMENTS_NOT_EXIST = -21;

    /**
     * 贴纸包被下架或删除
     */
    public static final int ERROR_INVALID_STICKER_PAC = -30;

    /**
     * 贴纸被下架或删除
     */
    public static final int ERROR_INVALID_STICKER = -40;

    /**
     * 图片还未上传成功
     */
    public static final int ERROR_COSPLAY_NOT_UPLOAD = -22;
}

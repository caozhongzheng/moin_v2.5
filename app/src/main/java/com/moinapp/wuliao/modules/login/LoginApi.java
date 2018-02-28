package com.moinapp.wuliao.modules.login;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.util.TDevice;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 用户登陆部分请求http接口的方法
 * Created by liujiancheng on 15/9/2.
 */
public class LoginApi {
    /**
     * moin自有用户的登陆, 获取返回结果需要在AsyncHttpResponseHandler里面使用BaseLoginResult类
     * @param username
     * @param md5：密码的md5值
     * @param smsCode：验证码,当输错密码三次后需要
     * @param handler
     */
    public static void login(String username, String md5, String smsCode,
                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.USERNAME, username);
        params.put(LoginConstants.PASSWORD, md5);
        params.put(LoginConstants.CODE, smsCode);
        ApiHttpClient.post(LoginConstants.LOGIN_URL, params, handler);
    }

    /**
     * 第三方用户的登陆，获取返回结果需要在AsyncHttpResponseHandler里面使用BaseLoginResult类
     * @param platform 第三方登录的平台
     * @param token：第三方登录的token
     * @param handler
     */
    public static void login(String platform, Object token,
                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.TOKEN, LoginUtils.getThirdLoginJson(platform, (Bundle) token));
        ApiHttpClient.post(LoginConstants.LOGIN_URL, params, handler);
    }

    /**
     * 注册用户的接口方法，获取返回结果需要在AsyncHttpResponseHandler里面使用RegisterResult类
     * @param username: 用户名,必填
     * @param phone: 电话
     * @param md5: 密码的md5
     * @param smscode: 短信码
     * @param sex: 性别
     * @param avatar: 头像文件id
     */
    public static void registerUser(String username, String phone, String md5, String smscode,
                                    String sex, String avatar, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.USERNAME, username);
        params.put(LoginConstants.PHONE, phone);
        params.put(LoginConstants.PASSWORD, md5);
        params.put(LoginConstants.CODE, smscode);
        params.put(LoginConstants.SEX, sex);
        params.put(LoginConstants.AVATAR, avatar);
        params.put(LoginConstants.DEVICEINFO, LoginUtils.getDeviceInfo());
        params.put(LoginConstants.APPINFO, LoginUtils.getAppInfo());
        ApiHttpClient.post(LoginConstants.LOGIN_REGISTER_URL, params, handler);
    }

    /**
     *  注册手机唯一性检查，获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param phone 手机号
     * @param handler
     */
    public static void checkPhone(String phone, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.PHONE, phone);
        ApiHttpClient.post(LoginConstants.LOGIN_CHECK_PHONE_URL, params, handler);
    }

    /**
     *  获取手机验证码,获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param phone 手机号
     * @param handler
     */
    public static void getPhoneSms(String phone, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.PHONE, phone);
        ApiHttpClient.post(LoginConstants.LOGIN_GET_SMSCODE_URL, params, handler);
    }

    /**
     *  通过手机号找回密码,获取返回结果需要在AsyncHttpResponseHandler里面使用RetrievePasswordResult类
     * @param phone 手机号
     * @param smscode 手机验证码
     * @param handler
     */
    public static void retrievePasswordByPhone(String phone,  String smscode,
                                               AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.PHONE, phone);
        params.put(LoginConstants.SMSCODE, smscode);
        ApiHttpClient.post(LoginConstants.LOGIN_RETRIEVE_PASSWORD_URL, params, handler);
    }

    /**
     *  退出登陆, 获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param handler
     */
    public static void userLogout(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(LoginConstants.LOGOUT_URL, null, handler);
    }

    /**
     *  用户名唯一性检查,获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param username 用户名
     * @param handler
     */
    public static void checkUserName(String username, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.USERNAME, username);
        ApiHttpClient.post(LoginConstants.LOGIN_CHECK_USERNAME_URL, params, handler);
    }

    /**
     *  得到用户登陆类型,获取返回结果需要在AsyncHttpResponseHandler里面使用GetUserTypeResult类
     * @param handler
     * 返回：type: 1 常规用户 2 第三方用户；
     *   namelist：第三方账户的名称数组集合，如[qq, weichat, sina] 表示同时绑定了QQ、微信和微博三个第三方平台。
     */
    public static void getUserLoginType(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(LoginConstants.GET_LOGIN_TYPE, null, handler);
    }

    /**
     * 用户反馈信息,获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param contact: 联系信息
     * @param content: 反馈正文
     * @param handler
     */
    public static void userFeedback(String contact, String content,
                               AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.CONTACT, contact);
        params.put(LoginConstants.CONTENT, content);
        params.put(LoginConstants.LOGTIME, String.valueOf(System.currentTimeMillis()));
        params.put(LoginConstants.DEVICEINFO, LoginUtils.getDeviceInfo());
        params.put(LoginConstants.APPINFO, LoginUtils.getAppInfo());
        ApiHttpClient.post(LoginConstants.USER_FEEDBACK, params, handler);
    }

    /**
     * 获取热门用户，获取返回结果需要在AsyncHttpResponseHandler里面使用GetHotUserResponse类
     * @param uid: 用户uid
     * @param sex: 用户性别
     * @param handler
     */
    public static void getHotUser(String uid, String sex, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.USER_ID, uid);
        params.put(LoginConstants.SEX, sex);
        ApiHttpClient.post(LoginConstants.GET_HOT_USER_URL, params, handler);
    }

    /**
     * 验证手机号和验证码
     * @param phone: 电话号码
     * @param code: 短信验证码
     * @param handler: callback
     */
    public static void verifyPhone(String phone, String code, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(LoginConstants.PHONE, phone);
        params.put(LoginConstants.CODE, code);
        ApiHttpClient.post(LoginConstants.VERIFY_PHONE_URL, params, handler);
    }

}

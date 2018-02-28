package com.moinapp.wuliao.modules.mine;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.modules.login.LoginUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 我的模块请求http接口的方法
 * Created by liujiancheng on 15/9/2.
 */
public class MineApi {
    /**
     *  用户上传图像,获取返回结果需要在AsyncHttpResponseHandler里面使用UploadAvatarResult类
     * @param filePath 用户头像文件的path
     * @param handler
     */
    public static void uploadAvatar(String filePath, AsyncHttpResponseHandler handler) {
        File avatar = new File(filePath);
        if (avatar == null || !avatar.exists()) {
            return;
        }

        RequestParams params = new RequestParams();
        try {
            params.put(MineConstants.AVATAR, avatar);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ApiHttpClient.post(MineConstants.LOGIN_UPLOAD_URL, params, handler);
    }

    /**
     *  更新用户头像
     * @param avatar 用户头像
     * @param handler
     */
    public static void updateUserAvatar(String avatar, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.AVATAR,avatar);
        ApiHttpClient.post(MineConstants.LOGIN_UPDATE_URL, params, handler);
    }

    /**
     *  更新用户信息,获取返回结果需要在AsyncHttpResponseHandler里面使用UpdateUserInfoResult类
     * @param info 用户信息对象
     * @param handler
     */
    public static void updateUserInfo(UserInfo info, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(info.getUsername())) {
            params.put(MineConstants.USERNAME, info.getUsername());
        }
        if (!TextUtils.isEmpty(info.getPhone())) {
            params.put(MineConstants.PHONE, info.getPhone());
        }

        if (!TextUtils.isEmpty(info.getEmail())) {
            params.put(MineConstants.EMAIL, info.getEmail());
        }

        if (!TextUtils.isEmpty(info.getNickname())) {
            params.put(MineConstants.NICKNAME, info.getNickname());
        }

        if (!TextUtils.isEmpty(info.getContact())) {
            params.put(MineConstants.CONTACT, info.getContact());
        }

        if (!TextUtils.isEmpty(info.getAges())) {
            params.put(MineConstants.AGES, info.getAges());
        }

        if (!TextUtils.isEmpty(info.getSex())) {
            params.put(MineConstants.SEX, info.getSex());
        }

        if (info.getLocation() != null) {
            params.put(MineConstants.LOCATION, (new Gson()).toJson(info.getLocation()));
        }

        if (info.getSignature() != null) {
            params.put(MineConstants.SIGNATURE, info.getSignature());
        }

        if (info.getStars() != -1) {
            params.put(MineConstants.STARS, String.valueOf(info.getStars()));
        }

        if (info.getBirthday() != 0) {
            params.put(MineConstants.BIRTHDAY, String.valueOf(info.getBirthday()));
        }

        params.put(MineConstants.DEVICEINFO, LoginUtils.getDeviceInfo());
        params.put(MineConstants.APPINFO, LoginUtils.getAppInfo());
        ApiHttpClient.post(MineConstants.LOGIN_UPDATE_URL, params, handler);
    }

    /**
     *  更新密码,获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param md5 新密码的md5
     * @param handler
     */
    public static void updatePassword(String md5, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.PASSWORD, md5);
        ApiHttpClient.post(MineConstants.LOGIN_UPDATE_PASSWORD_URL, params, handler);
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
        params.put(MineConstants.CONTACT, contact);
        params.put(MineConstants.CONTENT, content);
        params.put(MineConstants.LOGTIME, String.valueOf(System.currentTimeMillis()));
        params.put(MineConstants.DEVICEINFO, LoginUtils.getDeviceInfo());
        params.put(MineConstants.APPINFO, LoginUtils.getAppInfo());
        ApiHttpClient.post(MineConstants.USER_FEEDBACK, params, handler);
    }

    /**
     * 获取用户基本信息,获取返回结果需要在AsyncHttpResponseHandler里面使用GetUserInfoResult类
     * @param userid: 需要获取的用户信息，如果为空则为当前登录用户的信息；
     * @param handler
     */
    public static void getUserInfo(String userid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, userid);
        ApiHttpClient.post(MineConstants.GET_USER_INFO, params, handler);
    }

    /**
     * 获取我关注的用户列表,获取返回结果需要在AsyncHttpResponseHandler里面使用GetMyIdolsResult类
     * @param keyword: 关键字,搜索用
     * @param lastid: 最后一个用户的id信息，用于分页；
     * @param handler
     */
    public static void getMyIdols(String userid,String keyword, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, userid);
        params.put(MineConstants.LASTID, lastid);
        params.put(MineConstants.KEYWORD, keyword);
        ApiHttpClient.post(MineConstants.GET_MY_IDOS, params, handler);
    }

    /**
     * 获取我的粉丝用户列表,获取返回结果需要在AsyncHttpResponseHandler里面使用GetMyFansResult类
     * @param keyword: 关键字,搜索用
     * @param lastid: 最后一个用户的id信息，用于分页；
     * @param handler
     */
    public static void getMyFans(String userid, String keyword, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, userid);
        params.put(MineConstants.LASTID, lastid);
        params.put(MineConstants.KEYWORD, keyword);
        ApiHttpClient.post(MineConstants.GET_MY_FANS, params, handler);
    }

    /**
     * 关注／取消关注用户，获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param userid: 关注/取消关注的用户id信息；
     * @param action: 1 关注 0 取消关注
     * @param handler
     */
    public static void followUser(String userid, int action, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, userid);
        params.put(MineConstants.ACTION, action);
        ApiHttpClient.post(MineConstants.USER_FOLLOW, params, handler);
    }

    /**
     * 获取用户制作的大咖秀列表
     * @param uid: 需要查看的用户ID，如果为空则查看当前登录的用户ID；
     * @param lastid: 最后一个大咖秀的ID，用于分页
     * @param handler
     */
    public static void getUserCosplay(String uid, String lastid,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.LASTID, lastid);
        params.put(MineConstants.USERID, uid);
        ApiHttpClient.post(MineConstants.GET_USER_COSPLAY, params, handler);
    }

    /**
     * 查询用户关系
     * @param uid: 需要查询的用户ID，必填项；
     * @param handler: callback
     */
    public static void getRelation(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, uid);
        ApiHttpClient.post(MineConstants.GET_USER_RELATION_URL, params, handler);
    }

    /**
     * 搜索用户
     * @param keyword: 搜索的关键词，必填项
     * @param lastid: 选填项
     * @param handler: callback
     */
    public static void searchUser(String keyword, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.KEYWORD, keyword);
        params.put(MineConstants.LASTID, lastid);
        ApiHttpClient.post(MineConstants.SEARCH_USER_URL, params, handler);
    }

    /**
     * 获取我的消息,注意这个是由服务器接口获取的,与通知栏消息不一样
     * @param action: 消息类型，必填。其中：0 全部，202 关注，101 赞、102 评论（包括105 @、106 回复评论、
     *              107 评论赞过的图、108 回复你发的图中的评论）、103 转发（包括104 转改）
     * @param lastid: 消息ID用于分页，选填
     * @param handler: callback
     */
    public static void getMyMessages(int action, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.ACTION, action);
        params.put(MineConstants.LASTID, lastid);
        ApiHttpClient.post(MineConstants.GET_MY_MESSAGE_URL, params, handler);
    }

    /**
     * 获取我的动态
     * @param type: 类别 1 列表格式 2 九宫格，必填
     * @param action: 消息类型，必填。其中：0 全部，202 关注，101 赞、102 评论（包括105 @、106 回复评论、
     *              107 评论赞过的图、108 回复你发的图中的评论）、103 转发（包括104 转改）
     * @param lastid: 消息ID用于分页，选填
     * @param userid 需要获取的用户标识，如果为空则为当前用户，选填
     * @param handler: callback
     */
    public static void getMyActivity(String userid, int type, int action, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, userid);
        params.put(MineConstants.TYPE, type);
        params.put(MineConstants.ACTION, action);
        params.put(MineConstants.LASTID, lastid);
        ApiHttpClient.post(MineConstants.GET_USER_ACTIVITY_URL, params, handler);
    }

    /**
     * 获取系统消息
     * @param updatedAt: 最后一个下发的资源更新时间，用于分页，可选
     * @param handler: callback
     */
    public static void getSysMessages(long updatedAt, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.UPDATEAT, updatedAt);
        ApiHttpClient.post(MineConstants.GET_SYS_MESSAGE_URL, params, handler);
    }

    /**
     * 修改备注
     * @param userid: 用户id信息；
     * @param alias: 备注名称
     * @param handler: callback
     */
    public static void modifyAlias(String userid, String alias, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.USERID, userid);
        params.put(MineConstants.ALIAS, alias);
        ApiHttpClient.post(MineConstants.MODIFY_ALIAS, params, handler);
    }

    /**
     * 发送第三方邀请
     * @param name: 第三方平台名称，默认sina
     * @param target: 被邀请用户第三方平台用户id
     * @param token: token对象 例如：
     * @param handler: callback
     */
    public static void sendInvite(String name,String target, String token, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.NAME, name);
        params.put(MineConstants.TARGET, target);
        params.put(MineConstants.TOKEN, token);
        ApiHttpClient.post(MineConstants.SEND_INVITE, params, handler);
    }

    /**
     * 获取好友列表(微博和电话本)
     * @param phoneList ；以逗号分隔的手机号码字符串，例如：phone,phone,phone
     * @param token: token对象
     * @param handler: callback
     */
    public static void getThirdFriends(String phoneList, String token, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.PHONE_LIST, phoneList);
        params.put(MineConstants.TOKEN, token);
        ApiHttpClient.post(MineConstants.GET_THIRD_FRIENDS, params, handler);
    }

    /**
     * 获取好友列表(微博和电话本)
     * @param target ；接收消息用户id列表，形如：xxxxx,xxxx,xxx
     * @param content: 消息内容
     * @param picture: 消息图片地址
     * @param timestamp: 本地消息时间戳
     * @param handler: callback
     */
    public static void sendChatMessage(String target, String content, String picture, long timestamp,
                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.TARGETS, target);
        params.put(MineConstants.CONTENT, content);
        params.put(MineConstants.PICTURE, picture);
        params.put(MineConstants.TIMESTAMP, timestamp);
        ApiHttpClient.post(MineConstants.SEND_CHAT_MESSAGE, params, handler);
    }

    /**
     * 举报接口
     * @param uid: 被举报用户的uid
     * @param ucid: 被举报图片的ucid*
     * @param content: 消息内容
     * @param handler: callback
     */
    public static void report(String uid, String ucid, String content, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.UID, uid);
        params.put(MineConstants.UCID, ucid);
        params.put(MineConstants.CONTENT, content);
        ApiHttpClient.post(MineConstants.REPORT, params, handler);
    }

    /**
     * 分享接口
     * @param type: 类型，1：cosplay，2: activity
     * @param id: 分享对象的标识ID
     * @param handler: callback
     */
    public static void share(int type, String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.TYPE, type);
        params.put(MineConstants.ID, id);
        ApiHttpClient.post(MineConstants.SHARE, params, handler);
    }

    /**
     * 获取魔豆信息接口
     * @param id: 用户id
     * @param handler: callback
     */
    public static void getMoinBean(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.ID, id);
        ApiHttpClient.post(MineConstants.GET_MOIN_BEAN, params, handler);
    }

    /**
     * 用户更新水印和通知开关信息的接口
     * @param push: 0 关闭通知 1 开启通知的选项(声音+震动/震动/关闭通知)
     * @param push: mark: 0 关闭水印 1 开启水印
     * @param handler: callback
     */
    public static void updateSettings(int push, int mark, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(MineConstants.PUSH, push);
        params.put(MineConstants.MARK, mark);
        ApiHttpClient.post(MineConstants.UPDATE_SETTINGS_URL, params, handler);
    }
}

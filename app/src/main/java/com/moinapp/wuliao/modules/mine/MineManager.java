package com.moinapp.wuliao.modules.mine;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.keyboard.db.DBHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.AppManager;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.SimpleBackPage;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.db.DataProvider;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.init.InitManager;
import com.moinapp.wuliao.commons.init.InitPreference;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mine.model.ChatMessage;
import com.moinapp.wuliao.modules.mine.model.FollowResultList;
import com.moinapp.wuliao.modules.mine.model.GetMoinBeanResult;
import com.moinapp.wuliao.modules.mine.model.GetMyFansResult;
import com.moinapp.wuliao.modules.mine.model.GetMyIdolsResult;
import com.moinapp.wuliao.modules.mine.model.GetSysMessageResult;
import com.moinapp.wuliao.modules.mine.model.GetThirdFriendsResult;
import com.moinapp.wuliao.modules.mine.model.GetUserActivityResult;
import com.moinapp.wuliao.modules.mine.model.GetUserInfoResult;
import com.moinapp.wuliao.modules.mine.model.RelationResult;
import com.moinapp.wuliao.modules.mine.model.SearchUserResult;
import com.moinapp.wuliao.modules.mine.model.SendChatMessageResult;
import com.moinapp.wuliao.modules.mine.model.ShareResult;
import com.moinapp.wuliao.modules.mine.model.UpdatePushChannelIdEvent;
import com.moinapp.wuliao.modules.mine.model.UploadAvatarResult;
import com.moinapp.wuliao.modules.mine.tables.ChatTable;
import com.moinapp.wuliao.modules.mine.tables.PushMessageTable;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.OssToken;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.ui.StickerDetailActivity;
import com.moinapp.wuliao.modules.update.AbsManager;
import com.moinapp.wuliao.ui.DetailActivity;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.SimpleBackActivity;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by liujiancheng on 15/10/24.
 */
public class MineManager extends AbsManager {
    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger("MineManager");
    private static MineManager mInstance;
    private Gson mGson = new Gson();

    // ===========================================================
    // Constructors
    // ===========================================================
    private MineManager() {
        EventBus.getDefault().register(this);
    }

    public static synchronized MineManager getInstance() {
        if (mInstance == null) {
            mInstance = new MineManager();
        }

        return mInstance;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void init() {
    }

    /**
     *  用户上传图像
     * @param filePath 用户头像文件的path
     * @param listener: callback
     */
    public void uploadAvatar(String filePath, IListener listener) {
        MineApi.uploadAvatar(filePath, buildUploadAvatarCallback(listener));
    }

    /**
     *  更新用户信息
     * @param info 用户信息对象
     * @param listener: callback
     */
    public void updateUserInfo(UserInfo info, IListener listener) {
        MineApi.updateUserInfo(info, buildGetUserInfoCallback(listener));
    }

    /**
     *  更新用户头像
     * @param avatar 用户头像文件的id
     * @param listener: callback
     */
    public void updateUserAvatar(String avatar, IListener listener) {
        MineApi.updateUserAvatar(avatar, buildGetUserInfoCallback(listener));
    }

    /**
     * 获取用户基本信息
     * @param userid: 需要获取的用户信息，如果为空则为当前登录用户的信息；
     * @param listener: callback
     */
    public void getUserInfo(String userid, IListener listener) {
        MineApi.getUserInfo(userid, buildGetUserInfoCallback(listener));
    }

    /**
     * 更新密码
     * @param md5 新密码的md5
     * @param listener: callback
     */
    public void updatePassword(String md5, IListener listener) {
        MineApi.updatePassword(md5, buildCommonCallback(listener));
    }

    /**
     * 获取我关注的用户列表
     * @param userid: 用户id；
     * @param keyword: 关键字,搜索用
     * @param lastid: 最后一个用户的id信息，用于分页；
     * @param listener: callback
     */
    public void getMyIdols(String userid, String keyword, String lastid, IListener listener) {
        MineApi.getMyIdols(userid, keyword, lastid, buildGetMyIdolCallback(listener));
    }

    /**
     * 获取我的粉丝用户列表
     * @param userid: 用户id；
     * @param keyword: 关键字,搜索用
     * @param lastid: 最后一个用户的id信息，用于分页；
     * @param listener: callback
     */
    public void getMyFans(String userid,String keyword,String lastid, IListener listener) {
        MineApi.getMyFans(userid, keyword, lastid, buildGetMyFansCallback(listener));
    }

    /**
     * 获取用户制作的大咖秀列表
     * @param uid: 需要查看的用户ID，如果为空则查看当前登录的用户ID；
     * @param lastid: 最后一个大咖秀的ID，用于分页
     * @param listener: callback
     */
    public void getUserCosplay(String uid, String lastid, IListener listener) {
        MineApi.getUserCosplay(uid, lastid, buildCommonCallback(listener));
    }

    /**
     * 查询用户关系
     * @param uid: 需要查询的用户ID，必填项；
     * @param listener: callback
     */
    public void getRelation(String uid, IListener listener) {
        MineApi.getRelation(uid, buildRelationCallback(listener));
    }

    /**
     * 搜索人
     * @param keyword: 搜索的关键词，必填项
     * @param lastid: 选填项
     * @param listener: callback
     */
    public void searchUser(String keyword, String lastid, IListener listener) {
        MineApi.searchUser(keyword, lastid, buildSearchUserCallback(listener));
    }

    /**
     * 获取我的消息,注意这个是由服务器接口获取的,与通知栏消息不一样
     * @param action: 消息类型，必填。其中：0 全部，202 关注，101 赞、102 评论（包括105 @、106 回复评论、
     *              107 评论赞过的图、108 回复你发的图中的评论）、103 转发（包括104 转改）
     * @param lastid: 消息ID用于分页，选填
     * @param listener: callback
     */
    public void getMyMessages(int action, String lastid, IListener listener) {
        MineApi.getMyMessages(action, lastid, buildGetActivityCallback(listener));
    }

    /**
     * 关注／取消关注用户
     * @param userid: 关注/取消关注的用户id信息；
     * @param action: 1 关注 0 取消关注
     * @param listener: callback
     */
    public void followUser(String userid,int action, IListener listener) {
        MineApi.followUser(userid, action, buildFollowActionCallback(listener));
    }

    /**
     * 获取我的动态
     * @param type: 类别 1 列表格式 2 九宫格，必填
     * @param action: 消息类型，必填。其中：0 全部，202 关注，101 赞、102 评论（包括105 @、106 回复评论、
     *              107 评论赞过的图、108 回复你发的图中的评论）、103 转发（包括104 转改）
     * @param lastid: 消息ID用于分页，选填
     * @param userid 需要获取的用户标识，如果为空则为当前用户，选填
     * @param listener: callback
     */
    public void getMyActivity(String userid, int type, int action, String lastid, IListener listener) {
        MineApi.getMyActivity(userid, type, action, lastid, buildGetActivityCallback(listener));
    }

    /**
     * 获取系统消息
     * @param updatedAt: 最后一个下发的资源更新时间，用于分页，可选
     * @param listener: callback
     */
    public void getSysMessages(long updatedAt, IListener listener) {
        MineApi.getSysMessages(updatedAt, buildGetSysMessageCallback(listener));
    }

    /**
     * 修改备注
     * @param userid: 用户id信息；
     * @param alias: 备注名称
     * @param listener: callback
     */
    public void modifyAlias(String userid,String alias, IListener listener) {
        MineApi.modifyAlias(userid, alias, buildCommonCallback(listener));
    }

    /**
     * 发送第三方邀请
     * @param name: 第三方平台名称，默认sina
     * @param target: 被邀请用户第三方平台用户id
     * @param token: token对象 例如：{access_token:xxxxx,refresh_token:xxxxx,uid:xxxx,refresh_token_expires:xxxx}
     * @param listener: callback
     */
    public void sendInvite(String name,String target, String token,IListener listener) {
        MineApi.sendInvite(name, target, token, buildCommonCallback(listener));
    }

    /**
     * 获取好友列表(微博和电话本)
     * @param phoneList ；以逗号分隔的手机号码字符串，例如：phone,phone,phone
     * @param token: token对象 例如：{access_token:xxxxx,refresh_token:xxxxx,uid:xxxx,refresh_token_expires:xxxx}
     * @param listener: callback
     */
    public void getThirdFriends(String phoneList, String token,IListener listener) {
        MineApi.getThirdFriends(phoneList, token, buildGetThirdFriendsCallback(listener));
    }

    /**
     * 发送聊天消息
     * @param target ；接收消息用户id列表，形如：xxxxx,xxxx,xxx
     * @param content: 消息内容
     * @param picture: 消息图片地址
     * @param timestamp: 本地消息时间戳
     * @param listener: callback
     */
    public void sendChatMessage(String target, String content, String picture, long timestamp, IListener listener) {
        MineApi.sendChatMessage(target, content, picture, timestamp, buildSendChatCallback(listener));
    }

    /**
     * 分享接口
     * @param type: 类型，1：cosplay，2: activity
     * @param id: 分享对象的标识ID
     */
    public void share(int type, String id, IListener listener) {
        MineApi.share(type, id, buildShareCallback(listener));
    }

    /**
     * 获取魔豆信息接口
     * @param id: 用户id
     */
    public void getMoinBean(String id, IListener listener) {
        MineApi.getMoinBean(id, buildGetMoinBeanCallback(listener));
    }

    /**
     * 用户更新水印和通知开关信息的接口
     * @param push: 0 关闭通知 1 开启通知的选项(声音+震动/震动/关闭通知)
     * @param push: mark: 0 关闭水印 1 开启水印
     */
    public void updateSettings(int push, int mark, IListener listener) {
        MineApi.updateSettings(push, mark, buildCommonCallback(listener));
    }

    /**
     * 举报接口
     * @param uid: 被举报用户的uid
     * @param ucid: 被举报图片/帖子的ucid
     * @param content: 举报内容
     * @param listener: callback
     */
    public void report(String uid, String ucid, String content, IListener listener) {
        HashMap<String, String> map = new HashMap<String, String>();
        String eventID = null;
        if (!StringUtil.isNullOrEmpty(ucid)) {
            map.put(UmengConstants.ITEM_ID, ucid + "_" + content);
            if (!StringUtil.isNullOrEmpty(content)) {
                eventID = UmengConstants.REPORT_POST;
            } else {
                eventID = UmengConstants.REPORT_COSPLAY;
            }
        } else {
            eventID = UmengConstants.REPORT_USER;
            map.put(UmengConstants.ITEM_ID, uid + "_" + content);
        }
        // TODO 如果区分图片,帖子详情页的话,可以在调用接口时区分
//        map.put(UmengConstants.FROM, isUcid ? "图片,帖子详情页" : "用户动态");
        MobclickAgent.onEvent(AppContext.context(), eventID, map);
        MobclickAgent.onEvent(AppContext.context(), UmengConstants.REPORT, map);

        MineApi.report(uid, ucid, content, buildCommonCallback(listener));
    }

    private AsyncHttpResponseHandler buildRelationCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                RelationResult result = XmlUtils.JsontoBean(RelationResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getRelation());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildCommonCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener == null) {
                    return;
                }
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                BaseHttpResponse result = XmlUtils.JsontoBean(BaseHttpResponse.class, responseBody);
                if (result != null) {
                    if (result.getResult() > 0) {
                        listener.onSuccess(result.getResult());
                    } else {
                        listener.onErr(result.getError());
                    }
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure:" + statusCode);
                if (listener != null) {
                    listener.onNoNetwork();
                }
            }
        };
    }

    private AsyncHttpResponseHandler buildUploadAvatarCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("upload avatar response =" + new String(responseBody));
                UploadAvatarResult result = XmlUtils.JsontoBean(UploadAvatarResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    MyLog.i("onSucceed: url = " + result.getUrl());
                    listener.onSuccess(result);
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.onNoNetwork();
            }
        };
    }


    private AsyncHttpResponseHandler buildGetUserInfoCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("update user response =" + new String(responseBody));
                GetUserInfoResult result = XmlUtils.JsontoBean(GetUserInfoResult.class, responseBody);
                if (result != null && result.getUser() != null) {
                    if (listener != null) {
                        listener.onSuccess(result.getUser());
                    }
                    ClientInfo.setUserName(result.getUser().getUsername());
                    //保存用户信息到本地
                    AppContext.getInstance().saveUserInfo(result.getUser());
                } else {
                    if (listener != null) {
                        listener.onErr(null);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onNoNetwork();
                }
            }
        };
    }

    private AsyncHttpResponseHandler buildGetMyIdolCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GetMyIdolsResult result = XmlUtils.JsontoBean(GetMyIdolsResult.class, responseBody);
                if (result != null && result.getIdol() != null) {
                    MyLog.i("onSucceed: idol list size = " + result.getIdol().size());
                    listener.onSuccess(result.getIdol());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetMyFansCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GetMyFansResult result = XmlUtils.JsontoBean(GetMyFansResult.class, responseBody);
                if (result != null && result.getFans() != null) {
                    MyLog.i("onSucceed: fans list size = " + result.getFans().size());
                    listener.onSuccess(result.getFans());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildSearchUserCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                SearchUserResult result = XmlUtils.JsontoBean(SearchUserResult.class, responseBody);
                if (result != null) {
                    if (result.getUsers() != null) {
                        MyLog.i("search users size = " + result.getUsers().size());
                    }
                    listener.onSuccess(result.getUsers());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetActivityCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetUserActivityResult result = XmlUtils.JsontoBean(GetUserActivityResult.class, responseBody);
                if (result != null) {
                    if (result.getList() != null) {
                        MyLog.i("user activity size = " + result.getList().size());
                    }
                    listener.onSuccess(result.getList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildFollowActionCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                FollowResultList result = XmlUtils.JsontoBean(FollowResultList.class, responseBody);
                if (result != null ) {
                    listener.onSuccess(result);
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetSysMessageCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                GetSysMessageResult result = XmlUtils.JsontoBean(GetSysMessageResult.class, responseBody);
                if (result != null ) {
                    listener.onSuccess(result.getMessageList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetThirdFriendsCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                GetThirdFriendsResult result = XmlUtils.JsontoBean(GetThirdFriendsResult.class, responseBody);
                if (result != null ) {
                    listener.onSuccess(result);
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildSendChatCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                SendChatMessageResult result = XmlUtils.JsontoBean(SendChatMessageResult.class, responseBody);
                if (result != null) {
                    if (result.getResult() > 0) {
                        listener.onSuccess(result);
                    } else {
                        listener.onErr(result.getError());
                    }
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildShareCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                ShareResult result = XmlUtils.JsontoBean(ShareResult.class, responseBody);
                if (result != null) {
                    if (result.getResult() > 0) {
                        listener.onSuccess(result.getMoinBean());
                    } else {
                        listener.onErr(result.getError());
                    }
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetMoinBeanCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                GetMoinBeanResult result = XmlUtils.JsontoBean(GetMoinBeanResult.class, responseBody);
                if (result != null) {
                    if (result.getResult() > 0) {
                        listener.onSuccess(result.getMoinBean());
                    } else {
                        listener.onErr(result.getError());
                    }
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    /**
     * 把接收到的消息存入本地数据库
     * @param title: 消息的标题
     * @param content: 消息的内容
     */
    public void saveMessage2Database(String title, String content) {
        if (TextUtils.isEmpty(content)) return;

        try {
            Messages message = mGson.fromJson(content, Messages.class);
            if (message != null) {
                if (message.getAction() == Messages.ACTION_CHAT) {
                    save2ChatTable(message);
                    //post 聊天event给聊天详情界面
                    EventBus.getDefault().post(new NewChatMessageEvent(message2ChatMessage(message)));
                } else {
                    save2MessageTable(title, content, message);
                    //post消息event
                    EventBus.getDefault().post(new ReceivedMessage(title, content));
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    //test code
    public void testInsertChat() {
        for (int i = 0; i < 10; i++) {
            Messages message = new Messages();
            UserInfo user = new UserInfo();
            if (i%2 == 0) {
                user.setUId("user2");
            } else if (i != 0 && i%3 == 0) {
                user.setUId("user3");
            } else {
                user.setUId("user1");
            }
            message.setUser(user);
            message.setTitle("message chat" + i);
            message.setUpdatedAt(12000 + i);
            save2ChatTable(message);
        }
    }

    /**
     * 获取本地的聊天记录(每一个聊天对象的最新一条聊天记录组成的列表)
     */
    public List<ChatMessage> getChatRecordList() {
        List<ChatMessage> listMessages = new ArrayList<ChatMessage>();
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            String[] CALL_LOG_PROJECTION = new String[] {
                    ChatTable.CHAT_UID,
                    "MAX(" +ChatTable.CHAT_SERVER_TIME + ")" + " AS time"};
            String selection = "0==0 GROUP BY " + ChatTable.CHAT_UID;
            cursor = contentResolver.query(ChatTable.CHAT_CACHE_URI, CALL_LOG_PROJECTION, selection, null, "MAX(" +ChatTable.CHAT_SERVER_TIME + ") DESC");

            if (cursor == null ) {
                return null;
            }

            Map<String, Boolean> map = new HashMap<>();
            while (cursor.moveToNext()) {
                String uid = cursor.getString(cursor.getColumnIndex(ChatTable.CHAT_UID));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                ChatMessage message = buildChatMessage(uid, time);
                // 去重
                if (message != null && !map.containsKey(message.getMessageId())) {
                    listMessages.add(message);
                    map.put(message.getMessageId(), true);
                    MyLog.i("getChatRecordList message " + message);
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return listMessages;
    }

    /**
     * 获取和指定用户的聊天记录
     * @param uid: 指定用户的uid
     * @param lastTimeStamp: 当前最旧一条聊天记录的时间戳
     * @param count: 取的条数
     */
    public List<ChatMessage> getChatMessages(String uid, long lastTimeStamp, int count) {
        List<ChatMessage> listMessages = new ArrayList<ChatMessage>();
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            String selection = ChatTable.CHAT_UID + " =?" + " AND "
                    + ChatTable.CHAT_SERVER_TIME + " < " + lastTimeStamp;
            cursor = contentResolver.query(ChatTable.CHAT_CACHE_URI, null, selection,
                    new String[]{uid}, ChatTable.CHAT_SERVER_TIME + " DESC,"
                            + ChatTable.CHAT_LOCAL_TIME + " DESC LIMIT 0," + (count - 1));

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                ChatMessage message = cursor2ChatMessage(cursor);
                if (message != null) {
                    listMessages.add(message);
                    MyLog.i("getChatMessages message uid= " + uid + ", time=" + message.getServerTime());
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return listMessages;
    }

    /**
     * 获取和指定用户的聊天记录(单条)
     * @param uid: 指定用户的uid
     * @param msgId: 消息id
     */
    public ChatMessage getChatMessage(String uid, String msgId) {
        ChatMessage chatMessage = null;
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            String selection = ChatTable.CHAT_UID + " =?" + " AND "
                    + ChatTable.CHAT_MESSAGEID + " =? ";
            cursor = contentResolver.query(ChatTable.CHAT_CACHE_URI, null, selection,
                    new String[]{uid, msgId}, ChatTable.CHAT_SERVER_TIME + " DESC LIMIT 1");

            if (cursor == null ) {
                return null;
            }

            cursor.moveToFirst();
            chatMessage = cursor2ChatMessage(cursor);
            if (chatMessage != null) {
                MyLog.i("getChatMessage " + chatMessage);
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return chatMessage;
    }

    /**
     * 删除和指定用户的聊天记录(单条)
     * @param uid: 指定用户的uid
     * @param msgId: 消息id
     */
    public int deleteChatMessage(String uid, String msgId) {
        int count = 0;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            String selection = ChatTable.CHAT_UID + " =?" + " AND "
                    + ChatTable.CHAT_MESSAGEID + " =? ";
            count = contentResolver.delete(ChatTable.CHAT_CACHE_URI, selection,
                    new String[]{uid, msgId});

            return count;
        } catch (Exception e) {
            MyLog.e(e);
        }

        return 0;
    }

    /**
     * 查询和指定的用户是否有未读的聊天记录, uid为空的话则是查询登录用户是否有未读聊天记录
     */
    public boolean hasUnreadChat(String uid) {
        boolean result = false;
        Cursor cursor = null;
        String selection = TextUtils.isEmpty(uid) ? ChatTable.CHAT_LOGIN_UID + " =? " : ChatTable.CHAT_UID + " =? ";
        String args = TextUtils.isEmpty(uid) ? ClientInfo.getUID() : uid;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            cursor = contentResolver.query(ChatTable.CHAT_CACHE_URI, null,
                    selection + " AND " + ChatTable.CHAT_READ_FLAG + " = 0",
                    new String[]{args}, null);

            if (cursor == null ) {
                return false;
            }

            if (cursor.moveToNext()) {
                result = true;
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return result;
    }

    /**
     * 删除指定用户的聊天记录
     */
    public void deleteChatRecord(String uid) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;

            //先删除旧的
            ContentProviderOperation.Builder del = ContentProviderOperation.newDelete(ChatTable.CHAT_CACHE_URI)
                    .withSelection(ChatTable.CHAT_UID + " =? ",
                            new String[]{uid});
            ops.add(del.build());
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    /**
     * 设置用户的聊天记录已读状态
     * flag: 0未读 1已读
     */
    public void markChatRecord(String uid, int flag) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;

            ContentValues values = new ContentValues();
            values.put(ChatTable.CHAT_READ_FLAG, flag);
            b = ContentProviderOperation.newUpdate(ChatTable.CHAT_CACHE_URI).withValues(
                    values).withSelection(ChatTable.CHAT_UID + " =? ",
                    new String[]{uid});
            ops.add(b.build());
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    /**
     * 把接收到的消息存入本地数据库的聊天表
     */
    public void save2ChatTable(Messages message) {
        //从消息取到聊天对象的userinfo 缓存起来
        try {
            UserInfo userInfo = message.getUser();
            if (userInfo != null && userInfo.getUId() != null
                    && userInfo.getAvatar() != null
                    && userInfo.getUsername() != null) {
                CacheManager.saveObject(BaseApplication.context(), userInfo, userInfo.getUId());
            }
        } catch (Exception e) {
            MyLog.e(e);
        }

        // 如果数据库中已经存在messageid相同的记录,不重复插入了
        if (isMessageIdentical(message.getMessageId())) return;

        //把接收到的消息存入本地数据库
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation.Builder b = null;

        ContentValues values = new ContentValues();
        values.put(ChatTable.CHAT_UID, message.getUser().getUId());
        values.put(ChatTable.CHAT_MESSAGEID, message.getMessageId());
        values.put(ChatTable.CHAT_TYPE, 2);
        MyLog.i("save2ChatTable "
                        + message.toString()
        );
        values.put(ChatTable.CHAT_CONTENT_TYPE, TextUtils.isEmpty(message.getTitle()) ? 2 : 1);
        values.put(ChatTable.CHAT_CONTENT, TextUtils.isEmpty(message.getTitle()) ?
                message.getPicture() : message.getTitle());
        values.put(ChatTable.CHAT_LOCAL_TIME, System.currentTimeMillis());
        values.put(ChatTable.CHAT_SERVER_TIME, message.getUpdatedAt());
        values.put(ChatTable.CHAT_LOGIN_UID, ClientInfo.getUID());
        values.put(ChatTable.CHAT_JSON, mGson.toJson(message));

        b = ContentProviderOperation.newInsert(ChatTable.CHAT_CACHE_URI).withValues(
                values);
        ops.add(b.build());
        try {
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把发送的消息存入本地数据库的聊天表
     */
    public void save2ChatTable(ChatMessage message) {
        // 如果数据库中已经存在messageid相同的记录,不重复插入了
        if (isMessageIdentical(message.getMessageId())) return;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation.Builder b = null;

        ContentValues values = new ContentValues();
        values.put(ChatTable.CHAT_UID, message.getChatUser().getUId());
        values.put(ChatTable.CHAT_MESSAGEID, message.getMessageId());
        values.put(ChatTable.CHAT_TYPE, message.getType());
        values.put(ChatTable.CHAT_CONTENT_TYPE, message.getContentType());
        values.put(ChatTable.CHAT_CONTENT, message.getContent());
        values.put(ChatTable.CHAT_LOCAL_TIME, message.getLocalTime());
        values.put(ChatTable.CHAT_SERVER_TIME, message.getServerTime());
        values.put(ChatTable.CHAT_SEND_STATUS, message.getSendStatus());
        values.put(ChatTable.CHAT_READ_FLAG, message.getReadStatus());
        values.put(ChatTable.CHAT_LOGIN_UID, ClientInfo.getUID());

        b = ContentProviderOperation.newInsert(ChatTable.CHAT_CACHE_URI).withValues(
                values);
        ops.add(b.build());
        try {
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新已发送消息的消息id 发送状态 服务器时间等
     */
    public void updateSentMessage(String oldMesssageId, ChatMessage message) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation.Builder b = null;

        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(message.getMessageId())) {
            values.put(ChatTable.CHAT_MESSAGEID, message.getMessageId());
        }
        if (message.getServerTime() != 0) {
            values.put(ChatTable.CHAT_SERVER_TIME, message.getServerTime());
        }
        values.put(ChatTable.CHAT_SEND_STATUS, message.getSendStatus());

        b = ContentProviderOperation.newUpdate(ChatTable.CHAT_CACHE_URI).withValues(
                values).withSelection(ChatTable.CHAT_MESSAGEID + " =? ",
                new String[]{oldMesssageId});
        ops.add(b.build());
        try {
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 看数据库里是否已经存在和传入的messageid相同的消息
     */
    private boolean isMessageIdentical(String messageId) {
        boolean result = false;
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            cursor = contentResolver.query(ChatTable.CHAT_CACHE_URI, null,
                    ChatTable.CHAT_MESSAGEID + " =?",
                    new String[]{messageId}, null);

            if (cursor == null ) {
                return false;
            }

            if (cursor.moveToNext()) {
                result = true;
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return result;
    }

    /**
     * 把接收到的消息转为聊天消息
     */
    private ChatMessage message2ChatMessage(Messages message) {
        if (message == null) return null;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatUser(message.getUser());
        chatMessage.setMessageId(message.getMessageId());
        chatMessage.setType(2);
        chatMessage.setLocalTime(System.currentTimeMillis());
        chatMessage.setServerTime(message.getUpdatedAt());
        chatMessage.setContentType(TextUtils.isEmpty(message.getTitle()) ? 2 : 1);
        chatMessage.setContent(TextUtils.isEmpty(message.getTitle()) ?
                message.getPicture() : message.getTitle());
        chatMessage.setLoginUid(ClientInfo.getUID());

        return chatMessage;
    }

    private ChatMessage cursor2ChatMessage(Cursor cursor) {
        if (cursor == null) return null;

        ChatMessage message = new ChatMessage();
        message.setChatUser(getChatUser(cursor));
        message.setMessageId(cursor.getString(cursor.getColumnIndex(ChatTable.CHAT_MESSAGEID)));
        message.setType(cursor.getInt(cursor.getColumnIndex(ChatTable.CHAT_TYPE)));
        message.setLocalTime(cursor.getLong(cursor.getColumnIndex(ChatTable.CHAT_LOCAL_TIME)));
        message.setServerTime(cursor.getLong(cursor.getColumnIndex(ChatTable.CHAT_SERVER_TIME)));
        message.setContentType(cursor.getInt(cursor.getColumnIndex(ChatTable.CHAT_CONTENT_TYPE)));
        message.setContent(cursor.getString(cursor.getColumnIndex(ChatTable.CHAT_CONTENT)));
        message.setSendStatus(cursor.getInt(cursor.getColumnIndex(ChatTable.CHAT_SEND_STATUS)));
        message.setReadStatus(cursor.getInt(cursor.getColumnIndex(ChatTable.CHAT_READ_FLAG)));
        message.setLoginUid(cursor.getString(cursor.getColumnIndex(ChatTable.CHAT_LOGIN_UID)));

        return message;
    }

    private UserInfo getChatUser(Cursor cursor) {
        UserInfo userInfo = null;
        try {
            //先从本地缓存中取
            String uid = cursor.getString(cursor.getColumnIndex(ChatTable.CHAT_UID));
            userInfo = (UserInfo) CacheManager.readObject(BaseApplication.context(), uid);
            if (userInfo == null) {
                //再从消息的json串中获取userinfo
                Messages tmp = mGson.fromJson(cursor.getString(cursor.getColumnIndex(ChatTable.CHAT_JSON)),
                        Messages.class);
                if (tmp != null) {
                    userInfo = tmp.getUser();
                }
                if (userInfo == null) {
                    //走到这里的话userinfo还是不全 todo?
                    userInfo = new UserInfo();
                    userInfo.setUId(uid);
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        }
        return userInfo;
    }

    private ChatMessage buildChatMessage(String uid, long time) {
        ChatMessage message = new ChatMessage();
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.context().getContentResolver();
        try {
            cursor = contentResolver.query(ChatTable.CHAT_CACHE_URI, null,
                    ChatTable.CHAT_UID + " =? " + " and " + ChatTable.CHAT_SERVER_TIME + " =" + time,
                             new String[]{uid}, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                message = cursor2ChatMessage(cursor);
                if (message != null) {
                    MyLog.i("buildChatMessage message uid= " + uid
                            + ", type=" + (message.getType()==1?"发送":"接收")
                            + ", cType=" + message.getContentType()
                            + ", content=" + message.getContent()
                            + ", serTime=" + message.getServerTime()
                    );
                }
                break;
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return message;
    }

    /**
     * 聊天时上传图片到oss,成功后回调传回oss上的存储url
     * 然后外部需要调用sendChatMessage 把url作为picture参数传入
     * @param path
     * @param listener
     */
    public void uploadChatImage2Oss(String path, IListener listener) {
        StickerManager stickerManager = StickerManager.getInstance();
        stickerManager.getOssToken(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                OssToken ossToken = (OssToken) obj;
                if (ossToken != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //初始化oss对象
                            String endpoint = "http://" + stickerManager.getBestServerDomain();

                            OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
                                @Override
                                public OSSFederationToken getFederationToken() {
                                    return new OSSFederationToken(ossToken.getAccessKeyId(),
                                            ossToken.getAccessKeySecret(),
                                            ossToken.getSecurityToken(),
                                            ossToken.getExpiration());
                                }
                            };
                            OSS oss = new OSSClient(BaseApplication.context(), endpoint, credentialProvider);
                            if (oss != null) {
                                // 构造上传请求
                                String objectKey = StringUtil.getUploadKey(DiscoveryConstants.TYPE_XGT, path, null);
                                String bucketName = stickerManager.getBestServerBucket();
                                MyLog.i("bucket=" + bucketName + ",objectKey =" + objectKey + ", path=" + path);

                                PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, path);
                                OSSAsyncTask task = oss.asyncPutObject(put,
                                        new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                            @Override
                                            public void onSuccess(PutObjectRequest request,
                                                                  PutObjectResult result) {
                                                MyLog.i("upload succeed!");

                                                String url = "/" + objectKey.substring(0, objectKey.indexOf("/"))
                                                        + "/" + bucketName +
                                                        objectKey.substring(objectKey.indexOf("/"));
                                                MyLog.i("url="+url);
                                                listener.onSuccess(url);
                                            }

                                            @Override
                                            public void onFailure(PutObjectRequest request,
                                                                  ClientException clientExcepion,
                                                                  ServiceException serviceException) {
                                                MyLog.i("upload failed!");
                                                listener.onErr(null);
                                                // 请求异常
                                                if (clientExcepion != null) {
                                                    // 本地异常如网络异常等
                                                    clientExcepion.printStackTrace();
                                                }
                                            }
                                        });

                            } else {
                                MyLog.i("oss init faliled!!");
                                listener.onErr(null);
                            }
                        }
                    }).start();
                    ;
                }
            }

            @Override
            public void onErr(Object obj) {
                listener.onErr(null);
            }

            @Override
            public void onNoNetwork() {
                listener.onErr(null);
            }
        });
    }

    /**
     * 把消息存入本地数据库的消息表
     * @param title: 消息的标题
     * @param content: 消息的内容
     */
    private void save2MessageTable(String title, String content, Messages message) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;

            ContentValues values = new ContentValues();
            values.put(PushMessageTable.MESSAGE_UID, ClientInfo.getUID());
            values.put(PushMessageTable.MESSAGE_TITLE, title);
            values.put(PushMessageTable.MESSAGE_BODY, content);
            values.put(PushMessageTable.MESSAGE_CLICK_FLAG, 0);

            if (!TextUtils.isEmpty(content)) {
                if (message != null) {
                    // 存储消息ID(moin sesrver下发的messageID)
                    values.put(PushMessageTable.MESSAGE_ID, message.getResId());
                    values.put(PushMessageTable.MESSAGE_CREATETIME, message.getUpdatedAt());

                    //如果type是贴纸包更新的话,更新
                    if (message.getType() == Messages.TYPE_STICKER) {
                        int type = convertStickerType(message.getAction());
//                        if(isNeedRefresh(type)) {
//                            MinePreference.getInstance().setLastRefreshTime(type, System.currentTimeMillis());
//                            updateSticker(type);
//                        }

                        // TODO 如果是预制类型2的更新,可以不用uid,传入msg.getid或者type即可. 如果是普通类型的贴纸更新,则需要传入uid+msg.getid,
                        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
                        int flag = dbHelper.queryEmoticonSetFlag(type, message.getResId());
                        if(flag == StickerConstants.FLAG_UNUPDATED) {
                            // 已经是待更新状态了
                        } else if(flag == StickerConstants.FLAG_INVALID) {
                            // 原来的贴纸是下架状态, 收到消息后设置flag为待更新状态
                            dbHelper.updateEmoticonSetFlag(message.getResId(), StickerConstants.FLAG_UNUPDATED);
                        } else if(flag >= StickerConstants.FLAG_NORMAL) {
                            // 设置flag为待更新状态
                            if (type == StickerPackage.STICKER_INTIME) {
                                dbHelper.updateEmoticonSetFlag(type, StickerManager.getInstance().resetUpdatedFlag(flag));
                            } else {
                                dbHelper.updateEmoticonSetFlag(message.getResId(), StickerManager.getInstance().resetUpdatedFlag(flag));
                            }
                        }

                        // 如果是贴纸包更新消息,不保存到本地消息表中
//                        return;
                    } else if (message.getType() == Messages.TYPE_SYSTEM) {
                        if (message.getAction() == Messages.ACTION_SYSTEM_BOOT_IMAGE) {
                            if (!StringUtils.isBlank(message.getPicture())) {
                                InitPreference.getInstance().setHasBootImageMsg(true);
                                InitPreference.getInstance().setBootImageUrl(message.getPicture());
                                InitPreference.getInstance().setBootImageUpdateAt(message.getUpdatedAt());

                                String url = ImageLoaderUtils.buildNewUrl(message.getPicture(), new ImageSize((int) TDevice.getScreenWidth(), (int) TDevice.getScreenHeight()));
                                String bootImgPath = BitmapUtil.getBootImagePath();
//                                MyLog.i("启动图URL存在, 下载: " + url + "\n到 " + bootImgPath);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean result = HttpUtil.download(url, bootImgPath, true);
                                        if (result) {
                                            InitPreference.getInstance().setHasBootImageMsg(false);
                                            InitManager.getInstance().backupBootImage();
                                        }
                                    }
                                }).start();
                            }
                            //不保存到本地消息数据库,直接return
//                            return;
                        }
                    }
                }
            }
            b = ContentProviderOperation.newInsert(PushMessageTable.PUSH_MESSAGE_CACHE_URI).withValues(
                    values);
            ops.add(b.build());
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    private final long MIN_5 = 1000 * 60 * 5;

    private boolean isNeedRefresh(int type) {
        long delta = System.currentTimeMillis() - MinePreference.getInstance().getLastRefreshTime(type);
        if (delta >= MIN_5) {
            return true;
        }

        return false;
    }

    public List<Messages> getMessagesFromDatabase() {
        List<Messages> listMessages = new ArrayList<Messages>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = BaseApplication.context().getContentResolver();
            cursor = contentResolver.query(PushMessageTable.PUSH_MESSAGE_CACHE_URI, null,
                    PushMessageTable.MESSAGE_UID + " = ?", new String[]{ClientInfo.getUID()},
                    PushMessageTable._ID + " DESC");

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(PushMessageTable.MESSAGE_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(PushMessageTable.MESSAGE_BODY));
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    Messages message = mGson.fromJson(content, Messages.class);
                    if (message != null) {
                        message.setTitle(title);
                        listMessages.add(message);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return listMessages;
    }

    public int getUnreadMessages(long timeMills) {
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = BaseApplication.context().getContentResolver();
            cursor = contentResolver.query(PushMessageTable.PUSH_MESSAGE_CACHE_URI, null,
                    PushMessageTable.MESSAGE_CREATETIME + " >= " + timeMills + " and " +
                            PushMessageTable.MESSAGE_UID + " = ?", new String[]{ClientInfo.getUID()}, null);

            if (cursor == null ) {
                return 0;
            }

            return cursor.getCount();
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return 0;
    }

    public List<Messages> getUnpopMessageList(long timeMills) {
        List<Messages> listMessages = new ArrayList<Messages>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = BaseApplication.context().getContentResolver();
            cursor = contentResolver.query(PushMessageTable.PUSH_MESSAGE_CACHE_URI, null,
                    PushMessageTable.MESSAGE_CREATETIME + " >= " + timeMills + " and " +
                            PushMessageTable.MESSAGE_UID + " = ?", new String[]{ClientInfo.getUID()}, null);

            if (cursor == null ) {
                return null;
            }

            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(PushMessageTable.MESSAGE_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(PushMessageTable.MESSAGE_BODY));
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    // messageID在MESSAGE_BODY中有存在,所以不用重新去取MESSAGE_ID
                    Messages message = mGson.fromJson(content, Messages.class);
                    MyLog.i("msgJson:"+content);
                    MyLog.i("msgResId:"+message.getResId());
                    if (message != null) {
                        message.setTitle(title);
                        listMessages.add(message);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return listMessages;
    }

    /**
     * 把一个消息分组到具体的哪一类(Messages类定义的8种之一)
     * @param message
     * @return
     */
    public int ConvertMessageType(Messages message) {
        int type = 0;
        if (message != null) {
            if (message.getType() == Messages.TYPE_IMAGE) {
                if (message.getAction() == Messages.ACTION_COMMENT_COSPLAY
                        || message.getAction() == Messages.ACTION_REPLY_COSPLAY
                        || message.getAction() == Messages.ACTION_COMMENT_LIKE_COSPLAY
                        || message.getAction() == Messages.ACTION_REPLY_COMMENT_COSPLAY) {
                    type = Messages.MESSAGE_COMMENT;
                } else if (message.getAction() == Messages.ACTION_FORWARD_COSPLAY
                        || message.getAction() == Messages.ACTION_MODIFY_COSPLAY) {
                    type = Messages.MESSAGE_FORWARD;
                } else if (message.getAction() == Messages.ACTION_LIKE_COSPLAY) {
                    type = Messages.MESSAGE_LIKE;
                } else if (message.getAction() == Messages.ACTION_AT_COSPLAY) {
                    type = Messages.MESSAGE_AT;
                }
            } else if (message.getType() == Messages.TYPE_USER) {
                type = Messages.MESSAGE_FOLLOW;
            } else if (message.getType() == Messages.TYPE_STICKER) {
                type = Messages.MESSAGE_STICKER;
            } else if (message.getType() == Messages.TYPE_SYSTEM) {
                type = Messages.MESSAGE_SYSTEM;
            }
        }

        return type;
    }

//    private void updateSticker(int type) {
//        new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                StickerManager.getInstance().checkUpdate(type);
//            }
//        });
//    }

    private int convertStickerType(int action) {
        int type;
        switch (action) {
            case Messages.ACTION_STICKER_INTIME:
                type = StickerPackage.STICKER_INTIME;
                break;
            case Messages.ACTION_STICKER_TEXT:
                type = StickerPackage.STICKER_TEXT;
                break;
            case Messages.ACTION_STICKER_SPECIAL:
                type = StickerPackage.STICKER_SPECIAL;
                break;
            case Messages.ACTION_STICKER_BUBBLE:
                type = StickerPackage.STICKER_BUBBLE;
                break;
            case Messages.ACTION_STICKER_FRAME:
                type = StickerPackage.STICKER_FRAME;
                break;
            case Messages.ACTION_STICKER_NORMAL:
                type = StickerPackage.STICKER_NORMAL;
                break;
            default:
                type = 0;
                break;
        }
        return type;
    }

    //=====================收到通知栏点击后跳转相应的页面=============================
    public void onEvent(UpdatePushChannelIdEvent updatePushChannelIdEvent) {
        if (AppContext.getInstance().isLogin()) {
            MyLog.i("onEvent(UpdatePushChannelIdEvent updatePushChannelIdEvent): updatePushChannelIdEvent=" + updatePushChannelIdEvent.getChannelId());
            new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    UserInfo userInfo = new UserInfo();
                    updateUserInfo(userInfo, null);
                }
            });
        }
    }

    public void onEvent(MineManager.NotifyClickMessage clickMessage) {
        MyLog.i("onEvent(NotifyClickMessage clickMessage): message=" + clickMessage.getMessage());
        //解析
        if (clickMessage == null || TextUtils.isEmpty(clickMessage.getMessage())) return;
        Messages message = mGson.fromJson(clickMessage.getMessage(), Messages.class);
        if (message != null) {
            // 聊天消息
            if (message.getAction() == Messages.ACTION_CHAT) {
                //跳转聊天界面
                showChat(BaseApplication.context(), message.getUser().getUId(),
                        message.getUser().getUsername());
            } else {
                // 其他的通知栏消息
                switch (message.getType()) {
                    case Messages.TYPE_IMAGE:
                    case Messages.TYPE_POST:
                        MyLog.i("你点击了 通知栏 " + message.toString());

                        gotoCosplayDetail(BaseApplication.context(), message.getResId(), message.getType());
                        break;
                    case Messages.TYPE_USER:
                        //todo
                        showUserCenter(BaseApplication.context(), message.getUid());
                        break;
                    case Messages.TYPE_STICKER:
                        if (message.getAction() == Messages.ACTION_STICKER_NORMAL) {
                            gotoStickerDetail(message.getResId());
                        }
                        break;
                }
            }
        }
    }

    private void showChat(Context activity, String uid, String username) {
        if (StringUtil.isNullOrEmpty(uid)) {
            AppContext.showToast("未知用户");
            return;
        }
        SimpleBackPage page = SimpleBackPage.CHAT;
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_KEY_UID, uid);
        args.putString(Constants.BUNDLE_KEY_USERNAME, username);
        Intent intent = new Intent(activity, SimpleBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.BUNDLE_KEY_ARGS, args);
        intent.putExtra(Constants.BUNDLE_KEY_PAGE, page.getValue());

        // 如果app已经退出,需要先启动 MainActivity
        startApp(activity, intent);
        // 设置聊天已读
        markChatRecord(uid, 1);
    }

    private void startApp(Context activity, Intent intent) {
        // 如果app已经退出,需要先启动 MainActivity
        if (AppManager.getActivity(MainActivity.class) == null) {
            int tabIndex = AppContext.getInstance().isLogin() ? MainActivity.KEY_TAB_FOLLOW : MainActivity.KEY_TAB_DISCOVERY;
            Intent i = new Intent(activity, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Constants.BUNDLE_KEY_TABINDEX, tabIndex);
            Intent[] intents = {i, intent};
            activity.startActivities(intents);
        } else {
            activity.startActivity(intent);
        }
    }

    public void showUserCenter(Context activity, String userid) {
        if (StringUtil.isNullOrEmpty(userid)) {
            AppContext.showToast("未知用户");
            return;
        }
        Bundle args = new Bundle();
        args.putString(DiscoveryConstants.USERID, userid);
        SimpleBackPage page;
        if(userid.equals(ClientInfo.getUID())) {
            // 3.0之前是进消息tab[0], 3.0以后是进入空间[1]
            args.putInt(Constants.BUNDLE_KEY_TABINDEX, 1);
            page = SimpleBackPage.MSG_MINE;
        } else {
            page = SimpleBackPage.USER_ACTIVITY;
        }
        Intent intent = new Intent(activity, SimpleBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.BUNDLE_KEY_ARGS, args);
        intent.putExtra(Constants.BUNDLE_KEY_PAGE, page.getValue());
        // 如果app已经退出,需要先启动 MainActivity
        startApp(activity, intent);
    }

    public void gotoStickerDetail(String stickId) {
        Intent intent = new Intent(BaseApplication.context(), StickerDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(StickerDetailActivity.STICKER_ID, stickId);
        BaseApplication.context().startActivity(intent);
    }

    /**
     * 查看大咖秀或者帖子详情
     * @param activity
     * @param ucid
     * @param type Messages.TYPE_IMAGE, Messages.TYPE_POST
     */
    public void gotoCosplayDetail(Context activity, String ucid, int type) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(DiscoveryConstants.UCID, ucid);
        bundle.putBoolean(Constants.BUNDLE_KEY_HAS_ACTIONBAR, false);
        bundle.putInt(Constants.BUNDLE_KEY_DISPLAY_TYPE,
                type == Messages.TYPE_IMAGE ? DetailActivity.DISPLAY_DISCOVER_COS : DetailActivity.DISPLAY_POST);
        bundle.putLong(Constants.BUNDLE_KEY_CLICK_TIME, TimeUtils.getCurrentTimeInLong());
        intent.putExtras(bundle);

        // 如果app已经退出,需要先启动 MainActivity
        startApp(activity, intent);
    }

    /**
     * 分享接口,服务器根据接口判断是否应该给魔豆
     * @param ucid 图片id
     * @param eventid 活动id
     */
    public void callShareServer(String ucid, String eventid) {
        int type;
        String id;
        if (!StringUtils.isEmpty(ucid)) {
            type = 1;
            id = ucid;
        } else {
            type = 2;
            id = eventid;
        }
        share(type, id, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    MoinBean bean = (MoinBean) obj;
                    if (bean != null && bean.getTotalBean() > 0 && bean.getObtainBean() > 0) {
                        //弹出获得魔豆的页面
                        UIHelper.showMoinBeanActivity(bean, MissionConstants.MISSION_SHARE);
                    }
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    public static class NotifyClickMessage {
        private String mMessage;

        public NotifyClickMessage(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class ReceivedMessage {
        private String mMessageTitle;
        private String mMessageBody;

        public ReceivedMessage(String title, String body) {
            mMessageTitle = title;
            mMessageBody = body;
        }

        public String getMessageTitle() {
            return mMessageTitle;
        }

        public String getMessageBody() {
            return mMessageBody;
        }
    }

    public static class NewChatMessageEvent {
        private ChatMessage mChatMessage;

        public NewChatMessageEvent(ChatMessage message) {
            mChatMessage = message;
        }

        public ChatMessage getChatMessage() {
            return mChatMessage;
        }
    }

    public static class SelectPhotoEvent {
        private String mImagePath;

        public SelectPhotoEvent(String path) {
            mImagePath = path;
        }

        public String getImagePath() {
            return mImagePath;
        }
    }

    private Stack<Activity> chatActivity = new Stack<Activity>();
    public void close() {
        for (Activity chat : chatActivity) {
            try {
                chat.finish();
            } catch (Exception e) {

            }
        }
        chatActivity.clear();
    }

    public void addActivity(Activity act) {
        chatActivity.add(act);
    }

    public void removeActivity(Activity act) {
        chatActivity.remove(act);
    }
}

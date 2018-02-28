package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.result.GetEmojiDetailResult;
import com.moinapp.wuliao.modules.login.model.BaseLoginResult;
import com.moinapp.wuliao.modules.login.model.GetHotUserResult;
import com.moinapp.wuliao.modules.login.model.GetUserTypeResult;
import com.moinapp.wuliao.modules.update.AbsManager;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

import java.util.Stack;

/**
 * Created by liujiancheng on 15/9/8.
 */
public class LoginManager extends AbsManager {
    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger("LoginManager");
    private static LoginManager mInstance;

    // ===========================================================
    // Constructors
    // ===========================================================
    private LoginManager() {
    }

    public static synchronized LoginManager getInstance() {
        if (mInstance == null) {
            mInstance = new LoginManager();
        }

        return mInstance;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void init() {
    }

    private Stack<Activity> logins = new Stack<Activity>();
    public void addActivity(Activity act) {
        logins.add(act);
    }
    public void removeActivity(Activity act) {
        logins.remove(act);
    }
    public void closeLoginUI() {
        for (Activity act : logins) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        logins.clear();
    }

    /**
     * 魔映自有用户的登录接口
     * @param username
     * @param md5：密码的md5值
     * @param smsCode：验证码,当输错密码三次后需要
     * @param listener: callback result：0 失败，1 成功,  2 在另外一台设备上的登录被踢出，需要重新上传deviceInfo和appInfo；3 使用第三方帐号第一次登录或者老用户没有用户名，需要更新用户个人信息；
        error： (出错信息)，-100 系统错误 -1 用户名密码错误 -2 第三方登录失败，无法创建用户
     */
    public void userOurLogin(String username, String md5, String smsCode, final IListener listener) {
        LoginApi.login(username, md5, smsCode, buildLoginCallback(false, listener));
    }


    /**
     * 第三方用户的登录接口
     * @param platform 第三方登录的平台
     * @param token：第三方登录的token
     * @param listener: callback
     */
    public void userThirdLogin(String platform, Object token, final IListener listener) {
        LoginApi.login(platform, token, buildLoginCallback(false, listener));
    }

    /**
     * 注册用户的接口方法
     * @param username: 用户名,必填
     * @param phone: 电话
     * @param md5: 密码的md5
     * @param smscode: 短信码
     * @param sex: 性别
     * @param avatar: 头像文件id
     * @param listener: callback
     */
    public void registerUser(String username, String phone, String md5, String smscode, String sex, String avatar, IListener listener) {
        LoginApi.registerUser(username, phone, md5, smscode, sex, avatar, buildLoginCallback(true, listener));
    }

    /**
     *  注册手机唯一性检查
     * @param phone 手机号
     * @param listener: callback
     */
    public void checkPhone(String phone, IListener listener) {
        LoginApi.checkPhone(phone, buildCommonCallback(listener));
    }

    /**
     *  获取手机验证码,获取返回结果需要在AsyncHttpResponseHandler里面使用BaseHttpResponse类
     * @param phone 手机号
     * @param listener: callback
     */
    public void getPhoneSms(String phone, IListener listener) {
        LoginApi.getPhoneSms(phone, buildCommonCallback(listener));
    }

    /**
     *  通过手机号找回密码
     * @param phone 手机号
     * @param smscode 手机验证码
     * @param listener: callback
     */
    public void retrievePasswordByPhone(String phone,  String smscode, IListener listener) {
        LoginApi.retrievePasswordByPhone(phone, smscode, buildLoginCallback(false, listener));
    }

    /**
     *  退出登陆
     * @param listener:callback
     */
    public void userLogout(IListener listener) {
        LoginApi.userLogout(buildCommonCallback(listener));
    }




    /**
     *  用户名唯一性检查
     * @param username 用户名
     * @param listener: callback
     */
    public void checkUserName(String username, IListener listener) {
        LoginApi.checkUserName(username, buildCommonCallback(listener));
    }

    /**
     *  得到用户登陆类型
     * @param listener：callback
     * 返回：type: 1 常规用户 2 第三方用户；
     *   namelist：第三方账户的名称数组集合，如[qq, weichat, sina] 表示同时绑定了QQ、微信和微博三个第三方平台。
     */
    public void getUserLoginType(IListener listener) {
        LoginApi.getUserLoginType(buildGetUserTypeCallback(listener));
    }

    /**
     * 用户反馈信息
     * @param contact: 联系信息
     * @param content: 反馈正文
     * @param listener: callback
     */
    public void userFeedback(String contact, String content, IListener listener) {
        LoginApi.userFeedback(contact, content, buildCommonCallback(listener));
    }

    /**
     * 验证手机号和验证码
     * @param phone: 电话号码
     * @param code: 短信验证码
     * @param listener: callback
     */
    public void verifyPhone(String phone, String code, IListener listener) {
        LoginApi.verifyPhone(phone, code, buildCommonCallback(listener));
    }

    /**
     * 获取热门用户
     * @param uid: 用户uid
     * @param sex: 用户性别
     * @param listener: callback
     */
    public void getHotUser(String uid, String sex, IListener listener) {
        LoginApi.getHotUser(uid, sex, buildGetHotUserCallback(listener));
    }

    private AsyncHttpResponseHandler buildCommonCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildLoginCallback(final boolean isFromRegister, final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                BaseLoginResult result = XmlUtils.JsontoBean(BaseLoginResult.class, responseBody);
                MyLog.i("responseBody="+new String(responseBody));
                if (result != null) {
                    if (result.getResult() > 0) {
                        ClientInfo.setUID(result.getUid());
                        ClientInfo.setPassport(result.getPassport());
                        if (result.getResult() == 1 && !TextUtils.isEmpty(result.getUser().getUsername())) {
                            ClientInfo.setUserName(result.getUser().getUsername());
                            //保存登陆成功后的用户信息
                            AppContext.getInstance().saveUserInfo(result.getUser());

                            //这里主要是显式的调用下更新接口,把deviceinfo传上去
                            AppContext.getInstance().updateUserInfo(new UserInfo());
                        }
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
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetUserTypeCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GetUserTypeResult result = XmlUtils.JsontoBean(GetUserTypeResult.class, responseBody);
                if (result != null) {
                    //这里把GetUserTypeResult对象传回去，ui根据getType类型做相应处理
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



    private AsyncHttpResponseHandler buildGetHotUserCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i(" response = " + new String(responseBody));
                GetHotUserResult result = XmlUtils.JsontoBean(GetHotUserResult.class, responseBody);
                if (result != null) {
                    if (result.getUsers() != null) {
                        MyLog.i(" hot users result.size = " + result.getUsers().size());
                        listener.onSuccess(result.getUsers());
                    } else {
                        listener.onErr(null);
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

    private AsyncHttpResponseHandler buildGetEmojiDetailCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetEmojiDetailResult result = XmlUtils.JsontoBean(GetEmojiDetailResult.class, responseBody);
                if (result != null && result.getEmoji() != null) {
                    MyLog.i("onSucceed: emoji detail list size = " + result.getEmoji().getEmojis().size());
                    listener.onSuccess(result.getEmoji());
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
}

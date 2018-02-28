package com.moinapp.wuliao.modules.login;

/**
 * Created by liujiancheng on 15/5/7.
 */
public class LoginConstants {
    /**
     * 我的／登陆模块的domain
     */
    public static final String LOGIN_BASE_URL = "user/";

    /**
     * 用户登录请求的url
     */
    public static final String LOGIN_URL = LOGIN_BASE_URL + "login";

    /**
     * 用户退出登录请求的url
     */
    public static final String LOGOUT_URL = LOGIN_BASE_URL + "logout";

    /**
     * 用户更新数据的请求url
     */
    public static final String LOGIN_UPDATE_URL = LOGIN_BASE_URL + "updatebasic/";

    /**
     * 用户注册的请求url
     */
    public static final String LOGIN_REGISTER_URL = LOGIN_BASE_URL + "register";

    /**
     * 检查电话号码唯一性的请求url
     */
    public static final String LOGIN_CHECK_PHONE_URL = LOGIN_BASE_URL + "checkphone";

    /**
     * 检查邮箱唯一性的请求url
     */
    public static final String LOGIN_CHECK_EMAIL_URL = LOGIN_BASE_URL + "checkemail";

    /**
     * 获取短信验证码的请求url
     */
    public static final String LOGIN_GET_SMSCODE_URL = LOGIN_BASE_URL + "sentsmscode";

    /**
     * 获取邮箱验证码的请求url
     */
    public static final String LOGIN_GET_EMAILCODE_URL = LOGIN_BASE_URL + "sentemailcode";

    /**
     * 找回密码的请求url
     */
    public static final String LOGIN_RETRIEVE_PASSWORD_URL = LOGIN_BASE_URL + "findpassword";

    /**
     * 修改密码的请求url
     */
    public static final String LOGIN_UPDATE_PASSWORD_URL = LOGIN_BASE_URL + "updatepassword";

    /**
     * 上传图像的请求url
     */
    public static final String LOGIN_UPLOAD_URL = LOGIN_BASE_URL + "uploadavatar";

    /**
     * 获取用户信息的请求url
     */
    public static final String LOGIN_GET_INFO_URL = LOGIN_BASE_URL + "getinfo";

    /**
     * 检查用户名唯一性的请求url
     */
    public static final String LOGIN_CHECK_USERNAME_URL = LOGIN_BASE_URL + "checkname";

    /**
     * 得到用户登陆的类型接口的url
     */
    public static final String GET_LOGIN_TYPE = LOGIN_BASE_URL + "gettype";

    /**
     * 得到用户登陆的类型接口的url
     */
    public static final String USER_FEEDBACK = LOGIN_BASE_URL + "feedback";

    /**
     * 获取用户基本信息接口的url
     */
    public static final String GET_USER_INFO = LOGIN_BASE_URL + "getinfo";

    /**
     * 获取我关注的用户接口的url
     */
    public static final String GET_MY_IDOS = LOGIN_BASE_URL + "getmyidol";

    /**
     * 获取我的粉丝用户接口的url
     */
    public static final String GET_MY_FANS = LOGIN_BASE_URL + "getmyfans";

    /**
     * 关注／取消关注接口的url
     */
    public static final String USER_FOLLOW = LOGIN_BASE_URL + "follow";

    /**
     * 获取热门用户接口的url
     */
    public static final String GET_HOT_USER = LOGIN_BASE_URL + "gethotuser";

    /**
     * 获取版本更新接口的url
     */
    public static final String GET_UPDATE_URL = LOGIN_BASE_URL + "getappversion";

    /**
     * 获取表情专辑列表接口的url
     */
    public static final String GET_EMOJI_List = "emoji/getlist";

    /**
     * 获取表情专辑详情接口的url
     */
    public static final String GET_EMOJI_Detail = "emoji/getdetail";

    /**
     * 发送表情到微信qq接口的url
     */
    public static final String SEND_EMOJI = "emoji/send";

    /**
     * 获取用户制作的大咖秀列表接口的url
     */
    public static final String GET_USER_COSPLAY = "cosplay/mine";

    /**
     * 获取用户关系接口的url
     */
    public static final String GET_USER_RELATION_URL = LOGIN_BASE_URL + "getRelation";

    /**
     * 验证手机号和验证码接口的url
     */
    public static final String VERIFY_PHONE_URL = LOGIN_BASE_URL + "verify";

    /**
     * MOIN 移动推广url
     */
    public static final String MOIN_APP_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.moinapp.wuliao";

    /**
     * 搜索用户接口的url
     */
    public static final String SEARCH_USER_URL = LOGIN_BASE_URL + "searchUser";

    /**
     * 获取热门用户接口的url
     */
    public static final String GET_HOT_USER_URL = LOGIN_BASE_URL + "hotList";

    /**
     * 获取我的消息接口的url
     */
    public static final String GET_MY_MESSAGE_URL = LOGIN_BASE_URL + "getMessage";

    /**
     * 获取用户动态接口的url
     */
    public static final String GET_USER_ACTIVITY_URL = LOGIN_BASE_URL + "getActivity";

    /**
     * 第三方登录从ui上传来的bundle key
     */
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS_KEY = "access_key";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String OPEN_ID = "openid";
    public static final String EXPIRE_IN = "expires_in";
    public static final String REFRESH_TOKEN_EXPIRES = "refresh_token_expires";
    public static final String UNION_ID = "unionid";

    /**
     * 同服务器交互时的参数key
     */
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String OPENID = "openid";
    public static final String UID = "uid";
    public static final String MOIN_PASSPORT = "passport";
    public static final String IMEI = "imei";
    public static final String ERROR = "error";
    public static final String MD5 = "md5";
    public static final String SMSCODE = "smscode";
    public static final String EMAILCODE = "emailcode";
    public static final String DEVICEINFO = "device";
    public static final String APPINFO = "app";
    public static final String CODE = "code";
    public static final String NICKNAME= "nickname";
    public static final String SEX = "sex";
    public static final String AGES = "ages";
    public static final String STARS = "stars";
    public static final String AVATAR = "avatar";
    public static final String LOCATION = "location";
    public static final String SIGNATURE = "signature";
    public static final String CONTACT = "contact";
    public static final String CONTENT = "content";
    public static final String LOGTIME = "logtime";
    public static final String USERID = "userid";
    public static final String LASTID = "lastid";
    public static final String ACTION = "action";
    public static final String IPNAME = "ipname";
    public static final String OPNAME = "opname";
    public static final String TAG = "tag";
    public static final String EMOJIID = "id";
    public static final String PARENTID = "parentid";
    public static final String TYPE = "type";
    public static final String VERSION_CODE = "versionCode";
    public static final String CHANNEL = "channel";
    public static final String KEYWORD = "keyword";
    public static final String AID = "aid";
    public static final String CID = "cid";
    public static final String USER_ID = "userId";
    /**
     * DeviceInfo的key
     */
    public static final String OS_NAME = "osName";
    public static final String OS_VERSION = "osVersion";
    public static final String SCREEN_WIDTH = "width";
    public static final String SCREEN_HEIGHT = "height";
    public static final String NETWORK = "net";
    public static final String LANGUAGE = "language";
    public static final String COUNTRY = "country";
    public static final String IMSI = "imsi";
    public static final String MAC = "mac";

    /**
     * AppInfo的key
     */
    public static final String EDITION = "edition";
    public static final String CHANNEL_ID = "channel";


}

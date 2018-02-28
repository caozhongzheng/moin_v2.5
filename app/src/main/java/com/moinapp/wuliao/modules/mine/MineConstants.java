package com.moinapp.wuliao.modules.mine;

/**
 * Created by liujiancheng on 16/1/27.
 */
public class MineConstants {
    /**
     * 我的／登陆模块的domain
     */
    public static final String LOGIN_BASE_URL = "user/";
    public static final String LOGIN_SYS_URL = "sys/";

    /**
     * 用户更新数据的请求url
     */
    public static final String LOGIN_UPDATE_URL = LOGIN_BASE_URL + "updatebasic/";

    /**
     * 修改密码的请求url
     */
    public static final String LOGIN_UPDATE_PASSWORD_URL = LOGIN_BASE_URL + "updatepassword";

    /**
     * 上传图像的请求url
     */
    public static final String LOGIN_UPLOAD_URL = LOGIN_BASE_URL + "uploadavatar";

    /**
     * 用户反馈接口的url
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
     * 获取版本更新接口的url
     */
    public static final String GET_UPDATE_URL = LOGIN_BASE_URL + "getappversion";

    /**
     * 获取用户制作的大咖秀列表接口的url
     */
    public static final String GET_USER_COSPLAY = "cosplay/mine";

    /**
     * 获取用户关系接口的url
     */
    public static final String GET_USER_RELATION_URL = LOGIN_BASE_URL + "getRelation";

    /**
     * 搜索用户接口的url
     */
    public static final String SEARCH_USER_URL = LOGIN_BASE_URL + "searchUser";

    /**
     * 获取我的消息接口的url
     */
    public static final String GET_MY_MESSAGE_URL = LOGIN_BASE_URL + "getMessage";

    /**
     * 获取用户动态接口的url
     */
    public static final String GET_USER_ACTIVITY_URL = LOGIN_BASE_URL + "getActivity";

    /**
     * 获取系统消息接口的url
     */
    public static final String GET_SYS_MESSAGE_URL = LOGIN_SYS_URL + "getMessage";

    /**
     *  修改备注名称接口的url
     */
    public static final String MODIFY_ALIAS = LOGIN_BASE_URL + "alias";

    /**
     *  发送第三方邀请接口的url
     */
    public static final String SEND_INVITE = LOGIN_BASE_URL + "invite";

    /**
     *  获取通讯录,微博好友接口的url
     */
    public static final String GET_THIRD_FRIENDS = LOGIN_BASE_URL + "findFriend";

    /**
     *  发送聊天消息接口的url
     */
    public static final String SEND_CHAT_MESSAGE = LOGIN_SYS_URL + "sendMessage";

    /**
     *  举报接口的url
     */
    public static final String REPORT = LOGIN_BASE_URL + "alert";

    /**
     *  分享接口的url
     */
    public static final String SHARE = "cosplay/share";

    /**
     *  获取魔豆信息接口的url
     */
    public static final String GET_MOIN_BEAN = LOGIN_BASE_URL + "getCoinPkRank";

    /**
     * 修改设置信息接口的url
     */
    public static final String UPDATE_SETTINGS_URL = LOGIN_SYS_URL + "updateSetting";

    /**
     * 同服务器交互时的参数key
     */
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String UID = "uid";
    public static final String ERROR = "error";
    public static final String MD5 = "md5";
    public static final String DEVICEINFO = "device";
    public static final String APPINFO = "app";
    public static final String NICKNAME= "nickname";
    public static final String SEX = "sex";
    public static final String AGES = "ages";
    public static final String BIRTHDAY = "birthday";
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
    public static final String TAG = "tag";
    public static final String TYPE = "type";
    public static final String KEYWORD = "keyword";
    public static final String UPDATEAT = "updatedAt";
    public static final String ALIAS = "alias";
    public static final String NAME = "name";
    public static final String TARGET = "target";
    public static final String TOKEN = "token";
    public static final String PHONE_LIST = "phoneList";
    public static final String TARGETS = "targets";
    public static final String PICTURE = "picture";
    public static final String TIMESTAMP = "timestamp";
    public static final String UCID = "ucid";
    public static final String ID = "id";
    public static final String PUSH = "push";
    public static final String MARK = "mark";
}

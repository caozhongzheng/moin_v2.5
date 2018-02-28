package com.moinapp.wuliao.bean;

/**
 * 常量类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月27日 下午12:14:42
 * 
 */

public class Constants {

    public static final String INTENT_ACTION_USER_CHANGE = "moinapp.wuliao.action.USER_CHANGE";

    public static final String INTENT_ACTION_COMMENT_CHANGED = "moinapp.wuliao.action.COMMENT_CHANGED";

    public static final String INTENT_ACTION_NOTICE = "moinapp.wuliao.action.APPWIDGET_UPDATE";

    public static final String INTENT_ACTION_LOGOUT = "moinapp.wuliao.action.LOGOUT";

    public static final String INTENT_GET_MOIN_BEAN = "moinapp.wuliao.action.GET_MOIN_BEAN";

    public static final String WEICHAT_APPID = "wxa0ec193d90132a01";
    public static final String WEICHAT_SECRET = "89105e57cbb5b5b3e8671458173c133a";

    public static final String QQ_APPID = "1103489550";
    public static final String QQ_APPKEY = "FefQ11y77jiijERS";

    // 在百度开发者中心查询应用的API Key
    public static final String BAIDU_APPKEY = "RrDYy9FeSxvdKNtLdYflwlgX";
    // test服务器的百度推送key
    public static final String BAIDU_APPKEY_TEST = "pG9ibFuU5mQhUHRu2iCUEQYo";
//    APP ID 7515114
//    SECRET KEY tvExPuEBpc9iZfVpGy67Qy1gCbj1vS3W

    public static final String JPG_EXTENSION = ".j";
    public static final String COMPRESS_EXTENSION = ".jpg";
    public static final String GIF_EXTENSION = ".g";
    public static final String WEBP_EXTENSION = ".webp";
    public static final String PNG_EXTENSION = ".png";

    // --- stickDemo start
    public static final String APP_DIR                    = "/MOIN";
    public static final String APP_TEMP                   = APP_DIR + "/temp";
    /**扫描本地图片后存放到这个目录【无扩展名】*/
    public static final String APP_IMAGE                  = APP_DIR + "/image";

    /**这个标签代表地址标签*/
    public static final int    POST_TYPE_POI              = 1;
    /**这个标签代表文字标签*/
    public static final int    POST_TYPE_TAG              = 0;
    public static final int    POST_TYPE_DEFAULT		  = 0;


    public static final float  DEFAULT_PIXEL              = 1242;                           //按iphone6设置
    public static final String PARAM_STICKER_TYPE         = "PARAM_STICKER_TYPE";
    public static final String PARAM_MAX_SIZE             = "PARAM_MAX_SIZE";
    public static final String PARAM_EDIT_TEXT            = "PARAM_EDIT_TEXT";
    public static final String KEY_DEFAULT_TEXT           = PARAM_EDIT_TEXT; //"KEY_DEFAULT_TEXT";
    public static final String KEY_MAX_LENGTH             = PARAM_MAX_SIZE; //"KEY_MAX_LENGTH";
    public static final String KEY_FOCUS_STICKER_POSITION = "KEY_FOCUS_STICKER_POSITION";
    public static final String KEY_RECT_WIDTH             = "KEY_RECT_WIDTH";
    public static final String KEY_RECT_HEIGHT            = "KEY_RECT_HEIGHT";

    public static final String KEY_ONE_ROW_TEXT_COUNT     = "KEY_ONE_ROW_TEXT_COUNT";
    public static final String KEY_ROW_COUNT              = "KEY_ROW_COUNT";
    public static final String KEY_SCALE                  = "KEY_SCALE";

    public static final String KEY_BUBBLE_TEXT_LIST       = "KEY_BUBBLE_TEXT_LIST";

    public static final int    ACTION_EDIT_COLOR_TEXT     = 6060;
    public static final int    ACTION_EDIT_BUBBLETEXT     = 7070;
    public static final int    ACTION_EDIT_LABEL          = 8080;
    public static final int    ACTION_EDIT_LABEL_POI      = 9090;

    public static final String FEED_INFO                  = "FEED_INFO";


    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;

    public static final int RESULT_OK = 0;
    public static final int RESULT_NETWORK_ERROR = -99;
    public static final int RESULT_NO_NETWORK = -98;

    public static final int PHOTO_NUM = 15;
    // --- stickDemo end

    /**
     * ************* 一些传递参数KEY **************
     */
    public final static String BUNDLE_KEY_ID = "id"; // 贴纸ID.话题ID等任意ID
    public final static String BUNDLE_KEY_TAG = "tag"; // 话题名称等
    public final static String BUNDLE_KEY_TYPE = "type"; // 话题类型等
    public final static String BUNDLE_KEY_FROM_MISSION = "from"; // 从哪个任务跳转过来, 1点赞 2评论
    public final static String BUNDLE_KEY_STICKER = "sticker"; // 话题对应贴纸包等

    public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url";

    public static final String BUNDLE_KEY_TITLE = "title";

    public final static String BUNDLE_KEY_UID = "uid";
    public final static String BUNDLE_KEY_USERNAME = "username";// 接收第三方传递过来的参数
    public final static String BUNDLE_KEY_GENDER = "gender";// 接收第三方传递过来的参数
    public final static String BUNDLE_KEY_AVATAR = "avatar";// 接收第三方传递过来的参数
    public final static String BUNDLE_KEY_SEX = "sex";
    public final static String BUNDLE_KEY_JUMP = "jump";
    public final static String BUNDLE_KEY_TOKEN = "token";

    public final static String BUNDLE_KEY_NAME = "name";// 商城分类名等用到的参数
    public final static String BUNDLE_KEY_DOWN_AREA_VISIBLE = "down_area_visible";

    public final static String BUNDLE_KEY_TABINDEX = "tabindex";// 进入第几个tab
    public final static String BUNDLE_KEY_CANCEL_APPSTART = "cancel_appstart";// 进入main时不启动应用启动界面
    public static final String BUNDLE_KEY_GOTO_FOLLOW = "goto_follow"; // 跳到关注页

    public static final String BUNDLE_KEY_HAS_ACTIONBAR = "has_actionbar"; // 是否显示ActionBar
    public static final String BUNDLE_KEY_DISPLAY_TYPE = "display_type"; // 详情页需要显示哪个详情fragment
    public static final String BUNDLE_KEY_CLICK_TIME = "click_time"; // 点击时间,做umeng统计用

    public static final String BUNDLE_KEY_SHOW_INVITE = "show_invite"; // 是否显示邀请好友按钮
    public final static String BUNDLE_KEY_CONTACTS = "contacts"; // 通讯录列表
    public static final String BUNDLE_KEY_EVENTINFO = "eventinfo"; // 活动详情

    public static final String BUNDLE_KEY_CATALOG = "catalog";
    public static final String BUNDLE_KEY_BLOG = "blog";
    public static final String BUNDLE_KEY_OWNER_ID = "owner_id";

    public static final String BUNDLE_KEY_COMMENT = "comment";
    public static final String BUNDLE_KEY_OPERATION = "operation";

    /*** 图片预览页用参数 **/
    public static final String BUNDLE_KEY_INDEX = "index";
    public static final String BUNDLE_KEY_IMAGES = "images";
    public static final String BUNDLE_KEY_FULLSCREEN = "fullscreen";

    /*** SimpleBack用参数 **/
    public final static String BUNDLE_KEY_PAGE = "bundle_key_page";
    public final static String BUNDLE_KEY_ARGS = "bundle_key_args";
    public final static String BUNDLE_KEY_POPUP = "bundle_key_popup";

    public static final String BUNDLE_KEY_OPENIDINFO = "openid_info";
    //    // 登陆实体类
    public static final String BUNDLE_KEY_LOGINBEAN = "loginbean";

    public final static String BUNDLE_KEY_STYLE = "style";
    public final static String BUNDLE_KEY_MARGIN = "margin";
    public final static String BUNDLE_KEY_COLOR = "color";

    public static final String BUNDLE_KEY_REQUEST_CODE = "request_code";

    public static final String BUNDLE_KEY_USERINFO = "userinfo";
}

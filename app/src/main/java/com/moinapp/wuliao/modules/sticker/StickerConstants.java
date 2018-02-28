package com.moinapp.wuliao.modules.sticker;

import com.keyboard.utils.Utils;

/**
 * Created by liujiancheng on 15/9/16.
 */
public class StickerConstants {
    /**
     * 贴纸模块的domain
     */
    public static final String STICKER_BASE_URL = "sticker/";
    public static final String COSPLAY_BASE_URL = "cosplay/";
    public static final String TAG_BASE_URL = "tag/";
    public static final String SYS_BASE_URL = "sys/";

    /**
     * 获取贴纸包列表请求的url
     */
    public static final String GET_STICKER_LIST_URL = STICKER_BASE_URL + "getlist";

    /**
     * 获取贴纸包详情请求的url
     */
//    public static final String GET_STICKER_DETAIL_URL = STICKER_BASE_URL + "getdetail";
    public static final String GET_STICKER_DETAIL_URL = STICKER_BASE_URL + "getGroupInfo";

    /**
     * 获取内置贴纸更新包请求的url
     */
    public static final String GET_STICKER_UPDATE_URL = STICKER_BASE_URL + "update";

    /**
     * 上传文件请求的url
     */
    public static final String UPLOAD_FILE_URL = STICKER_BASE_URL + "upload";

    /**
     * 发表编辑好的图片请求的url
     */
    public static final String SUBMIT_STICKER_EDIT_URL = COSPLAY_BASE_URL + "show";

    /**
     * 获取模板列表请求的url
     */
    public static final String GET_TEMPLATE_LIST_URL = STICKER_BASE_URL + "getTemplate";

    /**
     * 获取模板详情请求的url
     */
    public static final String GET_TEMPLATE_DETAIL_URL = STICKER_BASE_URL + "getTemplateDetail";

    /**
     * 下载贴纸包请求的url
     */
    public static final String DOWNLOAD_STICKER_URL = STICKER_BASE_URL + "download";

    /**
     * 获取用户获取已经下载的贴纸包请求的url
     */
    public static final String GET_MY_STICKER_URL = STICKER_BASE_URL + "mine";

    /**
     * 获取用户使用过的历史贴纸请求的url
     */
    public static final String GET_HISTORY_STICKER_URL = STICKER_BASE_URL + "historyList";

    /**
     * 删除贴纸包请求的url
     */
    public static final String DELETE_MY_STICKER_URL = STICKER_BASE_URL + "remove";

    /**
     * 获取贴纸商城的分类
     */
    public static final String GET_MALL_FOLDER_URL = STICKER_BASE_URL + "folderList";

    /**
     * 获取贴纸商城特定分类下的贴纸包列表
     */
    public static final String GET_STICKER_GROUP_LIST_URL = STICKER_BASE_URL + "groupList";

    /**
     * 获取单个贴纸的详情
     */
    public static final String GET_STICKER_INFO_URL = STICKER_BASE_URL + "getInfo";

    /**
     * 获取热词列表
     */
    public static final String GET_HOT_LIST_URL = TAG_BASE_URL + "hotList";

    /**
     * 搜索贴纸
     */
    public static final String SEARCH_STICKER_URL = STICKER_BASE_URL + "search";

    /**
     * 上传服务器列表
     */
    public static final String GET_UPLOAD_SERVER_URL = SYS_BASE_URL + "serverList";

    /**
     * 连接oss的token
     */
    public static final String GET_OSS_TOKEN_URL = SYS_BASE_URL + "getOssputtoken";

    /**
     * 获取推荐的话题文字说明
     */
    public static final String GET_RECOMMEND_TEXT_URL = TAG_BASE_URL + "getText";

    public static final String IPNAME = "ipname";
    public static final String OPNAME = "opname";
    public static final String NAME = "name";
    public static final String TAG = "tag";
    public static final String LASTID = "lastid";
    public static final String STICKERID = "id";
    public static final String STICKER_ID = "stickerId";
    public static final String SHOW_DETAIL = "showDetail";
    public static final String TYPE = "type";
    public static final String UPDATE_TIME = "updatedAt";

    public static final String FILE = "file";
    public static final String PICTURE = "picture";
    public static final String PICTURELIST = "pictureList";
    public static final String STICKERS = "stickers";
    public static final String TOPICID = "tpid";
    public static final String AUDIO = "audio";
    public static final String PARENT = "parent";
    public static final String USERS = "users";
    public static final String READ_AUTH = "readAuth";
    public static final String WRITE_AUTH = "writeAuth";
    public static final String CONTENT = "content";
    public static final String TOPICNAME = "topicName";

    public static final String TEMPLATE_ID = "id";
    public static final String ID = "id";
    public static final String PAGE_NUMBER = "pageNum";
    public static final String UCID = "ucid";
    public static final String BUCKET = "bucket";

    public static final int FLAG_NORMAL = 0;
    public static final int FLAG_UPDATE = 1;
    public static final int FLAG_VIEWED_UPDATED = 2;// 有更新,不过已看了详情
    public static final int FLAG_UNUPDATED = 3;// 有更新,未更新

    public static final int STICKER_NORMAL = 1;// 普通贴纸（常规的IP贴纸包，用户预置或者下载后使用）
    public static final int STICKER_RECOMMEND = 2;// 推荐贴纸（对应客户端的推荐贴纸栏目，需要检查更新）
    public static final int STICKER_TEXT_PATTERN = 3;// 文字图案（对应客户端的文字图案贴纸栏目，需要检查更新）
    public static final int STICKER_SPECIAL_SYMBOL = 4;// 特殊符号（对应客户端的特殊符号栏目，需要检查更新）
    public static final int STICKER_TEXT_BUBBLE = 5;// 文字气泡（对应客户端的文字气泡栏目，包含的贴纸拥有共同的IP名称文字气泡，需要检查更新）
    public static final int STICKER_FRAME = 6;// 边框（对应客户端的边框栏目，包含的贴纸拥有共同的IP名称边框，需要检查更新）
    public static final int STICKER_AUDIO = 7;// 声音（对应客户端的声音栏目，需要检查更新）
    public static final int STICKER_COLOR_TEXT = 8;// 彩色文字（对应客户端的彩色文字栏目）
    public static final int STICKER_RECENT = 100;// 最近贴纸（对应客户端的最近贴纸栏目）

    /** -1表示贴纸包已经下架*/
    public static final int FLAG_INVALID = -1;

    /** 100表示最近贴纸包的类型*/
    public static final int RECENTLY_STICKER_MAX_COUNT = 48;
    public static final int STICKER_TYPE_RECENTLY = Utils.STICKER_TYPE_RECENTLY;
    /** 1表示[商城内]普通贴纸包的类型*/
    public static final int STICKER_TYPE_NORMAL = 1;
    /** 200表示话题贴纸包的类型*/
    public static final int STICKER_TYPE_TOPIC = Utils.STICKER_TYPE_TOPIC;

    public static final String STICKER_DETAIL_CACHE_PREFIX = "download_sticker_detail";

    public static final String DEFAULT_UPLOAD_OSS_DOMAIN = "oss-cn-beijing.aliyuncs.com";
    public static final String DEFAULT_UPLOAD_OSS_BUCKET = "moon-image";

    /** 3.2.6版本以前的推荐贴纸包ID */
    public static final String INTIME_STICKER_ID = "563b0f510cf2361f2e3dd1ef";
}

package com.moinapp.wuliao.modules.discovery;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class DiscoveryConstants {
    /**
     * 关注和发现频道的domain
     */
    public static final String DISCOVERY_BASE_URL = "cosplay/";
    public static final String TAG_BASE_URL = "tag/";

    /**
     * 获取大咖秀图片请求的url
     */
    public static final String GET_COSPLAY_URL = DISCOVERY_BASE_URL + "getCosplay";

    /**
     * 获取大咖秀点赞列表请求的url
     */
    public static final String GET_LIKE_COSPLAY_URL = DISCOVERY_BASE_URL + "getLikeList";

    /**
     * 获取图片转改列表请求的url
     */
    public static final String GET_COSPLAY_FAMILY_URL = DISCOVERY_BASE_URL + "getFamily";

    /**
     * 获取可能喜欢的图片请求的url
     */
    public static final String GET_GUESS_LIKE_URL = DISCOVERY_BASE_URL + "guessLike";

    /**
     * 转发图片请求的url
     */
    public static final String FORWARD_COSPLAY_URL = DISCOVERY_BASE_URL + "forward";

    /**
     * 给发表的图片点赞请求的url
     */
    public static final String LIKE_COSPLAY_URL = DISCOVERY_BASE_URL + "like";

    /**
     * 给话题点赞请求的url
     */
    public static final String LIKE_TOPIC_URL = TAG_BASE_URL + "like";

    /**
     * 评论图片请求的url
     */
    public static final String COMMENT_COSPLAY_URL = DISCOVERY_BASE_URL + "comment";

    /**
     * 删除评论请求的url
     */
    public static final String DELETE_COMMENT_URL = DISCOVERY_BASE_URL + "deleteComment";

    /**
     * 获取评论列表请求的url
     */
    public static final String GET_COSPLAY_COMMENT_URL = DISCOVERY_BASE_URL + "getComment";

    /**
     * 获取热门标签列表请求的url
     */
    public static final String GET_HOT_TAG_URL = TAG_BASE_URL + "hotList";

    /**
     * 获取发现图片列表请求的url
     */
    public static final String GET_HOT_COSPLAY_URL = DISCOVERY_BASE_URL + "basicHotList";

    /**
     * 获取关注的图片列表请求的url
     */
    public static final String GET_IDOL_COSPLAY_URL = DISCOVERY_BASE_URL + "idolList";

    /**
     * 关注标签请求的url
     */
    public static final String FOLLOW_TAG_URL = TAG_BASE_URL + "follow";

    /**
     * 获取热门标签的内容请求的url
     */
    public static final String GET_TAG_DETAIL_URL = TAG_BASE_URL + "list";

    /**
     * 获取热门标签的内容请求的url
     */
    public static final String GET_FOLLOW_TAG_URL = TAG_BASE_URL + "idolList";

    /**
     * 改图转发请求的url
     */
    public static final String UPDATE_COSPLAY_URL = DISCOVERY_BASE_URL + "update";

    /**
     * 删除大咖秀图片请求的url
     */
    public static final String DELETE_COSPLAY_URL = DISCOVERY_BASE_URL + "deleteCosplay";

    /**
     * 搜索标签请求的url
     */
    public static final String SEARCH_TAG_URL = TAG_BASE_URL + "searchTag";

    /**
     * 获取banner请求的url
     */
    public static final String GET_BANNER_URL = DISCOVERY_BASE_URL + "getBanner";

    /**
     * 获取话题列表请求的url
     */
    public static final String GET_TOPIC_URL = TAG_BASE_URL + "topicList";

    /**
     * 获取话题参与的用户列表请求的url
     */
    public static final String GET_TOPIC_USER_URL = TAG_BASE_URL + "topicUserList";

    /**
     * 获取浏览大咖秀图片的用户列表请求的url
     */
    public static final String GET_VIEW_USER_URL = DISCOVERY_BASE_URL + "viewUserList";

    /**
     * 获取话题详情请求的url
     */
    public static final String GET_TOPIC_DETAIL_URL = TAG_BASE_URL + "topicDetail";

    /**
     * 获取话题下图片和帖子专区请求的url
     */
    public static final String GET_TOPIC_COSPLAY_URL = TAG_BASE_URL + "postList";

    /**
     * 获取话题分类标签请求的url
     */
    public static final String GET_TOPIC_CATEGORY_URL = TAG_BASE_URL + "topicCategoryList";

    /**
     * 搜索话题请求的url
     */
    public static final String SEARCH_TOPIC_URL = TAG_BASE_URL + "searchTopic";

    public static final String USERID = "userid";
    public static final String USERS = "users";
    public static final String UCID = "ucid";
    public static final String CID = "cid";
    public static final String IPNAME = "ipName";
    public static final String OPNAME = "opName";
    public static final String OPTAG = "optag";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String TAG = "tag";
    public static final String LASTID = "lastid";
    public static final String CONTENT = "content";
    public static final String REPLYID = "replyid";
    public static final String META = "meta";
    public static final String ACTION = "action";
    public static final String CATEGORY = "category";
    public static final String CATEGORYNAME = "categoryName";
    public static final String PAGE = "page";
    public static final String PAGENUM = "pageNum";
    public static final String COSPLAY_INFO = "cosplayinfo";
    public static final String FROM = "from";
    public static final String KEYWORD = "keyword";
    public static final String BANNER_INFO = "banner_info";
    public static final String TOPIC_ID = "topicid";
    public static final String TOPIC_NAME = "topicName";
    public static final String COSPLAY_LIKE = "cosplayLike";
    public static final String TOPIC_LIKE = "topicLike";
    public static final String PICTURE = "picture";
    public static final String ISLIKE = "isLike";

    /**
     * 大咖秀效果图
     */
    public static final int TYPE_XGT = 1;
    /**
     * 大咖秀工程文件
     */
    public static final int TYPE_PRJ = 2;
    /**
     * 贴纸音频文件
     */
    public static final int TYPE_AUDIO = 3;
    /**
     * 贴纸图片
     */
    public static final int TYPE_PHOTO = 4;
}

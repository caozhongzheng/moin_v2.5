package com.moinapp.wuliao.modules.discovery;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;
import com.moinapp.wuliao.modules.discovery.model.LikeCosplay;
import com.moinapp.wuliao.modules.discovery.model.LikeTopic;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class DiscoveryApi {
    /**
     * 获取发表的大咖秀
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param isLike: 是否下发猜你喜欢图片列表，可选 0 不下发，1 下发*
     * @param handler: callback
     */
    public static void getCosplay(String ucid, int isLike, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        params.put(DiscoveryConstants.ISLIKE, isLike);
        ApiHttpClient.post(DiscoveryConstants.GET_COSPLAY_URL, params, handler);
    }

    /**
     * 获取给大咖秀点赞的用户列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param lastid: 最后一个用户的uid，用于分页，选填
     * @param handler: callback
     */
    public static void getLikeList(String ucid, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_LIKE_COSPLAY_URL, params, handler);
    }

    /**
     * 获取图片相关图片，包括原创、前作和转发的图片列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param handler: callback
     */
    public static void getGuessLikeCosplay(String ucid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        ApiHttpClient.post(DiscoveryConstants.GET_GUESS_LIKE_URL, params, handler);
    }

    /**
     * 获取图片相关图片，包括原创、前作和转发的图片列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param handler: callback
     */
    public static void getCosplayFamily(String ucid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        ApiHttpClient.post(DiscoveryConstants.GET_COSPLAY_FAMILY_URL, params, handler);
    }

    /**
     * 转发图片（没有修改，直接发送）
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param content: 用户发表的评论信息，选填
     * @param users: @的好友列表,用逗号分割，"55f12e1498f3bc8177b232b9, 55f12e1498f3bc8177b232b8" 选填*
     * @param handler: callback
     */
    public static void forwardCosplay(String ucid, String content, String users, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        params.put(DiscoveryConstants.CONTENT, content);
        params.put(DiscoveryConstants.USERS, users);
        ApiHttpClient.post(DiscoveryConstants.FORWARD_COSPLAY_URL, params, handler);
    }

    /**
     * 给发表的图片点赞
     //     * @param ucid: 用户发布的大咖秀信息ID，必填项
     //     * @param action: 0 取消 1 点赞
     * @param cosplayLike: 增加点赞数量LikeCosplay对象
     * @param handler: callback
     */
    public static void likeCosplay(List<LikeCosplay> cosplayLike, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.COSPLAY_LIKE, (new Gson().toJson(cosplayLike)));
        ApiHttpClient.post(DiscoveryConstants.LIKE_COSPLAY_URL, params, handler);
    }

    /**
     * 评论图片
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param meta: 摘要信息，可选
     * @param content: 评论内容，必填项
     * @param replyid: 回复用户的ID,可选*
     * @param picture: 增加评论图片（图片url地址）,可选*
     * @param handler: callback
     */
    public static void commentCosplay(String ucid, String meta, String content, String replyid,
                                      String picture, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        params.put(DiscoveryConstants.META, meta);
        params.put(DiscoveryConstants.CONTENT, content);
        params.put(DiscoveryConstants.REPLYID, replyid);
        params.put(DiscoveryConstants.PICTURE, picture);
        ApiHttpClient.post(DiscoveryConstants.COMMENT_COSPLAY_URL, params, handler);
    }

    /**
     * 删除评论
     * @param cid: 评论的ID，必填
     * @param handler: callback
     */
    public static void deleteComment(String cid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.CID, cid);
        ApiHttpClient.post(DiscoveryConstants.DELETE_COMMENT_URL, params, handler);
    }

    /**
     * 获取大咖秀评论列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param lastid: 最后一个评论的ID，用于分页，选填
     * @param handler: callback
     */
    public static void getCommentList(String ucid, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_COSPLAY_COMMENT_URL, params, handler);
    }

    /**
     * 获取热门标签
     * @param handler: callback
     */
//    public static void getHotTag(AsyncHttpResponseHandler handler) {
//        ApiHttpClient.post(DiscoveryConstants.GET_HOT_TAG_URL, null, handler);
//    }

    /**
     * 获取发现频道(首页)的图片列表
     * @param lastid: 最后一个大咖秀图片的ID，用于分页，选填
     * @param pageNum: 每页显示数量，默认为20，可选
     * @param handler: callback
     */
    public static void getHotList(String lastid, int pageNum, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (pageNum > 0) {
            params.put(DiscoveryConstants.PAGENUM, String.valueOf(pageNum));
        }
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_HOT_COSPLAY_URL, params, handler);
    }

    /**
     * 获取关注的图片列表
     * @param lastid: 最后一个评论的ID，用于分页，选填
     * @param type: 图片类型信息，1 大咖秀图 2 图片帖 3 文字帖 4 音频帖，默认为1
     * @param handler: callback
     */
    public static void getIdolCosplay(int type, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.LASTID, lastid);
        params.put(DiscoveryConstants.TYPE, type);
        ApiHttpClient.post(DiscoveryConstants.GET_IDOL_COSPLAY_URL, params, handler);
    }

    /**
     * 关注标签
     * @param name: 标签名称，必填项
     * @param type: 标签类型，必填项
     * @param action：1 关注 0 取消关注
     * @param handler: callback
     */
    public static void followTag(String name, String type, int action, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.NAME, name);
        params.put(DiscoveryConstants.TYPE, type);
        params.put(DiscoveryConstants.ACTION, String.valueOf(action));
        ApiHttpClient.post(DiscoveryConstants.FOLLOW_TAG_URL, params, handler);
    }

    /**
     * 获取热门标签的内容: 返回的是标签相关图片和表情的列表
     * @param tag: 标签名称
     * @param type: 标签类型: ip op tp等
     * @param category：分类标识 1 图片 2 表情，必填项
     * @param lastid：
     * @param handler: callback
     */
    public static void getTagDetail(String tag, String type, int category, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.NAME, tag);
        params.put(DiscoveryConstants.TYPE, type);
        params.put(DiscoveryConstants.CATEGORY, String.valueOf(category));
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_TAG_DETAIL_URL, params, handler);
    }

    /**
     * 获取关注的标签
     * @param uid: 用户ID，如果为空则使用当前登录的用户ID
     * @param handler: callback
     */
    public static void getFollowTags(String uid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.USERID, uid);
        ApiHttpClient.post(DiscoveryConstants.GET_FOLLOW_TAG_URL, params, handler);
    }

    /**
     * 改图转发图片:获取被改图的工程文件信息
     * @param ucid: 用户发布的图片信息ID，必填项
     * @param handler: callback
     */
    public static void updateCosplay(String ucid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        ApiHttpClient.post(DiscoveryConstants.UPDATE_COSPLAY_URL, params, handler);
    }

    /**
     * 用户删除自己的大咖秀图片
     * @param ucid: 用户发布的图片信息ID，必填项
     * @param handler: callback
     */
    public static void deleteCosplay(String ucid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.UCID, ucid);
        ApiHttpClient.post(DiscoveryConstants.DELETE_COSPLAY_URL, params, handler);
    }

    /**
     * 搜索标签
     * @param keyword: 搜索的关键词，必填项
     * @param lastid: 选填项
     * @param handler: callback
     */
    public static void searchTag(String keyword, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.KEYWORD, keyword);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.SEARCH_TAG_URL, params, handler);
    }

    /**
     * 获取发现页面的banner
     * @param handler: callback
     */
    public static void getBanner(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(DiscoveryConstants.GET_BANNER_URL, null, handler);
    }

    /**
     * 获取话题列表
     * @param pageNum: 每一页的个数，必填项
     * @param lastid: 选填项
     * @param handler: callback
     */
    public static void getTopicList(int pageNum, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.PAGENUM, pageNum);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_TOPIC_URL, params, handler);
    }

    /**
     * 获取话题参与的用户列表
     * @param pageNum: 每一页的个数，必填项
     * @param lastid: 选填项*
     * @param topicid: 话题id
     * @param handler: callback
     */
    public static void getTopicUserList(int pageNum, String lastid, String topicid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.PAGENUM, pageNum);
        params.put(DiscoveryConstants.LASTID, lastid);
        params.put(DiscoveryConstants.TOPIC_ID, topicid);
        ApiHttpClient.post(DiscoveryConstants.GET_TOPIC_USER_URL, params, handler);
    }

    /**
     * 获取浏览用户列表
     * @param pageNum: 每一页的个数，必填项
     * @param lastid: 选填项*
     * @param ucid: 大咖秀图片的id
     * @param handler: callback
     */
    public static void getViewUserList(int pageNum, String lastid, String ucid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.PAGENUM, pageNum);
        params.put(DiscoveryConstants.LASTID, lastid);
        params.put(DiscoveryConstants.UCID, ucid);
        ApiHttpClient.post(DiscoveryConstants.GET_VIEW_USER_URL, params, handler);
    }

    /**
     * 获取话题详情
     * @param category: 分类标识 1 最热 2 最近，必填项
     * @param lastid: 选填项*
     * @param topicid: 话题id 和话题名称可以任意选填一个
     * @param topicName: id 话题名称可以任意选填一个
     * @param handler: callback
     */
    public static void getTopicDetail(int category, String topicid, String topicName, String lastid,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.CATEGORY, category);
        params.put(DiscoveryConstants.TOPIC_ID, topicid);
        params.put(DiscoveryConstants.TOPIC_NAME, topicName);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_TOPIC_DETAIL_URL, params, handler);
    }

    /**
     * 获取话题下图片和帖子专区
     * @param type: 需要列出的类型，如果包括多个类型，则使用','分隔的字符串。1 大咖秀图 2 图片帖 3 文字帖 4 音频帖，如进入帖子专区，则type = '2,3,4'，默认是全部内容，可选
     * @param topicid: 话题id
     * @param topicName: id 话题名称可以任意选填一个*
     * @param lastid: 选填项*
     * @param keyword: 关键词，用于搜索过滤，可选
     * @param handler: callback
     */
    public static void getTopicCosplay(String type, String topicid, String topicName, String keyword, String lastid,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.TYPE, type);
        params.put(DiscoveryConstants.TOPIC_ID, topicid);
        params.put(DiscoveryConstants.TOPIC_NAME, topicName);
        params.put(DiscoveryConstants.KEYWORD, keyword);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.GET_TOPIC_COSPLAY_URL, params, handler);
    }

    /**
     * 给话题点赞
     * @param topicLike: 增加点赞数量LikeTopic对象
     * @param handler: callback
     */
    public static void likeTopic(List<LikeTopic> topicLike, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.TOPIC_LIKE, (new Gson().toJson(topicLike)));
        ApiHttpClient.post(DiscoveryConstants.LIKE_TOPIC_URL, params, handler);
    }

    /**
     * 获取话题分类标签
     * @param handler: callback
     */
    public static void getTopicCategory(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        ApiHttpClient.post(DiscoveryConstants.GET_TOPIC_CATEGORY_URL, params, handler);
    }

    /**
     * 搜索标签
     * @param keyword: 搜索的关键词， 选填项
     * @param category: 分类信息, 选填项, 比如综艺 电影等等
     * @param lastid: 选填项
     * @param handler: callback
     */
    public static void searchTopic(String keyword, String category, String lastid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(DiscoveryConstants.KEYWORD, keyword);
        params.put(DiscoveryConstants.CATEGORYNAME, category);
        params.put(DiscoveryConstants.LASTID, lastid);
        ApiHttpClient.post(DiscoveryConstants.SEARCH_TOPIC_URL, params, handler);
    }
}

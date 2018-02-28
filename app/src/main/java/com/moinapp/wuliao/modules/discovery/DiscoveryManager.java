package com.moinapp.wuliao.modules.discovery;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.model.LikeCosplay;
import com.moinapp.wuliao.modules.discovery.model.LikeTopic;
import com.moinapp.wuliao.modules.discovery.result.CommentResult;
import com.moinapp.wuliao.modules.discovery.result.ForwardResult;
import com.moinapp.wuliao.modules.discovery.result.GetBannerResult;
import com.moinapp.wuliao.modules.discovery.result.GetCommentResult;
import com.moinapp.wuliao.modules.discovery.result.GetCosplayFamilyResult;
import com.moinapp.wuliao.modules.discovery.result.GetCosplayResult;
import com.moinapp.wuliao.modules.discovery.result.GetDiscoveryItemResult;
import com.moinapp.wuliao.modules.discovery.result.GetEmojiListResult;
import com.moinapp.wuliao.modules.discovery.result.GetFollowTagResult;
import com.moinapp.wuliao.modules.discovery.result.GetGuessLikeResult;
import com.moinapp.wuliao.modules.discovery.result.GetLikeListResult;
import com.moinapp.wuliao.modules.discovery.result.GetTagListResult;
import com.moinapp.wuliao.modules.discovery.result.GetTopicCategoryhResult;
import com.moinapp.wuliao.modules.discovery.result.GetTopicCosplayResult;
import com.moinapp.wuliao.modules.discovery.result.GetTopicDetailResult;
import com.moinapp.wuliao.modules.discovery.result.GetTopicListResult;
import com.moinapp.wuliao.modules.discovery.result.GetTopicUserListResult;
import com.moinapp.wuliao.modules.discovery.result.LikeResult;
import com.moinapp.wuliao.modules.discovery.result.SearchTagResult;
import com.moinapp.wuliao.modules.discovery.result.SearchTopicResult;
import com.moinapp.wuliao.modules.discovery.result.UpdateCosplayResult;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

import java.io.File;
import java.util.List;

/**
 * Created by liujiancheng on 15/10/8.
 */
public class DiscoveryManager {
    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger("DiscoveryManager");
    private static DiscoveryManager mInstance;

    public static int COMMENT_FROM_COSPLAY_DETAIL = 1;
    public static int COMMENT_FROM_COMMENT_LIST = 2;
    public static int COMMENT_FROM_CHAT = 3;
    private int mCommentFlag;
    // ===========================================================
    // Constructors
    // ===========================================================
    private DiscoveryManager() {
    }

    public static synchronized DiscoveryManager getInstance() {
        if (mInstance == null) {
            mInstance = new DiscoveryManager();
        }

        return mInstance;
    }

    // ===========================================================
    // Interfaces & public methods
    // ===========================================================
    /**
     * 获取发表的大咖秀
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param isLike: 是否下发猜你喜欢图片列表，可选 0 不下发，1 下发
     * @param listener: callback
     */
    public void getCosplay(String ucid, int isLike, IListener listener) {
        DiscoveryApi.getCosplay(ucid, isLike, buildGetCosplayCallback(listener));
    }

    /**
     * 获取给大咖秀点赞的用户列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param lastid: 最后一个用户的uid，用于分页，选填
     * @param listener: callback
     */
    public void getLikeList(String ucid, String lastid, IListener listener) {
        DiscoveryApi.getLikeList(ucid, lastid, buildGetLikeListCallback(listener));
    }

    /**
     * 获取图片相关图片，包括原创、前作和转发的图片列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param listener: callback
     */
    public void getCosplayFamily(String ucid, IListener listener) {
        DiscoveryApi.getCosplayFamily(ucid, buildGetCosplayFamilyCallback(listener));
    }

    /**
     * 获取可能喜欢的图片
     * @param ucid: 大咖秀信息ID，必填项
     * @param listener: callback
     */
    public void getGuessLikeCosplay(String ucid, IListener listener) {
        DiscoveryApi.getGuessLikeCosplay(ucid, buildGetGuessLikeCallback(listener));
    }

    /**
     * 转发图片（没有修改，直接发送）
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param content: 用户发表的评论信息，选填
     * @param users: @的好友列表,用逗号分割，"55f12e1498f3bc8177b232b9, 55f12e1498f3bc8177b232b8" 选填
     * @param listener: callback
     */
    public void forwardCosplay(String ucid, String content, String users, IListener listener) {
        DiscoveryApi.forwardCosplay(ucid, content, users,buildForwardCallback(listener));
    }

    /**
     * 给发表的图片点赞
//     * @param ucid: 用户发布的大咖秀信息ID，必填项
//     * @param action: 0 取消 1 点赞
     * @param cosplayLike: 增加点赞数量LikeCosplay对象
     * @param listener: callback
     */
    public void likeCosplay(List<LikeCosplay> cosplayLike, IListener2 listener) {
        DiscoveryApi.likeCosplay(cosplayLike, buildLikeCallback(listener));
    }

    /**
     * 评论图片
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param meta: 摘要信息，可选
     * @param content: 评论内容，必填项
     * @param replyid: 回复用户的ID,可选
     * @param picture: 增加评论图片（图片url地址）,可选
     * @param listener: callback
     */
    public void commentCosplay(String ucid, String meta, String content, String replyid,
                               String picture, IListener2 listener) {
        DiscoveryApi.commentCosplay(ucid, meta, content, replyid, picture, buildCommentCallback(listener));
    }

    /**
     * 删除评论
     * @param cid: 评论的ID，必填
     * @param listener: callback
     */
    public void deleteComment(String cid, IListener listener) {
        DiscoveryApi.deleteComment(cid, buildCommonCallback(listener));
    }

    /**
     * 获取大咖秀评论列表
     * @param ucid: 用户发布的大咖秀信息ID，必填项
     * @param lastid: 最后一个评论的ID，用于分页，选填
     * @param listener: callback
     */
    public void getCommentList(String ucid, String lastid, IListener listener) {
        DiscoveryApi.getCommentList(ucid, lastid, buildGetCommentListCallback(listener));
    }

    /**
     * 获取热门标签
     * @param listener: callback
     */
//    public void getHotTag(IListener listener) {
//        DiscoveryApi.getHotTag(buildGetHotTagCallback(listener));
//    }

    /**
     * 获取发现频道(首页)的图片列表
     * @param lastid: 最后一个大咖秀图片的ID，用于分页，选填
     * @param pageNum: 每页显示数量，默认为20，可选
     * @param listener: callback
     */
    public void getHotList(String lastid, int pageNum, IListener listener) {
        DiscoveryApi.getHotList(lastid, pageNum, buildGetCosplayListCallback(listener));
    }

    /**
     * 获取关注的图片列表
     * @param lastid: 最后一个评论的ID，用于分页，选填
     * @param type: 图片类型信息，1 大咖秀图 2 图片帖 3 文字帖 4 音频帖，默认为1*
     * @param listener: callback
     */
    public void getIdolCosplay(int type, String lastid, IListener listener) {
        DiscoveryApi.getIdolCosplay(type, lastid, buildGetCosplayListCallback(listener));
    }

    /**
     * 关注标签
     * @param name: 标签名称，必填项
     * @param type: 标签类型，必填项
     * @param action：1 关注 0 取消关注
     * @param listener: callback
     */
    public void followTag(String name, String type, int action, IListener listener) {
        DiscoveryApi.followTag(name, type, action, buildCommonCallback(listener));
    }

    /**
     * 获取热门标签的内容: 返回的是标签相关图片和表情的列表
     * @param tag: 标签名称
     * @param type: 标签类型: ip op tp等
     * @param category：分类标识 1 图片 2 表情，必填项
     * @param lastid：
     * @param listener: callback
     */
    public void getTagCosplay(String tag, String type, int category, String lastid, IListener listener) {
        if (category == 1) {
            DiscoveryApi.getTagDetail(tag, type, category, lastid, buildGetCosplayListCallback(listener));
        } else if (category == 2) {
            DiscoveryApi.getTagDetail(tag, type, category, lastid, buildGetEmojiListCallback(listener));
        }
    }

    /**
     * 获取关注的标签
     * @param uid: 用户ID，如果为空则使用当前登录的用户ID
     * @param listener: callback
     */
    public void getFollowTags(String uid, IListener listener) {
        DiscoveryApi.getFollowTags(uid, buildGetFollowTagCallback(listener));
    }

    /**
     * 改图转发图片
     * @param ucid: 用户发布的图片信息ID，必填项
     * @param listener: callback
     */
    public void updateCosplay(String ucid, IListener listener) {
        DiscoveryApi.updateCosplay(ucid, buildUpdateCosplayCallback(listener));
    }

    /**
     * 用户删除自己的大咖秀图片
     * @param ucid: 用户发布的图片信息ID，必填项
     * @param listener: callback
     */
    public void deleteCosplay(String ucid, IListener listener) {
        DiscoveryApi.deleteCosplay(ucid, buildCommonCallback(listener));
    }

    /**
     * 搜索标签
     * @param keyword: 搜索的关键词，必填项
     * @param lastid: 选填项
     * @param listener: callback
     */
    public void searchTag(String keyword, String lastid, IListener listener) {
        DiscoveryApi.searchTag(keyword, lastid, buildSearchTagCallback(listener));
    }

    /**
     * 获取话题分类标签
     * @param listener: callback
     */
    public void getTopicCategory(IListener listener) {
        DiscoveryApi.getTopicCategory(buildGetTopicCategoryCallback(listener));
    }

    /**
     * 搜索话题
     * @param keyword: 搜索的关键词， 选填项
     * @param category: 分类信息, 选填项, 比如综艺 电影等等
     * @param lastid: 选填项
     * @param listener: callback
     */
    public void searchTopic(String keyword, String category, String lastid, IListener listener) {
        DiscoveryApi.searchTopic(keyword, category, lastid, buildSearchTopicCallback(listener));
    }

    /**
     * 获取发现页面的banner
     * @param listener: callback
     */
    public void getBanner(IListener listener) {
        DiscoveryApi.getBanner(buildGetBannerCallback(listener));
    }

    /**
     * 获取话题列表
     * @param pageNum: 每一页的个数，必填项
     * @param lastid: 选填项*
     * @param listener: callback
     */
    public void getTopicList(int pageNum, String lastid, IListener listener) {
        DiscoveryApi.getTopicList(pageNum, lastid, buildGetTopicCallback(listener));
    }

    /**
     * 获取话题参与的用户列表
     * @param pageNum: 每一页的个数，必填项
     * @param lastid: 选填项*
     * @param topicid: 话题id
     * @param listener: callback
     */
    public void getTopicUserList(int pageNum, String lastid, String topicid, IListener listener) {
        DiscoveryApi.getTopicUserList(pageNum, lastid, topicid, buildGetTopicUserCallback(listener));
    }

    /**
     * 获取话题详情
     * @param category: 1 下发最热图片 2 下发全部图片 3 下发图片和帖子混合
     * @param lastid: 选填项*
     * @param topicid: 话题id
     * @param topicName: id 话题名称可以任意选填一个*
     * @param listener: callback
     */
    public void getTopicDetail(int category, String topicid, String topicName, String lastid, IListener listener) {
        DiscoveryApi.getTopicDetail(category, topicid, topicName, lastid, buildGetTopicDetailCallback(listener));
    }

    /**
     * 获取话题下图片和帖子专区
     * @param type: 需要列出的类型，如果包括多个类型，则使用','分隔的字符串。1 大咖秀图 2 图片帖 3 文字帖 4 音频帖，如进入帖子专区，则type = '2,3,4'，默认是全部内容，可选
     * @param topicid: 话题id
     * @param topicName: id 话题名称可以任意选填一个*
     * @param lastid: 选填项*
     * @param keyword: 关键词，用于搜索过滤，可选
     * @param listener: callback
     */
    public void getTopicCosplay(String type, String topicid, String topicName, String keyword, String lastid, IListener listener) {
        DiscoveryApi.getTopicCosplay(type, topicid, topicName, keyword, lastid, buildGetTopicCosplayCallback(listener));
    }

    /**
     * 给话题点赞
     * @param topicLike: 增加点赞数量LikeTopic对象
     * @param listener: callback
     */
    public void likeTopic(List<LikeTopic> topicLike, IListener2 listener) {
        DiscoveryApi.likeTopic(topicLike, buildLikeCallback(listener));
    }


    /**
     * 获取浏览用户列表
     * @param pageNum: 每一页的个数，必填项
     * @param lastid: 选填项*
     * @param ucid: 大咖秀图片的id
     * @param listener: callback
     */
    public void getViewUserList(int pageNum, String lastid, String ucid, IListener listener) {
        DiscoveryApi.getViewUserList(pageNum, lastid, ucid, buildGetTopicUserCallback(listener));
    }

    public static class CosplayDeleteEvent {
        public CosplayDeleteEvent(String ucid) {
            mUcid = ucid;
        }

        private static String mUcid;

        public String getUcid() {
            return mUcid;
        }
    }
    // ===========================================================
    // Inner class & private methods
    // ===========================================================
    private AsyncHttpResponseHandler buildGetCosplayCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetCosplayResult result = XmlUtils.JsontoBean(GetCosplayResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getCosplay());
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

    private AsyncHttpResponseHandler buildGetLikeListCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetLikeListResult result = XmlUtils.JsontoBean(GetLikeListResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
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

    private AsyncHttpResponseHandler buildGetCosplayFamilyCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("GetCosplayFamily onSucceed: response = " + new String(responseBody));
                GetCosplayFamilyResult result = XmlUtils.JsontoBean(GetCosplayFamilyResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
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

    private AsyncHttpResponseHandler buildGetGuessLikeCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildGetGuessLikeCallback onSucceed: response = " + new String(responseBody));
                GetGuessLikeResult result = XmlUtils.JsontoBean(GetGuessLikeResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getCosplayList());
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

    private AsyncHttpResponseHandler buildForwardCallback(IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                ForwardResult result = XmlUtils.JsontoBean(ForwardResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getUcid());
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

            @Override
            public void onStart() {
                super.onStart();
                if(listener instanceof IListener2) {
                    ((IListener2) listener).onStart();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if(listener instanceof IListener2) {
                    ((IListener2) listener).onFinish();
                }
            }
        };
    }

    private AsyncHttpResponseHandler buildCommentCallback(final IListener2 listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                CommentResult result = XmlUtils.JsontoBean(CommentResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getCid());

                    // 评论成功,如果获得魔豆则显示魔豆页面
                    if (result.getMoinBean() != null && result.getMoinBean().getTotalBean() > 0
                            && result.getMoinBean().getObtainBean() > 0) {
                        UIHelper.showMoinBeanActivity(result.getMoinBean(), MissionConstants.MISSION_COMMENT);
                    }
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure");
                listener.onNoNetwork();
            }

            @Override
            public void onFinish() {
                listener.onFinish();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetCommentListCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                GetCommentResult result = XmlUtils.JsontoBean(GetCommentResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getComments());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure");
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetHotTagCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                GetTagListResult result = XmlUtils.JsontoBean(GetTagListResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getTags());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure");
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetCosplayListCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("GetCosplayList:onSuccess: response =" + new String(responseBody));
                GetDiscoveryItemResult result = XmlUtils.JsontoBean(GetDiscoveryItemResult.class, responseBody);
                if (result != null ) {
                    //由于首页需要isLast字段,所以将返回值由List改为GetDiscoveryItemResult
                    listener.onSuccess(result);
                } else {
                    listener.onErr(null);
                }
//                if (result != null && result.getResult() > 0) {
//                    listener.onSuccess(result.getList());
//                } else {
//                    listener.onErr(null);
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("GetCosplayList :onFailure, statuscode=" + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetEmojiListCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                GetEmojiListResult result = XmlUtils.JsontoBean(GetEmojiListResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getEmojiList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetFollowTagCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                GetFollowTagResult result = XmlUtils.JsontoBean(GetFollowTagResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getTags());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildUpdateCosplayCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                UpdateCosplayResult result = XmlUtils.JsontoBean(UpdateCosplayResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getFile());
                } else {
                    listener.onErr(result.getError());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildSearchTagCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                SearchTagResult result = XmlUtils.JsontoBean(SearchTagResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    if (result.getTags() != null) {
                        MyLog.i("tags size =" + result.getTags().size());
                    }
                    listener.onSuccess(result.getTags());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildSearchTopicCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                SearchTopicResult result = XmlUtils.JsontoBean(SearchTopicResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    if (result.getTopicList() != null) {
                        MyLog.i("tags size =" + result.getTopicList().size());
                    }
                    listener.onSuccess(result.getTopicList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetBannerCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildGetBannerCallback:onSuccess: response =" + new String(responseBody));
                GetBannerResult result = XmlUtils.JsontoBean(GetBannerResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    if (result.getBanners() != null) {
                        MyLog.i("banners size =" + result.getBanners().size());
                    }
                    listener.onSuccess(result.getBanners());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildGetBannerCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetTopicCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildGetTopicCallback:onSuccess: response =" + new String(responseBody));
                GetTopicListResult result = XmlUtils.JsontoBean(GetTopicListResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    if (result.getTopicList() != null) {
                        MyLog.i("topic size =" + result.getTopicList().size());
                    }
                    listener.onSuccess(result.getTopicList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildGetTopicCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetTopicUserCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildGetTopicCallback:onSuccess: response =" + new String(responseBody));
                GetTopicUserListResult result = XmlUtils.JsontoBean(GetTopicUserListResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    if (result.getUsers() != null) {
                        MyLog.i("topic user size =" + result.getUsers().size());
                    }
                    listener.onSuccess(result.getUsers());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildGetTopicCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetTopicDetailCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildGetTopicDetailCallback:onSuccess: response =" + new String(responseBody));
                GetTopicDetailResult result = XmlUtils.JsontoBean(GetTopicDetailResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result);
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildGetTopicDetailCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetTopicCosplayCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildGetTopicCosplayCallback:onSuccess: response =" + new String(responseBody));
                GetTopicCosplayResult result = XmlUtils.JsontoBean(GetTopicCosplayResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getPostList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildGetTopicCosplayCallback:onFailure: status = " + statusCode);
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildLikeCallback(final IListener2 listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                LikeResult result = XmlUtils.JsontoBean(LikeResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getMoinBean());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure");
                listener.onNoNetwork();
            }

            @Override
            public void onFinish() {
                listener.onFinish();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetTopicCategoryCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildCommonCallback:onSuccess: response =" + new String(responseBody));
                GetTopicCategoryhResult result = XmlUtils.JsontoBean(GetTopicCategoryhResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getCategorys());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildCommonCallback:onFailure");
                listener.onNoNetwork();
            }
        };
    }

    public int getCommentFlag() {
        return mCommentFlag;
    }
    public void setCommentFlag(int flag) {
        mCommentFlag = flag;
    }

    private String DISCOVERY_LOAD_INFO_PATH = BitmapUtil.BITMAP_CACHE;
    private String DISCOVERY_LOAD_INFO_FILE = DISCOVERY_LOAD_INFO_PATH + "discoveryload.log";

    // 把一些数据加载的时间信息写入sd卡
    public void writeDiscoveryLoadInfo2File(String info) {
        File file = new File(DISCOVERY_LOAD_INFO_PATH);
        if (!file.exists())
            file.mkdirs();
        FileUtil.writeFile(info, DISCOVERY_LOAD_INFO_FILE);
    }

    // 删除时间信息记录的文件
    public void removeDiscoveryLoadInfo() {
        File file = new File(DISCOVERY_LOAD_INFO_FILE);
        if (file.exists())
            file.delete();
    }
}

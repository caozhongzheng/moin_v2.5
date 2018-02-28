package com.moinapp.wuliao.modules.sticker;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moinapp.wuliao.api.ApiHttpClient;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by liujiancheng on 15/9/16.
 */
public class StickerApi {

    /**
     * 获取贴纸包详情
     * @param id: 贴纸包的id
     * @param handler
     */
    public static void getStickerDetail(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.STICKERID, id);
        ApiHttpClient.post(StickerConstants.GET_STICKER_DETAIL_URL, params, handler);
    }

    /**
     * 获取内置贴纸更新包
     * @param type: 内置贴纸包的类型，
     * @param updatedAt: 该贴纸包的上次更新时间，时间戳格式，如 1441877595486
     * @param id: 贴纸包的id, 可选项, 如果客户端要主动查询某个贴纸包的更新情况,可传入这个id*
     * @param handler
     */
    public static void getStickerUpdate(int type, long updatedAt, String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.TYPE, type);
        params.put(StickerConstants.UPDATE_TIME, updatedAt);
        params.put(StickerConstants.ID, id);
        ApiHttpClient.post(StickerConstants.GET_STICKER_UPDATE_URL, params, handler);
    }

    /**
     * 发表图片
     * @param type: 图片类型信息，1 大咖秀图 2 图片帖 3 文字帖 4 音频帖*
     * @param bucket: 选择上传的最快服务器对应的bucket名称
     * @param projectKey: 需要上传的工程文件的objectKey，图片key构造规则  "prjmi" ＋ "/" + md5.substring(10,13) + / + md5.substring(22,25) + / + md5 + ".moi"
     * @param xgtKey: 需要上传的图片文件objectKey，文件key构造规则  "image" + "/" + md5.substring(10,13) + "/" + md5.substring(22,25) + "/" + md5 + "." + 图片后缀小写
     * @param stickers:sticker的{id, parentid}集合
     * @param pictureList:包含的图片列表. 以逗号分割的字符串. (发布帖子时用到)*
     * @param audio:sticker的声音集合
     * @param parent:对应改图转发时的原图的ID，为空则说明是原创
     * @param users:发表图片的时候@的用户ID，以逗号','分割，如 55f12e1498f3bc8177b232b9, 55f12e1498f3bc8177b232b8
     * @param content:发布图片时的评论*
     * @param topicName:话题名称
     * @param readAuth:读权限 0 全否 1 特定 2 关注 3 粉丝 4 全部，默认为4
     * @param writeAuth:写权限 0 全否 1 特定 2 关注 3 粉丝 4 全部，默认为4
     * @param handler
     */
    public static void submitStickerPicture(int type, String bucket, String projectKey, String xgtKey,
                                            String stickers, String pictureList, String audio, String parent, String users,
                                            String content, String topicName, int readAuth, int writeAuth,
                                            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.TYPE, type);
        params.put(StickerConstants.BUCKET, bucket);
        params.put(StickerConstants.FILE, projectKey);
        params.put(StickerConstants.PICTURE, xgtKey);
        params.put(StickerConstants.STICKERS, stickers);
        params.put(StickerConstants.PICTURELIST, pictureList);
        params.put(StickerConstants.AUDIO, audio);
        params.put(StickerConstants.PARENT, parent);
        params.put(StickerConstants.USERS, users);
        params.put(StickerConstants.CONTENT, content);
        params.put(StickerConstants.TOPICNAME, topicName);
        params.put(StickerConstants.READ_AUTH, String.valueOf(readAuth));
        params.put(StickerConstants.WRITE_AUTH, String.valueOf(writeAuth));
        ApiHttpClient.post(StickerConstants.SUBMIT_STICKER_EDIT_URL, params, handler);
    }

    /**
     * 获取发布图片时的推荐的话题文字说明
     * @param tpid
     * @param stickers
     * @param handler
     */
    public static void getRecommendTtext(String tpid, String stickers, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.TOPICID, tpid);
        params.put(StickerConstants.STICKERS, stickers);
        ApiHttpClient.post(StickerConstants.GET_RECOMMEND_TEXT_URL, params, handler);
    }


    /**
     * 获取模板列表，返回最近更新的TOP 10的模板
     * @param handler
     */
    public static void getTemplateList(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(StickerConstants.GET_TEMPLATE_LIST_URL, null, handler);
    }

    /**
     * 获取模板详情
     * @id 模版ID
     * @param handler
     */
    public static void getTemplateDetail(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.TEMPLATE_ID, id);
        ApiHttpClient.post(StickerConstants.GET_TEMPLATE_DETAIL_URL, params, handler);
    }

    /**
     * 在商城中下载贴纸包
     * @id 贴纸包ID
     * @param stickerId: 贴纸id*
     * @param showDetail: 0 不显示详细 1 显示详细信息*
     * @param handler
     */
    public static void downloadSticker(String id, String stickerId, int showDetail, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.STICKERID, id);
        params.put(StickerConstants.STICKER_ID, stickerId);
        params.put(StickerConstants.SHOW_DETAIL, String.valueOf(showDetail));
        ApiHttpClient.post(StickerConstants.DOWNLOAD_STICKER_URL, params, handler);
    }

    /**
     * 获取已经下载贴纸包列表
     * @param ipname: IP属性的标签名称；
     * @param opname: 运营属性的标签名称；
     * @param tag: 表情关键字；
     * @param lastid: 最后一个贴纸包的id信息，用于分页；
     * @param handler
     */
    public static void getMyStickerList(String ipname, String opname, String tag, String lastid,
                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.IPNAME, ipname);
        params.put(StickerConstants.OPNAME, opname);
        params.put(StickerConstants.TAG, tag);
        params.put(StickerConstants.LASTID, lastid);
        ApiHttpClient.post(StickerConstants.GET_MY_STICKER_URL, params, handler);
    }

    /**
     * 删除已经下载贴纸包
     * @id 贴纸包ID
     * @param handler
     */
    public static void deleteSticker(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.STICKERID, id);
        ApiHttpClient.post(StickerConstants.DELETE_MY_STICKER_URL, params, handler);
    }

    /**
     *  获取我的历史贴纸
     * @param pageNum 每页数量，必填；
     * @param lastid: 最后一个贴纸包的id信息，用于分页；
     * @param handler
     */
    public static void getHistoryStickers(int pageNum,String lastid,
                                        AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.PAGE_NUMBER, pageNum);
        params.put(StickerConstants.LASTID, lastid);
        ApiHttpClient.post(StickerConstants.GET_HISTORY_STICKER_URL, params, handler);
    }

    /**
     *  获取贴纸商城的分类
     * @param handler
     */
    public static void getFolderList(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(StickerConstants.GET_MALL_FOLDER_URL, null, handler);
    }

    /**
     * 获取贴纸商城特定分类下的贴纸包列表
     * @param name: IP属性的标签名称；
     * @param type: 运营属性的标签名称；
     * @param lastid: 最后一个贴纸包的id信息，用于分页；；
     * @param handler
     */
    public static void getStickerGroupList(String name, String type, String lastid,
                                          AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.NAME, name);
        params.put(StickerConstants.TYPE, type);
        params.put(StickerConstants.LASTID, lastid);
        ApiHttpClient.post(StickerConstants.GET_STICKER_GROUP_LIST_URL, params, handler);
    }

    /**
     * 获取单张贴纸详情
     * @param stickerId: id
     * @param handler
     */
    public static void getStickerInfo(String stickerId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.STICKER_ID, stickerId);
        ApiHttpClient.post(StickerConstants.GET_STICKER_INFO_URL, params, handler);
    }

    /**
     * 获取热词列表
     * @param handler
     */
    public static void getHotList(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(StickerConstants.GET_HOT_LIST_URL, null, handler);
    }

    /**
     * 搜索贴纸
     * @param name: 搜索的名称, 必填
     * @param type: 类型，选填
     * @param lastid: 最后一项的标识stickerId，用于分页，选填；
     * @param handler
     */
    public static void searchSticker(String name, String type, String lastid,
                                           AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put(StickerConstants.NAME, name);
        params.put(StickerConstants.TYPE, type);
        params.put(StickerConstants.LASTID, lastid);
        ApiHttpClient.post(StickerConstants.SEARCH_STICKER_URL, params, handler);
    }

    /**
     *  获取文件上传服务器列表
     * @param handler
     */
    public static void getUploadServer(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(StickerConstants.GET_UPLOAD_SERVER_URL, null, handler);
    }

    /**
     *  获取文件上传所用token
     * @param handler
     */
    public static void getOssToken(AsyncHttpResponseHandler handler) {
        ApiHttpClient.post(StickerConstants.GET_OSS_TOKEN_URL, null, handler);
    }
}

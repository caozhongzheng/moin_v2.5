package com.moinapp.wuliao.modules.sticker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.keyboard.utils.DefEmoticons;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.sticker.model.DownloadStickerResult;
import com.moinapp.wuliao.modules.sticker.model.GetFolderResult;
import com.moinapp.wuliao.modules.sticker.model.GetHistoryStickersResult;
import com.moinapp.wuliao.modules.sticker.model.GetHotListResult;
import com.moinapp.wuliao.modules.sticker.model.GetOssTokenResult;
import com.moinapp.wuliao.modules.sticker.model.GetRecommendTextResult;
import com.moinapp.wuliao.modules.sticker.model.GetServerInfoResult;
import com.moinapp.wuliao.modules.sticker.model.GetStickerDetailResult;
import com.moinapp.wuliao.modules.sticker.model.GetStickerGroupListResult;
import com.moinapp.wuliao.modules.sticker.model.GetStickerInfoResult;
import com.moinapp.wuliao.modules.sticker.model.GetStickerListResult;
import com.moinapp.wuliao.modules.sticker.model.GetStickerUpdateResult;
import com.moinapp.wuliao.modules.sticker.model.OssToken;
import com.moinapp.wuliao.modules.sticker.model.ServerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerId;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.model.submitPictureResult;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.event.AddMySticker;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoProcessActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.modules.update.AbsManager;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liujiancheng on 15/9/16.
 */
public class StickerManager extends AbsManager {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final long STICKER_UPDATE_INTERVAL = 7 * 24 * 60 * 60 * 1000;
    public static final long STICKER_NEWST_INTERVAL = 1 * 24 * 60 * 60 * 1000;
    public static final long STICKER_FOLDERLIST_INTERVAL = 1 * 24 * 60 * 60 * 1000;
    public static final long GET_UPLOAD_SERVER_INTERVAL = 1 * 24 * 60 * 60 * 1000;
    public static final long ERROR_RESPONSE = 10000;
    public static final String KEY_USING_SINGLE_STICKER = "key_using_single_sticker";
    public static final String KEY_STICKMALL_FOLDERLIST = "key_stickermall_folderlist";
    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger("StickerManager");
    private static StickerManager mInstance;
    private Gson mGson = new Gson();
    // ===========================================================
    // Constructors
    // ===========================================================
    private StickerManager() {
    }

    public static synchronized StickerManager getInstance() {
        if (mInstance == null) {
            mInstance = new StickerManager();
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
     * 获取贴纸包详情
     * @param id: 贴纸包的id；
     * @param listener: callback
     */
    public void getStickerDetail(String id, IListener listener) {
        StickerApi.getStickerDetail(id, buildGetStickerDetailCallback(listener));
    }

    /**
     * 获取内置贴纸更新包
     * @param type: 内置贴纸包的类型；
     *  1 正常（常规的IP贴纸包，用户预置或者下载后使用）
     *  2 限时贴纸（对应客户端的限时贴纸栏目，需要检查更新）
     *  3 文字图案（对应客户端的文字图案贴纸栏目，需要检查更新）
     *  4 特殊符号（对应客户端的特殊符号栏目，需要检查更新）
     *  5 文字气泡（对应客户端的文字气泡栏目，包含的贴纸拥有共同的IP名称文字气泡，需要检查更新）
     *  6 边框（对应客户端的边框栏目，包含的贴纸拥有共同的IP名称边框，需要检查更新
     * @param updatedAt: 该贴纸包的上次更新时间，时间戳格式，如 1441877595486
     * @param id: 贴纸包的id, 可选项, 如果客户端要主动查询某个贴纸包的更新情况,可传入这个id
     * @param listener: callback
     */
    public void getStickerUpdate(int type, long updatedAt, String id, IListener listener) {
        StickerApi.getStickerUpdate(type, updatedAt, id, buildGetStickerUpdateCallback(listener));
    }

    // TODO 需要更新贴纸时,在异步任务AsyncTask中调用该方法
    public void checkUpdate(String uid, int type, String packageId) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        ArrayList<EmoticonSetBean> setBeanList;
        if (type == StickerPackage.STICKER_INTIME) {
            setBeanList = dbHelper.queryEmoticonSetByType(uid, type);
        } else {
            setBeanList = dbHelper.queryEmoticonSetByID(uid, packageId);
        }
        if(setBeanList == null || setBeanList.isEmpty()) {
            return;
        }
        long updatedAt = setBeanList.get(0).getUpdateTime();
        long currentTime = System.currentTimeMillis();
        getStickerUpdate(type, updatedAt, packageId, new IListener() {
            @Override
            public void onSuccess(Object obj) {
//                StickerPackage sticker = (StickerPackage) obj;
//                if (sticker == null) {
//                    return;
//                }

                //2.7后主动检查后只记录检查时间,不做具体更新贴纸的逻辑
                if (type == StickerPackage.STICKER_INTIME) {
                    dbHelper.updateEmoticonSetLastCheck(type, currentTime);
                    dbHelper.updateEmoticonSetFlag(type, obj == null ? StickerConstants.FLAG_NORMAL : StickerConstants.FLAG_UNUPDATED);
                } else if (!TextUtils.isEmpty(packageId)) {
                    dbHelper.updateEmoticonSetLastCheck(packageId, currentTime);
                    dbHelper.updateEmoticonSetFlag(packageId, obj == null ? StickerConstants.FLAG_NORMAL : StickerConstants.FLAG_UNUPDATED);
                }
            }

            @Override
            public void onErr(Object obj) {
                // TODO 更新失败或者无网络时需要添加个标志啦,然后在贴纸制作activity中检查是否有更新没做,继续做
                if (obj != null) {
                    int error = (int) obj;
                    // 如果error=-30说明贴纸包已经下架
                    if (error == ErrorCode.ERROR_INVALID_STICKER_PAC) {
                        dbHelper.updateEmoticonSetFlag(packageId, StickerConstants.FLAG_INVALID);
                    }
//                    if (type == StickerPackage.STICKER_INTIME) {
//                        dbHelper.updateEmoticonSetLastCheck(type, currentTime);
//                    } else if (!TextUtils.isEmpty(packageId)){
//                        dbHelper.updateEmoticonSetLastCheck(packageId, currentTime);
//                    }
                }
            }

            @Override
            public void onNoNetwork() {
            }
        });

    }

    private void updateSticker(int type, ArrayList<EmoticonSetBean> setBeanList, StickerPackage sticker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmoticonSetBean setBean = setBeanList.get(0);
//                if(sticker.getIcon() != null) {
//                    if(!setBean.getIconUrl().equals(sticker.getIcon().getUri())) {
//                        // 更新贴纸包的icon
//                        Bundle result = downloadStickerIcon(sticker, setBean);
//                        if(result != null) {
//                            saveStickers2DB(type, sticker, result.getString(KEY_URL), result.getString(KEY_PATH));
////                    updateDBSticker(type, result, setBean);
//                            // TODO 需要重置贴纸更新标志
//                        }
//                    }
//                }
                // 更新贴纸包内的贴纸
                downloadStickerList(sticker, setBean);
                Bundle result = downloadStickerIcon(sticker, setBean);
                if(result != null) {
                    saveStickers2DB(type, sticker, result.getString(KEY_URL), result.getString(KEY_PATH));
                }
            }
        }).start();
    }

    /**
     * 获取发布图片时的推荐的话题文字说明
     * @param tpid
     * @param stickers
     */
    public void getRecommendTtext(String tpid, String stickers, IListener listener) {
        StickerApi.getRecommendTtext(tpid, stickers, buildGetRecommendTextCallback(listener));
    }

    /**
     * 发布帖子或大咖秀
     * @param type: 图片类型信息，1 大咖秀图 2 图片帖 3 文字帖 4 音频帖
     * @param bucket: 选择上传的最快服务器对应的bucket名称
     * @param projectKey: 需要上传的工程文件的objectKey，图片key构造规则  "prjmi" ＋ "/" + md5.substring(10,13) + / + md5.substring(22,25) + / + md5 + ".moi"
     * @param xgtKey: 需要上传的图片文件objectKey，文件key构造规则  "image" + "/" + md5.substring(10,13) + "/" + md5.substring(22,25) + "/" + md5 + "." + 图片后缀小写
     * @param stickerIds:sticker的{stickerId, parentid}集合
     * @param pictureList:包含的图片列表. 以逗号分割的字符串. (发布帖子时用到)
     * @param audio:sticker的声音集合
     * @param parent:对应改图转发时的原图的ID，为空则说明是原创
     * @param users:发表图片的时候@的用户ID，以逗号','分割，如 55f12e1498f3bc8177b232b9, 55f12e1498f3bc8177b232b8
     * @param content:发布图片时的评论
     * @param topicName:话题名称
     * @param readAuth:读权限 0 全否 1 特定 2 关注 3 粉丝 4 全部，默认为4
     * @param writeAuth:写权限 0 全否 1 特定 2 关注 3 粉丝 4 全部，默认为4
     * @param listener: callback
     */
    public void submitStickerPic(int type, String bucket, String projectKey, String xgtKey, List<StickerId> stickerIds,
                                 String pictureList, List<StickerAudioInfo> audio, String parent, String users, String content, String topicName,
                                 int readAuth, int writeAuth,
                                 IListener listener)  {
        String audioStr = audio == null || audio.isEmpty() ? null : mGson.toJson(audio);
        //如果说明内容是空格,过滤掉
        if (!TextUtils.isEmpty(content)) {
            content = content.trim();
            if (TextUtils.isEmpty(content)) {
                content = null;
            }
        }
        StickerApi.submitStickerPicture(type,bucket, projectKey, xgtKey, mGson.toJson(stickerIds), pictureList, audioStr,
                parent, users, content, topicName, readAuth, writeAuth, buildSubmitPicCallback(listener));
    }

    public void uploadFile2Oss(Activity activity, String ucid, int flag, String path, IListener listener) {
        if (!new File(path).exists()) {
            AppContext.showToast(activity.getString(R.string.cosplay_upload_file_not_exist) + ":" + path);
            //如果文件未上传而且本地已经丢失,暂时当作成功处理
            if (listener != null) {
                listener.onSuccess(null);
            }
            return;
        }
        getOssToken(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                OssToken ossToken = (OssToken) obj;
                if (ossToken != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //初始化oss对象
                            String endpoint = "http://" + getBestServerDomain();

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
                                String objectKey = StringUtil.getUploadKey(flag, path, null);
                                MyLog.i("bucket=" + getBestServerBucket() + ",objectKey =" + objectKey + ", path=" + path);
                                PutObjectRequest put = new PutObjectRequest(getBestServerBucket(), objectKey, path);
                                String type = StringUtil.getOssCallbackType(flag);
                                String callbackUrl = String.format(AppConfig.getBaseUrl(), "sys/clientUploadCallback");
                                MyLog.i("callbackUrl" + callbackUrl);
                                put.setCallbackParam(new HashMap<String, String>() {
                                    {
                                        put("callbackUrl", callbackUrl);
                                        put("callbackBody", "ucid=" + ucid + "&type=" + type);
                                        MyLog.i("callbackBody ucid=" + ucid + "&type=" + type);
                                    }
                                });
                                OSSAsyncTask task = oss.asyncPutObject(put,
                                        new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                            @Override
                                            public void onSuccess(PutObjectRequest request,
                                                                  PutObjectResult result) {
                                                MyLog.i("upload succeed!");
                                                if (listener != null) {
                                                    listener.onSuccess(null);
                                                }
                                            }

                                            @Override
                                            public void onFailure(PutObjectRequest request,
                                                                  ClientException clientExcepion,
                                                                  ServiceException serviceException) {
                                                MyLog.i("upload failed!");
                                                if (listener != null) {
                                                    listener.onErr(null);
                                                }
                                                // 请求异常
                                                if (clientExcepion != null) {
                                                    // 本地异常如网络异常等
                                                    clientExcepion.printStackTrace();
                                                }
                                            }
                                        });
                            } else {
                                MyLog.i("oss init faliled!!");
                                if (listener != null) {
                                    listener.onErr(null);
                                }
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onErr(Object obj) {
                if (listener != null) {
                    listener.onErr(null);
                }
            }

            @Override
            public void onNoNetwork() {
                if (listener != null) {
                    listener.onErr(null);
                }
            }
        });
    }

    /**
     * 获取最优的oss服务器domain
     */
    public String getBestServerDomain() {
        String domain = StickPreference.getInstance().getBestServerDomain();
        if (StringUtils.isEmpty(domain)) {
            domain = StickerConstants.DEFAULT_UPLOAD_OSS_DOMAIN;
        }
        return domain;
    }

    /**
     * 获取最优的oss服务器bucket
     */
    public String getBestServerBucket() {
        String bucket = StickPreference.getInstance().getBestServerBucket();
        if (StringUtils.isEmpty(bucket)) {
            bucket = StickerConstants.DEFAULT_UPLOAD_OSS_BUCKET;
        }
        return bucket;
    }

    /**
     * 更新最优的oss服务器信息
     */
    public void updateBestServerInfo(Activity activity) {
        long current = System.currentTimeMillis();
        long last = StickPreference.getInstance().getLastGetUploadServer();
        if ((current - last) > GET_UPLOAD_SERVER_INTERVAL) {
            //需要重新联网获取计算最优的oss
            getBestServerAndUpdate(activity);
        } else {
            CameraManager.getInst().startUpload(activity, null, null);
        }
    }

    private void getBestServerAndUpdate(Activity activity) {
        // 获取服务器列表
        getUploadServer(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                List<ServerInfo> serverInfos = (List<ServerInfo>)obj;
                if (serverInfos != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ServerInfo server = calculateBestServer(serverInfos);
                            MyLog.i("ljc: best server =" + server.getDomain());
                            updateBestServer(activity, server);
                        }
                    }).start();
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

    private ServerInfo calculateBestServer(List<ServerInfo> serverInfos) {
        ServerInfo best = null;

        long min = ERROR_RESPONSE;
        int index = 0;
        for (int i = 0; i < serverInfos.size(); i++) {
            long response = getDomainReponseTime(serverInfos.get(i).getDomain());
            if (response < min) {
                min = response;
                index = i;
            }
        }
        if (min != ERROR_RESPONSE && index < serverInfos.size()) {
            best = serverInfos.get(index);
        }
        if (best == null) {
            best = new ServerInfo();
            best.setDomain(StickerConstants.DEFAULT_UPLOAD_OSS_DOMAIN);
            best.setBucket(StickerConstants.DEFAULT_UPLOAD_OSS_BUCKET);
        }
        return best;
    }

    private long getDomainReponseTime(String domain) {
        Process p = null;
        try {
            String command = "/system/bin/ping -c 3 " + domain;
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            MyLog.i("ljc:IOException, return");
            return ERROR_RESPONSE;
        }
        int status = 0;
        try {
            status = p.waitFor();
        } catch (InterruptedException e) {
            MyLog.i("ljc:InterruptedException, return");
            e.printStackTrace();
            return ERROR_RESPONSE;
        }

        MyLog.i("ljc:status=" + status);
        if (status != 0) {
            MyLog.i("ljc:ping command not passed!");
            return ERROR_RESPONSE;
        }
        String delay = null;
        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String str = new String();
        try {
            while((str = buf.readLine()) != null) {
                if (str.contains("avg/max")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(str)) {
            String[] output = str.split("=");
            if (output != null) {
                delay = output[1].split("/")[1];
            }
            MyLog.i("ljc:delay=" + delay);
        } else {
            MyLog.i("ljc:str=" + str + ", did not get delay time!");
            return ERROR_RESPONSE;
        }

        long ret = 0;
        try {
            ret = (long)Math.ceil(Float.parseFloat(delay));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret ;
    }

    private void updateBestServer(Activity activity, ServerInfo serverInfo) {
        StickPreference.getInstance().setBestServerDomain(serverInfo.getDomain());
        StickPreference.getInstance().setBestServerBucket(serverInfo.getBucket());
        StickPreference.getInstance().setLastGetUploadServer(System.currentTimeMillis());

        //看看是否有未上传的文件
        CameraManager.getInst().startUpload(activity, null, null);
    }

    /**
     *  获取文件上传服务器列表
     * @param listener: callback
     */
    public void getUploadServer(IListener listener) {
        StickerApi.getUploadServer(buildGetUploadServerCallback(listener));
    }

    /**
     *  获取文件上传所用token
     * @param listener: callback
     */
    public void getOssToken(IListener listener) {
        StickerApi.getOssToken(buildGetOssTokenCallback(listener));
    }

    /**
     * 下载贴纸包的服务器接口 (同时也是使用单张贴纸的接口)
     * 3.2.6以前是用于下载贴纸包, 3.2.6以后没有下载贴纸包的概念了, 这个接口用于使用贴纸接口, 服务器记录客户端的
     * 单张贴纸使用情况并下发所属贴纸包的情况,客户端获取后用于在大咖秀页面增加贴纸包的逻辑
     * @param id: 贴纸包id
     * @param stickerId: 贴纸id
     * @param showDetail: 0 不显示详细 1 显示详细信息
     * @param listener: callback
     */
    public void downloadSticker(String id, String stickerId, int showDetail, IListener listener) {
        StickerApi.downloadSticker(id, stickerId, showDetail, buildDownloadStickerCallback(listener));
    }

    /**
     * 获取我的历史贴纸列表
     * @param pageNum 每页数量，必填；
     * @param lastid: 最后一个贴纸包的id信息，用于分页；
     * @param listener: callback
     */
    public void getHistoryStickers(int pageNum,String lastid, IListener listener) {
        StickerApi.getHistoryStickers(pageNum, lastid, buildGetHistoryStickerCallback(listener));
    }

    /**
     * 获取贴纸包的列表,当前三个参数都传空，lastid为上次从服务器获取的最后一个的id，非本地排序后的最后一个的id
     * @param ipname: IP属性的标签名称；
     * @param opname: 运营属性的标签名称；
     * @param tag: 贴纸包关键字；
     * @param lastid: 最后一个贴纸包的id信息，用于分页；
     * @param listener: callback
     */
    public void getMyStickerList(String ipname, String opname, String tag, String lastid,
                                 IListener2 listener) {
        StickerApi.getMyStickerList(ipname, opname, tag, lastid, buildGetStickerListCallback(listener));
    }

    /**
     *  获取贴纸商城的分类
     * @param listener: callback
     */
    public void getFolderList(IListener listener) {
        StickerApi.getFolderList(buildGetFolderListCallback(listener));
    }

    /**
     * 获取贴纸商城特定分类下的贴纸包列表
     * @param name: IP属性的标签名称；
     * @param type: 运营属性的标签名称；
     * @param lastid: 最后一个贴纸包的id信息，用于分页；
     * @param listener: callback
     */
    public void getStickerGroupList(String name, String type,String lastid,IListener listener) {
        StickerApi.getStickerGroupList(name, type, lastid, buildGetStickerGroupListCallback(listener));
    }

    /**
     * 获取单张贴纸详情
     * @param stickerId: id
     * @param listener: callback
     */
    public void getStickerInfo(String stickerId, IListener listener) {
        StickerApi.getStickerInfo(stickerId, buildGetStickerInfoCallback(listener));
    }

    /**
     * 获取热词列表
     * @param listener: callback
     */
    public void getHotList(IListener listener) {
        StickerApi.getHotList(buildGetHotListCallback(listener));
    }

    /**
     * 搜索贴纸
     * @param name: 搜索的名称, 必填
     * @param type: 类型，选填
     * @param lastid: 最后一项的标识stickerId，用于分页，选填
     * @param listener: callback
     */
    public void searchSticker(String name, String type, String lastid, IListener listener) {
        StickerApi.searchSticker(name, type, lastid, buildGetHistoryStickerCallback(listener));
    }

    /**
     * 删除已经下载贴纸包
     * @param id: 贴纸包id
     * @param listener: callback
     */
    public void deleteSticker(String id, IListener2 listener) {
        StickerApi.deleteSticker(id, buildDeleteStickerCallback(listener));
    }

    /**
     * 下载贴纸包Icon文件到本地
     */
    public void downloadStickerIconToLocal(StickerPackage sticker, Callback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadStickerPackage(sticker, listener, 1);
            }
        }).start();
    }

    /**
     * 下载贴纸包picture文件到本地
     */
    public void downloadStickerPictureToLocal(StickerPackage sticker, Callback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadStickerPackage(sticker, listener, 2);
            }
        }).start();
    }

    /**
     * 下载贴纸包文件到本地
     */
    public void downloadStickerToLocal(StickerPackage sticker, Callback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadStickerPackage(sticker, listener, 3);
            }
        }).start();
    }

    /**
     * 下载话题贴纸包文件到本地
     */
    public void downloadTopicSticker(StickerPackage sticker, Callback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle result = downloadStickerIcon(sticker);

                downloadStickerList(sticker, 1);
                if (listener != null) {
                    listener.onFinish(1);
                }
            }
        }).start();
    }

    /**
     * @param sticker
     * @param listener
     * @param flag: 1:only icon 2:only picture 3:all
     */
    public void downloadStickerPackage(StickerPackage sticker, Callback listener, int flag) {
        Bundle result = downloadStickerIcon(sticker);
        if(result != null) {
            //保存到数据库
            saveStickers2DB(StickerConstants.INTIME_STICKER_ID.equalsIgnoreCase(sticker.getStickerPackageId()) ? 2:1,
                    sticker, result.getString(KEY_URL), result.getString(KEY_PATH));
        }
        downloadStickerList(sticker, flag);
        if (listener != null) {
            listener.onFinish(1);
        }
    }

    public final String KEY_URL = "url";
    public final String KEY_PATH = "path";
    public final String KEY_OK = "ok";
    private Bundle downloadStickerIcon(StickerPackage sticker) {
        Bundle result = null;
        //下载贴纸包的icon
        try {
            String url = null;
            String path = null;
            if (sticker.getIcon() != null) {
                url = sticker.getIcon().getUri();
            }
            MyLog.i("SetIconUrl1="+url);
            url = StickerUtils.getStickerPackageIconUrl(url);
            MyLog.i("SetIconUrl2="+url);
            path = getStickPackagePath(sticker, null);
            HttpUtil.download(url, path, true);

            result = new Bundle();
            result.putString(KEY_URL, url);
            result.putString(KEY_PATH, path);
            result.putBoolean(KEY_OK, true);
        } catch (Exception e) {
            MyLog.e(e);
        }
        return result;
    }

    /** 下载贴纸包的ICON
     *
     * 如果前url相同,而且文件存在,就不下载了
     * */
    public Bundle downloadStickerIcon(StickerPackage sticker, EmoticonSetBean setBean) {
        Bundle result = null;
        //下载贴纸包的icon
        try {
            String url = null;
            String path = null;
            if (sticker.getIcon() != null) {
                url = sticker.getIcon().getUri();
            } else if (sticker.getPics() != null && sticker.getPics().getCover() != null) {
                url = sticker.getPics().getCover().getUri();
            }
            path = getStickPackagePath(sticker, setBean);
            File file = new File(path);
            if(!url.equals(setBean.getIconUrl()) || !file.exists()) {
                url = StickerUtils.getStickerPackageIconUrl(url);

                MyLog.i("path=" + path);
                if (file.exists()) {
                    file.delete();
                }
                HttpUtil.download(url, path);
            }

            result = new Bundle();
            result.putString(KEY_URL, url);
            result.putString(KEY_PATH, path);
            result.putBoolean(KEY_OK, true);
        } catch (Exception e) {
            MyLog.e(e);
        }
        return result;
    }

    private String getStickPackagePath(StickerPackage sticker, EmoticonSetBean setBean) {
        if(setBean != null && setBean.getStickType() > 1) {
            return StickerUtils.getStickePackagerIconPath(setBean.getStickType());
        }
        if(sticker != null) {
            return StickerUtils.getStickePackagerIconPath(sticker.getStickerPackageId());
        }
        return null;
    }

    /**
     * 下载贴纸包内的贴纸
     *
     * @param flag: 1:only icon 2:only picture 3:all
     */
    private void downloadStickerList(StickerPackage sticker, int flag) {
        List<StickerInfo> list = sticker.getStickers();
        if (list != null && list.size() > 0) {
            if (flag == 1 || flag == 3) {
                for (StickerInfo stickerInfo : list) {
                    downloadSingleStickerIcon(stickerInfo);
                }
            }
            if (flag == 2 || flag == 3) {
                for (StickerInfo stickerInfo : list) {
                    downloadSingleStickerPicture(stickerInfo);
                }
            }
        }
    }

    /** 下载贴纸包内的贴纸
     *
     * 如果前url相同,而且文件存在,就不下载了
     * */
    public void downloadStickerList(StickerPackage sticker, EmoticonSetBean setBean) {
        List<StickerInfo> list = sticker.getStickers();
        if (list != null && list.size() > 0) {
            ArrayList<EmoticonBean> beans = setBean.getEmoticonList();
            int beanSize = beans.size();
            for (int i = 0; i < list.size(); i++) {
                StickerInfo stickerInfo = list.get(i);
                if(beanSize > i) {
                    downloadSingleStickerIcon(stickerInfo, beans.get(i));
                } else {
                    downloadSingleStickerIcon(stickerInfo);
                }
            }

            for (int i = 0; i < list.size(); i++) {
                StickerInfo stickerInfo = list.get(i);
                if(beanSize > i) {
                    downloadSingleStickerPicture(stickerInfo, beans.get(i));
                } else {
                    // 不再自动下载大图了
//                    downloadSingleStickerPicture(stickerInfo);
                }
            }
        }
    }

    /** 下载单张贴纸的icon
     *
     * 如果前url相同,而且文件存在,就不下载了
     * */
    private void downloadSingleStickerIcon(StickerInfo stickerInfo, EmoticonBean emoticonBean) {
        try {
            //下载icon
            String url = stickerInfo.getIcon().getUri();
            String path = StickerUtils.getStickerIconPath(stickerInfo);
            File file = new File(path);
            MyLog.i("url=" + url);
            if(!url.equals(emoticonBean.getIconUrl()) || !file.exists()) {
                url = StickerUtils.getSingleStickerIconUrl(url);
                MyLog.i("newurl="+url);
                MyLog.i("path=" + path);
                if (file.exists()) {
                    file.delete();
                }
                HttpUtil.download(url, path);
            }
        } catch (NullPointerException e) {
            MyLog.e(e);
        }
    }

    /** 下载单张贴纸的大图
     *
     * 如果前url相同,而且文件存在,就不下载了
     * */
    private void downloadSingleStickerPicture(StickerInfo stickerInfo, EmoticonBean emoticonBean) {
        try {
            //下载大图
            String url = stickerInfo.getPicture().getUri();
            String path = StickerUtils.getStickerPicPath(stickerInfo);
            File file = new File(path);
            MyLog.i("url=" + url);
            if(!url.equals(emoticonBean.getGifUrl()) || !file.exists()) {
                url = StickerUtils.getSingleStickerPictureUrl(url);
                MyLog.i("newurl="+url);
                MyLog.i("path="+path);
                if (file.exists()) {
                    file.delete();
                }
                HttpUtil.download(url, path);
            }
        } catch (NullPointerException e) {
            MyLog.e(e);
        }
    }

    /**
     * 下载单张贴纸的icon
     */
    public void downloadSingleStickerIcon(StickerInfo stickerInfo) {
        try {
            //下载icon
            String url = stickerInfo.getIcon().getUri();
            url = StickerUtils.getSingleStickerIconUrl(url);
            String path = StickerUtils.getStickerIconPath(stickerInfo);
            MyLog.i("url="+url);
            MyLog.i("path="+path);

            HttpUtil.download(url, path, true);
        } catch (NullPointerException e) {
            MyLog.e(e);
        }
    }

    /**
     * 下载单张贴纸的大图
     */
    public void downloadSingleStickerPicture(StickerInfo stickerInfo) {
        try {
            //下载大图
            String url = StickerUtils.getSingleStickerPictureUrl(stickerInfo.getPicture().getUri());
            String path = StickerUtils.getStickerPicPath(stickerInfo);
            MyLog.i("url="+url);
            MyLog.i("path="+path);
            HttpUtil.download(url, path, true);
        } catch (NullPointerException e) {
            MyLog.e(e);
        }
    }

    /***
     * 把下载好的贴纸包入本地数据库
     * @param url: 贴纸包的icon url
     * @param path: 贴纸包的icon 本地地址
     * @return
     */
    public EmoticonSetBean saveStickers2DB(int type, StickerPackage sticker, String url, String path) {
        EmoticonSetBean bean = convertStickerPackageToEmoticonSetBean(type, sticker, url, path);
        if (bean != null) {
            DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
            int order;
            String originalId;
            synchronized (this) {
                if (type == StickerConstants.STICKER_TYPE_NORMAL) {
                    order = dbHelper.queryEmoticonSetOrder(type, ClientInfo.getUID(), bean.getId());
                    if (order < 0) {
                        order = dbHelper.queryEmoticonSetMaxOrder(ClientInfo.getUID());
                        bean.setOrder(1 + order);
                    } else {
                        bean.setOrder(order);
                    }
                    originalId = bean.getId();
                    dbHelper.deleteEmoticonSet(originalId);
                } else {
                    order = dbHelper.queryEmoticonSetOrder(type, null, null);
                    originalId = dbHelper.queryEmoticonSetId(type);
                    bean.setOrder(order);
                    bean.setUid(DefEmoticons.DEFAULT_EMOJISET_UID);
                    bean.setStickType(type);
                    if (TextUtils.isEmpty(bean.getName())) {
                        bean.setName(getDefaultStickerName(type));
                    }
                    dbHelper.deleteDefaultStickerSet(type);
                }
                MyLog.i("order=" + order + ", type = " + type + "id=" + originalId);
                dbHelper.insertEmoticonSet(bean);
            }
        }
        return bean;
    }

    public EmoticonSetBean convertStickerPackageToEmoticonSetBean(int type,StickerPackage sticker, String url, String path) {
        if(sticker == null)
            return null;
        List<StickerInfo> list = sticker.getStickers();
        if (list == null || list.isEmpty()) {
            return null;
        }
        EmoticonSetBean bean = new EmoticonSetBean();
        bean.setUid(ClientInfo.getUID());
        bean.setId(sticker.getStickerPackageId());
        bean.setStickType(type);
        bean.setName(sticker.getName());

        if (sticker.getIcon() != null) {
            url = sticker.getIcon().getUri();
        } else if (sticker.getPics() != null && sticker.getPics().getCover() != null) {
            url = sticker.getPics().getCover().getUri();
        }

        bean.setIconUrl(url);
        bean.setIconUri("file://" + path);
        bean.setUpdateTime(sticker.getUpdatedAt());
        bean.setItemPadding(15);
        bean.setVerticalSpacing(5);
        bean.setLine(2);
        bean.setRow(4);
        ArrayList<EmoticonBean> emoticonList = new ArrayList<EmoticonBean>();
        Gson mGson = null;
        for (StickerInfo stickerInfo : list) {
            EmoticonBean eb = StickerUtils.convertStickerInfoToEmoticonBean(type, sticker, stickerInfo);
            eb.setParentStickType(bean.getStickType());
            if (eb != null) {
                emoticonList.add(eb);
            }
        }
        bean.setEmoticonList(emoticonList);

        return bean;
    }

    /***
     * 把下载好的贴纸包入本地数据库
     * @return
     */
    public void deleteStickerFromDB(String id) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        dbHelper.deleteEmoticonSet(id);
    }

    private String getDefaultStickerName(int type) {
        switch (type) {
            case 2:
                return "推荐";
            case 3:
                return "文字图案";
            case 4:
                return "特殊符号";
            case 5:
                return "文字气泡";
            case 6:
                return "边框";
            default:
                return "推荐";
        }
    }

    /**
     * 获取用户已下载的贴纸包中需要检查更新的贴纸包
     * @param uid
     * @return
     */
    public List<String> getNeedCheckUpdateStickers(String uid) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        ArrayList<EmoticonSetBean> setBeanList;
        setBeanList = dbHelper.queryEmoticonSetByType(uid, StickerPackage.STICKER_NORMAL);

        if (setBeanList == null || setBeanList.size() == 0) return null;

        long current = System.currentTimeMillis();
        List<String> stickerIds = new ArrayList<String>();
        for (EmoticonSetBean bean : setBeanList) {
            // 如果已经下架或者已经是待更新状态,不检查
            if (bean.getFlag() != StickerConstants.FLAG_NORMAL) {
                continue;
            }
            // 如果距离上一次检查时间小于7天
            if (current - bean.getLastCheckTime() < STICKER_UPDATE_INTERVAL) {
                continue;
            }
            stickerIds.add(bean.getId());
        }

        return stickerIds;
    }

    /**
     * 获取用户已下载的贴纸包中需要重新下载的贴纸包,主要是2.7以前老用户下载的贴纸包升级到2.7后需要重新下载
     * 判断每一个贴纸包的所有单个贴纸的stickerid是否是空,如果是说明是老数据,需要重新下载
     *
     * @param uid
     * @return
     */
    public List<EmoticonSetBean> getNeedReloadStickers(String uid) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());

        ArrayList<EmoticonSetBean> setBeanList;
        setBeanList = dbHelper.queryEmoticonSetByType(uid, StickerPackage.STICKER_NORMAL);

        if (setBeanList == null || setBeanList.size() == 0) return null;
        List<EmoticonSetBean> stickers = new ArrayList<EmoticonSetBean>();
        for (EmoticonSetBean bean : setBeanList) {
            if (isPackageNeedReload(dbHelper, bean.getId())) {
                MyLog.i("ljc: isPackageNeedReload = true");
                stickers.add(bean);
            }
        }
        return stickers;
    }

    /**
     * 获取用户已下载的贴纸包中需要重新下载的贴纸包,主要是2.7以前老用户下载的贴纸包升级到2.7后需要重新下载
     * @param uid
     * @return
     */
    public List<EmoticonSetBean> getDownloadStickers(String uid) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());

        ArrayList<EmoticonSetBean> setBeanList;
        setBeanList = dbHelper.queryEmoticonSetByType(uid, StickerPackage.STICKER_NORMAL);

        return setBeanList;
    }

    /**
     * 判断每一个贴纸的stickerid是否是空,如果是说明是老数据,需要重新下载
     */
    public boolean isPackageNeedReload(DBHelper dbHelper, String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }

        ArrayList<EmoticonBean> beanList = dbHelper.queryAllEmoticon(id);
        if (beanList == null || beanList.size() == 0) {
            return true;
        }
//        MyLog.i("ljc: beanList.size = "+ beanList.size());
        for (EmoticonBean bean : beanList) {
            if (TextUtils.isEmpty(bean.getStickerId())) {
                return true;
            }
        }
        return false;
    }

    private AsyncHttpResponseHandler buildGetStickerListCallback(final IListener2 listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetStickerListResult result = XmlUtils.JsontoBean(GetStickerListResult.class, responseBody);
                if (result != null && result.getStickerList() != null) {
                    MyLog.i("onSucceed: sticker list size = " + result.getStickerList().size());
                    listener.onSuccess(result.getStickerList());
                } else {
                    listener.onErr(null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("onFailure: statusCode = " + statusCode);
                listener.onNoNetwork();
            }

            @Override
            public void onStart() {
                super.onStart();
                listener.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listener.onFinish();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetStickerGroupListCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetStickerGroupListResult result = XmlUtils.JsontoBean(GetStickerGroupListResult.class, responseBody);
                if (result != null) {
                    if (result.getStickerList() != null) {
                        MyLog.i("onSucceed: sticker list size = " + result.getStickerList().size());
                    }
                    listener.onSuccess(result.getStickerList());
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

    private AsyncHttpResponseHandler buildGetStickerInfoCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetStickerInfoResult result = XmlUtils.JsontoBean(GetStickerInfoResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getSticker());
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

    private AsyncHttpResponseHandler buildGetHotListCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetHotListResult result = XmlUtils.JsontoBean(GetHotListResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getHotList());
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

    private AsyncHttpResponseHandler buildGetHistoryStickerCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetHistoryStickersResult result = XmlUtils.JsontoBean(GetHistoryStickersResult.class, responseBody);
                if (result != null && result.getStickerList() != null) {
                    MyLog.i("onSucceed: sticker list size = " + result.getStickerList().size());
                    listener.onSuccess(result.getStickerList());
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

    private AsyncHttpResponseHandler buildGetFolderListCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetFolderResult result = XmlUtils.JsontoBean(GetFolderResult.class, responseBody);
                if (result != null) {
                    if (result.getFolderList() != null) {
                        MyLog.i("onSucceed: folder list size = " + result.getFolderList().size());
                    }
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

    private AsyncHttpResponseHandler buildGetStickerDetailCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetStickerDetailResult result = XmlUtils.JsontoBean(GetStickerDetailResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getSticker());
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

    private AsyncHttpResponseHandler buildDownloadStickerCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                DownloadStickerResult result = XmlUtils.JsontoBean(DownloadStickerResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getSticker());
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

    private AsyncHttpResponseHandler buildGetStickerUpdateCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetStickerUpdateResult result = XmlUtils.JsontoBean(GetStickerUpdateResult.class, responseBody);
                if (result != null) {
                    if (result.getResult() > 0) {
                        listener.onSuccess(result.getSticker());
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

    private AsyncHttpResponseHandler buildDeleteStickerCallback(final IListener2 listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildDeleteStickerCallback:onSuccess: response =" + new String(responseBody));
                BaseHttpResponse result = XmlUtils.JsontoBean(BaseHttpResponse.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    listener.onSuccess(result.getResult());
                } else {
                    listener.onErr(null);
                }
                listener.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildDeleteStickerCallback:onFailure");
                listener.onNoNetwork();
                listener.onFinish();
            }
        };
    }

    private AsyncHttpResponseHandler buildSubmitPicCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("buildSubmitCallback:onSuccess: response =" + new String(responseBody));
                submitPictureResult result = XmlUtils.JsontoBean(submitPictureResult.class, responseBody);
                if (result != null && result.getResult() > 0) {
                    MyLog.i("submit picture suceed: ucid=" + result.getUcid());
                    listener.onSuccess(result.getUcid());

                    if (result.getMoinBean() != null && result.getMoinBean().getTotalBean() > 0
                            && result.getMoinBean().getObtainBean() > 0) {
                        UIHelper.showMoinBeanActivity(result.getMoinBean(), MissionConstants.MISSION_MAKE_COSPLAY);
                    }
                } else {
                    if (result != null) {
                        listener.onErr(result.getError());
                    } else {
                        listener.onErr(null);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                MyLog.i("buildSubmitCallback:onFailure");
                listener.onNoNetwork();
            }
        };
    }

    private AsyncHttpResponseHandler buildGetUploadServerCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetServerInfoResult result = XmlUtils.JsontoBean(GetServerInfoResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getServers());
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

    private AsyncHttpResponseHandler buildGetOssTokenCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetOssTokenResult result = XmlUtils.JsontoBean(GetOssTokenResult.class, responseBody);
                if (result != null) {
                    listener.onSuccess(result.getOssToken());
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

    private AsyncHttpResponseHandler buildGetRecommendTextCallback(final IListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                MyLog.i("onSucceed: response = " + new String(responseBody));
                GetRecommendTextResult result = XmlUtils.JsontoBean(GetRecommendTextResult.class, responseBody);
                if (result != null) {
                    if (result.getResult() > 0) {
                        listener.onSuccess(result.getTextList());
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
     * 检查本地是否存在这个贴纸包
     * @param stickerId
     * @return
     */
    public boolean isDownloaded(String stickerId) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        List<EmoticonSetBean> x = dbHelper.queryEmoticonSetByID(ClientInfo.getUID(), stickerId);

        if(x == null || x.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 判断贴纸包是否是最新的
     */
    public boolean isNewest(StickerPackage item) {
        if (item == null) {
            return false;
        }
        if (item.getUpdatedAt() > System.currentTimeMillis() - STICKER_NEWST_INTERVAL &&
                !isViewedNewest(item)) {
            return true;
        }
        return false;
    }

    /**
     * 判断贴纸包是否在最新浏览过的历史中
     */
    private boolean isViewedNewest(StickerPackage item) {
        if (item == null) {
            return false;
        }
        String[] arr = MinePreference.getInstance().getViewedNewestSticker().split(";");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(item.getStickerPackageId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置这个最新贴纸包的被浏览过
     */
    public void setViewedNewest(StickerPackage item) {
        if (item == null) {
            return;
        }
        String viewed = MinePreference.getInstance().getViewedNewestSticker();
        MinePreference.getInstance().setViewedNewestSticker(viewed + ";" + item.getStickerPackageId());
    }

    /**
     * 判断下载的贴纸包是否有更新
     */
    public boolean hasUpdate(StickerPackage item) {
        int flag = getLocalFlag(item);
        return getHasUpdateFlag(flag) == 1;
    }


    /**
     * 判断下载的贴纸包是否未更新过
     */
    public boolean unUpdated(StickerPackage item) {
        int flag = getLocalFlag(item);
        return getUpdatedFlag(flag) == 1;
    }

    /**
     * 获取贴纸包在本地数据库中的标记
     */
    public int getLocalFlag(StickerPackage item) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        int flag = dbHelper.queryEmoticonSetFlag(StickerPackage.STICKER_NORMAL, item.getStickerPackageId());
        if (flag < 0) {
            return 0;
        }
        return flag;
    }

    /**
     * 获取贴纸包是否有更新标记
     *
     * @param flag 贴纸包在DB中的标记
     * @return 0: 无更新,  1:有更新
     */
    public int getHasUpdateFlag(int flag) {
        if (flag < 0) {
            return 0;
        }
        return flag & 0x1;
    }

    /**
     * 清除贴纸包是否有更新标记
     *
     * @param item 商城中的贴纸包
     */
    public int clearHasUpdateFlag(StickerPackage item) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        int flag = dbHelper.queryEmoticonSetFlag(StickerPackage.STICKER_NORMAL, item.getStickerPackageId());
        if(getHasUpdateFlag(flag) == 1) {
            return dbHelper.updateEmoticonSetFlag(item.getStickerPackageId(), flag & 2);
        }
        return 0;
    }

    /**
     * 清除贴纸包是否有更新标记
     *
     * @param item 贴纸编辑栏的贴纸包
     */
    public int clearHasUpdateFlag(EmoticonSetBean item) {
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        int flag = dbHelper.queryEmoticonSetFlag(item.getStickType(), item.getId());
        if(getHasUpdateFlag(flag) == 1) {
            return dbHelper.updateEmoticonSetFlag(item.getId(), flag & 2);
        }
        return 0;
    }

    /**
     * 获取贴纸包是否已更新过标记
     *
     * @param flag 贴纸包在DB中的标记
     * @return 0: 已更新过,  1:未更新过
     */
    public int getUpdatedFlag(int flag) {
        if (flag < 0) {
            return 0;
        }
        return flag >> 1 & 0x1;
    }

    /**
     * 清除贴纸包的更新过标记
     *
     * @param flag 贴纸包在DB中的标记
     */
    public int clearUpdatedFlag(int flag) {
        return flag & 0;
    }

    /**
     * 设置贴纸包的更新过标记 [设置为未更新过]
     *
     * @param flag 贴纸包在DB中的标记
     */
    public int resetUpdatedFlag(int flag) {
        if (flag < 0) {
            return flag;
        }
        return flag | 3;
    }

    // 下载后缓存贴纸详情
    public void saveStickerPackage(Context context, StickerPackage stickerPackage, String stickerPackageId) {
        new SaveCacheTask(context, stickerPackage, StickerConstants.STICKER_DETAIL_CACHE_PREFIX + stickerPackageId).execute();
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            MyLog.i("ljc: saveCacheTask key = " + key);
            return null;
        }
    }

    //使用贴纸包
    public void useStickerPackage(Activity activity, String stickerPackageId) {
        StickPreference.getInstance().setDefaultUseSticker(stickerPackageId);
        useSticker(activity);
    }

    /**
     *  使用单张贴纸
     */
    public void useSingleSticker(Activity activity, String stickerId, String stickerPackageId) {
        if (StringUtil.isNullOrEmpty(stickerId)) {
            return;
        }

        boolean needDownload = false;
        MyLog.i("使用单张贴纸 stickerId=" + stickerId + ", stickerPackageId=" + stickerPackageId);
        // 看这个贴纸包有没有下载下来
        if (!StringUtil.isNullOrEmpty(stickerPackageId)) {
//            MyLog.i("本地DB没有贴纸包 " + stickerPackageId);
            needDownload = true;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((BaseActivity) activity).showWaitDialog(R.string.waitting);
                    boolean isIntimeStickerPackage = StickerConstants.INTIME_STICKER_ID.equals(stickerPackageId);
                    downloadSticker(isIntimeStickerPackage ? null : stickerPackageId, stickerId, 1, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            // 如果是旧的推荐贴纸包是需要判断下载与否的, 如果不是旧推荐包,那么判断未下载时才去下载.
                            if (obj != null && !StickerConstants.INTIME_STICKER_ID.equals(((StickerPackage) obj).getStickerPackageId())) {
                                if (!isDownloaded(((StickerPackage) obj).getStickerPackageId())) {
                                    final StickerPackage mStickerPackage = (StickerPackage) obj;
//                                MyLog.i("那就联网获取贴纸包详情 " + mStickerPackage.toString());

                                    downloadStickerIconToLocal(mStickerPackage, new Callback() {
                                        @Override
                                        public void onStart() {
                                        }

                                        @Override
                                        public void onFinish(int result) {
//                                        MyLog.i("useSingleSticker download StickerPackage " + mStickerPackage.getName() + " succeed!");
                                            EventBus.getDefault().post(new AddMySticker(mStickerPackage));
                                            MinePreference.getInstance().setNeedRefreshPhotoEdit(true);
                                            //保存贴纸包详情到本地[这个showDetail==1才好存储的哦]
                                            StickerManager.getInstance().saveStickerPackage(activity, mStickerPackage, mStickerPackage.getStickerPackageId());

                                            StickPreference.getInstance().setStickerMallPackageId(mStickerPackage.getStickerPackageId());

//                                        MyLog.i("然后将贴纸包入库完毕再存储这个缓存 " + stickerPackageId);
                                        }
                                    });
                                }

                            } else {
//                                MyLog.i("那就联网获取贴纸包详情 failed " + stickerPackageId);
                            }

                            ((BaseActivity) activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((BaseActivity) activity).hideWaitDialog();
//                                    AppContext.getInstance().showToast(R.string.download_success);
//                                                MyLog.i("say OK " + stickerPackageId);
                                }
                            });

                            useSticker(activity);
                        }

                        @Override
                        public void onErr(Object obj) {
//                            MyLog.i("say onErr1 " + stickerPackageId);
                            ((BaseActivity) activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((BaseActivity) activity).hideWaitDialog();
//                                    AppContext.getInstance().showToast(R.string.download_fail);
//                                    MyLog.i("say onErr2 " + stickerPackageId);
                                }
                            });
                        }

                        @Override
                        public void onNoNetwork() {
//                            MyLog.i("say onNoNetwork1 " + stickerPackageId);
                            ((BaseActivity) activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((BaseActivity) activity).hideWaitDialog();
//                                    AppContext.getInstance().showToast(R.string.no_network);
//                                    MyLog.i("say onNoNetwork2 " + stickerPackageId);
                                }
                            });
                        }
                    });
                }
            });
        }

        final boolean needDown = needDownload;
        //首先调用接口获取单张贴纸详情
        getStickerInfo(stickerId, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StickerInfo stickerInfo = (StickerInfo) obj;
                        if (stickerInfo != null) {
                            setUseSingleStickerInfo(activity, stickerInfo);
                            StickPreference.getInstance().setUseSingleSticker(stickerId);

                            if (!needDown) {
                                useSticker(activity);
                            }
                        } else {
                            AppContext.toast(activity, activity.getString(R.string.cosplay_import_failed));
                        }
                    }
                });
            }

            @Override
            public void onErr(Object obj) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.toast(activity, activity.getString(R.string.cosplay_import_failed));
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.toast(activity, activity.getString(R.string.no_network));
                    }
                });
            }
        });
    }

    /**
     * 使用单张贴纸后跳转到大咖秀编辑界面后需要按照如下顺序调用
     * 首先判断是否有正在使用的单张贴纸,然后调用getUseSingleStickerInfo得到贴纸的情况, 使用后
     * 调用clearUseSingleSticker清除使用的贴纸
     */
    public boolean hasUsingSticker() {
        return !StringUtils.isEmpty(StickPreference.getInstance().getUseSingleSticker());
    }

    public StickerInfo getUseSingleStickerInfo(Activity activity) {
        final WeakReference<Context> mContext;
        mContext = new WeakReference<Context>(activity);
        Serializable seri = CacheManager.readObject(mContext.get(), KEY_USING_SINGLE_STICKER);
        if (seri == null) {
            return null;
        } else {
            return (StickerInfo)seri;
        }
    }

    public void clearUseSingleSticker() {
        StickPreference.getInstance().setUseSingleSticker("");
    }

    private void useSticker(Activity activity) {
        if (StickPreference.getInstance().getPhotoProcessRunning()) {
            // 大咖秀编辑页面已经启动, 跳转编辑页面
            Intent newIntent = new Intent(activity, PhotoProcessActivity.class);
            activity.startActivity(newIntent);
        } else {
            // 跳转照片拍摄页面
            CameraManager.getInst().openCamera(activity, null);
        }
    }

    private void setUseSingleStickerInfo(Activity activity, StickerInfo sticker) {
        new SaveCacheTask(activity, sticker, KEY_USING_SINGLE_STICKER).execute();
    }

    /**
     * 判断是贴纸商城的一级/二级列表是否过期
     */
    public boolean isFolderListExpired() {
        return (System.currentTimeMillis() - StickPreference.getInstance().getLastStickerMallFolders()) > STICKER_FOLDERLIST_INTERVAL;
    }

    public GetFolderResult getStickerMallFolderResult(Activity activity) {
        final WeakReference<Context> mContext;
        mContext = new WeakReference<Context>(activity);
        Serializable seri = CacheManager.readObject(mContext.get(), KEY_STICKMALL_FOLDERLIST);
        if (seri == null) {
            return null;
        } else {
            return (GetFolderResult)seri;
        }
    }

    public void setStickerMallFolderResult(Activity activity, GetFolderResult folderResult) {
        new SaveCacheTask(activity, folderResult, KEY_STICKMALL_FOLDERLIST).execute();
    }

}
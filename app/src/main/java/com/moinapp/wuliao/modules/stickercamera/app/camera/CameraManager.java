package com.moinapp.wuliao.modules.stickercamera.app.camera;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.db.DataProvider;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerImgUtils;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.model.StickerProject;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.CameraActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.CropPhotoActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoProcessActivity;
import com.moinapp.wuliao.modules.stickercamera.app.model.PhotoItem;
import com.moinapp.wuliao.modules.stickercamera.app.model.StickerDecode;
import com.moinapp.wuliao.modules.stickercamera.tables.UnUploadCosplayTable;
import com.moinapp.wuliao.modules.update.AbsManager;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * 大咖秀管理类
 */
public class CameraManager extends AbsManager {
    private ILogger MyLog = LoggerFactory.getLogger("CM");
    private final static long ONE_HOUR = 60 * 60 * 1000;
    private static CameraManager mInstance;
    private Stack<Activity> cameras = new Stack<Activity>();
    private Gson mGson = new Gson();

    public static CameraManager getInst() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new CameraManager();
            }
        }
        return mInstance;
    }


    @Override
    public void init() {

    }

    /**
     * 打开照相界面
     */
    public void openCamera(Context context, Bundle args) {
        Intent intent = new Intent(context, CameraActivity.class);
        MyLog.i("openCamera bundle="+args);
        intent.putExtra("args", args);
        intent.putExtra(DiscoveryConstants.FROM, args != null ? args.getString(DiscoveryConstants.FROM) : "");
        context.startActivity(intent);
    }

    /**使用贴纸进入照相界面*/
//    public void openCamera(Context context, ) {
//        Intent intent = new Intent(context, CameraActivity.class);
//        MyLog.i("openCamera bundle="+args);
//        intent.putExtra("args", args);
//        intent.putExtra(DiscoveryConstants.FROM, args != null ? args.getString(DiscoveryConstants.FROM) : "");
//        context.startActivity(intent);
//    }

    /**判断图片是否需要裁剪*/
    public void processPhotoItem(Activity activity, PhotoItem photo, String from) {
        Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        if (ImageUtils.isSquare(photo.getImageUri())) {
            Intent newIntent = new Intent(activity, PhotoProcessActivity.class);
            newIntent.setData(uri);
            newIntent.putExtra(DiscoveryConstants.FROM, StringUtil.nullToEmpty(from));
            activity.startActivity(newIntent);
        } else {
            Intent i = new Intent(activity, CropPhotoActivity.class);
            i.setData(uri);
            //TODO稍后添加
            activity.startActivityForResult(i, Constants.REQUEST_CROP);
        }
    }

    public void close() {
        for (Activity act : cameras) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        cameras.clear();
    }

    public void addActivity(Activity act) {
        cameras.add(act);
    }

    public void removeActivity(Activity act) {
        cameras.remove(act);
    }

    /**
     * 发布图片时生成工程文件
     * @param sticker:描述贴纸信息的对象
     * @param imageFile:底图的图片文件
     */
    public String encodeToProjectFile(StickerProject sticker, String imageFile) {
        String encodeFile = FileUtil.getInst().getCacheDir() + "/projectcache.moi";
        File output = new File(encodeFile);
        if (output.exists()) {
            output.delete();
        }
        long cmt = System.currentTimeMillis();
        try{
            byte[] img = StickerImgUtils.readFile(imageFile);
            byte[] moi = StickerImgUtils.encode(mGson.toJson(sticker), img);

            MyLog.i("gcwj加密=" + mGson.toJson(sticker));

            StickerImgUtils.writeFile(encodeFile, moi);
        }catch(IOException ex) {
            ex.printStackTrace();
        }
        MyLog.i("sampleEncode completed. output file ="+ encodeFile+ "cost:" + (System.currentTimeMillis() - cmt));
        output = new File(encodeFile);
        if (output.exists()) {
            return encodeFile;
        } else {
            return null;
        }
    }

    /**
     * 从工程文件解析出贴纸信息和原图
     * @param projectFileName
     * @return StickerDecode对象,包括贴纸信息和原图
     */
    public StickerDecode decodeProjectFile(String projectFileName) {
        StickerDecode decode = null;
        String path = FileUtil.getInst().getCacheDir() + "/decodecache";
        long cmt = System.currentTimeMillis();
        try {
            MyLog.i("project file" + projectFileName);
            byte[] moi = StickerImgUtils.readFile(projectFileName);
            StickerImgUtils.StickerImg si = StickerImgUtils.decode(moi);
            MyLog.i("project json" + si.getJson());
            StickerImgUtils.writeFile(path, si.getImg());

            decode = new StickerDecode();
            decode.setImageFile(path);
            StickerProject stickerProject = XmlUtils.JsontoBean(StickerProject.class, si.getJson());
            MyLog.i("gcwj解密=" + si.getJson());
            decode.setSticker(stickerProject);
            MyLog.i("decode completed. cost:" + (System.currentTimeMillis() - cmt));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            return decode;
        }
    }


    /**
     * 大咖秀文件是否未上传,
     * -1表示已上传,找不到记录
     * 返回是重试的次数, 0表示正在上传 >0表示已经重试的次数
     */
    public int isFileUploadFailed(String ucid) {
        Cursor cursor = null;
        int result = -1;
        try {
            ContentResolver contentResolver = BaseApplication.context().getContentResolver();
            cursor = contentResolver.query(UnUploadCosplayTable.UN_UPLOAD_COSPLAY_CACHE_URI, null,
                    UnUploadCosplayTable.COSPLAY_ID + " = ?", new String[]{ucid}, null);

            if (cursor == null ) {
                return -1;
            }

            if (cursor.moveToNext()) {
                result = cursor.getInt(cursor.getColumnIndex(UnUploadCosplayTable.RETRY_TIMES));
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return result;
    }

    /**
     * 保存或刷新 未上传成功的信息到数据库
     */
    public void saveUnuploadFiles2DataBase(int flag, String ucid, String path, int retry_times, long last_fail_time) {
        if (StringUtil.isNullOrEmpty(ucid)) return;
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder b = null;

            //先删除旧的
            ContentProviderOperation.Builder del = ContentProviderOperation.newDelete(UnUploadCosplayTable.UN_UPLOAD_COSPLAY_CACHE_URI)
                    .withSelection(UnUploadCosplayTable.COSPLAY_ID + " =? " + " AND " + UnUploadCosplayTable.FLAG + " = " + flag,
                            new String[]{ucid});
            ops.add(del.build());
            ContentValues values = new ContentValues();
            values.put(UnUploadCosplayTable.COSPLAY_ID, ucid);
            values.put(UnUploadCosplayTable.FLAG, flag);
            values.put(UnUploadCosplayTable.FILE_PATH, path);
            values.put(UnUploadCosplayTable.RETRY_TIMES, retry_times);
            values.put(UnUploadCosplayTable.LAST_FAIL_TIME, last_fail_time);
            b = ContentProviderOperation.newInsert(UnUploadCosplayTable.UN_UPLOAD_COSPLAY_CACHE_URI).withValues(
                    values);
            ops.add(b.build());
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    /**
     * 删除上传成功的信息
     */
    public void deleteSucceedFilesFromDataBase(int flag, String ucid, String path) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder del = ContentProviderOperation.newDelete(UnUploadCosplayTable.UN_UPLOAD_COSPLAY_CACHE_URI)
                    .withSelection(UnUploadCosplayTable.COSPLAY_ID + " =? "
                                    + " AND " + UnUploadCosplayTable.FLAG + " = " + flag
                                    + " AND " + UnUploadCosplayTable.FILE_PATH + " =?",
                            new String[]{ucid, path});
            ops.add(del.build());
            BaseApplication.context().getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    /**
     * 如果upload_ucid不为空,则上传upload_ucid指定的文件
     * 否则,开始上传所有未上传的文件
     */
    public void startUpload(Activity activity, String upload_ucid, IListener listener) {
        if (!TDevice.hasInternet()) {
            if (listener != null) {
                listener.onErr(null);
            }
            return;
        }

        Cursor cursor = null;
        try {
            boolean hasUcid = !StringUtils.isEmpty(upload_ucid);
            ContentResolver contentResolver = BaseApplication.context().getContentResolver();
            if (!hasUcid) {
                cursor = contentResolver.query(UnUploadCosplayTable.UN_UPLOAD_COSPLAY_CACHE_URI, null,
                        null, null, null);
            } else {
                cursor = contentResolver.query(UnUploadCosplayTable.UN_UPLOAD_COSPLAY_CACHE_URI, null,
                        UnUploadCosplayTable.COSPLAY_ID + " = ?", new String[]{upload_ucid}, null);
            }

            if (cursor == null) {
                MyLog.i("ljc: startUpload cursor=null" );
                return;
            }

            while (cursor.moveToNext()) {
                String ucid = cursor.getString(cursor.getColumnIndex(UnUploadCosplayTable.COSPLAY_ID));
                int flag = cursor.getInt(cursor.getColumnIndex(UnUploadCosplayTable.FLAG));
                String path = cursor.getString(cursor.getColumnIndex(UnUploadCosplayTable.FILE_PATH));
                int retry_times = cursor.getInt(cursor.getColumnIndex(UnUploadCosplayTable.RETRY_TIMES));
                long last_fail_time = cursor.getLong(cursor.getColumnIndex(UnUploadCosplayTable.LAST_FAIL_TIME));

                if (hasUcid) {
                    // 查找指定未上传或上传失败的大咖秀,重传[单次]
//                    saveUnuploadFiles2DataBase(flag, ucid, path, 0, System.currentTimeMillis());
                    uploadFiles(activity, flag, upload_ucid, path, 0, last_fail_time, listener);
                } else {
                    // 遍历未上传的大咖秀,重传
                    if (canStart(retry_times, last_fail_time)) {
//                        saveUnuploadFiles2DataBase(flag, ucid, path, 0, System.currentTimeMillis());
                        uploadFiles(activity, flag, ucid, path, 0, last_fail_time, null);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * 上传单个文件
     * @param activity 回调用activity
     * @param flag 1:效果图 2:工程文件 3:音频文件 4:帖子图片文件
     * @param ucid
     * @param path filePath
     * @param retry_times 重试次数
     * @param last_fail_time 上次上传失败时间
     * @param listener 回调
     */
    public void uploadFiles(Activity activity, int flag, String ucid,
                             String path, final int retry_times, long last_fail_time,
                            IListener listener) {

        if (retry_times >= 3) {
            saveUnuploadFiles2DataBase(flag, ucid, path, retry_times, System.currentTimeMillis());
            MyLog.i("ljc: post upload fail event....");
            if (listener == null) {
                EventBus.getDefault().post(new CosplayUploadStatus(ucid, flag, false));
            } else {
                listener.onErr(null);
            }
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StickerManager.getInstance().uploadFile2Oss(activity, ucid, flag, path, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.e("上传文件成功 path=[" + path + "]  @ " + TimeUtils.getCurrentTimeInString());
                        deleteSucceedFilesFromDataBase(flag, ucid, path);
                        if (flag != DiscoveryConstants.TYPE_PHOTO) {
                            deleteUploadedFile(path);
                        }
                        MyLog.i("ljc: post upload succeed event....");
                        if (listener == null) {
                            EventBus.getDefault().post(new CosplayUploadStatus(ucid, flag, true));
                        } else {
                            listener.onSuccess(null);
                        }
                    }

                    @Override
                    public void onErr(Object obj) {
                        MyLog.e("上传文件失败 onErr  @ucid=" + ucid + " @flag=" + flag + " @retry_times=" + retry_times);
                        int times = retry_times;
                        times++;
                        uploadFiles(activity, flag, ucid, path, times, last_fail_time, listener);
                    }

                    @Override
                    public void onNoNetwork() {
                        MyLog.e("上传文件失败 onErr  @ucid=" + ucid + " @flag=" + flag + " @retry_times=" + retry_times);
                        int times = retry_times;
                        times++;
                        uploadFiles(activity, flag, ucid, path, times, last_fail_time, listener);
                    }
                });
            }
        });
    }

    private void deleteUploadedFile(String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 5s后删除是为了在关注页面时后台下发数据未准备好时显示的本地图,5s后刷新时如果后台仍未准备好,则可能会有图->无图.准备好,则能看到后台的图了. 优化体验用
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FileUtil.getInst().delete(new File(path));
            }
        }).start();
    }

    public boolean isMyCosplay(CosplayInfo cosplayInfo) {
        if (cosplayInfo.getAuthor() == null
                || !AppContext.getInstance().isLogin()) {
            return false;
        }
        return ClientInfo.getUID().equalsIgnoreCase(cosplayInfo.getAuthor().getUId());
    }

    /** 判断我的图片是否上传成功(效果图和工程文件都上传完毕,且缓存被清除) */
    public boolean isCosplayUploaded(CosplayInfo cosplayInfo, boolean judgeFile) {
        if (cosplayInfo == null || StringUtil.isNullOrEmpty(cosplayInfo.getUcid())){
            return true;
        }
        if (!isMyCosplay(cosplayInfo)) {
            return true;
        }

        if (judgeFile) {
            File pic = new File(getCosplayElementPath(cosplayInfo.getUcid(), 1));
            if (pic != null && pic.exists()) {
                // 本地效果图缓存还在时,先显示本地缓存效果图
                return false;
            } else {
                File prj = new File(getCosplayElementPath(cosplayInfo.getUcid(), 2));
                if (prj != null && prj.exists()) {
                    // 工程文件上传中或者上传失败
                    return false;
                } else {
                    return true;
                }
            }
        }

        return true;
    }

    /**
     * 获取大咖秀的效果图或者工程文件的filePath
     *
     * @param flag 1:效果图 2:工程文件 3:音频文件
     */
    public String getCosplayElementPath(String ucid, int flag) {
        if (StringUtil.isNullOrEmpty(ucid)) return "";
        return BitmapUtil.BITMAP_CACHE + "/" + ucid + (flag == 1 ? ".jpg" : (flag == 2 ? ".moi" : ".amr"));
    }
    /**
     * 策略判断是否可以开始上传, 由已经重试的次数,上次失败时间等等决定
     * 1H内重试次数不能超过3次
     * @return
     */
    private boolean canStart(int retry_times, long last_fail_time) {
        long current = System.currentTimeMillis();
        if (retry_times >= 3 && (current - last_fail_time) < ONE_HOUR) {
            return false;
        }
        return true;
    }

    /**
     * 我要发图
     *
     * @param context
     * @param stickPackageId 话题相关的贴纸包ID
     * @param topicId 话题ID
     * @param topicType 话题类型
     * @param topicName 话题名称
     */
    public void makeCosplay(Context context, String stickPackageId, String topicId, String topicType, String topicName) {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }
        StickPreference.getInstance().setJoinTopicName(topicName);
        StickPreference.getInstance().setJoinTopicID(topicId);
        MyLog.i("话题,发图的话题id="
                        + StickPreference.getInstance().getJoinTopicID()
                        + ", name=" + StickPreference.getInstance().getJoinTopicName()
        );

        StickerManager.getInstance().getStickerDetail(stickPackageId, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    onTopicStickGot(context, (StickerPackage) obj);

                    // 进入拍照页面
                    openCamera(context, null);
                } else {
                    AppContext.getInstance().showToast(R.string.get_topic_cosplay_failed);
                }
            }

            @Override
            public void onErr(Object obj) {
                MyLog.i("join topic spid ERR= ");
                AppContext.getInstance().showToast(R.string.get_topic_cosplay_failed);
            }

            @Override
            public void onNoNetwork() {
                AppContext.getInstance().showToast(R.string.no_network);
            }
        });

    }

    /**
     * 获取话题对应的贴纸包成功后,下载之
     * @param context
     * @param mStickerPackage
     */
    private void onTopicStickGot(Context context, StickerPackage mStickerPackage) {
        if (mStickerPackage == null) return;
        CacheManager.saveObject(context, mStickerPackage, StickerConstants.STICKER_DETAIL_CACHE_PREFIX + mStickerPackage.getStickerPackageId());

        StickPreference.getInstance().setJoinTopicStickerpackageId(mStickerPackage.getStickerPackageId());
        MyLog.i("join topic spid= " + mStickerPackage.getStickerPackageId());
        StickerManager.getInstance().downloadTopicSticker(mStickerPackage, new Callback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish(int result) {
                if (result != 1) {
                    MyLog.i("join topic sp download failed");
                } else {
                    MyLog.i("join topic sp download success");
                }
            }
        });

    }


    public static class CosplayUploadStatus {
        private String mUcid;
        private int mFlag;
        private boolean mSuceed;

        public CosplayUploadStatus(String ucid, int flag, boolean succeed) {
            this.mUcid = ucid;
            this.mFlag = flag;
            this.mSuceed = succeed;
        }

        public String getUcid() {
            return mUcid;
        }

        public int getFlag() {
            return mFlag;
        }

        public boolean isSuceed() {
            return mSuceed;
        }
    }
}

package com.moinapp.wuliao.util;

import android.content.Context;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.keyboard.utils.DefEmoticons;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EmoticonsUtils {

    public static final ILogger MyLog = LoggerFactory.getLogger("emj");
    /**
     * 初始化表情数据库
     * @param context
     */
    public static void initEmoticonsDB(final Context context) {
        final DBHelper dbHelper = DBHelper.getInstance(context);

        if (!Utils.isInitDb(context)) {
            doInitEmoticonsDB(context);
        } else {
            if(DBHelper.isUpgrade()) {
                if (DBHelper.getVERSION() == 3) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 如果是预制文字气泡贴纸
                            long startTime = System.currentTimeMillis();
                            MyLog.i("数据库升级到3的文件解压和DB初始化开始 " + startTime);
                            /**
                             * FROM FILE
                             */
                            final String folderPath = BitmapUtil.BITMAP_STICKRES + "default";//default 是zip的名字
                            MyLog.i("folderPath=" + folderPath);
                            try {
                                FileUtil.unzip(
                                        context.getAssets().open("default.zip"),
                                        folderPath,
                                        new IListener() {
                                            @Override
                                            public void onSuccess(Object obj) {
                                                MyLog.i("unzip success=" + folderPath);
                                                File path = new File(folderPath);
                                                if (!path.exists() || !path.isDirectory())
                                                    return;
                                                File[] files = path.listFiles();
                                                if (files != null) {
                                                    for (int i = 0; i < files.length; i++) {
                                                        File file = files[i];
                                                        MyLog.i("unzip success=" + file.getName());

                                                        if (!file.getName().equals("default5.xml")) {
                                                            file.delete();// 用完删除
                                                            continue;
                                                        }

                                                        XmlUtil xmlUtil = new XmlUtil(context);
                                                        EmoticonSetBean bean = xmlUtil.ParserXml(
                                                                xmlUtil.getXmlFromSD(file.getAbsolutePath()), BitmapUtil.BITMAP_STICKRES);
                                                        // 也可以在xml中配置,15比较合适
                                                        bean.setUid(DefEmoticons.DEFAULT_EMOJISET_UID);
                                                        bean.setOrder(1000);
                                                        bean.setItemPadding(15);
                                                        bean.setVerticalSpacing(5);
                                                        bean.setIconUri("file://" + BitmapUtil.BITMAP_STICKRES + bean.getId() + "/" + bean.getIconUri());
                                                        // 如果是stickType=1正常类型的就不能这么删除了啊，好像也能
                                                        int dwx = dbHelper.deleteEmoticonSet(bean.getId());
                                                        // 清空以前的旧文件
                                                        FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(bean.getId()));
                                                        long lwx = dbHelper.insertEmoticonSet(bean);
//                                        WowoPreference.getInstance().setDefaultEmojisetId("default");
                                                        MyLog.i("from file default.zip: " + bean.toString());
                                                        MyLog.i("delete count: " + dwx);
                                                        MyLog.i("from file default.zip count: " + lwx + ", listSize=" + bean.getEmoticonList().size());
                                                        file.delete();// 用完删除
                                                    }
                                                }
                                                //删除默认资源文件夹
                                                FileUtil.removeFolder(folderPath);
                                            }

                                            @Override
                                            public void onNoNetwork() {

                                            }

                                            @Override
                                            public void onErr(Object object) {
                                                MyLog.i("unzip onErr=" + folderPath);
                                            }
                                        });
                            } catch (IOException e) {
                                MyLog.e(e);
                            }

                            /**
                             * FROM ASSETS
                             */
                            // 注意,先将数据库文件插入后,然后解压文件,否则前面的 FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(bean.getId())); 会清空掉解压文件
                            try {
                                String[] resZips = context.getAssets().list("sticker");
                                if (resZips != null && resZips.length > 0) {
                                    for (int i = 0; i < resZips.length; i++) {
                                        MyLog.i("sticker zip =" + resZips[i]);
                                        if (!resZips[i].equals("5656a9990cf25cfee6533ce4.zip")) {
                                            continue;
                                        }
                                        String zipAssetPath = "sticker/" + resZips[i];
                                        String outPath = BitmapUtil.BITMAP_STICKRES + resZips[i];
                                        boolean copyOk = FileUtil.getInst().copyAssetFileToFiles(context, zipAssetPath, new File(outPath));
                                        if (copyOk) {
                                            MyLog.i("sticker zip copyOK ");
                                            boolean unzipok = FileUtil.unzip(new FileInputStream(outPath), outPath.substring(0, outPath.lastIndexOf(".")));
                                            MyLog.i("sticker zip unzip result = " + unzipok);
                                            if (unzipok) {
                                                FileUtil.getInst().delete(new File(outPath));
//                                        MyLog.i("sticker zip unzip lengthH = " + new File(outPath.substring(0, outPath.lastIndexOf("."))).listFiles().length);
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                MyLog.e(e);
                            }

                            MyLog.i("数据库升级到3的文件解压和DB初始化结束, 用时 " + (System.currentTimeMillis() - startTime));
                        }
                    }).start();
                } else if (DBHelper.getVERSION() == 4) {
                    doInitEmoticonsDB(context);
                }
                DBHelper.setUpgrade(false);
            } else {
                long updatetime = dbHelper.queryEmoticonSetUpdatetime(2, null);
                if (updatetime < Long.parseLong(context.getResources().getString(R.string.sticker2_updatetime))) {
                    doInitEmoticonsDB(context);
                }
            }
        }
    }

    private static void doInitEmoticonsDB(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final DBHelper dbHelper = DBHelper.getInstance(context);

                /**
                 * FROM DRAWABLE
                 */
//                    ArrayList<EmoticonBean> emojiArray = ParseData(DefEmoticons.emojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.DRAWABLE);
//                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
//                    emojiEmoticonSetBean.setIconUri("drawable://icon_emoji");
//                    emojiEmoticonSetBean.setItemPadding(20);
//                    emojiEmoticonSetBean.setVerticalSpacing(10);
//                    emojiEmoticonSetBean.setShowDelBtn(true);
//                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
//                    long emojic = dbHelper.insertEmoticonSet(emojiEmoticonSetBean);
//                    MyLog.i("from drawable emoji count: " + emojic + ", listSize=" + emojiArray.size());

                /**
                 * FROM FILE
                 */
//                    String filePath = Environment.getExternalStorageDirectory() + "/default";
                final String folderPath = BitmapUtil.BITMAP_STICKRES + "default";//default 是zip的名字
                MyLog.i("folderPath=" + folderPath);
                try{
                    FileUtil.unzip(
                            context.getAssets().open("default.zip"),
                            folderPath,
                            new IListener() {
                                @Override
                                public void onSuccess(Object obj) {
                                    MyLog.i("unzip success=" + folderPath);
                                    File path = new File(folderPath);
                                    if(!path.exists() || !path.isDirectory())
                                        return;
                                    File[] files = path.listFiles();
                                    if(files != null) {
                                        for (int i = 0; i < files.length; i++) {
                                            File file = files[i];

                                            XmlUtil xmlUtil = new XmlUtil(context);
                                            EmoticonSetBean bean = xmlUtil.ParserXml(
                                                    xmlUtil.getXmlFromSD(file.getAbsolutePath()), BitmapUtil.BITMAP_STICKRES);
                                            // 也可以在xml中配置,15比较合适
                                            bean.setUid(DefEmoticons.DEFAULT_EMOJISET_UID);
                                            bean.setOrder(1000);
                                            bean.setItemPadding(15);
                                            bean.setVerticalSpacing(5);
                                            bean.setIconUri("file://" + BitmapUtil.BITMAP_STICKRES + bean.getId() + "/" + bean.getIconUri());
                                            // 如果是stickType=1正常类型的就不能这么删除了啊，好像也能
                                            int dwx;
                                            if (bean.getStickType() == 1) {
                                                dwx = dbHelper.deleteEmoticonSet(bean.getId());
                                            } else {
                                                dwx = dbHelper.deleteDefaultStickerSet(bean.getStickType());
                                            }
                                            // 清空以前的旧文件
                                            FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(bean.getId()));
                                            long lwx = dbHelper.insertEmoticonSet(bean);
//                                        WowoPreference.getInstance().setDefaultEmojisetId("default");
                                            MyLog.i("from file default.zip: " + bean.toString());
                                            MyLog.i("delete count: " + dwx);
                                            MyLog.i("from file default.zip count: " + lwx + ", listSize=" + bean.getEmoticonList().size());
                                            file.delete();// 用完删除
                                        }
                                    }
                                    //删除默认资源文件夹
                                    FileUtil.removeFolder(folderPath);
                                }

                                @Override
                                public void onNoNetwork() {

                                }

                                @Override
                                public void onErr(Object object) {
                                    MyLog.i("unzip onErr=" + folderPath);
                                }
                            });
                }catch(IOException e){
                    MyLog.e(e);
                }

                /**
                 * FROM ASSETS
                 */
                // 注意,先将数据库文件插入后,然后解压文件,否则前面的 FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(bean.getId())); 会清空掉解压文件
                try {
                    String[] resZips = context.getAssets().list("sticker");
                    if(resZips != null && resZips.length > 0) {
                        for (int i = 0; i < resZips.length; i++) {
                            String zipAssetPath = "sticker/" + resZips[i];
                            String outPath = BitmapUtil.BITMAP_STICKRES;//2.7 版本直接放在 BITMAP_STICKRES 目录下.  + resZips[i];
                            boolean copyOk = FileUtil.getInst().copyAssetFileToFiles(context, zipAssetPath, new File(outPath));
                            if(copyOk) {
                                MyLog.i("sticker zip copyOK ");
                                boolean unzipok = FileUtil.unzip(new FileInputStream(outPath), outPath.substring(0, outPath.lastIndexOf(".")));
                                MyLog.i("sticker zip unzip result = " + unzipok);
                                if(unzipok) {
                                    FileUtil.getInst().delete(new File(outPath));
//                                        MyLog.i("sticker zip unzip lengthH = " + new File(outPath.substring(0, outPath.lastIndexOf("."))).listFiles().length);
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    MyLog.e(e);
                }

//                    ArrayList<EmoticonBean> xhsfaceArray = ParseData(xhsemojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
//                    EmoticonSetBean xhsEmoticonSetBean = new EmoticonSetBean("xhs", 3, 7);
//                    xhsEmoticonSetBean.setIconUri("assets://xhsemoji_19.png");
//                    xhsEmoticonSetBean.setItemPadding(20);
//                    xhsEmoticonSetBean.setVerticalSpacing(10);
//                    xhsEmoticonSetBean.setShowDelBtn(true);
//                    xhsEmoticonSetBean.setEmoticonList(xhsfaceArray);
//                    long xhs = dbHelper.insertEmoticonSet(xhsEmoticonSetBean);
//                    MyLog.i("from assets xhs count: " + xhs + ", listSize=" + xhsfaceArray.size());
//
//                    ArrayList<EmoticonBean> dkxfaceArray = ParseData(dkxemojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
//                    EmoticonSetBean dkxEmoticonSetBean = new EmoticonSetBean("dakaxiu", 2, 4);
//                    dkxEmoticonSetBean.setIconUri("assets://xhsemoji_19.png");
//                    dkxEmoticonSetBean.setItemPadding(20);
//                    dkxEmoticonSetBean.setVerticalSpacing(10);
//                    dkxEmoticonSetBean.setShowDelBtn(true);
//                    dkxEmoticonSetBean.setEmoticonList(dkxfaceArray);
//                    long dkx = dbHelper.insertEmoticonSet(dkxEmoticonSetBean);
//                    MyLog.i("from assets 大咖秀 count: " + dkx + ", listSize=" + dkxfaceArray.size());


                /**
                 * FROM HTTP/HTTPS
                 */


                /**
                 * FROM CONTENT
                 */

                /**
                 * FROM USER_DEFINED
                 */

                dbHelper.cleanup();
                Utils.setIsInitDb(context, true);
            }
        }).start();
    }

    public static EmoticonsKeyboardBuilder getSimpleBuilder(Context context) {

        DBHelper dbHelper = DBHelper.getInstance(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryEmoticonSet(ClientInfo.getUID()/*"emoji","xhs"*/);//这两个预制表情不要了
        dbHelper.cleanup();
//
//        ArrayList<AppBean> mAppBeanList = new ArrayList<AppBean>();
//        String[] funcArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func);
//        String[] funcIconArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
//        for (int i = 0; i < funcArray.length; i++) {
//            AppBean bean = new AppBean();
//            bean.setId(i);
//            bean.setIcon(funcIconArray[i]);
//            bean.setFuncName(funcArray[i]);
//            mAppBeanList.add(bean);
//        }

        if (mEmoticonSetBeanList == null) {
            mEmoticonSetBeanList = new ArrayList<>();
        }
        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context) {
        return getBuilder(context, -1);
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context, int type) {

        MyLog.i("EmoticonsKeyboardBuilder，getBuilder start:" + type);
        DBHelper dbHelper = DBHelper.getInstance(context);
        MyLog.i("EmoticonsKeyboardBuilder，getBuilder queryAllEmoticonSet ");
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = null;
        if (type < 0 ) {
            mEmoticonSetBeanList = dbHelper.queryAllEmoticonSet(ClientInfo.getUID());
        } else if (StickerUtils.isRecent(type)) {
            mEmoticonSetBeanList = dbHelper.queryEmoticonSetByType(DefEmoticons.DEFAULT_EMOJISET_UID, type);
        }
        checkEmojiStatus(mEmoticonSetBeanList);
        if(mEmoticonSetBeanList == null || mEmoticonSetBeanList.size() == 0) {
            MyLog.i("EmoticonsKeyboardBuilder，getBuilder mEmoticonSetBeanList==null; ? " + (mEmoticonSetBeanList==null));
            mEmoticonSetBeanList = new ArrayList<>();
        } else {
            MyLog.i("EmoticonsKeyboardBuilder，getBuilder end:SetListsize=" + mEmoticonSetBeanList.size());
        }
        dbHelper.cleanup();

//        ArrayList<AppBean> mAppBeanList = new ArrayList<AppBean>();
//        String[] funcArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func);
//        String[] funcIconArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
//        for (int i = 0; i < funcArray.length; i++) {
//            AppBean bean = new AppBean();
//            bean.setId(i);
//            bean.setIcon(funcIconArray[i]);
//            bean.setFuncName(funcArray[i]);
//            mAppBeanList.add(bean);
//        }

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    /**防止用户在手动删除sd卡中的表情图片后，发帖和回复时找不到图*/
    public static void checkEmojiStatus(ArrayList<EmoticonSetBean> mEmoticonSetBeanList) {
        MyLog.i("checkEmojiStatus queryAllEmoticonSet ");
        if(mEmoticonSetBeanList == null || mEmoticonSetBeanList.size() < 1) {
            return;
        }
        for (EmoticonSetBean set:mEmoticonSetBeanList) {
            MyLog.i("防止用户在手动删除sd卡中的表情图片后，发帖和回复时找不到图:"+set.getName());
            if(set != null) {
                if(!StringUtil.isNullOrEmpty(set.getId())) {
                    final String parent = set.getIconUri().replaceFirst("file://", "");
                    FileUtil.createFolder(parent);
                    File file = new File(parent);
                    if(!file.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MyLog.i("download emojiResource icon: " + parent);
//                                final String url = StickerUtils.getStickerPackageIconUrl(set.getIconUrl());
                                final String url = set.getIconUrl();
                                MyLog.i("download emojiResource icon from: "+url);
                                HttpUtil.download(url, parent);
                            }
                        }).start();
                    }
                }
                ArrayList<EmoticonBean> beanList = set.getEmoticonList();
                if (beanList != null && beanList.size() > 0) {
                    for (EmoticonBean bean : beanList) {
                        if (bean != null) {

                            final String iconuri = bean.getIconUri().replaceFirst("file://", "");
                            File icon = new File(iconuri);
                            if (!icon.exists()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyLog.i("download emoji icon: " + iconuri);
                                        final String iconurl = StickerUtils.getSingleStickerIconUrl(bean.getIconUrl());
                                        MyLog.i("download emoji icon from: " + iconurl);
                                        HttpUtil.download(iconurl, iconuri);
                                    }
                                }).start();
                            }

                            // 3.2.6内取消主动下载大图的逻辑,让用户手动,主动下载
/*                            final String gifuri = bean.getGifUri().replaceFirst("file://", "");
                            File gif = new File(iconuri);
                            if (!gif.exists()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyLog.i("download emoji gif: " + gifuri);
                                        final String gifurl = StickerUtils.getSingleStickerPictureUrl(bean.getGifUrl());
                                        MyLog.i("download emoji gif from: " + gifurl);
                                        HttpUtil.download(gifurl, gifuri);
                                    }
                                }).start();
                            }*/
                        }
                    }
                }
            }
        }
    }
//
//    public static ArrayList<EmoticonBean> ParseData(String[] arry, long eventType, ImageBase.Scheme scheme) {
//        try {
//            ArrayList<EmoticonBean> emojis = new ArrayList<EmoticonBean>();
//            for (int i = 0; i < arry.length; i++) {
//                if (!TextUtils.isEmpty(arry[i])) {
//                    String temp = arry[i].trim().toString();
//                    String[] text = temp.split(",");
//                    if (text != null && text.length == 2) {
//                        String fileName = null;
//                        if (scheme == ImageBase.Scheme.DRAWABLE) {
//                            if(text[0].contains(".")){
//                                fileName = scheme.toUri(text[0].substring(0, text[0].lastIndexOf(".")));
//                            }
//                            else {
//                                fileName = scheme.toUri(text[0]);
//                            }
//                        } else {
//                            fileName = scheme.toUri(text[0]);
//                        }
//                        String content = text[1];
//                        EmoticonBean bean = new EmoticonBean(eventType, fileName, content);
//                        emojis.add(bean);
//                    }
//                }
//            }
//            return emojis;
//        } catch (
//                Exception e
//                )
//
//        {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    /**
    ASSETS表情
     */
//    public static String[] xhsemojiArray = {
//            "xhsemoji_1.png,[无语]",
//            "xhsemoji_2.png,[汗]",
//            "xhsemoji_3.png,[瞎]",
//            "xhsemoji_4.png,[口水]",
//            "xhsemoji_5.png,[酷]",
//            "xhsemoji_6.png,[哭] ",
//            "xhsemoji_7.png,[萌]",
//            "xhsemoji_8.png,[挖鼻孔]",
//            "xhsemoji_9.png,[好冷]",
//            "xhsemoji_10.png,[白眼]",
//            "xhsemoji_11.png,[晕]",
//            "xhsemoji_12.png,[么么哒]",
//            "xhsemoji_13.png,[哈哈]",
//            "xhsemoji_14.png,[好雷]",
//            "xhsemoji_15.png,[啊]",
//            "xhsemoji_16.png,[嘘]",
//            "xhsemoji_17.png,[震惊]",
//            "xhsemoji_18.png,[刺瞎]",
//            "xhsemoji_19.png,[害羞]",
//            "xhsemoji_20.png,[嘿嘿]",
//            "xhsemoji_21.png,[嘻嘻]"
//    };
    public static String[] dkxemojiArray = {
            "dkx_1.png,[制作一个]",
    };

}

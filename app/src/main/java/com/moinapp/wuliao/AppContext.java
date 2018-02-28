package com.moinapp.wuliao;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.moinapp.wuliao.api.ApiHttpClient;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Location;
import com.moinapp.wuliao.bean.User;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.DataCleanManager;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.util.MethodsCompat;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TLog;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.Properties;
import java.util.UUID;

import static com.moinapp.wuliao.AppConfig.KEY_FRITST_START;
import static com.moinapp.wuliao.AppConfig.KEY_LOAD_IMAGE;
import static com.moinapp.wuliao.AppConfig.KEY_NIGHT_MODE_SWITCH;
import static com.moinapp.wuliao.AppConfig.KEY_TWEET_DRAFT;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends BaseApplication {
    protected static AppContext       mInstance;

    //本地缓存的用户信息key
    public static final String UID = "user.uid";
    public static final String NICKNAME = "user.nickname";
    public static final String AVATAR = "user.avatar";
    public static final String USERNAME = "user.username";
    public static final String LOCATION = "user.location";
    public static final String AGE = "user.age";
    public static final String CONTACT = "user.contact";
    public static final String SEX = "user.sex";
    public static final String COSPLAYNUM = "user.cosplaynum";
    public static final String TAGNUM = "user.tagnum";
    public static final String IDOLNUM = "user.idolnum";
    public static final String FANNUM = "user.fannum";
    public static final String SIGNATURE = "user.signature";

    public static final int PAGE_SIZE = 20;// 默认分页大小

    private static AppContext instance;

    private int loginUid;

    private boolean login;


    public AppContext(){
        mInstance = this;
    }

    public static AppContext getApp() {
        if (mInstance != null && mInstance instanceof AppContext) {
            return (AppContext) mInstance;
        } else {
            mInstance = new AppContext();
            mInstance.onCreate();
            return (AppContext) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
//        initLogin();

        Thread.setDefaultUncaughtExceptionHandler(AppException
                .getAppExceptionHandler(this));
    }

    private void init() {
        // 初始化网络请求
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);

        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));

        // Log控制器
        KJLoger.openDebutLog(true);
        TLog.DEBUG = BuildConfig.DEBUG;

        // Bitmap缓存地址, 切记,不要和ImageLoader的diskCache路径设为一样的,否则缓存会被清除
//        BitmapConfig.CACHEPATH = "MOIN/tmpcache";
    }

    /**登录用户信息初始化*/
    private void initLogin() {
        User user = getLoginUser();
        if (null != user && user.getId() > 0) {
            login = true;
            loginUid = user.getId();
        } else {
            this.cleanLoginInfo();
        }
    }

    /**
     * 获得当前app运行的AppContext
     * 
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     * 
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     * 
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     * 
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 保存登录信息
     * 
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final UserInfo user) {
        this.loginUid = user.getId();
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty(UID, String.valueOf(user.getUId()));
                if (user.getNickname() != null)
                    setProperty(NICKNAME, user.getNickname());
                if (user.getAvatar() != null && user.getAvatar().getUri() != null)
                    setProperty(AVATAR, user.getAvatar().getUri());// 用户头像-url
                setProperty(USERNAME, StringUtil.nullToEmpty(user.getUsername()));
                setProperty(SIGNATURE, StringUtil.nullToEmpty(user.getSignature()));
                if (user.getLocation() != null && user.getLocation().getCity() != null)
                    setProperty(LOCATION, user.getLocation().getCity());
                if (user.getAges() != null)
                    setProperty(AGE, user.getAges());
                if (user.getContact() != null)
                    setProperty(CONTACT, user.getContact());
                if (user.getSex() != null)
                    setProperty(SEX, user.getSex());
                if (user.getCosplayNum() >= 0)
                    setProperty(COSPLAYNUM, String.valueOf(user.getCosplayNum()));
                if (user.getTagNum() >= 0)
                    setProperty(TAGNUM, String.valueOf(user.getTagNum()));
                if (user.getIdolNum() >= 0)
                    setProperty(IDOLNUM, String.valueOf(user.getIdolNum()));
                if (user.getFansNum() >= 0)
                    setProperty(FANNUM, String.valueOf(user.getFansNum()));
            }
        });
    }

    /**
     * 更新用户信息
     * 
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final UserInfo user) {
        MineManager.getInstance().updateUserInfo(user, new IListener() {
            @Override
            public void onSuccess(Object obj) {

            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    /**
     * 获得登录用户的信息
     * 
     * @return
     */
    public User getLoginUser() {
        User user = new User();
        user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
        user.setName(getProperty("user.name"));
        user.setPortrait(getProperty("user.face"));
        user.setAccount(getProperty("user.account"));
        user.setLocation(getProperty("user.location"));
        user.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
        user.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
        user.setScore(StringUtils.toInt(getProperty("user.score"), 0));
        user.setFavoritecount(StringUtils.toInt(
                getProperty("user.favoritecount"), 0));
        user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
        user.setGender(getProperty("user.gender"));
        return user;
    }

    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        UserInfo user = new UserInfo();
        user.setUId(getProperty(UID));
        user.setUsername(getProperty(USERNAME));
        user.setNickname(getProperty(NICKNAME));
        user.setSignature(getProperty(SIGNATURE));
        BaseImage avatar = new BaseImage();
        avatar.setUri(getProperty(AVATAR));
        user.setAvatar(avatar);

        Location location = new Location();
        location.setCity(getProperty(LOCATION));
        user.setLocation(location);

        user.setSex(getProperty(SEX));
        user.setContact(getProperty(CONTACT));
        user.setAges(getProperty(AGE));

        try {
            if (!TextUtils.isEmpty(getProperty(COSPLAYNUM))) {
                user.setCosplayNum(Integer.parseInt(getProperty(COSPLAYNUM)));
            } else {
                user.setCosplayNum(0);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(getProperty(IDOLNUM))) {
                user.setIdolNum(Integer.parseInt(getProperty(IDOLNUM)));
            } else {
                user.setIdolNum(0);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(getProperty(TAGNUM))) {
                user.setTagNum(Integer.parseInt(getProperty(TAGNUM)));
            } else {
                user.setTagNum(0);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(getProperty(FANNUM))) {
                user.setFansNum(Integer.parseInt(getProperty(FANNUM)));
            } else {
                user.setFansNum(0);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty(UID, USERNAME, NICKNAME, LOCATION,
                AVATAR, SEX, AGE, FANNUM, COSPLAYNUM, TAGNUM, IDOLNUM);
    }

    public int getLoginUid() {
        return loginUid;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(ClientInfo.getUID()) &&
                !TextUtils.isEmpty(ClientInfo.getUserName());
    }

    /**
     * 用户注销
     */
    public void Logout() {
        cleanLoginInfo();
        ApiHttpClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;

        ClientInfo.setUID(null);
        ClientInfo.setPassport(null);
        ClientInfo.setUserName(null);
        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);
    }

    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        new KJBitmap().cleanCache();
    }

    public static void setLoadImage(boolean flag) {
        set(KEY_LOAD_IMAGE, flag);
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     * 
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static String getTweetDraft() {
        return getPreferences().getString(
                KEY_TWEET_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setTweetDraft(String draft) {
        set(KEY_TWEET_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static String getNoteDraft() {
        return getPreferences().getString(
                AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }

    //夜间模式
    public static boolean getNightModeSwitch() {
        return getPreferences().getBoolean(KEY_NIGHT_MODE_SWITCH, false);
    }

    // 设置夜间模式
    public static void setNightModeSwitch(boolean on) {
        set(KEY_NIGHT_MODE_SWITCH, on);
    }
}

package com.moinapp.wuliao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午5:29:00
 * 
 */
public class AppConfig {

    private final static String APP_CONFIG = "config";

    public final static String CONF_COOKIE = "cookie";

    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

    public static final String KEY_LOAD_IMAGE = "KEY_LOAD_IMAGE";
    public static final String KEY_NOTIFICATION_ACCEPT = "KEY_NOTIFICATION_ACCEPT";
    public static final String KEY_NOTIFICATION_SOUND = "KEY_NOTIFICATION_SOUND";
    public static final String KEY_NOTIFICATION_VIBRATION = "KEY_NOTIFICATION_VIBRATION";
    public static final String KEY_NOTIFICATION_DISABLE_WHEN_EXIT = "KEY_NOTIFICATION_DISABLE_WHEN_EXIT";
    public static final String KEY_CHECK_UPDATE = "KEY_CHECK_UPDATE";
    public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";

    public static final String KEY_TWEET_DRAFT = "KEY_TWEET_DRAFT";
    public static final String KEY_NOTE_DRAFT = "KEY_NOTE_DRAFT";

    public static final String KEY_FRITST_START = "KEY_FRIST_START";

    public static final String KEY_NIGHT_MODE_SWITCH="night_mode_switch";

    // 保存原图开关
    public static final String KEY_SAVE_ORIGINAL = "save_original_pic";

    // 正式环境和测试环境联网的开关
    public static final String KEY_TEST_ENV_SWICH = "test_env_network_switch";
    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "OSChina"
            + File.separator + "osc_img" + File.separator;

    // 默认存放文件下载的路径
    public final static String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "OSChina"
            + File.separator + "download" + File.separator;

    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }

    //***************环境配置******************
    public static boolean isDebug() {
        return APP_FIXED_TPYE == VERSION_STYLE_TEST;//CommonDefine.debug;
    }

    /** 是否将日志写入文件 **/
    public static boolean isWriteFile() {
        return false;
    }

    /** 是否使用本地路径，测试时可以使用此路径打印日记(注：这个对特殊机型， 在开关机是非常有用)  **/
    public static boolean isUseLocalPath() {
        return false;
    }

    //***************环境配置******************
    // 不同版本对应的标志（和服务器地址有关）
    public final static int VERSION_STYLE_RELEASE 		= 0;		// Release
    public final static int VERSION_STYLE_TEST 			= 1;		// Verify

    private final static String BASE_URL_HEAD_RELEASE = "https://prd.mo-image.com/v69/%s";
    private final static String BASE_URL_HEAD_TEST = "https://dev.mo-image.com/v69/%s";

    private final static String BASE_SHARE_URL_RELEASE = "http://prd.mo-image.com/";
    private final static String BASE_SHARE_URL_TEST = "http://dev.mo-image.com/";
    private final static String BASE_IMAGE_URL_TEST = "http://devimg.mo-image.com/";
    private final static String BASE_IMAGE_URL_RELEASE = "http://prdimg.mo-image.com/";

    private final static String ENV_KEY = "moin_env";
    private final static String BUILD_NO_KEY = "build_number";
    private final static String CHANNEL_KEY = "UMENG_CHANNEL";
    private final static String ENV_VALUE_TEST = "verify";
    private final static String ENV_VALUE_RELEASE = "production";
    /** DEBUG 是否打开 **/
    private static boolean debug = true;

    private static int APP_FIXED_TPYE;	// 应用的固定开关,由manifest决定
    private static int VERSION_STYLE_TYPE;	// 测试环境和正式环境的开关,可更改
    private static String mBuildNo;
    private static String mChannel;

    /**
     * 每次出包时，需要来修改下测试环境和正是环境的配置
     */
    static{
//    	setStyleType(CommonDefine.VERSION_STYLE_RELEASE);
//        setStyleType();
//        setStyleType(VERSION_STYLE_RELEASE);
    }

    public static void setStyleType(){
        String s = getEnv();
        if (TextUtils.isEmpty(s)) {
            APP_FIXED_TPYE = VERSION_STYLE_RELEASE;
            return;
        }
        if (s.equalsIgnoreCase(ENV_VALUE_TEST)) {
            APP_FIXED_TPYE = VERSION_STYLE_TEST;
        } else {
            APP_FIXED_TPYE = VERSION_STYLE_RELEASE;
        }

        VERSION_STYLE_TYPE = AppContext.get(KEY_TEST_ENV_SWICH, (APP_FIXED_TPYE == VERSION_STYLE_TEST)) ?
                VERSION_STYLE_TEST : VERSION_STYLE_RELEASE;
    }

    /**
     * 获取manifest里面指定的环境
     * @return
     */
    static String getEnv() {
        ApplicationInfo appInfo = null;
        if (BaseApplication.context() == null) {
            return ENV_VALUE_RELEASE;
        }
        try {
            appInfo = BaseApplication.context().getPackageManager()
                    .getApplicationInfo(BaseApplication.context().getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo == null) return ENV_VALUE_RELEASE;
        String msg = appInfo.metaData.getString(ENV_KEY);

        mBuildNo = String.valueOf(appInfo.metaData.getInt(BUILD_NO_KEY));
        mChannel = appInfo.metaData.getString(CHANNEL_KEY);
        return msg;
    }

    public static int getBuildType() {
        switch(VERSION_STYLE_TYPE) {
            case VERSION_STYLE_RELEASE:	// Release
                return VERSION_STYLE_RELEASE;
            case VERSION_STYLE_TEST:	// 测试
                return VERSION_STYLE_TEST;
            default:
                return VERSION_STYLE_RELEASE;
        }
    }

    public static String getBaseUrl(){
        switch(VERSION_STYLE_TYPE) {
            case VERSION_STYLE_RELEASE:	// Release
                return BASE_URL_HEAD_RELEASE;
            case VERSION_STYLE_TEST:	// 测试
                return BASE_URL_HEAD_TEST;
            default:
                return BASE_URL_HEAD_RELEASE;
        }
    }

    public static String getBaseShareUrl(){
        switch(VERSION_STYLE_TYPE) {
            case VERSION_STYLE_RELEASE:	// Release
                return BASE_SHARE_URL_RELEASE;
            case VERSION_STYLE_TEST:	// 测试
                return BASE_SHARE_URL_TEST;
            default:
                return BASE_SHARE_URL_RELEASE;
        }
    }

    public static String getBaseImageUrl(){
        switch(VERSION_STYLE_TYPE) {
            case VERSION_STYLE_RELEASE:	// Release
                return BASE_IMAGE_URL_RELEASE;
            case VERSION_STYLE_TEST:	// 测试
                return BASE_IMAGE_URL_TEST;
            default:
                return BASE_IMAGE_URL_RELEASE;
        }
    }

    public static String getBaiduPushKey() {
        switch(VERSION_STYLE_TYPE) {
            case VERSION_STYLE_RELEASE:    // Release
                return Constants.BAIDU_APPKEY;
            case VERSION_STYLE_TEST:    // 测试
                return Constants.BAIDU_APPKEY_TEST;
            default:
                return Constants.BAIDU_APPKEY;
        }
    }

    public static String getBuildNo() {
        return mBuildNo;
    }

    public static String getCHANNEL() {
        return mChannel;
    }
}

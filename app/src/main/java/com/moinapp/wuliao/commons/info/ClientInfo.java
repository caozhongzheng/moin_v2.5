package com.moinapp.wuliao.commons.info;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.util.TDevice;

import java.util.Map;

public class ClientInfo {
	private static ILogger MyLog = LoggerFactory.getLogger("ImageResizer");
	private static IClientInfo sClientInfo;
	private static final int EDITION = 20;

	/**
	 * Baidu; 91; AnZhuo; MyApp; XiaoMi; WanDouJia;360;等渠道号
	 * */

	private static final String CHANNEL_WANDOUJIA = "WanDouJia";//豌豆荚1
	private static final String CHANNEL_MYAPP = "MyApp";//应用宝2
	private static final String CHANNEL_XIAOMI = "XiaoMi";//小米商店3
	private static final String CHANNEL_360 = "360";//360应用开发平台4
	private static final String CHANNEL_BAIDU = "Baidu";//百度手机助手5
	private static final String CHANNEL_HUAWEI = "Huawei";//华为开发者联盟6
	private static final String CHANNEL_ANZHI = "Anzhi";//安智市场7
	private static final String CHANNEL_APPCHINA = "AppChina";//应用汇8
	private static final String CHANNEL_MEIZU = "Meizu";//魅族应用中心9
	private static final String CHANNEL_LENOVO = "Lenovo";//联想开发者平台10
	private static final String CHANNEL_SUNING = "SuNing";//苏宁应用商店11
	private static final String CHANNEL_PP = "Pp";//PP助手(淘宝手机助手)12
	private static final String CHANNEL_YIYONGHUI = "Liqu";//易用汇(金立手机)13
	private static final String CHANNEL_10010 = "10010";//联通沃商店14
	private static final String CHANNEL_MGYAPP = "MgyApp";//应用酷15
	private static final String CHANNEL_MUMAYI = "Mumayi";//木蚂蚁16
	private static final String CHANNEL_SOGOU = "SoGou";//搜狗手机助手17
	private static final String CHANNEL_MOPO = "Mopo";//冒泡堂18
//	private static final String CHANNEL_10086 = "10086";//移动应用商场
//	private static final String CHANNEL_GFAN = "Gfan";//机锋市场
//	private static final String CHANNEL_NDUOA = "Nduoa";//N多网
//	private static final String CHANNEL_OPPO = "Oppo";//OPPO可可商店
//	private static final String CHANNEL_163 = "163";//网易应用中心
//	private static final String CHANNEL_LETV = "Letv";//乐视应用商店
//	private static final String CHANNEL_189 = "189";//电信商城
//
//	private static final String CHANNEL_91 = "91";//91安卓市场
//	private static final String CHANNEL_ANZHUO = "AnZhuo";//安卓网
	private static final String CHANNEL_MOIN = "Moin";


	private static final String CHANNEL = CHANNEL_MOIN;
	static {
//		NqTest.init();
		sClientInfo = ClientInfoFactory.getInstance();
	}

	public static int getBusinessId(){
		return sClientInfo.getBusinessId();
	}
	
	public static int getEditionId(){
		return TDevice.getVersionCode();
	}
	
	public static String getPackageName(){
		return sClientInfo.getPackageName();
	}
	
	public static boolean isGP() {
		return sClientInfo.isGP();
	}

	public static boolean hasLocalTheme(){
		return sClientInfo.hasLocalTheme();
	}
	
	public static boolean isUseBingSearchUrl(){
		return sClientInfo.isUseBingSearchUrl();
	}

	public static void onUpgrade(int lastVer){
		sClientInfo.onUpgrade(lastVer);
	}
	
	public static String getClientLanguage(Context context) {
		Resources resources = context.getResources();
		String lang = null;
		

		int resource_id = resources.getIdentifier("lang", "string",
				context.getPackageName());
		if (resource_id != 0) {
			try {
				lang = resources.getString(resource_id);
				MyLog.d("lang in resource file: " + lang);
			} catch (Exception e) {
				MyLog.e(e);
			}
		}
		if (!TextUtils.isEmpty(lang)){
			lang = lang.replace('_', '-');//防御性
			int n = lang.indexOf("-");
			if (n < 2){//语言代码至少2个字符
				lang = null;
			}
				
		}
		
		if (TextUtils.isEmpty(lang)){
			MyLog.e("client language is unknown!");
			lang = MobileInfo.getMobileLanguage(context);
		}
		
		return lang;
	}
	
//	public static String getChannelId() {
//		return CHANNEL;
//	}
	public static String getChannelId() {
		return AppConfig.getCHANNEL();
	}

	public static Map<String, Boolean> overrideModuleDefaults() {
		return sClientInfo.overrideModuleDefaults();
	}

	public static boolean isLoginUser(String mUid) {
		return getUID().equals(mUid);
	}

	public static UserInfo getLoginUser() {
		UserInfo loginUser = new UserInfo();
		loginUser.setUId(getUID());
		loginUser.setUsername(getUserName());
		return loginUser;
	}

	public static String getUID() {
		return CommonsPreference.getInstance().getUID();
	}

	public static void setUID(String uid) {
		CommonsPreference.getInstance().setUID(uid);
	}

	public static String getPassport() {
		return CommonsPreference.getInstance().getPassport();
	}

	public static void setUserName(String userName) {
		CommonsPreference.getInstance().setUsername(userName);
	}

	public static String getUserName() {
		return CommonsPreference.getInstance().getUserName();
	}

	public static void setPassport(String passport) {
		CommonsPreference.getInstance().setPassport(passport);
	}

	public static String getPushChannel() {
		return CommonsPreference.getInstance().getBaiduPushChannel();
	}

	public static void setPushChannel(String channel) {
		CommonsPreference.getInstance().setBaiduPushChannel(channel);
	}

	public static String getPushAppId() {
		return CommonsPreference.getInstance().getBaiduPushAppid();
	}

	public static void setPushAppId(String appid) {
		CommonsPreference.getInstance().setBaiduPushAppid(appid);
	}

	public static String getPushUserId() {
		return CommonsPreference.getInstance().getBaiduPushUserid();
	}

	public static void setPushUserId(String userid) {
		CommonsPreference.getInstance().setBaiduPushUserid(userid);
	}
}

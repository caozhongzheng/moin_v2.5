package com.moinapp.wuliao.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统信息操作类
 * 
 */
@SuppressLint("SimpleDateFormat")
public class AppTools {
	
	public static void collapseSoftInputMethod(EditText editText) {
		InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
		editText.clearFocus();
	}

	public static void opencollapseSoftInputMethod(EditText editText) {
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, 0);
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 判断是否是平板
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean deleteFiles(File file) {
		boolean resultdelete = false;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFiles(files[i]);
			}
			resultdelete = file.delete();
		} else {
			resultdelete = file.delete();
		}
		return resultdelete;
	}

	/**
	 * 计算两个日期型的时间相差多少时间
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static String twoDateDistance(Date startDate, Date endDate) {
		if (null == startDate || null == endDate) {
			return "";
		}
		if ("".equals(startDate) || "".equals(endDate)) {
			return "";
		}

		long timeLong = endDate.getTime() - startDate.getTime();
		if (timeLong < 60 * 1000)
			return timeLong / 1000 + "秒前";
		else if (timeLong < 60 * 60 * 1000) {
			timeLong = timeLong / 1000 / 60;
			return timeLong + "分钟前";
		} else if (timeLong < 60 * 60 * 24 * 1000) {
			timeLong = timeLong / 60 / 60 / 1000;
			return timeLong + "小时前";
		} else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
			timeLong = timeLong / 1000 / 60 / 60 / 24;
			return timeLong + "天前";
		} else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
			timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
			return timeLong + "周前";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			return sdf.format(startDate);
		}
	}

	public static boolean isName(String str) {
//		return str.matches("^[a-zA-Z0-9\\u4E00-\\uFA29][-_\\.a-zA-Z0-9\\u4E00-\\uFA29]*$");
		return true;
	}

	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	/**
	 * 
	 * @param beginTime
	 * @return
	 */
	public static String getDate(Date beginTime) {
		String temString = "秒";

		Date date1 = null;
		date1 = beginTime;
		try {
			Date date2 = new Date();
			long timeLine = (date2.getTime() - date1.getTime()) / 1000;

			if (timeLine > 59) {
				timeLine = timeLine / 60;
				temString = "分钟";
			} else {
				return "刚刚";
			}

			if (timeLine > 59) {
				timeLine = timeLine / 60;
				temString = "小时";
			} else {
				return String.valueOf(timeLine) + temString + "前";
			}
			if (timeLine > 23) {
				timeLine = timeLine / 24;
				;
				temString = "天";
			} else {
				return String.valueOf(timeLine) + temString + "前";
			}
			if (timeLine > 29) {
				timeLine = timeLine / 30;
				temString = "月";
			} else {

				return String.valueOf(timeLine) + temString + "前";
			}
			if (timeLine > 11) {// 年
				timeLine = timeLine / 12;
				temString = "年";
			}
			return String.valueOf(timeLine) + temString + "前";
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * @param beginTime
	 * @return
	 */
	public static String getDate(String beginTime) {
		if (null == beginTime || "".equals(beginTime) || "null".equals(beginTime)) {
			return "";
		}

		if (beginTime.length() < 10) {
			return "";
		}
		String temString = "秒";
		SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date1 = null;
		try {

			date1 = sDate.parse(beginTime);
		} catch (ParseException e1) {

		}
		try {
			Date date2 = new Date();
			long timeLine = (date2.getTime() - date1.getTime()) / 1000;

			if (timeLine > 59) {
				timeLine = timeLine / 60;
				temString = "分钟";
			} else {
				return "发表于" + "刚刚";
			}

			if (timeLine > 59) {
				timeLine = timeLine / 60;
				temString = "小时";
			} else {
				return "发表于" + String.valueOf(timeLine) + temString + "前";
			}
			if (timeLine > 23) {
				timeLine = timeLine / 24;
				;
				temString = "天";
			} else {
				return "发表于" + String.valueOf(timeLine) + temString + "前";
			}
			if (timeLine > 29) {
				timeLine = timeLine / 30;
				temString = "月";
			} else {

				return "发表于" + String.valueOf(timeLine) + temString + "前";
			}
			if (timeLine > 11) {// 年
				timeLine = timeLine / 12;
				temString = "年";
			}
			return "发表于" + String.valueOf(timeLine) + temString + "前";
		} catch (Exception e) {
			return "";
		}

	}

	// 时间戳转成年月日
	public static String TimeStamp2Date(String timestampString) {
		String dataString = "";
		try {
			Long timestamp = Long.parseLong(timestampString) * 1000;
			dataString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
		} catch (Exception e) {
			dataString = timestampString;

		}
		return dataString;
	}

	// JAVA 去掉 这符串中的html代码;

	public static String splitAndFilterString(String input, int length) {
		if (input == null || input.trim().equals("")) {
			return "";
		}
		// 去掉所有html元素,
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		int len = str.length();
		if (len <= length) {
			return str;
		} else {
			str = str.substring(0, length);
			str += "......";
		}
		return str;
	}

	public static boolean checkLabel(String str) {
		boolean bl = false;

		String all = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		String substring = str.replace("_", "");
		if (str.length() != substring.length()) {
			return false;
		}
		Pattern pattern = Pattern.compile(all);
		bl = pattern.matcher(str).matches();
		return bl;

	}

	public static String getSrc(String html) {

		Pattern p = Pattern.compile("<img.*?src=\"(.*?)\"");
		Matcher m = p.matcher(html);
		while (m.find()) {
			return m.group(1);
		}
		return "";

	}

	/**
	 * webview 加载html
	 */
	public static void webViewLoadHtml(String htmlString, WebView webview, final Context context) {
		final WebSettings webSettings = webview.getSettings(); // webView:
																// 类WebView的实例
		// final ProgressDialogUtil dialogTools = new
		// ProgressDialogUtil(context);
		// 图片自适应宽;
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// webSettings.setUseWideViewPort(false);

		// 方式二
		// webSettings.setUseWideViewPort(true);
		// webSettings.setLoadWithOverviewMode(true);
		webSettings.setBuiltInZoomControls(false);
		// webSettings.setJavaScriptEnabled(true);
		// 不使用缓存;
		// wSet.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setDefaultTextEncodingName("UTF-8");
		if (AppTools.isNetworkAvailable(context)) {
			// 如果有网络，从网络上取，不用缓 存
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {// 如果没有网络，用缓 存
				// 优先使用缓存
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}

		// String style="<style type=\"text/css\">img{width:380px;}</style>";//
		// webview.loadData(htmlString, "text/html; charset=UTF-8", null);

		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				webSettings.setBlockNetworkImage(false);// 文字
														// 显示完，再下载图片
			}
		});
		try {
			// htmlString.replace("<th>", "<br/>");
			// htmlString.replace("</th>", "<br/>");
			// htmlString.replace("<tr>", "<br/>");
			// htmlString.replace("</tr>", "<br/>");
			// htmlString.replace("<td>", "<br/>");
			// htmlString.replace("</td>", "<br/>");
			//
			webview.loadDataWithBaseURL("", htmlString, "text/html", "UTF-8", "");
			webSettings.setBlockNetworkImage(true);// 将图片下载阻塞
		} catch (Exception e) {
		}

	}

	/**
	 * 解决LISTVIEW 与 scollview 共存冲突问题 ;
	 */

	public static void setListViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight  + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		listView.setLayoutParams(params);
	}
//
//	public static void setListView1Height(ListView listView, int num) {
//
//		int totalHeight = 0;
//		for (int i = 0; i < num; i++) {
//
//			totalHeight += listView.getContext().getResources().getDimension(R.dimen.main_adapter_item_layout_height) + listView.getContext().getResources().getDimension(R.dimen.main_adapter1_top_bottom_margin) + listView.getContext().getResources().getDimension(R.dimen.main_adapter3_top_bottom_margin);
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight + (int)listView.getContext().getResources().getDimension(R.dimen.margin_20) +(listView.getDividerHeight() * (num - 1));
//		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
//		listView.setLayoutParams(params);
//	}
//
//	public static void setListView2Height(ListView listView, int num) {
//
//		int totalHeight = 0;
//		for (int i = 0; i < num; i++) {
//
//			totalHeight += listView.getContext().getResources().getDimension(R.dimen.main_adapter_item_layout_height) + listView.getContext().getResources().getDimension(R.dimen.main_adapter3_top_bottom_margin) * 2;
//		}
//
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight + (listView.getDividerHeight() * (num - 1));
//		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
//		listView.setLayoutParams(params);
//	}

	/**
	 * 信息复制到剪切板
	 * 
	 * @param context
	 * @param str
	 */
	public static void copyClipboard(Context context, String str) {
		ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(str); // 复制
		// ToastUtil.makeText(context, R.string.wht_copy_success,
		// Toast.LENGTH_SHORT).show();
	}

	/**
	 * 根据经纬度，获取两点间的距离
	 * 
	 * @author zhijun.wu
	 * @param lng1
	 *            经度
	 * @param lat1
	 *            纬度
	 * @param lng2
	 * @param lat2
	 * @return
	 * 
	 * @date 2011-8-10
	 */
	public static double distanceByLngLat(double lng1, double lat1, double lng2, double lat2) {
		double radLat1 = lat1 * Math.PI / 180;
		double radLat2 = lat2 * Math.PI / 180;
		double a = radLat1 - radLat2;
		double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
		s = Math.round(s * 10000) / 10000;

		return s;
	}

	public static void toHomeScreen(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果是服务里调用，必须加入new
														// task标识
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
	}

	/**
	 * 检测当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		return info != null && info.isConnectedOrConnecting();
	}

	/**
	 * 使用手机浏览器打开url地址
	 * 
	 * @param url
	 * @param context
	 */
	public static void open_Url(String url, Context context) {
		Uri uri = Uri.parse(url);

		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(it);
	}

	public static void sendSms(Context context, String info) {
		Uri uri = Uri.parse("smsto:");
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", info);
		context.startActivity(it);
	}

	/**
	 * 页面跳转
	 * 
	 * @param context
	 * @param cls
	 */
	public static void toIntent(Context context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
	}

	/**
	 * 页面跳转
	 *
	 * @param context
	 * @param cls
	 */
	public static void toIntent(Context context, Class<?> cls, int flags) {
		Intent intent = new Intent(context, cls);
		intent.setFlags(flags);
		context.startActivity(intent);
	}

	/**
	 * 页面跳转
	 * 
	 * @param context
	 * @param cls
	 */
	public static void toIntent(Activity context, Class<?> cls, int requestCode) {
		Intent intent = new Intent(context, cls);
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 页面跳转带参数
	 * 
	 * @param context
	 * @param bundle
	 * @param cls
	 */
	public static void toIntent(Context context, Bundle bundle, Class<?> cls) {
		if (context != null) {
			Intent intent = new Intent(context, cls);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			context.startActivity(intent);
		}
	}

	public static void toIntent(Context context, Bundle bundle, Class<?> cls, int flags) {
		Intent intent = new Intent(context, cls);
		intent.putExtras(bundle);
		intent.setFlags(flags);
		context.startActivity(intent);
	}

	/**
	 * 页面跳转带参数,带返回值
	 * 
	 * @param context
	 * @param bundle
	 * @param cls
	 */
	public static void toIntentWithRequestCode(Activity context, Bundle bundle, Class<?> cls, int requestCode) {
		Intent intent = new Intent(context, cls);
		intent.putExtras(bundle);
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean existsSDCARD() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 调用本地播放器播放视频
	 * 
	 * @param context
	 * @param url
	 */
	public static void playVideo(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(url), "video/*");
		context.startActivity(intent);
	}

	/**
	 * 获取当前网络状态 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络
	 * 
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = 3;
			} else {
				netType = 2;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
		}
		return netType;
	}

	/**
	 * 清除登陆信息
	 * 
	 * @param context
	 */
	public static void clearUsernameAndPassword(Context context) {
		RememberUsernameAndPassword(context, "", "", false);
	}

	/**
	 * 保存账号密码
	 * 
	 * @param context
	 * @param username
	 * @param uid
	 * @param isremember
	 */
	public static void RememberUsernameAndPassword(Context context, String username, String uid, boolean isremember) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("login_remeber", Context.MODE_PRIVATE);

		Editor editor = sharedPreferences.edit();
		editor.putString("username", username);
		editor.putString("uid", uid);
		editor.putBoolean("isremember", isremember);
		editor.commit();
	}
//
//	/**
//	 * 获取保存的账号或密码
//	 *
//	 * @param context
//	 * @param key
//	 * @return
//	 */
//	public static RememberUser getRemeberUsernameAndPassword(Context context) {
//		SharedPreferences sharedPreferences = context.getSharedPreferences("login_remeber", Context.MODE_PRIVATE);
//		String username = sharedPreferences.getString("username", "");
//		String uid = sharedPreferences.getString("uid", "");
//		boolean isremember = sharedPreferences.getBoolean("isremember", false);
//		RememberUser rememberuser = new RememberUser(username, uid, isremember);
//		return rememberuser;
//	}
	
	//****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    public static final String SHAREDPREFERENCES_NAME = "my_pref";
    public static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    public static boolean isFirstEnter(Context context, String className) {
        if (context==null || className==null || "".equalsIgnoreCase(className)) { 
        	return false;
        }
        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
        					.getString(KEY_GUIDE_ACTIVITY, "");	//取得所有类名 如 com.my.MainActivity
        if (mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }
    
    public static void setGuided(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }

    

	public static void setDownloadNetworkConfig(Context context, boolean isCheck) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("k_network_config", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("isNoWifiDownload", isCheck);
		editor.commit();
	}

	public static boolean getDownloadNetworkConfig(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("k_network_config", Context.MODE_PRIVATE);
		boolean result = sharedPreferences.getBoolean("isNoWifiDownload", true);
		return result;
	}

	public static void setDownloadNetwork_tip(Context context, int tip_stype) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("k_network_config", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("tip_style", tip_stype);
		editor.commit();
	}

	// public static int getDownloadNetwork_tip(Context context) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("k_network_config", Context.MODE_PRIVATE);
	// int result = sharedPreferences.getInt("tip_style",
	// K_Constant.DOWNLOAD_TIP_NO);
	// return result;
	// }

	public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}

	public static int getVersionCode(Context context) {
		int currentversion = -1;
		try {
			currentversion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return currentversion;
	}

	public static String getVersionName(Context context) {
		String currentversion = "";
		try {
			currentversion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return currentversion;
	}
	
//	/**
//	 * zip文件的解压
//	 */
//	public static void unZipFile(String zipFilePath, String unZipPach, final Handler mHandler) {
//
//		UnZipUtil unzip = new UnZipUtil();
//		unzip.setUnZipListener(zipFilePath, unZipPach, new UnZipListener() {
//			@Override
//			public void isComplete(boolean t) {
//				if (t == true)
//					mHandler.sendEmptyMessage(1);
//				else
//					mHandler.sendEmptyMessage(0);
//			}
//		});
//	}
}

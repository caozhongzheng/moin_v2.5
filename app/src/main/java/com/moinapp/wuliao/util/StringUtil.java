package com.moinapp.wuliao.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.bean.Web2NativeParams;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.sensitiveWord.SensitivewordFilter;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StyledText;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {

    private static ILogger MyLog = LoggerFactory.getLogger("StringUtil");
    private static final long MINUTE = 60 * 1000;
    private static final long UNIT = 10000;
    private static final long HOUR = 60 * MINUTE;
    public static final long ONE_DAY = 24 * HOUR;
    public static final String UESRACTIVITY_PATTERN = "MM/dd";
    public static final String TIME_PATTERN = "yyyy.MM.dd";
    public static final String TIME_PATTERN_NO_YEAR = "MM.dd";
    public static final String COMMENT_DATE_PATTERN = "MM-dd HH:mm";
    public static SensitivewordFilter mSensitivewordFilter;

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSensitivewordFilter = new SensitivewordFilter();
            }
        }).start();
    }
    /**
     * @param longDateTime 1970年1月1日到现在的毫秒数 ，类型为long
     * @param pattern     格式，如日期yyyy-MM-dd，如日期加时间yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatDate(long longDateTime, String pattern) {
        if (longDateTime == 0) {
            return "";
        }
        try {
            return new SimpleDateFormat(pattern).format(new Date(longDateTime));
        } catch (Exception e) {
            MyLog.e("formatDate " + e.toString());
            return "";
        }
    }

    /**
     * 根据格式化时间字符串时间，返回1970年1月1日到此时间的毫秒数
     *
     * @param formatDate
     * @param pattern    格式，如日期yyyy-MM-dd，如日期加时间yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long getDateTime(String formatDate, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(formatDate).getTime();
        } catch (Exception e) {
            MyLog.e("getDateTime " + e.toString());
            return -1;
        }
    }

    /**
     * 是否为null或""
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.length() == 0;
    }

    /**
     * null转为""
     *
     * @param str
     * @return
     */
    public static String nullToEmpty(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * null转为"",为了不报错临时用，要删掉。????
     *
     * @param param
     * @return
     */
    public static String nullToEmpty(int param) {
        if (param == 0) {
            return "";
        } else {
            return String.valueOf(param);
        }
    }
    public static boolean isEmail(String strEmail) {
        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(strEmail);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellphone(String strPhone) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(strPhone);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static char[] passwordDigits() {
        return new char[] {'1','2','3','4','5','6','7','8','9','0',
                ',','.','+','-','*','/','_','=','%','[',']','{','}','\\','|','~','`','!','@','#','$','^','(',')','?',
                'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    }
    /**
     * 输出M、K等单位
     *
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        if (size <= 0) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("###.#");
        float f;
        f = (float) ((float) size / (float) (1024 * 1024));
        if (f < 0.1f)
            f = 0.1f;
        return (df.format(new Float(f).doubleValue()) + "M");
        /*if (size < 1024 * 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "K");
		} else {
			f = (float) ((float) size / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "M");
		}*/
    }

    /**
     * 获取0到size范围内（包括0和size）的2个随机数。
     *
     * @param size
     */
    public static int[] getTwoRandomNum(int size) {
        int[] intRet = new int[2];
        intRet[0] = (int) Math.round(Math.random() * size);
        intRet[1] = (int) Math.round(Math.random() * size);
        return intRet;
    }

	public static Object getExt(String strIconUrl) {
		// TODO Auto-generated method stub
		String JPG = ".jpg";
		String JPEG = ".jpeg";
		String PNG = ".png";
		String ext = strIconUrl.substring(strIconUrl.lastIndexOf(".")).toLowerCase();
		if(ext.startsWith(JPG) || ext.startsWith(JPEG))
			return JPG;
		else if(ext.startsWith(PNG))
			return PNG;
		if(ext.contains("?"))
			return ext.substring(0, ext.lastIndexOf("?"));
		return ext;
	}


    public static String humanDate(long longDateTime) {
        return humanDate(longDateTime, TIME_PATTERN);
    }
    public static String humanDate(long longDateTime, String pattern) {
        if (longDateTime == 0) {
            return "";
        }
//        long now = System.currentTimeMillis();
        long now = new Date().getTime();
        long minus = now - longDateTime;
        if(ONE_DAY < minus) {
            return formatDate(longDateTime, pattern);
        } else if(HOUR < minus) {
            return (minus/HOUR) + AppContext.context().getString(R.string.hours_before);
        } else if(MINUTE < minus) {
            return (minus/MINUTE) + AppContext.context().getString(R.string.minutes_before);
        } else {
            return AppContext.context().getString(R.string.just_before);
        }
    }

    public static String getGender(Context context, String sex, boolean isShow) {
        String man = context.getResources().getString(R.string.male);
        String woman = context.getResources().getString(R.string.female);
        if("1".equals(sex) || "male".equals(sex) || "男".equals(sex)) {
            return isShow ? man : "male";
        } else if("2".equals(sex) || "female".equals(sex) || "女".equals(sex)) {
            return isShow ? woman : "female";
        } else {
            return context.getResources().getString(R.string.unknown_gender);
        }
    }

    public static String humanNumber(Context context, int value) {
        if (value >= UNIT) {
            return (Math.round((float)(value)/UNIT)) + "万+";
        } else {
            return String.valueOf(value);
        }
    }

    public static String humanNumber(int value) {
        if (value < 0) {
            return "0";
        }
        if (value >= UNIT) {
            return (value / UNIT) + "万+";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     *  list转成逗号分隔的字符串（与服务器约定好的）
     */
    public static String list2String(List<String> pics) {
        return list2String(pics, ",");
    }

    /**
     *  list转成逗号分隔的字符串（与服务器约定好的）
     */
    public static String list2String(List<String> pics, String separator) {
        if (pics == null || pics.size() == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (String pic:pics) {
            if (!TextUtils.isEmpty(pic)) {
                builder.append(pic).append(separator);
            }
        }

        String s = builder.toString();
        if (s.length() > 0)
            return s.substring(0, s.length()-separator.length());
        else
            return null;
    }


    /**
     *  过滤敏感词
     * */
    public static String filter(String txt) {
        long beginTime = System.currentTimeMillis();
        Set<String> set = mSensitivewordFilter.getSensitiveWord(txt, 2);
        String result = mSensitivewordFilter.replaceSensitiveWord(txt, 2, "*");
        long endTime = System.currentTimeMillis();
        MyLog.i("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            MyLog.i("包含敏感词:"+key);
        }
        MyLog.i("总共消耗时间为：" + (endTime - beginTime));
        return result;
    }

    /***
     * 判断用户是否需要补填用户名
     * 3 使用第三方帐号第一次登录或者老用户没有用户名，需要更新用户个人信息；
     * @param type
     * @return
     */
    public static boolean needFillInfo(int type) {
        return type == 3;
    }

    /***
     * AT好友内容的起始长度list；
     * @param text
     * @return
     */
    public static List<StyledText> getStyledTextList(String text) {
        if(StringUtil.isNullOrEmpty(text)) {
            return null;
        }
        List<StyledText> sTextsStartList = new ArrayList<>();

        int start = -1;
        int end = -1;
        do {
            start = text.indexOf("@", end);
            end = text.indexOf(" ", start);

            if (start != -1 && end != -1) {
                try {
                    sTextsStartList.add(new StyledText(start, (end - start), text.substring(start + 1, end)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } while (start != -1 && end != -1);

        return sTextsStartList;
    }

    /***
     * AT好友内容的SpannableStringBuilder；
     * @param text
     * @return
     */
    public static SpannableStringBuilder getStyledText(String text) {
        List<StyledText> styledTextList = getStyledTextList(text);
        if(styledTextList == null) {
            return null;
        }
        SpannableStringBuilder styledText = new SpannableStringBuilder(text);
        for(StyledText st : styledTextList)
        {
            styledText.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#FF0000")),
                    st.getStart(),
                    st.getEnd(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return styledText;
    }

    public static SpannableStringBuilder getStyledSpanCount(String count) {
        String countStr = StringUtil.nullToEmpty(count);
        SpannableStringBuilder countSpanText = new SpannableStringBuilder(countStr);
        countSpanText.setSpan(
                new ForegroundColorSpan(Color.parseColor("#000000")),
                0,
                countStr.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        countSpanText.append("人赞");
        return countSpanText;
    }

    /***
     * 是否输入或删除时在AT好友内容的SpannableStringBuilder内；
     * @param text
     * @return
     */
    public static boolean isInStyledTextList(String text, int index) {
        boolean result = false;
        List<StyledText> list = getStyledTextList(text);
        if (list == null || list.size() == 0) {
            result = false;
        } else {
            for (StyledText styledText : list) {
                if(index > styledText.getStart() && index <= styledText.getEnd()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /***
     * 如果删除时在AT好友内容的SpannableStringBuilder内,则删除@的全部；
     * @param text
     * @return
     */
    public static StyledText getDelPosition(String text, int index) {
        StyledText result = null;
        if(isNullOrEmpty(text))
            return null;
        if(index > text.length()) {
            int start = text.lastIndexOf("@");
            int end = text.lastIndexOf(" ", start);
            if(start != -1) {
                return new StyledText(start, index - start - 1, text.substring(start+1, index - 1));
            }
        }
        List<StyledText> list = getStyledTextList(text);
        if (list == null || list.size() == 0) {
            result = null;
        } else {
            MyLog.i("getDelPosition index="+index);
            for (StyledText styledText : list) {
                if(index > styledText.getStart() && index <= (styledText.getEnd()+1)) {
                    MyLog.i("getDelPosition shot!! styledText="+styledText.toString());
                    result = styledText;
                    break;
                }
                MyLog.i("getDelPosition styledText="+styledText.toString());
            }
        }
        return result;
    }


    /***
     * 如果输入时在AT好友内容的SpannableStringBuilder内,应该跳到本@内容尾巴的地方开始@；
     * @param text
     * @return
     */
    public static int getInputPosition(String text, int index) {
        int result = index;
        List<StyledText> list = getStyledTextList(text);
        if (list == null || list.size() == 0) {
            result = index;
        } else {
            for (StyledText styledText : list) {
                if(index > styledText.getStart() && index <= styledText.getEnd()) {
                    result = styledText.getEnd() + 1;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 复制文本到系统粘贴板
     */
    public static void copyToClipboard(String text) {
        ClipboardManager cmb = (ClipboardManager) BaseApplication.context().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text.trim());
    }

    public static final String FROM_PERSONAL = "personalInfo";
    public static final String FROM_REGISTER = "register";
    public static final String FROM_CHAT = "chat";
    public static final String FROM_MISSION = "mission";
    public static boolean isFromPersonalInfo(String from) {
        return FROM_PERSONAL.equals(from);
    }
    public static boolean isFromRegister(String from) {
        return FROM_REGISTER.equals(from);
    }
    public static boolean isFromChat(String from) {
        return FROM_CHAT.equals(from);
    }
    public static boolean isFromMission(String from) {
        return FROM_MISSION.equals(from);
    }

    /**
     * 根据文件名拼接上传的key
     * @param flag 1:图片 2:工程文件 3:音频 4:帖子图片
     * @param bucket oss服务器的bucket名称
     */
    public static String getUploadKey(int flag, String path, String bucket) {
        File file = new File(path);
        if (file == null || !file.exists()) return null;

        if (TextUtils.isEmpty(bucket)) {
            bucket = "";
        } else {
            bucket = bucket + "/";
        }
        String md5 = MD5.calculateMD5(file);
        String body = bucket + md5.substring(10, 13) + "/" + md5.substring(22, 25) + "/" + md5;
        if (flag == DiscoveryConstants.TYPE_XGT || flag == DiscoveryConstants.TYPE_PHOTO) {
            return "image/" + body + ".jpg";
        } else if (flag == DiscoveryConstants.TYPE_PRJ) {
            return "prjmi/" + body + ".moi";
        } else if (flag == DiscoveryConstants.TYPE_AUDIO) {
            return "audio/" + body + ".amr";
        }
        return "";
    }

    /**
     * 根据上传文件类型返回oss回调服务器的文件类型
     * @param flag 1:图片 2:工程文件 3:音频
     */
    public static String getOssCallbackType(int flag) {
        String type;
        if (flag == DiscoveryConstants.TYPE_XGT || flag == DiscoveryConstants.TYPE_PHOTO) {
            return "img";
        } else if (flag == DiscoveryConstants.TYPE_PRJ) {
            return "moi";
        } else if (flag == DiscoveryConstants.TYPE_AUDIO) {
            return "audio";
        }
        return "img";
    }

    /** 检查备注动作 0:
     *
     * */
    public static int checkAlias(Activity context, String alias, String aliasNew) {
        int result = UserDefineConstants.ALIAS_UND;
        if (!StringUtil.isNullOrEmpty(alias)) {
            if (StringUtil.isNullOrEmpty(aliasNew)) {
                // 删除备注
                return UserDefineConstants.ALIAS_DEL;
            } else {
                // 修改备注
                result = UserDefineConstants.ALIAS_CHG;
            }
        } else {
            if (StringUtil.isNullOrEmpty(aliasNew)) {
                // 放弃备注 do nothing
                return UserDefineConstants.ALIAS_UND;
            } else {
                // 添加备注
                result = UserDefineConstants.ALIAS_ADD;
            }
        }
        if ((result == UserDefineConstants.ALIAS_ADD || result == UserDefineConstants.ALIAS_CHG)
                &&  aliasNew.length() > context.getResources().getInteger(R.integer.username_max_len)) {
            // 备注过长
            return UserDefineConstants.ALIAS_LNG;
        }
        if (!AppTools.isName(aliasNew)) {
            AppContext.toast(context, context.getString(R.string.alias_invalid2));
            // 备注不合格
            return UserDefineConstants.ALIAS_INV;
        }

        return result;
    }

    /**
     * 获取用户名,优先返回备注名
     */
    public static String getUserName(UserInfo userInfo) {
        if (userInfo == null) {
            return "";
        }
        if (isNullOrEmpty(userInfo.getAlias())) {
            return userInfo.getUsername();
        }
        return userInfo.getAlias();
    }

    /**
     * 获取web跳转native的协议解析返回的参数
     */
    public static Web2NativeParams parseUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        Uri uri = Uri.parse(url);
        String resource = null, action = null;
        String[] params = uri.getPath().split("/");
        if (params == null || params.length == 0) {
            return null;
        }
        boolean first = true;
        for (String par : params) {
            if (!StringUtil.isNullOrEmpty(par) && first) {
                resource = par.toLowerCase();
                first = false;
            } else if (!first) {
                action = par.toLowerCase();
                break;
            }
        }

        MyLog.i("resource=" + resource + ", action=" + action);
        if (StringUtil.isNullOrEmpty(resource) || StringUtil.isNullOrEmpty(action)) {
            return null;
        }

        Web2NativeParams web2NativeParams = new Web2NativeParams();
        web2NativeParams.setResource(resource);
        web2NativeParams.setAction(action);
        String urlQuery = uri.getQuery();
        if (!StringUtils.isEmpty(urlQuery)) {
            String[] querys = uri.getQuery().split("&");
            if (querys != null && querys.length > 0) {
                HashMap<String, String> map = new HashMap<>();
                for (String que : querys) {

                    MyLog.i("que=" + que);
                    String key = que.substring(0, que.indexOf("="));
                    String val = que.substring(que.indexOf("=") + 1);
                    MyLog.i("que=" + que + " ,key=" + key + " ,val=" + val);
                    map.put(key, val);
                }
                web2NativeParams.setParams(map);
            }
        }
        return web2NativeParams;
    }

    /**
     * 把string类型的0x12345678转成int类型的color
     */
    public static int parseColor(String colorStr) {
        int color = 0;
        if (StringUtils.isEmpty(colorStr)) {
            return 0;
        }
//        String s = "#" + colorStr.replace("0x", "");
        try {
            color = Color.parseColor(colorStr);
        }catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return color;
    }

}

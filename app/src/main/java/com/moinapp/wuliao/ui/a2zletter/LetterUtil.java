package com.moinapp.wuliao.ui.a2zletter;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 字母工具类
 * Created by moying on 15/7/8.
 */
public class LetterUtil {
    private static final ILogger MyLog = LoggerFactory.getLogger(LetterUtil.class.getSimpleName());

    /**
     * @param chinese 一个汉字
     * @return 拼音首字母
     * @Description:
     */
    public static String[] getFirstPinyin(char chinese) {
        Character.UnicodeBlock ub = isChinese(chinese);

        if (ub != null) {
            if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                return new String[] {""};
            }
            String[] result = new String[0];
            try {
                result = PinyinHelper.toHanyuPinyinStringArray(chinese);
            } catch (Exception e) {
                MyLog.e(e);
            }
            return result;
        }
        return new String[] {chinese + ""};
    }

    /**
     * 是否是字母
     *
     * @return true 字母¸,false 非字母
     * @Description:
     */
    public static boolean isLetter(char c)
    {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 112);
    }

    /**
     * 是否是中文字符
     *
     * @return true 中文字符¸,false 非中文字符
     * @Description:
     */
    private static final Character.UnicodeBlock isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

            return ub;
        }
        return null;
    }
}

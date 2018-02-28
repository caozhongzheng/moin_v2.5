package com.keyboard.db;

import android.provider.BaseColumns;

public final class TableColumns {

    private TableColumns() {}

    public interface EmoticonColumns extends BaseColumns {

        public static final String ID = "id";

        public static final String PARENT_ID = "pid";

        /**v2.7 增加贴纸的唯一ID*/
        public static final String STICKER_ID = "stickerid";

        public static final String EVENTTYPE = "eventtype";

        public static final String CONTENT = "content";

        public static final String ICONURI = "iconuri";
        public static final String ICONURL = "iconurl";

        public static final String GIFURI = "gifuri";
        public static final String GIFURL = "gifurl";

        public static final String BUBBLETEXT = "bubbletext";

        public static final String TAG = "tag";
        public static final String USESTAT = "usestat";

        public static final String EMOTICONSET_NAME = "emoticonset_name";

        /**v2.5 同EmoticonSetColumns.STICK_TYPE, 2.7时只和单个贴纸类型保持一致 */
        public static final String STICK_TYPE = "type";

        /**v2.7 同EmoticonSetColumns.STICK_TYPE */
        public static final String PARENT_STICK_TYPE = "ptype";
        /**v2.9 每个贴纸使用时相对屏幕宽度的缩放比例百分值 */
        public static final String ZOOM = "zoom";
    }

    public interface EmoticonSetColumns extends BaseColumns {

        public static final String ID = "id";

        public static final String UID = "uid";

        public static final String ORDER = "orderNo";

        public static final String NAME = "name";

        public static final String LINE = "line";

        public static final String ROW = "row";

        public static final String ICONURI = "iconuri";

        public static final String ICONURL = "iconurl";

        public static final String ICONNAME = "iconname";

        public static final String ISSHOWDELBTN = "isshowdelbtn";

        public static final String ITEMPADDING = "itempadding";

        public static final String HORIZONTALSPACING = "horizontalspacing";

        public static final String VERTICALSPACING = "verticalspacing";

        public static final String UPDATETIME = "updatetime";

        /**v2.0这种的表情type=0, v2.5 1-6表示贴纸类型*/
        public static final String STICK_TYPE = "type";

        /**2.7加入的贴纸包标记, 默认是0, 有更新了为1*/
        public static final String FLAG = "flag";

        /**2.7后新增贴纸的主动检查更新的发起时间,每7天检查一次         */
        public static final String LAST_CHECKTIME = "last_checktime";

    }

}

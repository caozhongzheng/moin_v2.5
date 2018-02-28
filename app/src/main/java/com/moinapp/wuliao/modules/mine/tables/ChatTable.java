package com.moinapp.wuliao.modules.mine.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * 记录聊天信息的表
 */
public class ChatTable extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "chat_message_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri CHAT_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String CHAT_UID = "uid";//聊天对象的uid
    public static final String CHAT_MESSAGEID = "message_id";//聊天对象的uid
    public static final String CHAT_TYPE = "type";//1发送 2接收
    public static final String CHAT_CONTENT_TYPE = "content_type";//聊天的内容类型 1文本 2图片 3预置图片
    public static final String CHAT_CONTENT = "content";///聊天的内容
    public static final String CHAT_LOCAL_TIME= "local_time";//聊天消息的本地时间
    public static final String CHAT_SERVER_TIME= "server_time";//聊天消息的服务器时间
    public static final String CHAT_SEND_STATUS = "send_status";//-1失败 -2图片上传失败 0发送中 1成功
    public static final String CHAT_READ_FLAG = "read_flag";//聊天内容是否已读 0未读 1已读
    public static final String CHAT_LOGIN_UID = "login_uid";//当前用户的uid
    public static final String CHAT_JSON = "json";//接收到的聊天消息的json串,做冗余保存到本地

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public void create(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CHAT_UID + " VARCHAR(20),"
                    + CHAT_MESSAGEID + " VARCHAR(20),"
                    + CHAT_TYPE + " INTEGER default 1,"
                    + CHAT_CONTENT_TYPE + " INTEGER default 1,"
                    + CHAT_CONTENT + " VARCHAR(20),"
                    + CHAT_LOCAL_TIME + " INTEGER default 0,"
                    + CHAT_SERVER_TIME + " INTEGER default 0,"
                    + CHAT_SEND_STATUS + " INTEGER default 0,"
                    + CHAT_READ_FLAG + " INTEGER default 0,"
                    + CHAT_LOGIN_UID + " VARCHAR(20),"
                    + CHAT_JSON + " VARCHAR(20))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO

    }
}

package com.moinapp.wuliao.modules.mine.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * 推送消息的缓存表
 * Created by liujiancheng on 15/5/14.
 */
public class PushMessageTable extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "push_message_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri PUSH_MESSAGE_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String MESSAGE_ID = "message_id";
    public static final String MESSAGE_UID = "message_uid";
    public static final String MESSAGE_TITLE = "message_title";
    public static final String MESSAGE_BODY = "message_body";
    public static final String MESSAGE_CLICK_FLAG = "flag";//
    public static final String MESSAGE_CREATETIME = "createtime";//
    public static final String MESSAGE_RESERVE_INTEGER = "reserve_int";//
    public static final String MESSAGE_RESERVE_STRING = "reserve_string";//

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
                    + MESSAGE_ID + " VARCHAR(20),"
                    + MESSAGE_UID + " VARCHAR(20),"
                    + MESSAGE_TITLE + " VARCHAR(20),"
                    + MESSAGE_BODY + " VARCHAR(20),"
                    + MESSAGE_CLICK_FLAG + " INTEGER default 0,"
                    + MESSAGE_CREATETIME + " INTEGER default 0,"
                    + MESSAGE_RESERVE_INTEGER + " INTEGER default 0,"
                    + MESSAGE_RESERVE_STRING + " VARCHAR(20))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO

    }
}

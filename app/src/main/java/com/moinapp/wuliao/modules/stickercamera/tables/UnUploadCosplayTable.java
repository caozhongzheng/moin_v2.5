package com.moinapp.wuliao.modules.stickercamera.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * Created by liujiancheng on 16/2/24.
 * 未上传大咖秀文件的缓存表
 */
public class UnUploadCosplayTable extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "un_upload_cosplay_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri UN_UPLOAD_COSPLAY_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String COSPLAY_ID = "ucid";
    public static final String FILE_PATH = "file_path";
    public static final String FLAG = "flag";//1工程文件 2效果图
    public static final String RETRY_TIMES = "retry_times";
    public static final String LAST_FAIL_TIME = "last_fail_time";

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
                    +  COSPLAY_ID + " VARCHAR(20),"
                    +  FILE_PATH + " VARCHAR(20),"
                    +  FLAG + " INTEGER DEFAULT 0,"
                    +  RETRY_TIMES + " INTEGER DEFAULT 0,"
                    + LAST_FAIL_TIME + " INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO

    }
}

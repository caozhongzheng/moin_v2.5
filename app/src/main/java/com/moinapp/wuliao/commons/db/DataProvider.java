package com.moinapp.wuliao.commons.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.commons.init.InitManager;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.IModule;
import com.moinapp.wuliao.commons.moduleframework.ModuleContainer;
import com.moinapp.wuliao.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataProvider extends ContentProvider {

	private ILogger MyLog = LoggerFactory.getLogger("db");
	private List<IDataTable> mTables = new ArrayList<IDataTable>();

	private static final String DB_NAME = "data.db";
	private static final int DB_VERSION = 15;// 13添加了template表 15增加了chat_table

	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public static final String DATA_AUTHORITY;

	static {
		DATA_AUTHORITY = "com.moinapp.wuliao.dataprovider";
	}

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	public void registerTables() {
		Collection<IModule> modules = ModuleContainer.getInstance().getModules();
		MyLog.d("DataProviderNew registerTables modules:" + modules);
		for (IModule module : modules) {
			List<IDataTable> tables = module.getTables();
			MyLog.d("DataProviderNew registerTables module:" + module + " tables:" + tables);
			if (tables != null && !tables.isEmpty()){	
				mTables.addAll(tables);
			}
		}

		int i = 0;
		for (IDataTable table : mTables) {
			String tableName = table.getName();
			sURIMatcher.addURI(DATA_AUTHORITY, tableName, i/*mTables.size() - 1*/);
			i++;
			MyLog.d("DataProviderNew sURIMatcher:" + DATA_AUTHORITY + " " + tableName + " " + (mTables.size() - 1) + " i:" + i);
		}
		
	}
	
	@Override
	public boolean onCreate() {
		MyLog.d("DataProvider onCreate is coming....");
		BaseApplication.setContext(getContext());

		//setup env
		AppConfig.setStyleType();

		InitManager init = InitManager.getInstance(getContext());
		init.registerModules();
//		init.init();
		
		registerTables();
		
		if(mDbHelper == null) {
			mDbHelper = new DbHelper(getContext());
		}
		if(mDb == null) {
			mDb = mDbHelper.getWritableDatabase();
		}

		return true;
	}
	
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();

		try {
			ContentProviderResult[] results = super.applyBatch(operations);
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		int table = sURIMatcher.match(uri);

        try{
			if (table < mTables.size()){
				c = mDb.query(mTables.get(table).getName(), projection,
                        selection, selectionArgs, null, null, sortOrder);
			} else {
				throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
		 }
		return c;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int table = sURIMatcher.match(uri);
		//MyLog.d("insert uri:" + uri + " table:" + table);
		Uri resultUri = null;
		try{
			if (table < mTables.size()){
				long id = mDb.insert(mTables.get(table).getName(), "", values);
                resultUri = ContentUris.withAppendedId(uri, id);
			} else {
				throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }catch(SQLiteException e){
			e.printStackTrace();
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return resultUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = 0;
		int table = sURIMatcher.match(uri);
		try{
			if (CollectionUtils.isNotEmpty(mTables) && table < mTables.size()){
				result = mDb.delete(mTables.get(table).getName(), selection, selectionArgs);
			} else {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int result = 0;
		int table = sURIMatcher.match(uri);

		try{
			if (table < mTables.size()){
				result = mDb.update(mTables.get(table).getName(), values, selection, selectionArgs);
			} else {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}
	
	private class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for(IDataTable table : mTables){
				table.create(db);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			MyLog.i("ljc: database onUpgrade is coming...........");
            for(IDataTable table : mTables){
                table.create(db);
            }
			
			new InitManager(getContext()).upgradeDb(db, oldVersion, newVersion);
		}
		
		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // do nothing
	    }
	}
}

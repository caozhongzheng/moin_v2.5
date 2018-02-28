package com.keyboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.utils.DefEmoticons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DBHelper {

    /**
     * 0-->1 add emoticons,emoticonset
     */
    private static final int VERSION = 5;
    private static final String TAG = "dbh";

    private static final String DATABASE_NAME = "moinemoticons.db";
    private static final String TABLE_NAME_EMOTICONS = "emoticons";
    private static final String TABLE_NAME_EMOTICONSET = "emoticonset";

    private static SQLiteDatabase db;

    private static DBHelper mInstance;

    private static DBOpenHelper dbOpenHelper;

    private static boolean mUpgrade;

    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }

        return mInstance;
    }

    private DBHelper(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
        establishDb();
    }

    private static void establishDb() {
        if (db == null) {
            db = dbOpenHelper.getWritableDatabase();
        }
    }


    public static boolean isUpgrade() {
        return mUpgrade;
    }

    public static void setUpgrade(boolean upgrade) {
        mUpgrade = upgrade;
    }


    public ContentValues createEmoticonSetContentValues(EmoticonBean bean, String beanSetName) {
        if (bean == null) {
            return null;
        }
        return buildEmoticonValues(bean, beanSetName);
    }

    public ContentValues buildEmoticonValues(EmoticonBean bean, String beanSetName) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonColumns.ID, bean.getId());
        values.put(TableColumns.EmoticonColumns.PARENT_ID, nullToEmpty(bean.getParentId()));
        values.put(TableColumns.EmoticonColumns.STICKER_ID, nullToEmpty(bean.getStickerId()));
        values.put(TableColumns.EmoticonColumns.STICK_TYPE, bean.getStickType());
        values.put(TableColumns.EmoticonColumns.PARENT_STICK_TYPE, bean.getParentStickType());
        values.put(TableColumns.EmoticonColumns.TAG, nullToEmpty(bean.getTags()));
        values.put(TableColumns.EmoticonColumns.USESTAT, bean.getUseStat());
        values.put(TableColumns.EmoticonColumns.GIFURI, nullToEmpty(bean.getGifUri()));
        values.put(TableColumns.EmoticonColumns.GIFURL, nullToEmpty(bean.getGifUrl()));
        values.put(TableColumns.EmoticonColumns.ICONURL, nullToEmpty(bean.getIconUrl()));
        values.put(TableColumns.EmoticonColumns.EVENTTYPE, bean.getEventType());
        values.put(TableColumns.EmoticonColumns.CONTENT, nullToEmpty(bean.getContent()));
        values.put(TableColumns.EmoticonColumns.ZOOM, bean.getZoom());
        values.put(TableColumns.EmoticonColumns.BUBBLETEXT, nullToEmpty(bean.getBubbleText()));
        values.put(TableColumns.EmoticonColumns.ICONURI, nullToEmpty(bean.getIconUri()));
        values.put(TableColumns.EmoticonColumns.EMOTICONSET_NAME, nullToEmpty(beanSetName));
        return values;
    }

    private String nullToEmpty(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public long insertEmoticonBean(EmoticonBean bean, String beanSetName) {

        long result = -1;
        if (bean == null || db == null) {
            return result;
        }
        ContentValues values = createEmoticonSetContentValues(bean, beanSetName);
        try {
            result = db.insert(TABLE_NAME_EMOTICONS, null, values);
        } catch (SQLiteConstraintException e) {
        }
        return result;
    }

    public long insertEmoticonBeans(ContentValues[] values) {
        db.beginTransaction();
        int insertSuccessCount = values.length;
        try {
            for (ContentValues cv : values) {
                if (db.insert(TABLE_NAME_EMOTICONS, null, cv) < 0) {
                    insertSuccessCount--;
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteConstraintException e) {
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        return insertSuccessCount;
    }

    public long insertEmoticonSet(EmoticonSetBean bean) {
        long result = -1;
        if (bean == null || db == null || TextUtils.isEmpty(bean.getName())) {
            return result;
        }

        bean.setItemPadding(15);
        bean.setVerticalSpacing(5);
        ContentValues values = buildEmoticonSetValues(bean);

        result = db.insert(TABLE_NAME_EMOTICONSET, null, values);

        ArrayList<EmoticonBean> emoticonList = bean.getEmoticonList();
        long count = 0;
        if (emoticonList != null) {
            String emoticonSetname = bean.getName();
            ContentValues[] contentValues = new ContentValues[emoticonList.size()];
            for (int i = 0; i < emoticonList.size(); i++) {
                contentValues[i] = createEmoticonSetContentValues(emoticonList.get(i), emoticonSetname);
            }
            count = insertEmoticonBeans(contentValues);
        }
//        android.util.Log.i(TAG, "insertEmoticonSet countSet="+result + ", countBeans="+ count);
        return result * count;
    }

    private ContentValues buildEmoticonSetValues(EmoticonSetBean bean) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.ID, nullToEmpty(bean.getId()));
        values.put(TableColumns.EmoticonSetColumns.UID, nullToEmpty(bean.getUid()));
        values.put(TableColumns.EmoticonSetColumns.STICK_TYPE, bean.getStickType());
        values.put(TableColumns.EmoticonSetColumns.ORDER, bean.getOrder());
        values.put(TableColumns.EmoticonSetColumns.NAME, nullToEmpty(bean.getName()));
        values.put(TableColumns.EmoticonSetColumns.LINE, bean.getLine());
        values.put(TableColumns.EmoticonSetColumns.ROW, bean.getRow());
        values.put(TableColumns.EmoticonSetColumns.ICONURI, bean.getIconUri());
        values.put(TableColumns.EmoticonSetColumns.ICONURL, bean.getIconUrl());
        values.put(TableColumns.EmoticonSetColumns.ICONNAME, bean.getIconName());
        values.put(TableColumns.EmoticonSetColumns.ISSHOWDELBTN, bean.isShowDelBtn() ? 1 : 0);
        values.put(TableColumns.EmoticonSetColumns.ITEMPADDING, bean.getItemPadding());
        values.put(TableColumns.EmoticonSetColumns.HORIZONTALSPACING, bean.getHorizontalSpacing());
        values.put(TableColumns.EmoticonSetColumns.VERTICALSPACING, bean.getVerticalSpacing());
        values.put(TableColumns.EmoticonSetColumns.UPDATETIME, bean.getUpdateTime());
        return values;
    }

    /**我的贴纸拖动排序
     * @param fromIndex 拖动item开始的order
     * @param toIndex  拖动item目标的order*/
    public void changeStickerOrder(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return;
        }
        boolean fromGreater = fromIndex > toIndex;
        int startIndex = fromGreater ? toIndex : fromIndex;
        int endIndex = fromGreater ? fromIndex : toIndex;
        final String ORDER = TableColumns.EmoticonSetColumns.ORDER;
        String result = fromGreater ? " (" + ORDER + " + 1) " : " (" + ORDER + " - 1) ";
        String where = " and " + TableColumns.EmoticonSetColumns.STICK_TYPE + " = 1 ";

        StringBuffer sb = new StringBuffer("update " + TABLE_NAME_EMOTICONSET + " set "
                + ORDER + " = case when " + ORDER + " == ");
        sb.append(String.valueOf(fromIndex))
                .append(" then ")
                .append(String.valueOf(toIndex))
                .append(" else ")
                .append(result)
                .append(" end where " + ORDER + " >= ")
                .append(String.valueOf(startIndex))
                .append(" and " + ORDER + " <= ")
                .append(String.valueOf(endIndex))
                .append(where);
        db.beginTransaction();
        try {
            db.execSQL(sb.toString());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int updateEmoticonSetOrder(String stickerId, int order) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.ORDER, order);
        int result = db.update(TABLE_NAME_EMOTICONSET, values, TableColumns.EmoticonSetColumns.STICK_TYPE + "=1 and " + TableColumns.EmoticonSetColumns.ID + "='" + stickerId + "'", null);

        return result;
    }

    /**删除正常贴纸*/
    public int deleteEmoticonSet(String EmoticonSetId) {
        int result = db.delete(TABLE_NAME_EMOTICONSET, TableColumns.EmoticonSetColumns.ID+"=?", new String[]{EmoticonSetId});
        int result2 = db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.PARENT_ID + "=?", new String[]{EmoticonSetId});

        return result * result2;
    }

    /**删除预制贴纸[根据类型删除贴纸]*/
    public int deleteDefaultStickerSet(int type) {
        int result = db.delete(TABLE_NAME_EMOTICONSET, TableColumns.EmoticonSetColumns.STICK_TYPE + "=" + type, null);
        // v2.7 用PARENT_STICK_TYPE, 而不是STICK_TYPE了
//        int result2 = db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.STICK_TYPE + "=" + type, null);
        int result2 = db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.PARENT_STICK_TYPE + "=" + type, null);
//        android.util.Log.i(TAG, type+" deleteDefaultStickerSet countSet="+result + ", countBeans="+ result2);

        return result * result2;
    }

    public int deleteEmoticon(String EmoticonId) {
        int result = db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.ID + "=?", new String[]{EmoticonId});

        return result;
    }

    public EmoticonBean queryEmoticonBean(String contentStr) {
        String sql = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.CONTENT + " = '" + contentStr + "'";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                return buildEmoticonBeanFromCursor(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public EmoticonBean queryEmoticonBeanByID(String id) {
        String sql = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                return buildEmoticonBeanFromCursor(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private EmoticonBean buildEmoticonBeanFromCursor(Cursor cursor) {
        if(cursor == null)
            return null;
        String id = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ID));
        int useStat = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonColumns.USESTAT));
        int stickType = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonColumns.STICK_TYPE));
        int parentStickType = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonColumns.PARENT_STICK_TYPE));
        String pid = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.PARENT_ID));
        String stickerid = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.STICKER_ID));
        long eventType = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonColumns.EVENTTYPE));
        int zoom = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonColumns.ZOOM));
        String content = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.CONTENT));
        String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICONURI));
        String iconUrl = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICONURL));
        String gifUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.GIFURI));
        String gifUrl = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.GIFURL));
        String tag = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.TAG));
        String bubbleText = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.BUBBLETEXT));
        return new EmoticonBean(id, pid, stickerid, stickType, parentStickType, iconUri, iconUrl, gifUri, gifUrl, tag, useStat, eventType, zoom, content, bubbleText);
    }

    public ArrayList<EmoticonBean> queryEmoticonBeanList(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        try {
            int count = cursor.getCount();
//            android.util.Log.i(TAG, "EmoticonBean count="+count);
            ArrayList<EmoticonBean> beanList = new ArrayList<EmoticonBean>();
            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    EmoticonBean emoticonBeanTmp = buildEmoticonBeanFromCursor(cursor);
                    beanList.add(emoticonBeanTmp);
//                    android.util.Log.i(TAG, i+":="+emoticonBeanTmp.toString());
                    cursor.moveToNext();
                }
                return beanList;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public ArrayList<EmoticonBean> queryAllEmoticonBeans() {
        String sql = "select * from " + TABLE_NAME_EMOTICONS;
        return queryEmoticonBeanList(sql);
    }

    /**获取正常类型贴纸的最大order值*/
    public int queryEmoticonSetMaxOrder(String uid) {
        String sql = "select max(" + TableColumns.EmoticonSetColumns.ORDER + ") from " + TABLE_NAME_EMOTICONSET + " where "
                + TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and "
                + TableColumns.EmoticonSetColumns.STICK_TYPE + " = 1 ";
        int result = -1;
        Cursor cursor = null;
        try {
            sql = sql + " order by '" + TableColumns.EmoticonSetColumns.STICK_TYPE + "' desc, "
                    + TableColumns.EmoticonSetColumns.ORDER + " desc, "
                    + TableColumns.EmoticonSetColumns.UPDATETIME + " desc ";
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**获取贴纸包的更新时间值*/
    public long queryEmoticonSetUpdatetime(int type, String id) {
        String sql = "select " + TableColumns.EmoticonSetColumns.UPDATETIME + " from " + TABLE_NAME_EMOTICONSET + " where "
                + TableColumns.EmoticonSetColumns.STICK_TYPE + " = " + type;
        if (!TextUtils.isEmpty(id)) {
            sql = sql + " and " + TableColumns.EmoticonSetColumns.ID + " = '" + id + "'";
        }
        long updatetime = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                updatetime = cursor.getLong(0);
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return updatetime;
    }

    /**获取预置贴纸的原来order值*/
    public int queryEmoticonSetOrder(int type, String uid, String id) {
        String sql = "select " + TableColumns.EmoticonSetColumns.ORDER + " from " + TABLE_NAME_EMOTICONSET + " where ";
        if(type > 1) {
            sql += TableColumns.EmoticonSetColumns.UID + " = '" + DefEmoticons.DEFAULT_EMOJISET_UID + "' and "
                    + TableColumns.EmoticonSetColumns.STICK_TYPE + " = " + type;
        } else if(type == 1) {
            sql += TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and "
                    + TableColumns.EmoticonSetColumns.STICK_TYPE + " = 1 " + " and "
                    + TableColumns.EmoticonSetColumns.ID + " = '" + id + "'"
                    + " order by " + TableColumns.EmoticonSetColumns.UPDATETIME + " desc ";
        }
        int result = -1;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**获取预置贴纸的原来id值*/
    public String queryEmoticonSetId(int type) {
        String sql = "select " + TableColumns.EmoticonSetColumns.ID + " from " + TABLE_NAME_EMOTICONSET + " where "
                + TableColumns.EmoticonSetColumns.UID + " = '" + DefEmoticons.DEFAULT_EMOJISET_UID + "' and "
                + TableColumns.EmoticonSetColumns.STICK_TYPE + " = " + type;
        String result = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                result = cursor.getString(0);
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**获取贴纸的flag值*/
    public int queryEmoticonSetFlag(int type, String id) {
        String sql = "select " + TableColumns.EmoticonSetColumns.FLAG + " from " + TABLE_NAME_EMOTICONSET + " where "
                + TableColumns.EmoticonSetColumns.STICK_TYPE + " = " + type;
        if (!TextUtils.isEmpty(id)) {
            sql = sql + " and " + TableColumns.EmoticonSetColumns.ID + " = '" + id + "'";
        }
        int result = -1;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(0);
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public int updateEmoticonSetFlag(String stickerPackageId, int flag) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.FLAG, flag);
        int result = db.update(TABLE_NAME_EMOTICONSET, values, TableColumns.EmoticonSetColumns.ID + "='" + stickerPackageId + "'", null);

        return result;
    }

    public int updateEmoticonSetFlag(int type, int flag) {
        if(type < 2) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.FLAG, flag);
        int result = db.update(TABLE_NAME_EMOTICONSET, values, TableColumns.EmoticonSetColumns.STICK_TYPE + "=" + type, null);

        return result;
    }

    public int updateEmoticonSetLastCheck(String stickerPackageId, long lastUpdateTime) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.LAST_CHECKTIME, lastUpdateTime);
        int result = db.update(TABLE_NAME_EMOTICONSET, values, TableColumns.EmoticonSetColumns.ID + "='" + stickerPackageId + "'", null);

        return result;
    }

    public int updateEmoticonSetLastCheck(int type, long lastUpdateTime) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.LAST_CHECKTIME, lastUpdateTime);
        int result = db.update(TABLE_NAME_EMOTICONSET, values, TableColumns.EmoticonSetColumns.STICK_TYPE + "=" + type, null);

        return result;
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSetByID(String uid, String ... setIDs) {
        if(setIDs == null || setIDs.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and ";
        for(int i = 0 ;i < setIDs.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.ID + " = '" + setIDs[i] + "' ";
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSetByType(String uid, int ... setTypes) {
        if(setTypes == null || setTypes.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and ";
        for(int i = 0 ;i < setTypes.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.STICK_TYPE + " = " + setTypes[i] + " ";
        }
//        android.util.Log.i(TAG, sql);
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryUnUpdateEmoticonSetByType(String uid, int ... setTypes) {
        if(setTypes == null || setTypes.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and " +
                TableColumns.EmoticonSetColumns.FLAG + " > 1 and ";
        for(int i = 0 ;i < setTypes.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.STICK_TYPE + " = " + setTypes[i] + " ";
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSet(String uid, String ... setNames) {
        if(setNames == null || setNames.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and ";
        for(int i = 0 ;i < setNames.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.NAME + " = '" + setNames[i] + "' ";
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSet(ArrayList<String> setNameList) {
        if(setNameList == null || setNameList.size() == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " ;
        int i = 0 ;
        for(String name : setNameList){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.NAME + " = '" + name + "' ";
            i++;
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryAllEmoticonSet(String uid) {
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where (" +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' " +
                "or " + TableColumns.EmoticonSetColumns.UID + " = '" + DefEmoticons.DEFAULT_EMOJISET_UID + "') and " +
                TableColumns.EmoticonSetColumns.STICK_TYPE + " in (1,2) ";
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonBean> queryAllEmoticon(String parentId) {
        String sqlGetEmoticonBean = "select * from " + TABLE_NAME_EMOTICONS
                + " where " + TableColumns.EmoticonColumns.PARENT_ID + " = '" + parentId + "'";
        return queryEmoticonBeanList(sqlGetEmoticonBean);
    }

    //todo testonly
//    public ArrayList<EmoticonBean> removeStickerids(int type) {
//        String sqlGetEmoticonBean = "update " + TABLE_NAME_EMOTICONS + " set stickerid = ''"
//                + " where " + TableColumns.EmoticonColumns.STICK_TYPE + " = " + type ;
//        return queryEmoticonBeanList(sqlGetEmoticonBean);
//    }

    public ArrayList<EmoticonSetBean> queryEmoticonSet(String sql) {
        Cursor cursor = null;
        try {
            sql = sql + " order by " + TableColumns.EmoticonSetColumns.STICK_TYPE + " desc, "
                    + TableColumns.EmoticonSetColumns.ORDER + " desc, "
                    + TableColumns.EmoticonSetColumns.UPDATETIME + " desc ";
            android.util.Log.i(TAG, sql);
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
//            android.util.Log.i(TAG, "EmoticonSetBean count="+count);
            ArrayList<EmoticonSetBean> beanList = new ArrayList<EmoticonSetBean>();
            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    int stickType = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.STICK_TYPE));
                    // v3.1版本添加最近类型的贴纸[type == 100]
                    if (stickType == 100) {

                    } else if(stickType <= 0 || stickType >= 2) {
                        // v2.7版本只预置限时贴纸[type == 2] v3.2.6版本不需要抽出来推荐贴纸包了
                        cursor.moveToNext();
                        if(cursor == null) {
                            break;
                        }
                        continue;
                    }
                    String id = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ID));
                    String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.NAME));
                    int line = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.LINE));
                    int order = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ORDER));
                    int row = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ROW));
                    String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICONURI));
                    String iconUrl = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICONURL));
                    String iconName = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICONNAME));
                    boolean isshowdelbtn = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ISSHOWDELBTN)) == 1 ? true : false;
                    int itempadding = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ITEMPADDING));
                    int horizontalspacing = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.HORIZONTALSPACING));
                    int verticalSpacing = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.VERTICALSPACING));
                    int flag = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.FLAG));
                    long updatetime = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.UPDATETIME));
                    long lastchecktime = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.LAST_CHECKTIME));
                    ArrayList<EmoticonBean> emoticonList = null;
                    if(stickType > 1) {
                        String sqlGetEmoticonBean = "select * from " + TABLE_NAME_EMOTICONS
                                + " where " + TableColumns.EmoticonColumns.PARENT_STICK_TYPE + " = " + stickType + " ";
                        emoticonList = queryEmoticonBeanList(sqlGetEmoticonBean);
                    } else  /*if (!TextUtils.isEmpty(id))*/ {
                        String sqlGetEmoticonBean = "select * from " + TABLE_NAME_EMOTICONS
                                + " where " + TableColumns.EmoticonColumns.PARENT_ID + " = '" + id + "' and "
                                + TableColumns.EmoticonColumns.PARENT_STICK_TYPE + " = " + stickType + " ";
                        emoticonList = queryEmoticonBeanList(sqlGetEmoticonBean);
                    }

//                    int pageCount = 0;
                    if (emoticonList != null) {
                        android.util.Log.i(TAG, "EmoticonSetBean id= "+id + ",stickType="+stickType+"'s emoticonList  count="+emoticonList.size());
//                        int del = isshowdelbtn ? 1 : 0;
//                        int everyPageMaxSum = row * line - del;
//                        pageCount = (int) Math.ceil((double) emoticonList.size() / everyPageMaxSum);
                    } else {
                        android.util.Log.i(TAG, "EmoticonSetBean id= "+id + ",stickType="+stickType+"'s emoticonList  is null");
                    }

                    EmoticonSetBean bean = new EmoticonSetBean(id, order, name, stickType, line, row, iconUri, iconUrl, iconName,
                            isshowdelbtn, itempadding, horizontalspacing, verticalSpacing, flag, updatetime, lastchecktime, emoticonList);
                    beanList.add(bean);
                    cursor.moveToNext();
                    if(cursor == null) {
                        break;
                    }
                }
//                android.util.Log.i(TAG, "beanList's count  is " + beanList.size());
                if (true)
                    return beanList;
//
//                Collections.sort(beanList, new Comparator<EmoticonSetBean>() {
//                    @Override
//                    public int compare(EmoticonSetBean lhs, EmoticonSetBean rhs) {
//                        // 排序方式是111123456
//                        return rhs.getStickType() - lhs.getStickType();
//                    }
//                });

                int end = 0;
                for (int i = 0; i < beanList.size(); i++) {
//                    Log.i("sort", i + "---" + beanList.get(i).getStickType() + ":" + beanList.get(i).getId());
                    if(beanList.get(i).getStickType() > 1) {
                        end++;
                    } else {
                        break;
                    }
                }
                if (end < 2) {
                    return beanList;
                }

//                Log.i("sort", "-----------------------end=" + end + "\n");

                List<EmoticonSetBean> tmp = beanList.subList(0,end);
//                for (int i = 0; i < tmp.size(); i++) {
//                    Log.i("sort", i + "-tmp--" + tmp.get(i).getStickType());
//                }
                Collections.sort(tmp, new Comparator<EmoticonSetBean>() {
                    @Override
                    public int compare(EmoticonSetBean lhs, EmoticonSetBean rhs) {
                        // 排序方式是23456
                        return lhs.getStickType() - rhs.getStickType();
                    }
                });
                EmoticonSetBean recentSetBean = null;
                for (int i = 0; i < tmp.size(); i++) {
//                    Log.i("sort", i + "-tmp2--" + tmp.get(i).getStickType()+":"+tmp.get(i).getId());
                    if (tmp.get(i).getStickType() == 100) {
                        recentSetBean = tmp.get(i);
                        tmp.remove(i);
//                        Log.i("sort", i + "-将最近贴纸单独提出来--");
                        break;
                    }
                }
                ArrayList<EmoticonSetBean> beanList2 = new ArrayList<EmoticonSetBean>();
                if (recentSetBean != null) {
                    beanList2.add(recentSetBean);
                }
                beanList2.addAll(tmp);
                if (end < beanList.size()) {
                    List<EmoticonSetBean> tail = beanList.subList(end, beanList.size());
                    beanList2.addAll(tail);
                }
//                List<EmoticonSetBean> tail = beanList.subList(end, beanList.size());
//                for (int i = 0; i < tail.size(); i++) {
//                    Log.i("sort", i + "-tail--" + tail.get(i).getStickType()+":"+tail.get(i).getId());
//                }
//
//                for (int i = 0; i < beanList2.size(); i++) {
//                    Log.i("sort", i + "---" + beanList2.get(i).getStickType()+":"+beanList2.get(i).getId());
//                }
                return beanList2;
            }
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public void cleanup() {}

    public void close() {
        if (this.db != null) {
            this.db.close();
            this.db = null;
            mInstance = null;
        }
    }

    public static int getVERSION() {
        return VERSION;
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        private static void createEmoticonsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICONS + " ( " +
                    TableColumns.EmoticonColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonColumns.ID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.PARENT_ID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.STICKER_ID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.STICK_TYPE + " INTEGER, " +
                    TableColumns.EmoticonColumns.PARENT_STICK_TYPE + " INTEGER, " +
                    TableColumns.EmoticonColumns.EVENTTYPE + " INTEGER, " +
                    TableColumns.EmoticonColumns.ZOOM + " INTEGER DEFAULT 65, " +
                    TableColumns.EmoticonColumns.CONTENT + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.BUBBLETEXT + " TEXT, " +
                    TableColumns.EmoticonColumns.ICONURI + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.ICONURL + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.GIFURI + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.GIFURL + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.TAG + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.USESTAT + " INTEGER, " +
                    TableColumns.EmoticonColumns.EMOTICONSET_NAME + " TEXT NOT NULL);");


            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICONSET + " ( " +
                    TableColumns.EmoticonSetColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonSetColumns.ID + " TEXT NOT NULL UNIQUE, " +
                    TableColumns.EmoticonSetColumns.UID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonSetColumns.STICK_TYPE + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ORDER + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.NAME + " TEXT NOT NULL, " +
                    TableColumns.EmoticonSetColumns.LINE + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ROW + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ICONURI + " TEXT, " +
                    TableColumns.EmoticonSetColumns.ICONURL + " TEXT, " +
                    TableColumns.EmoticonSetColumns.ICONNAME + " TEXT, " +
                    TableColumns.EmoticonSetColumns.ISSHOWDELBTN + " BOOLEAN, " +
                    TableColumns.EmoticonSetColumns.ITEMPADDING + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.HORIZONTALSPACING + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.VERTICALSPACING + " TEXT, " +
                    TableColumns.EmoticonSetColumns.FLAG + " INTEGER DEFAULT 0, " +
                    TableColumns.EmoticonSetColumns.LAST_CHECKTIME + " INTEGER DEFAULT 0, " +
                    TableColumns.EmoticonSetColumns.UPDATETIME + " INTEGER);");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            android.util.Log.i(TAG, "DB创建为 " + VERSION +" 版本");
            createEmoticonsTable(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int currentVersion) {
            for (int version = oldVersion + 1; version <= currentVersion; version++) {
                upgradeTo(sqLiteDatabase, version);
                android.util.Log.i(TAG, "DB升级到 " + version +" 版本");
            }
            setUpgrade(true);
        }

        private void upgradeTo(SQLiteDatabase db, int version) {
            switch (version) {
                case 1:
                    createEmoticonsTable(db);
                    break;
                case 2:
                    boolean result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONS,TableColumns.EmoticonColumns.STICK_TYPE);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONS + " ADD " + TableColumns.EmoticonColumns.STICK_TYPE + " INTEGER default 0");
                    }
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONSET,TableColumns.EmoticonSetColumns.STICK_TYPE);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONSET + " ADD " + TableColumns.EmoticonSetColumns.STICK_TYPE + " INTEGER default 0");
                    }
                    break;
                case 3:
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONS,TableColumns.EmoticonColumns.BUBBLETEXT);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONS + " ADD " + TableColumns.EmoticonColumns.BUBBLETEXT + " TEXT");
                    }
                    // 需要将文字气泡的贴纸预制到DB中,以及文件解压
                    break;
                case 4:
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONS,TableColumns.EmoticonColumns.STICK_TYPE);
                    if(result){
                        // v2.7去除非普通类型的贴纸
                        db.delete(TABLE_NAME_EMOTICONSET, TableColumns.EmoticonSetColumns.STICK_TYPE+">1", null);
                        db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.STICK_TYPE + ">1", null);
                    }
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONS,TableColumns.EmoticonColumns.STICKER_ID);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONS + " ADD " + TableColumns.EmoticonColumns.STICKER_ID + " TEXT NOT NULL DEFAULT ''");
                    }
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONS,TableColumns.EmoticonColumns.PARENT_STICK_TYPE);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONS + " ADD " + TableColumns.EmoticonColumns.PARENT_STICK_TYPE + " INTEGER default 1");
                    }
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONSET,TableColumns.EmoticonSetColumns.FLAG);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONSET + " ADD " + TableColumns.EmoticonSetColumns.FLAG + " INTEGER default 0");
                    }
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONSET,TableColumns.EmoticonSetColumns.LAST_CHECKTIME);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONSET + " ADD " + TableColumns.EmoticonSetColumns.LAST_CHECKTIME + " INTEGER default 0");
                    }
                    break;
                case 5:
                    result = DatabaseUtil.checkColumnExists(db,TABLE_NAME_EMOTICONS,TableColumns.EmoticonColumns.ZOOM);
                    if(!result){
                        db.execSQL("ALTER TABLE " + TABLE_NAME_EMOTICONS + " ADD " + TableColumns.EmoticonColumns.ZOOM + " INTEGER default 65");
                    }
                    // 需要将文字气泡的贴纸预制到DB中,以及文件解压
                    break;
                default:
                    throw new IllegalStateException("Don't know how to upgrade to " + version);
            }
        }
    }
}
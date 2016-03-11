package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.vo.BleStatVo;

/**
 * 蓝牙定位统计表
 * 开发者：JimmyZhang
 * 日期：2016/3/10
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BleStatDBUtil {

    private final static String TAG = BleStatDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static BleStatDBUtil instance = null;

    private BleStatDBUtil(){}

    public synchronized static BleStatDBUtil getInstance(){
        if(instance == null){
            instance = new BleStatDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = SVIPApplication.getInst().getApplicationContext();
        helper   = new DBOpenHelper(context);
    }

    /**
     * 插入蓝牙定位统计信息
     * @param bleStatVo
     * @return
     */
    public long insertBleLog(BleStatVo bleStatVo) {
        long rowId = -1;
        SQLiteDatabase db = null;
        try {
            long timestamp = bleStatVo.getTimestamp();
            long retryCount = bleStatVo.getRetryCount();
            long totalCount = bleStatVo.getTotalCount();
            String IMEI = bleStatVo.getIMEI();
            ContentValues values =  new ContentValues();
            values.put("imei",IMEI);
            values.put("timestamp",timestamp);
            values.put("retry_count",retryCount);
            values.put("total_count",totalCount);
            db = helper.getWritableDatabase();
            rowId = db.insert(DBOpenHelper.BLE_STAT_TBL, null, values);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return rowId;
    }

    /**
     * 更新网络请求总个数
     */
    public void updateTotalCount(){
        SQLiteDatabase db = null;
        String sql = null;
        try {
            db= helper.getWritableDatabase();
            sql = "update "+DBOpenHelper.BLE_STAT_TBL+" set total_count=total_count+1 where imei = "+DeviceUtils.getIMEI();
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
    }

    /**
     * 更新网络请求错误个数
     */
    public void updateRetryCount(){
        SQLiteDatabase db = null;
        String sql = null;
        try {
            db= helper.getWritableDatabase();
            sql = "update "+DBOpenHelper.BLE_STAT_TBL+" set retry_count=retry_count+1 where imei = "+DeviceUtils.getIMEI();
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
    }

    /**
     * 更新上传记录日期
     * @param timestamp
     * @return
     */
    public int updateStatTime(long timestamp){
        int id = -1;
        SQLiteDatabase db = null;
        try {
            db= helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("timestamp", timestamp);
            id = db.update(DBOpenHelper.BLE_STAT_TBL, values, " imei = ? ", new String[]{DeviceUtils.getIMEI()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 数据库是否存在蓝牙定位统计信息
     * @return
     */
    public boolean isStatExist() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.BLE_STAT_TBL, null,
                        null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }
        }
        return false;
    }

    /**
     * 获取蓝牙定位统计信息
     * @return
     */
    public BleStatVo queryBleStat(){
        BleStatVo bleStatVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.BLE_STAT_TBL, null,
                        null, null, null, null,null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        bleStatVo = new BleStatVo();
                        bleStatVo.setIMEI(cursor.getString(0));
                        bleStatVo.setTimestamp(cursor.getLong(1));
                        bleStatVo.setRetryCount(cursor.getLong(2));
                        bleStatVo.setTotalCount(cursor.getLong(3));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }

        }
        return bleStatVo;
    }
    
}

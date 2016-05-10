package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.BleLogVo;

import java.util.ArrayList;

/**
 * 蓝牙定位日志表
 * 开发者：JimmyZhang
 * 日期：2016/3/10
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BleLogDBUtil {

    private final static String TAG = BleLogDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static BleLogDBUtil instance = null;

    private BleLogDBUtil(){}

    public synchronized static BleLogDBUtil getInstance(){
        if(instance == null){
            instance = new BleLogDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        context = SVIPApplication.getInst().getApplicationContext();
        if(CacheUtil.getInstance().isLogin()){
            DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId()+".db";
        }
        helper   = new DBOpenHelper(context);
    }

    /**
     * 插入蓝牙定位日志信息
     * @param bleLogVo
     * @return
     */
    public long insertBleLog(BleLogVo bleLogVo) {
        long rowId = -1;
        SQLiteDatabase db = null;
        try {
            String phoneNum = bleLogVo.getPhoneNum();
            String deviceType = bleLogVo.getDeviceType();
            String brand = bleLogVo.getBrand();
            String imei = bleLogVo.getIMEI();
            int connectType = bleLogVo.getConnectedType();
            String errorMsg = bleLogVo.getErrorMessage();
            ContentValues values =  new ContentValues();
            values.put("phone_num",phoneNum);
            values.put("device_type",deviceType);
            values.put("brand",brand);
            values.put("imei",imei);
            values.put("connected_type",connectType);
            values.put("error_message",errorMsg);
            db = helper.getWritableDatabase();
            rowId = db.insert(DBOpenHelper.BLE_LOG_TBL, null, values);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return rowId;
    }

    /**
     * 清空蓝牙定位日志信息
     * @return
     */
    public long clearBleLog(){
        SQLiteDatabase db = null;
        long rowId = -1;
        try {
            db = helper.getWritableDatabase();
            rowId = db.delete(DBOpenHelper.BLE_LOG_TBL, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return rowId;
    }

    /**
     * 获取蓝牙定位日志信息
     * @return
     */
    public ArrayList<BleLogVo> queryBleLogL(){
        ArrayList<BleLogVo> bleLogList = null;
        BleLogVo bleLogVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.BLE_LOG_TBL, null,
                        null, null, null, null,null);
                if (cursor != null && cursor.getCount() > 0) {
                    bleLogList = new ArrayList<BleLogVo>();
                    while (cursor.moveToNext()) {
                        bleLogVo = new BleLogVo();
                        bleLogVo.setPhoneNum(cursor.getString(1));
                        bleLogVo.setDeviceType(cursor.getString(2));
                        bleLogVo.setBrand(cursor.getString(3));
                        bleLogVo.setIMEI(cursor.getString(4));
                        bleLogVo.setConnectedType(cursor.getInt(5));
                        bleLogVo.setErrorMessage(cursor.getString(6));
                        bleLogList.add(bleLogVo);
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
        return bleLogList;
    }

}

package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.BleLogVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;

import java.util.ArrayList;

/**
 * 营销消息记录历史表
 * 开发者：杜健德
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BeaconMsgDBUtil {

    private final static String TAG = BeaconMsgDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static BeaconMsgDBUtil instance = null;

    private BeaconMsgDBUtil(){}

    public synchronized static BeaconMsgDBUtil getInstance(){
        if(instance == null){
            instance = new BeaconMsgDBUtil();
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
     * 插入营销推送消息
     * @param yunBaMsgVo
     * @return
     */
    public long insertBeaconMsg( final YunBaMsgVo yunBaMsgVo) {
        long rowId = -1;
        SQLiteDatabase db = null;
        if(yunBaMsgVo == null){
            return rowId;
        }
        try {
            ContentValues values =  new ContentValues();
            values.put("title",yunBaMsgVo.getTitle());
            values.put("locid",yunBaMsgVo.getLocid());
            values.put("alert",yunBaMsgVo.getAlert());
            values.put("shopid",yunBaMsgVo.getShopid());
            values.put("content",yunBaMsgVo.getContent());
            values.put("button",yunBaMsgVo.getButton());
            values.put("img_url",yunBaMsgVo.getImg_url());
            values.put("insert_time", System.currentTimeMillis());
            values.put("button_url",yunBaMsgVo.getButton_url());
            values.put("has_look",yunBaMsgVo.isHasLook() ? 1 : 0);
            db = helper.getWritableDatabase();
            rowId = db.insert(DBOpenHelper.BEACON_MSG_TBL, null, values);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return rowId;
    }



    /**
     * 获取前20条营销推送消息
     * @return
     */
    public ArrayList<YunBaMsgVo> queryBeaconMsg(){
        ArrayList<YunBaMsgVo> beaconMsgList = null;
        YunBaMsgVo yunBaMsgVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.BEACON_MSG_TBL, null,
                        null, null, null, null,"insert_time desc","20");
                if (cursor != null && cursor.getCount() > 0) {
                    beaconMsgList = new ArrayList<YunBaMsgVo>();
                    while (cursor.moveToNext()) {
                        yunBaMsgVo = new YunBaMsgVo();
                        yunBaMsgVo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                        yunBaMsgVo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        yunBaMsgVo.setLocid(cursor.getString(cursor.getColumnIndex("locid")));
                        yunBaMsgVo.setAlert(cursor.getString(cursor.getColumnIndex("alert")));
                        yunBaMsgVo.setShopid(cursor.getString(cursor.getColumnIndex("shopid")));
                        yunBaMsgVo.setContent(cursor.getString(cursor.getColumnIndex("content")));
                        yunBaMsgVo.setButton(cursor.getString(cursor.getColumnIndex("button")));
                        yunBaMsgVo.setImg_url(cursor.getString(cursor.getColumnIndex("img_url")));
                        yunBaMsgVo.setButton_url(cursor.getString(cursor.getColumnIndex("button_url")));
                        yunBaMsgVo.setInsert_time(cursor.getLong(cursor.getColumnIndex("insert_time")));
                        yunBaMsgVo.setHasLook(cursor.getInt(cursor.getColumnIndex("has_look")) == 1 ? true : false);
                        beaconMsgList.add(yunBaMsgVo);
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
        return beaconMsgList;
    }

    /**
     * 更新已读状态
     */
    public void updateBeaconMsgRead(int id){
        SQLiteDatabase db = null;
        String sql = null;
        try {
            db= helper.getWritableDatabase();
            sql = "update "+DBOpenHelper.BEACON_MSG_TBL+" set has_look=1 where _id = "+ id;
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
    }

    /**
     * 从数据库获取一条未读信息，并更新已读状态
     * @return
     */
    public YunBaMsgVo popUnReadBeaconMsg(){
        YunBaMsgVo yunBaMsgVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                String sql = "select * from "+DBOpenHelper.BEACON_MSG_TBL+" where has_look=? limit 1";
                cursor = db.rawQuery(sql,new String[]{"0"});
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        yunBaMsgVo = new YunBaMsgVo();
                        yunBaMsgVo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                        yunBaMsgVo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                        yunBaMsgVo.setLocid(cursor.getString(cursor.getColumnIndex("locid")));
                        yunBaMsgVo.setAlert(cursor.getString(cursor.getColumnIndex("alert")));
                        yunBaMsgVo.setShopid(cursor.getString(cursor.getColumnIndex("shopid")));
                        yunBaMsgVo.setContent(cursor.getString(cursor.getColumnIndex("content")));
                        yunBaMsgVo.setButton(cursor.getString(cursor.getColumnIndex("button")));
                        yunBaMsgVo.setImg_url(cursor.getString(cursor.getColumnIndex("img_url")));
                        yunBaMsgVo.setButton_url(cursor.getString(cursor.getColumnIndex("button_url")));
                        yunBaMsgVo.setInsert_time(cursor.getLong(cursor.getColumnIndex("insert_time")));
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
        if(yunBaMsgVo != null){
            updateBeaconMsgRead(yunBaMsgVo.getId());
        }
        return yunBaMsgVo;
    }

}

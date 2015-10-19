package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.factory.ServerPersonalFactory;

import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.ServerPersonalVo;


import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/10/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServerPeronalDBUtil {

    private final static String TAG = ServerPeronalDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper    helper;
    private static ServerPeronalDBUtil instance;

    private ServerPeronalDBUtil(){
    }

    public synchronized static ServerPeronalDBUtil getInstance(){
        if(null == instance){
            instance = new ServerPeronalDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 批量插入商家详细信息
     * @return
     */
    public long batchAddServerPernal (List<ServerPersonalVo> serverPersonalVoList){
        SQLiteDatabase database = null;
        ContentValues values = null;

        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (ServerPersonalVo item : serverPersonalVoList) {
                values = ServerPersonalFactory.getInstance().buildContentValues(item);
                try {
                    id = database.insert(DBOpenHelper.SERVER_PERSONAL_TBL, null,values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(id == -1){
                    id = database.update(DBOpenHelper.SERVER_PERSONAL_TBL, values, " shopid = ? ", new String[]{item.getShopid()});
                }
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".batchAddMessages->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.setTransactionSuccessful();
                database.endTransaction();
                database.close();
            }
        }
        return id;
    }

    /**
     * 获取专属客服
     * @param shopid
     * @return
     */
    public String getSalesid(String shopid){
        return queryValueByShopID("salesid",shopid);
    }


    /**
     * 根据商家ID查询某字段的值
     * @param field
     * @param shopID
     * @return
     */
    public String queryValueByShopID(String field,String shopID) {
        String shopValue = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.SERVER_PERSONAL_TBL, new String[]{field},
                    "shopid = ? ", new String[]{shopID}, null, null, null);

            if(null != cursor && cursor.getCount() > 0){
                if(cursor.moveToFirst()){
                    shopValue = cursor.getString(0);
                }
            }

        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryValueByShopID->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != cursor)
                cursor.close();
            if(null != db)
                db.close();

        }
        return shopValue;
    }
}

package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.activity.city.citylist.CityModel;
import com.zkjinshi.svip.factory.PrivilegeFactory;
import com.zkjinshi.svip.response.PrivilegeResponse;
import com.zkjinshi.svip.utils.VIPContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：杜健德
 * 日期：2016/1/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PrivilegeDBUtil {

    private final static String TAG = PrivilegeDBUtil.class.getSimpleName();
    private Context         context;
    private DBOpenHelper    helper;
    private static PrivilegeDBUtil instance;

    private PrivilegeDBUtil(){
    }

    public synchronized static PrivilegeDBUtil getInstance(){
        if(null == instance){
            instance = new PrivilegeDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 新增特权
     * @return
     */
    public long addPrivilegeModel(PrivilegeResponse privilegeModel,String shopid){
        ContentValues values = PrivilegeFactory.getInstance().buildUpdateContentValues(privilegeModel,shopid);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.PRIVILEGE_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".addPrivilegeModel->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 批量插入特权名称
     * @return
     */
    public long batchAddPrivilegeModels(ArrayList<PrivilegeResponse> privilegeResponses,String shopid){
        SQLiteDatabase database = null;
        ContentValues  values   = null;
        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (PrivilegeResponse privilegeModel : privilegeResponses) {
                values = PrivilegeFactory.getInstance().buildUpdateContentValues(privilegeModel,shopid);
                try {
                    id = database.insert(DBOpenHelper.PRIVILEGE_TBL, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
               
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".batchAddPrivilegeModels->"+e.getMessage());
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
     * 查询获取特权数据
     * @return
     */
    public ArrayList<PrivilegeResponse> queryPrivilegeByShopid(String shopid) {
        SQLiteDatabase database = null;
        ArrayList<PrivilegeResponse> privilegeResponses = new ArrayList<PrivilegeResponse>();
        try {
            database = helper.getReadableDatabase();
            Cursor cursor = database.query(DBOpenHelper.PRIVILEGE_TBL, null, "shopid=?", new String[]{shopid}, null, null,null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                PrivilegeResponse PrivilegeModel = new PrivilegeResponse();
                PrivilegeModel.setPrivilegeDesc(cursor.getString(cursor.getColumnIndex("privilegeDesc")));
                PrivilegeModel.setPrivilegeIcon(cursor.getString(cursor.getColumnIndex("privilegeIcon")));
                PrivilegeModel.setPrivilegeName(cursor.getString(cursor.getColumnIndex("privilegeName")));
                PrivilegeModel.setShopName(cursor.getString(cursor.getColumnIndex("shopName")));
                privilegeResponses.add(PrivilegeModel);
            }
            cursor.close();
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryAll->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.close();
            }
            return privilegeResponses;
        }
    }

    /**
     * 删除特权数据
     * @return
     */
    public boolean deletePrivilegeByShopid(String shopid){
        SQLiteDatabase database = null;
        try {
            database = helper.getWritableDatabase();
            int num = database.delete(DBOpenHelper.PRIVILEGE_TBL, "shopid=?", new String[]{shopid});
            if(num > 0){
                return true;
            }

        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".delete->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.close();
            }

        }
        return false;
    }


    /**
     * 获取一个商家的特权
     * @return
     */
    public  ArrayList<PrivilegeResponse> getOneShopPrivileges(){
        String shopid = getTopPrivilegeShopid();
        return queryPrivilegeByShopid(shopid);
    }

    /**
     * 获取第一条特权的shopid
     * @return
     */
    public String getTopPrivilegeShopid(){
        String shopid = "";
        SQLiteDatabase database = null;
        try {
            database = helper.getReadableDatabase();
            String sql =   "SELECT shopid FROM "
                    + DBOpenHelper.PRIVILEGE_TBL
                    + " LIMIT 1";
            Cursor cursor = database.rawQuery(sql , null);
            if (cursor != null && cursor.getCount() > 0) {
                if(cursor.moveToFirst()){
                    shopid = cursor.getString(cursor.getColumnIndex("shopid"));
                }
            }
            cursor.close();
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".queryCityNames->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.close();
            }
            return shopid;
        }
    }


}

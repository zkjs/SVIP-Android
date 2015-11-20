package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.factory.ShopDetailFactory;
import com.zkjinshi.svip.factory.UserDetailFactory;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.ShopDetailVo;
import com.zkjinshi.svip.vo.UserDetailVo;

import java.util.HashMap;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopDetailDBUtil {

    private final static String TAG = ShopDetailDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper    helper;
    private static ShopDetailDBUtil instance;

    private ShopDetailDBUtil(){
    }

    public synchronized static ShopDetailDBUtil getInstance(){
        if(null == instance){
            instance = new ShopDetailDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 判断商家是否已经存在
     * @return
     */
    public boolean isShopExists(String shopID){
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.SHOP_INFO_TBL +
                    " where shopid = " + shopID, null);
            if(cursor.getCount() > 1){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isShopExists->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != cursor)
                cursor.close();
            if(null != db)
                db.close();

        }
        return false;
    }

    /**
     * 更新商家详细信息
     * @param shopDetailVo
     */
    public long updateShopDetail(ShopDetailVo shopDetailVo) {
        ContentValues values = ShopDetailFactory.getInstance().buildContentValues(shopDetailVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.SHOP_INFO_TBL, values, "shopid = ?",
                    new String[]{shopDetailVo.getShopid()});
        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".updateShopDetail->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 添加商家详细信息
     * @param shopDetailVo
     */
    public long addShopDetail(ShopDetailVo shopDetailVo) {
        ContentValues values = ShopDetailFactory.getInstance().buildContentValues(shopDetailVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.SHOP_INFO_TBL, null, values);
        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addShopDetail->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 批量插入商家详细信息
     * @return
     */
    public long batchAddShopInfo (List<ShopDetailVo> shopResponseList){
        SQLiteDatabase database = null;
        ContentValues values = null;

        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (ShopDetailVo item : shopResponseList) {
                values = ShopDetailFactory.getInstance().buildContentValues(item);
                try {
                    id = database.insert(DBOpenHelper.SHOP_INFO_TBL, null,values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(id == -1){
                    id = database.update(DBOpenHelper.SHOP_INFO_TBL, values, " shopid = ? ", new String[]{item.getShopid()});
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
     * 根据商家ID查询商家Name
     * @param shopID
     * @return
     */
    public String queryShopNameByShopID(String shopID) {
        return queryShopValueByShopID("fullname", shopID);
    }

    /**
     * 根据商家ID查询商家联系电话
     * @param shopID
     * @return
     */
    public String queryShopPhoneByShopID(String shopID) {
      return queryShopValueByShopID("phone",shopID);
    }

    /**
     * 根据商家ID查询商家Logo
     * @param shopID
     * @return
     */
    public String queryShopLogoByShopID(String shopID) {
        return queryShopValueByShopID("logo",shopID);
    }

    /**
     * 根据商家ID查询商家某字段的值
     * @param field
     * @param shopID
     * @return
     */
    public String queryShopValueByShopID(String field,String shopID) {
        String shopValue = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.SHOP_INFO_TBL, new String[]{field},
                    "shopid = ? ", new String[]{shopID}, null, null, null);

            if(null != cursor && cursor.getCount() > 0){
                if(cursor.moveToFirst()){
                    shopValue = cursor.getString(0);
                }
            }

        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryShopValueByShopID->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != cursor)
                cursor.close();
            if(null != db)
                db.close();

        }
        return shopValue;
    }

    public HashMap<String,String> queryShopNames(){
        HashMap<String,String> dateMap = new HashMap<String,String>();
        String shopValue = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.SHOP_INFO_TBL, new String[]{"fullname","shopid"},null, null, null, null, null);

            if(null != cursor && cursor.getCount() > 0){
                while(cursor.moveToNext()){
                    dateMap.put(cursor.getString(0),cursor.getString(1));
                }
            }

        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryShopValueByShopID->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != cursor)
                cursor.close();
            if(null != db)
                db.close();

        }
        return dateMap;
    }
}

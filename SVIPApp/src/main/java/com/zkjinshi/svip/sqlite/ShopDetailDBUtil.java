package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.factory.ShopDetailFactory;
import com.zkjinshi.svip.factory.UserDetailFactory;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.ShopDetailVo;
import com.zkjinshi.svip.vo.UserDetailVo;

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
     * 添加用户详细信息
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
     * 根据商家ID查询商家Name
     * @param shopID
     * @return
     */
    public String queryShopNameByShopID(String shopID) {
        String shopName = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.SHOP_INFO_TBL, new String[]{"fullname"},
                    "shopid = ? ", new String[]{shopID}, null, null, null);

            if(null != cursor && cursor.getCount() > 0){
                while(cursor.moveToFirst()){
                    shopName = cursor.getString(0);
                }
            }

        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryShopNameByShopID->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
            if(null != cursor)
                db.close();
        }
        return shopName;
    }
}

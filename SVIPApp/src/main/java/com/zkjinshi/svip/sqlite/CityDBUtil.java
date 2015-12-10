package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.activity.city.citylist.CityFactory;
import com.zkjinshi.svip.activity.city.citylist.CityModel;
import com.zkjinshi.svip.utils.VIPContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityDBUtil {

    private final static String TAG = CityDBUtil.class.getSimpleName();

    private Context         context;
    private DBOpenHelper    helper;
    private static CityDBUtil instance;

    private CityDBUtil(){
    }

    public synchronized static CityDBUtil getInstance(){
        if(null == instance){
            instance = new CityDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 新增城市
     * @return
     */
    public long addCityModel(CityModel cityModel){
        ContentValues values = CityFactory.getInstance().buildAddContentValues(cityModel);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.CITY_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".addCityModel->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 更新城市记录
     * @return
     */
    public long updateCityModel(CityModel cityModel){
        ContentValues values = CityFactory.getInstance().buildUpdateContentValues(cityModel);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.CITY_TBL, values, " city_name = ?", new String[]{cityModel.getCityName()});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".updateCityModel->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 批量插入城市名称
     * @return
     */
    public long batchAddCityModels(List<CityModel> cityModels){
        SQLiteDatabase database = null;
        ContentValues  values   = null;
        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (CityModel cityModel : cityModels) {
                values = CityFactory.getInstance().buildAddContentValues(cityModel);
                try {
                    id = database.insert(DBOpenHelper.CITY_TBL, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(id == -1){
                    id = database.update(DBOpenHelper.CITY_TBL, values, " city_name = ? ",
                                                   new String[]{cityModel.getCityName()});
                }
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".batchAddCityModels->"+e.getMessage());
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
     * 根据输入条件模糊查询获取城市数据
     * @param string
     * @return
     */
    public ArrayList<CityModel> queryCityNamesByInput(String string) {
        SQLiteDatabase database = null;
        ArrayList<CityModel> names = new ArrayList<>();
        try {
            database = helper.getReadableDatabase();
            String sql =   "SELECT CityName FROM "
                            + DBOpenHelper.CITY_TBL
                            + " where city_name like '% "
                            + string
                            + " %' ORDER BY name_sort";

            Cursor cursor = database.rawQuery(sql , null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                CityModel cityModel = new CityModel();
                cityModel.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                names.add(cityModel);
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".queryCityNames->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.close();
            }
            return names;
        }
    }

    /**
     * 模糊查询获取城市数据
     * @return
     */
    public ArrayList<CityModel> queryAll() {
        SQLiteDatabase database = null;
        ArrayList<CityModel> names = new ArrayList<>();
        try {
            database = helper.getReadableDatabase();
            Cursor cursor = database.query(DBOpenHelper.CITY_TBL, null, null, null, null, null, "name_sort");
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                CityModel cityModel = new CityModel();
                cityModel.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                cityModel.setNameSort(cursor.getString(cursor.getColumnIndex("name_sort")));
                names.add(cityModel);
            }
            cursor.close();
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryAll->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.close();
            }
            return names;
        }
    }

    /**
     * 查询所有城市
     * @return
     */
    public Cursor queryAllCursor() {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = helper.getReadableDatabase();
            cursor   = database.query(DBOpenHelper.CITY_TBL, null, null, null, null, null, "name_sort");
            if (cursor.getCount() > 0) {
                return cursor;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryAll->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != database){
                database.close();
            }
        }
        return cursor;
    }

}

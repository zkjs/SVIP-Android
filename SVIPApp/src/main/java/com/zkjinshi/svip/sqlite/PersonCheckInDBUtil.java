package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.factory.PersonCheckInFactory;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.PersonCheckInVo;

/**
 * 表语句操作帮助类
 * 开发者：WinkyQin
 * 日期： 2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PersonCheckInDBUtil {

    private final static String TAG = PersonCheckInDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper    helper;
    private static PersonCheckInDBUtil instance;

    private PersonCheckInDBUtil(){
    }

    public synchronized static PersonCheckInDBUtil getInstance(){
        if(null == instance){
            instance = new PersonCheckInDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 更新入住人信息
     * @param personCheckInVo
     */
    public long updatePersonCheckIn(PersonCheckInVo personCheckInVo) {
        ContentValues values = PersonCheckInFactory.getInstance().buildUpdateContentValues(personCheckInVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.PERSON_CHECK_IN_TBL, values, " id = ? ",
                    new String[]{personCheckInVo.getId()+""});
        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".updatePersonCheckIn->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 添加入住人信息
     * @param personCheckInVo
     */
    public long addNewPersonCheckIn(PersonCheckInVo personCheckInVo) {
        ContentValues values = PersonCheckInFactory.getInstance().buildContentValues(personCheckInVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.PERSON_CHECK_IN_TBL, null, values);
            if(id == -1){
                id = db.update(DBOpenHelper.USER_INFO_TBL, values, " userid = ? ", new String[]{ personCheckInVo.getId()+"" });
                LogUtil.getInstance().info(LogLevel.INFO, TAG + "本地数据库更新入住人信息");
            }else{
                LogUtil.getInstance().info(LogLevel.INFO, TAG + "本地数据库插入入住人信息");
            }
        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addPersonCheckIn->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 根据入住人ID查询入住人用户对象
     * @param index
     */
    public PersonCheckInVo queryPersonCheckInByID(int index) {
        PersonCheckInVo personCheckInVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db     = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.PERSON_CHECK_IN_TBL, null ,
                    " id = ? ", new String[]{ index + "" }, null, null, null);

            if(null != cursor && cursor.getCount() > 0){
                if(cursor.moveToFirst()){
                    personCheckInVo = PersonCheckInFactory.getInstance().buildPersonCheckInVoByCursor(cursor);
                }
            }

        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryPersonCheckInByID->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != cursor)
                cursor.close();
            if(null != db)
                db.close();
        }
        return personCheckInVo;
    }
}

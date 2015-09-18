package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.factory.UserDetailFactory;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.UserDetailVo;

/**
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserDetailDBUtil {

    private final static String TAG = UserDetailDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper    helper;
    private static UserDetailDBUtil instance;

    private UserDetailDBUtil(){
    }

    public synchronized static UserDetailDBUtil getInstance(){
        if(null == instance){
            instance = new UserDetailDBUtil();
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
     * @param userDetailVo
     */
    public long addUserDetail(UserDetailVo userDetailVo) {

        ContentValues values = UserDetailFactory.getInstance().buildContentValues(userDetailVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();

            try {
                id = db.insert(DBOpenHelper.USER_INFO_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(id == -1){
                id = db.update(DBOpenHelper.USER_INFO_TBL, values, " userid = ? ", new String[]{userDetailVo.getUserid()});
                LogUtil.getInstance().info(LogLevel.INFO, TAG + "本地数据库更新用户资料");
            }else{
                LogUtil.getInstance().info(LogLevel.INFO, TAG + "本地数据库插入用户资料");
            }

        } catch (Exception e){
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addUserDetail->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(null != db)
            db.close();
        }
        return id;
    }

    /**
     * 根据userID查询用户信息
     * @param userID
     * @return
     */
    public UserDetailVo queryUserDetailByUserID(String userID) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        UserDetailVo userDetailVo = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.USER_INFO_TBL +
                    "where user_id = ?", new String[]{userID});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    userDetailVo = UserDetailFactory.getInstance().buildUserDetailVoByCursor(cursor);
                }
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryUserDetailByUserID->"+e.getMessage());
            e.printStackTrace();
        } finally{

            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
            return userDetailVo;
        }

    }

}

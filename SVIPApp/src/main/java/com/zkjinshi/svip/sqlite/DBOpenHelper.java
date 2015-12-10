package com.zkjinshi.svip.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

/**
 * 自定义SQLiteOpenHelper类
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DBOpenHelper extends SQLiteOpenHelper{

    public static String DB_NAME = "svip_sqlite.db";//根据每个用户创建一份数据库
    public static final int VERSION = 1;// 数据库版本
    public static final String MESSAGE_TBL = "messagetbl";// 消息表名
    public static final String CHAT_ROOM_TBL = "chatroomtbl";//聊天室表名
    public static final String CHAT_MEMBER_TBL = "chatmembertbl";//聊天室成员表
    public static final String USER_INFO_TBL   = "userinfotbl";//用户个人信息表
    public static final String SHOP_INFO_TBL   = "shopinfotbl";
    public static final String SERVER_PERSONAL_TBL   = "serverpersonaltbl"; //专属客服列表
    public static final String PERSON_CHECK_IN_TBL   = "person_check_in_tbl";//订单入住人表
    public static final String CITY_TBL              = "city_tbl";//城市名列表

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建数据库时调用
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        ORMOpenHelper.createTables(db);
    }

    /**
     * 当数据库更新时调用
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.getInstance().info(LogLevel.DEBUG, "【oldVersion】" + oldVersion);
        LogUtil.getInstance().info(LogLevel.DEBUG,"【newVersion】"+oldVersion);
        ORMOpenHelper.upgradeTables(db, TableOpenHelper.getTableNames());
    }

}

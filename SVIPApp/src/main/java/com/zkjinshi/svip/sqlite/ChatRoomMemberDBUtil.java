package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.factory.ChatRoomFactory;
import com.zkjinshi.svip.utils.VIPContext;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomMemberDBUtil {

    private final static String TAG = ChatRoomMemberDBUtil.class.getSimpleName();

    private Context      context;
    private DBOpenHelper helper;

    private static ChatRoomMemberDBUtil instance;

    private ChatRoomMemberDBUtil(){};

    public synchronized static ChatRoomMemberDBUtil getInstance(){
        if(null == instance){
            instance = new ChatRoomMemberDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 添加新的聊天成员记录
     * @return
     */
    public long addChatRoomMember(MsgCustomerServiceMediaChat msgMedia){
        ContentValues values = ChatRoomFactory.getInstance().buildAddContentValues(msgMedia);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.CHAT_MEMBER_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addChatRoomMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 添加新的聊天成员记录
     * @return
     */
    public long addChatRoomMember(MsgCustomerServiceTextChat msgText){
        ContentValues values = ChatRoomFactory.getInstance().buildAddContentValues(msgText);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.CHAT_MEMBER_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addChatRoomMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 添加新的聊天成员记录
     * @return
     */
    public long addChatRoomMember(MsgCustomerServiceImgChat msgImg){
        ContentValues values = ChatRoomFactory.getInstance().buildAddContentValues(msgImg);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.CHAT_MEMBER_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addChatRoomMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 查询单条成员记录表
     * @return
     */
    public Cursor queryRawChatMember(String sessionID, String userID){
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.CHAT_MEMBER_TBL +
                    "where session_id = ? and user_id = ?", new String[]{sessionID, userID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".rawQueryChatRoomMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return cursor;
    }

    /**
     * 查询所有成员记录表
     * @return
     */
    public Cursor queryAllChatMember(){
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.CHAT_MEMBER_TBL, null);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryAllChatRoomMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return cursor;
    }

    /**
     * 删除单条成员记录表
     * @return
     */
    public int deleteRawChatMember(String sessionID, String userID){
        SQLiteDatabase db = null;
        int id = -1;
        try {
            db = helper.getWritableDatabase();
            id = db.delete(DBOpenHelper.CHAT_MEMBER_TBL, "session_id = ? and user_id = ?" ,
                           new String[]{sessionID, userID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".deleteRawChatMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 删除成员记录表
     * @return
     */
    public int deleteAllChatMember(){
        SQLiteDatabase db = null;
        int id = -1;
        try {
            db = helper.getWritableDatabase();
            id = db.delete(DBOpenHelper.CHAT_MEMBER_TBL, "1 = ?", new String[]{"1"});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".deleteAllChatMember->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }


}

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
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.ChatRoomVo;
import com.zkjinshi.svip.vo.MessageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomDBUtil {

    private final static String TAG = ChatRoomDBUtil.class.getSimpleName();

    private Context         context;
    private DBOpenHelper    helper;
    private static ChatRoomDBUtil instance;

    private ChatRoomDBUtil(){
    }

    public synchronized static ChatRoomDBUtil getInstance(){
        if(null == instance){
            instance = new ChatRoomDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = VIPContext.getInstance().getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 插入单条图片聊天室记录
     * @return
     */
    public long addChatRoom(MessageVo messageVo){
        ContentValues values = ChatRoomFactory.getInstance().buildAddContentValues(messageVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.CHAT_ROOM_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".addChatRoom->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 更新图片单条聊天室记录
     * @return
     */
    public long updateChatRoom(MessageVo messageVo){
        ContentValues values = ChatRoomFactory.getInstance().buildUpdateContentValues(messageVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.CHAT_ROOM_TBL, values, "shop_id = ?",
                    new String[]{messageVo.getShopId()});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".updateChatRoom->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 删除单条聊天室
     * @return
     */
    public long deleteChatRoomByShopID(String shopID){
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.delete(DBOpenHelper.CHAT_ROOM_TBL, "shop_id = ?", new String[]{shopID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+" deleteChatRoomByShopID->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }



    /**
     * 判断聊天室是否已经存在
     * @return
     */
    public boolean isChatRoomExistsByShopID(String shopID){
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.CHAT_ROOM_TBL +
                    " where shop_id = " + shopID, null);
            if(cursor.getCount() > 1){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isChatRoomExist->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != cursor)
                cursor.close();

            if(null != db)
                db.close();

        }
        return false;
    }



}

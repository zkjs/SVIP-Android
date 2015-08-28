package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.factory.MessageFactory;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.SendStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息表操作工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageDBUtil {

    public static final String TAG = "MessageDBUtil";

    private DBOpenHelper helper;
    private Context context;

    private MessageDBUtil (){}

    private static MessageDBUtil instance;

    public static synchronized MessageDBUtil getInstance(){
        if(null == instance){
            instance = new MessageDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init(){
        this.context = VIPContext.getInstance().getContext();
        this.helper = new DBOpenHelper(context);
    }

    /**
     * 插入MessageVo消息记录
     * @return
     */
    public long addMessage(MessageVo messageVo) {
        ContentValues values = MessageFactory.getInstance().buildContentValues(messageVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.MESSAGE_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 插入单条文本消息记录
     * @return
     */
    public long addMessage(MsgCustomerServiceTextChat msgText){
        ContentValues values = MessageFactory.getInstance().buildContentValues(msgText);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.MESSAGE_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+".addMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 插入音频消息记录
     * @return
     */
    public long addMessage(MsgCustomerServiceMediaChat msgMedia){
        ContentValues values = MessageFactory.getInstance().buildContentValues(msgMedia);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.MESSAGE_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".addMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 插入单条图片消息记录
     * @return
     */
    public long addMessage(MsgCustomerServiceImgChat msgImg){
        ContentValues values = MessageFactory.getInstance().buildContentValues(msgImg);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.MESSAGE_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".addMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 批量插入音频消息记录
     * @return
     */
    public long batchAddMediaMessages(List<MsgCustomerServiceMediaChat> msgMediaList){
        SQLiteDatabase database = null;
        ContentValues values = null;
        MsgCustomerServiceTextChat messageVo = null;
        String messageId = null;
        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (MsgCustomerServiceMediaChat msgText : msgMediaList) {
                values = MessageFactory.getInstance().buildContentValues(msgText);
                try {
                    id = database.insert(DBOpenHelper.MESSAGE_TBL, null,
                            values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(id == -1){
                    id = database.update(DBOpenHelper.MESSAGE_TBL, values, " message_id = ? ", new String[]{messageId});
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
     * 批量插入文本消息记录
     * @return
     */
    public long batchAddTextMessages(List<MsgCustomerServiceTextChat> msgTextList){
        SQLiteDatabase database = null;
        ContentValues values = null;
        MsgCustomerServiceTextChat messageVo = null;
        String messageId = null;
        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (MsgCustomerServiceTextChat msgText : msgTextList) {
                values = MessageFactory.getInstance().buildContentValues(msgText);
                try {
                    id = database.insert(DBOpenHelper.MESSAGE_TBL, null,
                            values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(id == -1){
                    id = database.update(DBOpenHelper.MESSAGE_TBL, values, " message_id = ? ", new String[]{messageId});
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
     * 更新消息记录
     * @return
     */
    public long updateMessage(ContentValues values){
        String messageId;
        int id = -1;
        SQLiteDatabase db = null;
        try {
            messageId = values.getAsString("message_id");
            db= helper.getWritableDatabase();
            id = db.update(DBOpenHelper.MESSAGE_TBL, values, " message_id = ? ", new String[]{messageId});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 根据消息ID更新消息记录
     * @return
     */
    public long updateMessageVoByMessageID(MessageVo messageVo, String messageID){
        int id = -1;
        SQLiteDatabase db = null;
        ContentValues value = MessageFactory.getInstance().buildContentValues(messageVo);
        try {
            db= helper.getWritableDatabase();
            id = db.update(DBOpenHelper.MESSAGE_TBL, value, " message_id = ? ", new String[]{messageID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 根据sessionID更新消息状态为已读
     * @return
     */
    public long updateMsgReadedBySessionID(String sessionID){
        int id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("is_read", 1);
            id = db.update(DBOpenHelper.MESSAGE_TBL, values, " message_id = ? ", new String[]{sessionID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 根据shopID更新消息状态为已读
     * @return
     */
    public long updateMsgReadedByShopID(String shopID){
        int id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("is_read", 1);
            id = db.update(DBOpenHelper.MESSAGE_TBL, values, " shop_id = ? ", new String[]{shopID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessage->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 设置指定消息为发送成功
     * @param realMsgID
     * @param tempID
     * @param sendTime
     */
    public int updateMessageSendSuccess(String realMsgID, String tempID, long sendTime){
        SQLiteDatabase db = null;
        int id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("message_id", realMsgID);
            values.put("send_time", sendTime);
            values.put("send_status", SendStatus.SEND_SUCCESS.getVlaue());
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.MESSAGE_TBL, values, " message_id = ? ", new String[]{tempID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessageSendSuccess->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 将正在发送中的消息置为发送失败
     * @return
     */
    public long updateMessageSendFail(){
        String messageId;
        int id = -1;
        SQLiteDatabase db = null;
        try {
            ContentValues values = new ContentValues();
            values.put("send_status", SendStatus.SEND_FAIL.getVlaue());
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.MESSAGE_TBL, values, " send_status = ? ", new String[]{""+SendStatus.SENDING.getVlaue()});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessageSendFail->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 超过五分钟还在发送中消息，置为发送失败
     * @return
     */
    public long updateMessageTimeOut(){
        SQLiteDatabase db = null;
        ContentValues values = new ContentValues();
        long id = 0;
        long lastActionTime = System.currentTimeMillis() - 5 * 60 * 1000;//五分钟前时间戳
        values.put("send_status", SendStatus.SEND_FAIL.getVlaue());//设置为发送失败
        try {
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.MESSAGE_TBL, values, " send_status = ? and send_time < ? ",
                           new String[] { ""+SendStatus.SENDING.getVlaue(), ""+lastActionTime});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".updateMessageTimeOut->"+e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return id;
    }

    /**
     * 根据消息ID查询消息对象
     * @return
     */
    public MessageVo queryMessageByMessageID(String messageID){
        SQLiteDatabase db = null;
        Cursor    cursor  = null;
        MessageVo message = null;
        if (null != helper) {
            db = helper.getReadableDatabase();
            String sql = "select * from " + DBOpenHelper.MESSAGE_TBL + " where message_id = ?";
            cursor = db.rawQuery(sql, new String[]{messageID});
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    message = MessageFactory.getInstance().buildMessageVo(cursor);
                }
            }
        }
        if (null != cursor) cursor.close();
        if (null != db)     db.close();
        return message;
    }

    /**
     * 获取历史消息记录集合
     * @return
     */
    public ArrayList<MessageVo> queryHistoryMessageList() {
        ArrayList<MessageVo> messageList = new ArrayList<>();
        MessageVo message = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
				/*Cursor cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
				        null, null, "session_id","MAX(send_time)", " send_time desc ");*/
                String sql = "select * from " + DBOpenHelper.MESSAGE_TBL + " m where send_time=(select MAX(send_time) from "+DBOpenHelper.MESSAGE_TBL+" m1 where m.session_id = m1.session_id ) order by m.send_time desc";
                cursor = db.rawQuery(sql,  null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        message = MessageFactory.getInstance().buildMessageVo(cursor);
                        messageList.add(message);
                    }
                }

            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryHistoryMessageList->"+e.getMessage());
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }

        }

        return messageList;
    }

    /**
     * 删除消息记录
     * @return
     */
    public long deleteMessage(String messageId){
        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = helper.getWritableDatabase();
            id = db.delete(DBOpenHelper.MESSAGE_TBL, "message_id = " + messageId,
                    null);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".deleteMessage->"+e.getMessage());
            e.printStackTrace();
        }finally{
            if(null != db)
                db.close();
        }
        return id;

    }

    /**
     * 根据sessionId和时间戳以及查询个数获取消息记录
     * @param sessionId
     * @param lastSendTime
     * @param limitSize
     * @param isInclude 是否包含lastSendTime这条消息记录
     * @return
     */
    public List<MessageVo> queryMessageListBySessionId(String sessionId,long lastSendTime, int limitSize,boolean isInclude){
        List<MessageVo> messageList = new ArrayList<MessageVo>();
        MessageVo messageVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                if(isInclude){
                    cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                            " session_id = ? and send_time <= ? ",
                            new String[] {sessionId, "" + lastSendTime },
                            null, null, " send_time desc", "" + limitSize);
                }else{
                    cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                            " session_id = ? and send_time < ? ",
                            new String[] {sessionId, "" + lastSendTime },
                            null, null, " send_time desc", "" + limitSize);
                }
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        messageVo = MessageFactory.getInstance().buildMessageVo(cursor);
                        messageList.add(messageVo);
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryMessageListBySessionId->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return  messageList;
    }

    /**
     * 根据sessionId和时间戳以及查询个数获取消息记录
     * @param shopID
     * @param lastSendTime
     * @param limitSize
     * @param isInclude 是否包含lastSendTime这条消息记录
     * @return
     */
    public List<MessageVo> queryMessageListByShopID(String shopID,long lastSendTime, int limitSize,boolean isInclude){
        List<MessageVo> messageList = new ArrayList<MessageVo>();
        MessageVo messageVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                if(isInclude){
                    cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                            " shop_id = ? and send_time <= ? ",
                            new String[] {shopID, "" + lastSendTime },
                            null, null, " send_time desc", "" + limitSize);
                }else{
                    cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                            " shop_id = ? and send_time < ? ",
                            new String[] {shopID, "" + lastSendTime },
                            null, null, " send_time desc", "" + limitSize);
                }
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        messageVo = MessageFactory.getInstance().buildMessageVo(cursor);
                        messageList.add(messageVo);
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryMessageListByShopID->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return  messageList;
    }

    /**
     * 根据sessionId获取回话所有消息
     * @param sessionId
     * @return
     */
    public List<MessageVo> queryMessageListBySessionId(String sessionId){
        List<MessageVo> messageList = new ArrayList<MessageVo>();
        MessageVo messageVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " session_id = ? ",
                        new String[]{sessionId},
                        null, null, " send_time desc");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        messageVo = MessageFactory.getInstance().buildMessageVo(cursor);
                        messageList.add(messageVo);
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryMessageListBySessionId->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return  messageList;
    }

    /**
     * 获得所有消息中最近发送时间戳
     * @return
     */
    public long queryLastSendTime() {
        SQLiteDatabase db = null;
        long lastSendTime = 0;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        null,null, null, null,
                        " send_time desc ", "1");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        lastSendTime = cursor.getLong(cursor
                                .getColumnIndex("send_time"));
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryLastSendTime->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return lastSendTime;
    }

    /**
     * 获得某个会话中最近发送时间戳
     * @return
     */
    public long queryLastSendTime(String sessionId) {
        SQLiteDatabase db = null;
        long lastSendTime = 0;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " session_id = ? ", new String[]{sessionId}, null, null,
                        " send_time desc ", "1");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        lastSendTime = cursor.getLong(cursor
                                .getColumnIndex("send_time"));
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryLastSendTime->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return lastSendTime;
    }

    /**
     * 获得某个商家中最近的发送时间戳
     * @return
     */
    public long queryLastSendTimeByShopID(String shopID) {
        SQLiteDatabase db = null;
        long lastSendTime = 0;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " shop_id = ? ", new String[]{shopID}, null, null,
                        " send_time desc ", "1");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        lastSendTime = cursor.getLong(cursor
                                .getColumnIndex("send_time"));
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryLastSendTimeByShopID->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return lastSendTime;
    }

    /**
     * 根据消息ID查询消息对象
     *
     * @return
     */
    public MessageVo queryLastMsgByShopId(String shopID) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        MessageVo message = null;
        if (null != helper) {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                    " shop_id = ? ", new String[]{shopID}, null, null,
                    " send_time desc ", "1");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    message = MessageFactory.getInstance().buildMessageVo(cursor);
                }
            }
        }
        if (null != cursor) cursor.close();
        if (null != db) db.close();
        return message;
    }


    /**
     * 获得某个会话中最近发送时间戳
     * @return
     */
    public String queryLastMsgByShopID(String shopID) {
        SQLiteDatabase db      = null;
        String         lastMsg = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " shop_id = ? ", new String[]{shopID}, null, null,
                        " send_time desc ", "1");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        lastMsg = cursor.getString(cursor.getColumnIndex("content"));
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryLastSendTime->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return lastMsg;
    }

    /**
     * 获得消息未读总个数
     * @return
     */
    public int queryNotifyCount(){
        int nofifyCount  = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " is_read = ? ",
                        new String[]{"0"}, null, null, null);
                nofifyCount = cursor.getCount();
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryNotifyCount->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return nofifyCount;
    }

    /**
     * 获取某个会话未读消息个数
     */
    public int queryNotifyCount(String sessionId){
        int nofifyCount  = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " session_id = ? and is_read = ?  ",
                        new String[]{sessionId, "0"}, null, null, null);
                nofifyCount = cursor.getCount();
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryNotifyCount->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return nofifyCount;
    }

    /**
     * 获取某个商家未读消息个数
     */
    public int queryNotifyCountByShopID(String shopID){
        int nofifyCount  = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, null,
                        " shop_id = ? and is_read = ?  ",
                        new String[]{shopID, "0"}, null, null, null);
                nofifyCount = cursor.getCount();
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryNotifyCountByShopID->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return nofifyCount;
    }

    /**
     * query the message send status by sessionID
     */
    public int querySendStatusBySessionID(String sessionID) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int sendStatus = 0;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.MESSAGE_TBL, new String[]{"send_status"},
                        " session_id = ? ", new String[]{sessionID}, null, null, null);
                while(cursor.moveToFirst()){
                    sendStatus = cursor.getInt(0);
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".querySendStatusBySessionID->"+e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != db) {
                    db.close();
                }
            }
        }
        return sendStatus;
    }
}

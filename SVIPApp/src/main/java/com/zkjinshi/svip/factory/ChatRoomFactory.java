package com.zkjinshi.svip.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;
import com.zkjinshi.svip.sqlite.ChatRoomDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.ChatRoomVo;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.VisibleStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomFactory {

    private static ChatRoomFactory chatRoomFactory;

    private ChatRoomFactory() {
    }

    public synchronized static ChatRoomFactory getInstance(){
        if(null == chatRoomFactory){
            chatRoomFactory = new ChatRoomFactory();
        }
        return chatRoomFactory;
    }

    /**
     * 构建聊天室键值对
     * @return
     */
    public ContentValues buildAddContentValues(MsgCustomerServiceTextChat msgText){
        ContentValues values = new ContentValues();
        values.put("shop_id", msgText.getShopid());
        values.put("session_id", msgText.getSessionid());
        values.put("remark", "");//备注暂时为空
        values.put("created", msgText.getTimestamp());//创建是才有的
        values.put("end_time", msgText.getTimestamp());//更新时
        values.put("end_user_id", msgText.getFromid());//更新时
        values.put("client_id",  msgText.getFromid());//创建人
        values.put("client_name", msgText.getFromname());//创建者名称
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildUpdateContentValues(MsgCustomerServiceTextChat msgText){
        ContentValues values = new ContentValues();
        values.put("shop_id", msgText.getShopid());
        values.put("session_id", msgText.getSessionid());
        values.put("remark", "");//备注暂时为空
        values.put("created", "");//创建是才有的
        values.put("end_time", msgText.getTimestamp());//更新时
        values.put("end_user_id", msgText.getFromid());//更新时
        values.put("client_id",  "");//创建人
        values.put("client_name", "");//创建者名称
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildAddContentValues(MsgCustomerServiceImgChat msgImg){
        ContentValues values = new ContentValues();
        values.put("shop_id", msgImg.getShopid());
        values.put("session_id", msgImg.getSessionid());
        values.put("remark", "");//备注暂时为空
        values.put("created", msgImg.getTimestamp());//创建是才有的
        values.put("end_time", msgImg.getTimestamp());//更新时
        values.put("end_user_id", msgImg.getFromid());//更新时
        values.put("client_id", msgImg.getFromid());//创建人
        values.put("client_name", msgImg.getFromname());//创建者名称
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildUpdateContentValues(MsgCustomerServiceImgChat msgImg){
        ContentValues values = new ContentValues();
        values.put("shop_id", msgImg.getShopid());
        values.put("session_id", msgImg.getSessionid());
        values.put("remark", "");//备注暂时为空
        values.put("created", "");//创建是才有的
        values.put("end_time", msgImg.getTimestamp());//更新时
        values.put("end_user_id", msgImg.getFromid());//更新时
        values.put("client_id",  "");//创建人
        values.put("client_name", "");//创建者名称
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildAddContentValues(MsgCustomerServiceMediaChat msgMedia){
        ContentValues values = new ContentValues();
        values.put("shop_id", msgMedia.getShopid());
        values.put("session_id", msgMedia.getSessionid());
        values.put("remark", "");//备注暂时为空
        values.put("created", msgMedia.getTimestamp());//创建是才有的
        values.put("end_time", msgMedia.getTimestamp());//更新时
        values.put("end_user_id", msgMedia.getFromid());//更新时
        values.put("client_id", msgMedia.getFromid());//创建人
        values.put("client_name", msgMedia.getFromname());//创建者名称
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildUpdateContentValues(MsgCustomerServiceMediaChat msgMedia){
        ContentValues values = new ContentValues();
        values.put("shop_id", msgMedia.getShopid());
        values.put("session_id", msgMedia.getSessionid());
        values.put("remark", "");//备注暂时为空
        values.put("created", "");//创建是才有的
        values.put("end_time", msgMedia.getTimestamp());//更新时
        values.put("end_user_id", msgMedia.getFromid());//
        values.put("client_id", "");//创建人
        values.put("client_name", "");//创建者名称
        return values;
    }

    /**
     * 根据消息中心对象生成聊天室对象
     * @param messageVo
     */
    public ChatRoomVo buildChatRoomByMessageVo(MessageVo messageVo) {
        ChatRoomVo chatRoom = new ChatRoomVo();
        String shopID    = messageVo.getShopId();
        String sessionID = messageVo.getSessionId();
        long   endTime   = messageVo.getSendTime();
        String contactID = messageVo.getContactId();
        String clientID   = CacheUtil.getInstance().getUserId();
        String clientName = CacheUtil.getInstance().getUserName();
        if(!TextUtils.isEmpty(shopID)){
            chatRoom.setShopid(shopID);
        }
        if(!TextUtils.isEmpty(sessionID)){
            chatRoom.setSessionid(sessionID);
        }
        chatRoom.setCreated(System.currentTimeMillis());
        chatRoom.setEndtime(endTime);
        if(!TextUtils.isEmpty(contactID)){
            chatRoom.setEnduserid(contactID);
        }
        if(!TextUtils.isEmpty(clientID)){
            chatRoom.setClientid(clientID);
        }
        if(!TextUtils.isEmpty(clientName)){
            chatRoom.setClientname(clientName);
        }
        //默认情况下消息为可见
        chatRoom.setIsVisible(VisibleStatus.VISIBLE);
        return chatRoom;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildAddContentValues(MessageVo messageVo){
        String shopID    = messageVo.getShopId();
        String sessionID = messageVo.getSessionId();
        long   endTime   = messageVo.getSendTime();
        String contactID = messageVo.getContactId();
        String clientID   = CacheUtil.getInstance().getUserId();
        String clientName = CacheUtil.getInstance().getUserName();

        ContentValues values = new ContentValues();
        values.put("shop_id", shopID);
        values.put("session_id", sessionID);
        values.put("remark", "");//备注暂时为空
        values.put("created", System.currentTimeMillis());
        values.put("end_time", endTime);//更新时
        values.put("end_user_id", contactID);
        values.put("client_id", clientID);//创建人
        values.put("client_name", clientName);//创建者名称
        values.put("is_visible", VisibleStatus.INVISIBLE.getVlaue());
        return values;
    }

    /**
     * 更新数据库对象
     * @param messageVo
     * @return
     */
    public ContentValues buildUpdateContentValues(MessageVo messageVo){
        String shopID    = messageVo.getShopId();
        String sessionID = messageVo.getSessionId();
        long   endTime   = messageVo.getSendTime();
        String contactID = messageVo.getContactId();
        String clientID   = CacheUtil.getInstance().getUserId();
        String clientName = CacheUtil.getInstance().getUserName();

        ContentValues values = new ContentValues();
        values.put("shop_id", shopID);
        values.put("session_id", sessionID);
        values.put("remark", "");//备注暂时为空
        values.put("end_time", endTime);//更新时
        values.put("end_user_id", contactID);//
        values.put("client_id", clientID);//创建人
        values.put("client_name", clientName);//创建者名称
        values.put("is_visible", VisibleStatus.INVISIBLE.getVlaue());
        return values;
    }

    /**
     * 更新数据库对象
     * @param chatRoomVo
     * @return
     */
    public ContentValues buildUpdateContentValues(ChatRoomVo chatRoomVo){
        ContentValues values = new ContentValues();
        values.put("shop_id", chatRoomVo.getShopid());
        values.put("session_id", chatRoomVo.getSessionid());
        values.put("remark", chatRoomVo.getRemark());//备注暂时为空
        values.put("end_time", chatRoomVo.getEndtime());//更新时
        values.put("end_user_id", chatRoomVo.getEnduserid());//
        values.put("client_id", chatRoomVo.getClientid());//创建人
        values.put("client_name", chatRoomVo.getClientname());//创建者名称
        values.put("is_visible", VisibleStatus.INVISIBLE.getVlaue());
        return values;
    }

    /**
     * 根据游标创建聊天室对象
     * @param cursor
     * @return
     */
    public ChatRoomVo buildChatRoomByCursor(Cursor cursor) {
        String shopID     = cursor.getString(0);
        String sessionID  = cursor.getString(1);
        String remark     = cursor.getString(2);
        long created      = cursor.getLong(3);
        long endTime      = cursor.getLong(4);
        String endUserID  = cursor.getString(5);
        String clientID   = cursor.getString(6);
        String clientName = cursor.getString(7);
        int    isVisible  = cursor.getInt(8);

        //新建聊天室对象
        ChatRoomVo chatRoom = new ChatRoomVo();
        chatRoom.setShopid(shopID);
        chatRoom.setSessionid(sessionID);
        chatRoom.setRemark(remark);
        chatRoom.setCreated(created);
        chatRoom.setEndtime(endTime);
        chatRoom.setEnduserid(endUserID);
        chatRoom.setClientid(clientID);
        chatRoom.setClientname(clientName);
        if(isVisible == VisibleStatus.INVISIBLE.getVlaue()){
            chatRoom.setIsVisible(VisibleStatus.INVISIBLE);
        }else {
            chatRoom.setIsVisible(VisibleStatus.VISIBLE);
        }
        return chatRoom;
    }
}

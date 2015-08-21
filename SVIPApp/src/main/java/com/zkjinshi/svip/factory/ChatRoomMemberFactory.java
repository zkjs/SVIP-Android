package com.zkjinshi.svip.factory;

import android.content.ContentValues;

import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceImgChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceMediaChat;
import com.zkjinshi.svip.bean.jsonbean.MsgCustomerServiceTextChat;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomMemberFactory {

    private static ChatRoomMemberFactory instance;

    private ChatRoomMemberFactory(){};

    public synchronized static ChatRoomMemberFactory getInstance(){
        if(null == instance){
            instance = new ChatRoomMemberFactory();
        }
        return instance;
    }

    /**
     * 添加新的聊天成员记录键对值
     * @param msgText
     * @return
     */
    public ContentValues buildAddContentValues(MsgCustomerServiceTextChat msgText){
        ContentValues values = new ContentValues();
        values.put("session_id", msgText.getSessionid());
        values.put("userid", msgText.getFromid());
        values.put("logintype", "");//用户类型 0:app用户  1:商家员工 默认为:0//暂无
        values.put("shopid", msgText.getShopid());//商家ID
        values.put("empid", "");//员工ID //暂无
        values.put("roleid", "");//角色ID//暂无
        values.put("created", System.currentTimeMillis());
        return values;
    }

    /**
     * 添加新的聊天成员记录键对值
     * @param msgMedia
     * @return
     */
    public ContentValues buildAddContentValues(MsgCustomerServiceMediaChat msgMedia){
        ContentValues values = new ContentValues();
        values.put("session_id", msgMedia.getSessionid());
        values.put("userid", msgMedia.getFromid());
        values.put("logintype", "");//用户类型 0:app用户  1:商家员工 默认为:0//暂无
        values.put("shopid", msgMedia.getShopid());//商家ID
        values.put("empid", "");//员工ID //暂无
        values.put("roleid", "");//角色ID//暂无
        values.put("created", System.currentTimeMillis());
        return values;
    }

    /**
     * 添加新的聊天成员记录键对值
     * @param msgImg
     * @return
     */
    public ContentValues buildAddContentValues(MsgCustomerServiceImgChat msgImg){
        ContentValues values = new ContentValues();
        values.put("session_id", msgImg.getSessionid());
        values.put("userid", msgImg.getFromid());
        values.put("logintype", "");//用户类型 0:app用户  1:商家员工 默认为:0//暂无
        values.put("shopid", msgImg.getShopid());//商家ID
        values.put("empid", "");//员工ID //暂无
        values.put("roleid", "");//角色ID//暂无
        values.put("created", System.currentTimeMillis());
        return values;
    }

}

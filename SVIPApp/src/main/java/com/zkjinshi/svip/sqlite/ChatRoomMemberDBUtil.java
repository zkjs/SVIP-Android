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
















}

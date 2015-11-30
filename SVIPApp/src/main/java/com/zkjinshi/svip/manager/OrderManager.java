package com.zkjinshi.svip.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.svip.notification.NotificationHelper;
import com.zkjinshi.svip.utils.VIPContext;

/**
 * 订单管理器
 * (暂时处理订单确认和订单取消)
 * 开发者：JimmyZhang
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderManager {

    private OrderManager(){}

    private static OrderManager instance;

    private EMMessage message;

    public synchronized static OrderManager getInstance(){
        if(null == instance){
            instance = new OrderManager();
        }
        return instance;
    }

    public synchronized void receiveOrderCmdMessage(EMNotifierEvent event,final Context context){
        switch (event.getEvent()) {
            case EventNewCMDMessage:{//接收透传消息
                try {
                    message = (EMMessage) event.getData();
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    String aciton = cmdMsgBody.action;
                    if(!TextUtils.isEmpty(aciton) && "sureOrder".equals(aciton)){
                        final String shopId = message.getStringAttribute("shopId");
                        final String orderNo = message.getStringAttribute("orderNo");
                        //1. 通过广播确认生成订单通知对话框
                        Intent intent = new Intent();
                        intent.setAction("com.zkjinshi.svip.ACTION_ORDER");
                        intent.putExtra("shopId", shopId);
                        intent.putExtra("orderNo", orderNo);
                        context.sendBroadcast(intent);
                        //2. 生成Notification通知
                        NotificationHelper.getInstance().showNotification(context, shopId, orderNo);
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}

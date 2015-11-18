package com.zkjinshi.svip.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.order.OrderDetailActivity;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.vo.OrderVo;

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

    public synchronized void receiveCmdMessage(EMNotifierEvent event,final Context context){
        switch (event.getEvent()) {
            case EventNewCMDMessage:{//接收透传消息
                try {
                    message = (EMMessage) event.getData();
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    String aciton = cmdMsgBody.action;
                    if(!TextUtils.isEmpty(aciton) && "sureOrder".equals(aciton)){
                        final String shopId = message.getStringAttribute("shopId");
                        final String orderNo = message.getStringAttribute("orderNo");
                        Intent intent = new Intent();
                        intent.setAction("com.zkjinshi.svip.ACTION_EMessage");
                        intent.putExtra("shopId", shopId);
                        intent.putExtra("orderNo",orderNo);
                        context.sendBroadcast(intent);
                       /* ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showBookHotelSuccDialog(context, shopId, orderNo);
                            }
                        });*/
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void showBookHotelSuccDialog(final Context context, final String shopId,final String orderNo) {
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("订单通知");
        customBuilder.setMessage("您的订单已经生成，请尽快确认");
        customBuilder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        customBuilder.setPositiveButton("查看", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("reservation_no", orderNo);
                intent.putExtra("shopid", shopId);
                context.startActivity(intent);
            }
        });
        Dialog dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

}

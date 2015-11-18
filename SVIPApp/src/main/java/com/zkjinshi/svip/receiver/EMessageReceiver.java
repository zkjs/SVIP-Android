package com.zkjinshi.svip.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.WindowManager;

import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.activity.order.OrderDetailActivity;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(!TextUtils.isEmpty(action) && action.equals("com.zkjinshi.svip.ACTION_ORDER")){
            String shopId = intent.getStringExtra("shopId");
            String orderNo = intent.getStringExtra("orderNo");
            showBookHotelSuccDialog(context,shopId,orderNo);
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
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCancelable(false);
        dialog.show();
    }

}

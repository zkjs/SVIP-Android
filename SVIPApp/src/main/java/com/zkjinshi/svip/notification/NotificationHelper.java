package com.zkjinshi.svip.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;


import com.zkjinshi.svip.R;

import com.zkjinshi.svip.activity.common.BeaconMsgActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.SplashActivity;
import com.zkjinshi.svip.activity.facepay.PayActivity;
import com.zkjinshi.svip.activity.facepay.PayConfirmActivity;
import com.zkjinshi.svip.activity.facepay.PayRecordActivity;
import com.zkjinshi.svip.receiver.NotificationClickReceiver;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.CallReadyVo;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;

/**
 * 消息通知帮助类
 * 开发者：JimmyZhang
 * 日期：2015/8/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NotificationHelper {

    public static int NOTIFY_ID = 1;

    private NotificationHelper() {
    }

    private static NotificationHelper instance;

    public synchronized static NotificationHelper getInstance() {
        if (null == instance) {
            instance = new NotificationHelper();
        }
        return instance;
    }



    public void showNotification(final Context context, final PayRecordDataVo amountStatusVo){
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String contactName = amountStatusVo.getShopname();

        String tipsMsg = null;
        //tipsMsg = "商家请求收款：¥ "+amountStatusVo.getAmount();
        tipsMsg = "您有一笔支付信息";
        if(!TextUtils.isEmpty(contactName)){
            notificationBuilder.setContentTitle(contactName);
        }
        notificationBuilder.setContentText(tipsMsg);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        // 2.设置点击跳转事件
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("amountStatusVo",amountStatusVo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    public void showNotificationResult(final Context context, final PayRecordDataVo amountStatusVo){
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String contactName = amountStatusVo.getShopname();
        String alert = amountStatusVo.getAlert();

        String tipsMsg = null;

        tipsMsg = "您有一笔支付信息";
        if(!TextUtils.isEmpty(alert)){
            notificationBuilder.setContentTitle(alert);
        } else if(!TextUtils.isEmpty(contactName)){
            notificationBuilder.setContentTitle(contactName);
        }
        notificationBuilder.setContentText(tipsMsg);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        // 2.设置点击跳转事件
        Intent intent = new Intent(context, PayRecordActivity.class);
        intent.putExtra("status","2");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    public void showNotification(final Context context, final YunBaMsgVo yunBaMsgVo){
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String title = yunBaMsgVo.getAlert();
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        Intent realIntent = new Intent();
        realIntent.setClass(context, SplashActivity.class);
        realIntent.putExtra("data",yunBaMsgVo);
        realIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, realIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    public void showCallReadyNotification(final Context context, final CallReadyVo callReadyVo){
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String title = "超级身份";
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        String content = "您呼叫的"+callReadyVo.getSrvname()+"已准备就绪，请稍等片刻，服务人员"+callReadyVo.getWaitername()+"马上到来。";
        notificationBuilder.setContentTitle(content);

        Intent realIntent = new Intent();
        realIntent.setClass(context, SplashActivity.class);
        realIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, realIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    public void showInvitationNotification(final Context context, final InvitationVo invitationVo,boolean isCancel){
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String title = invitationVo.getAlert();
        notificationBuilder.setContentText(title);
        notificationBuilder.setContentTitle("超级身份");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        Intent realIntent = new Intent();
        realIntent.setClass(context, SplashActivity.class);
        if(!isCancel){
            realIntent.putExtra("invitation_msg",invitationVo);
        }
        realIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, realIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

}

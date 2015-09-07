package com.zkjinshi.svip.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocAd;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.utils.VibratorUtil;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.MimeType;

/**
 * 消息通知帮助类
 * 开发者：JimmyZhang
 * 日期：2015/8/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NotificationHelper {

    private NotificationHelper() {
    }

    private static NotificationHelper instance;

    public synchronized static NotificationHelper getInstance() {
        if (null == instance) {
            instance = new NotificationHelper();
        }
        return instance;
    }

    /**
     * 后台通知栏通知用户收到消息
     *
     * @param context
     * @param messageVo
     */
    public void showNotification(Context context, MessageVo messageVo) {
        if (ActivityManagerHelper.isRunningBackground(context)) {
            int nofifyFlag = 0;
            NotificationCompat.Builder notificationBuilder = null;
            // 1.设置显示信息
            notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setContentTitle("" + messageVo.getContactName());
            MimeType msgType = messageVo.getMimeType();
            if (msgType == MimeType.TEXT) {
                notificationBuilder.setContentText("" + messageVo.getContent());
            } else if (msgType == MimeType.IMAGE) {
                notificationBuilder.setContentText("[图片]");
            } else {
                notificationBuilder.setContentText("[语音]");
            }
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            // 2.设置点击跳转事件
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            notificationBuilder.setContentIntent(pendingIntent);
            // 3.设置通知栏其他属性
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            notificationManager.notify(nofifyFlag, notificationBuilder.build());
        } else {
            MediaPlayerUtil.playNotifyVoice(context);
            VibratorHelper.vibratorShark(context);
        }
    }

    /**
     * 通知栏通知用户到店
     *
     * @param context
     * @param msgPushLocAd
     */
    public void showNotification(Context context, MsgPushLocAd msgPushLocAd) {
        int nofifyFlag = 0;
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle("" + msgPushLocAd.getAndtitle());
        notificationBuilder.setContentText("" + msgPushLocAd.getAndalert());
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        // 2.设置点击跳转事件
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(nofifyFlag, notificationBuilder.build());
    }

}

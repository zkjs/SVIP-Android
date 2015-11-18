package com.zkjinshi.svip.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocAd;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.utils.VibratorUtil;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.MimeType;
import com.zkjinshi.svip.vo.TxtExtType;

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
     * 后台通知栏通知用户收到消息
     * @param context
     * @param event
     */
    public void showNotification(Context context, EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                if(null != message){
                    String username = message.getFrom();
                    String titleName = null;
                    if(!username.equals(CacheUtil.getInstance().getUserId())){
                        EMMessage.Type msgType = message.getType();
                        if (ActivityManagerHelper.isRunningBackground(context)) {
                            int nofifyFlag = 0;
                            NotificationCompat.Builder notificationBuilder = null;
                            // 1.设置显示信息
                            notificationBuilder = new NotificationCompat.Builder(context);
                            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                               //TODO JIMMY 后续需要修改
                                titleName = message.getTo();
                            } else {
                                titleName = message.getFrom();
                            }
                            try {
                                String fromName = message.getStringAttribute("fromName");
                                String toName = message.getStringAttribute("toName");
                                if(!TextUtils.isEmpty(fromName) && !fromName.equals(CacheUtil.getInstance().getUserName())){
                                    titleName = fromName;
                                }else{
                                    if(!TextUtils.isEmpty(toName)){
                                        titleName = toName;
                                    }
                                }
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                            notificationBuilder.setContentTitle("" + titleName);
                            if (msgType == EMMessage.Type.TXT) {
                                try {
                                    int extType = message.getIntAttribute(Constants.MSG_TXT_EXT_TYPE);
                                    if(TxtExtType.DEFAULT.getVlaue() == extType){
                                        TextMessageBody txtBody = (TextMessageBody) message.getBody();
                                        String content = txtBody.getMessage();
                                        notificationBuilder.setContentText("" + content);
                                    }else{
                                        notificationBuilder.setContentText("[订单]");
                                    }
                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                }

                            } else if (msgType == EMMessage.Type.IMAGE) {
                                notificationBuilder.setContentText("[图片]");
                            } else if(msgType ==  EMMessage.Type.VOICE){
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
                }
                break;
        }
    }

    /**
     * 通知栏通知用户到店
     *
     * @param context
     * @param msgPushLocAd
     */
    public void showNotification(Context context, MsgPushLocAd msgPushLocAd) {

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
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

}

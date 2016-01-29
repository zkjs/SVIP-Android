package com.zkjinshi.svip.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.order.HotelConfirmActivity;
import com.zkjinshi.svip.activity.order.KTVConfirmActivity;
import com.zkjinshi.svip.activity.order.NormalConfirmActivity;

import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.receiver.NotificationClickReceiver;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.TxtExtType;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     * @param context
     * @param event
     */
    public void showNotification(Context context, EMNotifierEvent event) {
        int nofifyFlag = 0;
        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                if(null != message){
                    String username = message.getFrom();
                    String titleName = null;
                    if(!username.equals(CacheUtil.getInstance().getUserId())){
                        EMMessage.Type msgType = message.getType();
                        if (ActivityManagerHelper.isRunningBackground(context)) {
                            NotificationCompat.Builder notificationBuilder = null;
                            notificationBuilder = new NotificationCompat.Builder(context);
                            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                                EMConversationHelper.getInstance().requestGroupListTask();
                                String groupId = message.getTo();
                                EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
                                if (group != null){
                                    titleName = group.getGroupName();
                                }
                            } else {
                                titleName = message.getFrom();
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
     * 通知用户新订单生成
     * @param context
     * @param shopID
     * @param orderNo
     */
    public void showNotification(Context context, String shopID, String orderNo) {

        Log.i(com.zkjinshi.base.util.Constants.ZKJINSHI_BASE_TAG,"orderNo:"+orderNo);

        NotificationCompat.Builder notificationBuilder = null;

        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(context.getString(R.string.order_confirm));
        notificationBuilder.setContentText(context.getString(R.string.you_have_a_new_unconfirm_order));
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        // 2.设置点击跳转事件
        Intent intent = new Intent();
        if(orderNo.startsWith("H")){
            intent.setClass(context,HotelConfirmActivity.class);
            intent.putExtra("orderNo",orderNo);
        }else if(orderNo.startsWith("K")){
            intent.setClass(context,KTVConfirmActivity.class);
            intent.putExtra("orderNo",orderNo);
        }
        else if(orderNo.startsWith("O")){
            intent.setClass(context,NormalConfirmActivity.class);
            intent.putExtra("orderNo",orderNo);
        }

        ++NOTIFY_ID;

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    /**
     * 通知用户被销售添加了
     * @param context
     * @param salesId
     * @param salesName
     */
    public void showAddGuestNotification(Context context, String salesId, String salesName) {

        NotificationCompat.Builder notificationBuilder = null;

        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(salesName+"已添加您为专属客人.");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        String imageUrl =  ProtocolUtil.getAvatarUrl(salesId);
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context,36), DisplayUtil.dip2px(context,36));
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrl,imageSize);
        notificationBuilder.setLargeIcon(bitmap);

        // 2.设置点击跳转事件
        Intent  realIntent = new Intent();
        realIntent.setClass(context,MainActivity.class);
        realIntent.putExtra("pageIndex",2);
        realIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ++NOTIFY_ID;
        Intent clickIntent = new Intent(context, NotificationClickReceiver.class); //点击 Intent
        clickIntent.putExtra("realIntent", realIntent);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFY_ID, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    /**
     * 通知提示:客户退出当前账户
     * @param context
     */
    public void showExitAccountNotification(Context context) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        NotificationCompat.Builder notificationBuilder = null;

        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String content = "您的账号于" + sdf.format(new Date()) + "在另一台设备登录";
        notificationBuilder.setContentTitle("下线通知");
        notificationBuilder.setContentText(content);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        String userID    = CacheUtil.getInstance().getUserId();
        String avatarUrl = ProtocolUtil.getAvatarUrl(userID);
        Bitmap bitmap    = ImageLoader.getInstance().loadImageSync(avatarUrl);
        notificationBuilder.setLargeIcon(bitmap);

        // 2.设置点击跳转事件
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

}

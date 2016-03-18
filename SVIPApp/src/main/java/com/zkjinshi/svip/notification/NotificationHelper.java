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


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;

import com.zkjinshi.svip.receiver.NotificationClickReceiver;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.TxtExtType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

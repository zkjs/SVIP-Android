package com.zkjinshi.base.net.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.zkjinshi.base.net.ping.HeartBeatTimer;
import com.zkjinshi.base.util.Constants;

/**
 * 消息服务
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageService extends Service {

    public static final int WAKE_UP_INTERVAL = 120 * 1000;//唤醒cup时间间隔

    public static final String TAG = "MessageService";
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, TAG + ".onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startClient();
        Log.i(TAG, TAG + ".onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAlarmManager();
        stopClient();
        Log.i(TAG, TAG + ".onDestroy()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cancelAlarmManager();
        awakeAlarmManager(intent);
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onStartCommand()");
        return Service.START_NOT_STICKY;
    }

    /**
     * 使用系统时钟，定时唤醒消息服务
     *
     * @param intent
     */
    private void awakeAlarmManager(Intent intent) {
        if (null != intent) {
            Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + "-开始唤醒CPU服务");
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            pendingIntent = PendingIntent.getService(this, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);
            if (null != alarmManager) {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + WAKE_UP_INTERVAL, pendingIntent);
            }
        }
    }

    /**
     * 取消已有的pendingIntent
     */
    private void cancelAlarmManager() {
        if (null != pendingIntent) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent = null;
        }
    }

    /**
     * 开启client服务
     */
    private void startClient() {
        WebSocketManager.getInstance().initClient();
    }

    /**
     * 停止client服务
     */
    private void stopClient() {
        WebSocketManager.getInstance().close();
    }
}

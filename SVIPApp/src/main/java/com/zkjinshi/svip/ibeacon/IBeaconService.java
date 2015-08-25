package com.zkjinshi.svip.ibeacon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

/**
 * IBeaconService服务
 * 开发者：JimmyZhang
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconService extends Service{

    public static String TAG = "IBeaconService";

    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getInstance().info(LogLevel.DEBUG, "IBeaconService.onCreate()");
        IBeaconManager.getInstance().startCheckOutTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.getInstance().info(LogLevel.DEBUG, "IBeaconService.onStartCommand()");

        cancelAlarmManager();
        awakeAlarmManager(intent);
        IBeaconManager.getInstance().init(this).initScanDevices().scanLeDevice(true);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().info(LogLevel.DEBUG, "IBeaconService.onDestroy()");
        cancelAlarmManager();
        IBeaconManager.getInstance().stopScan();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** 使用系统时钟，定时唤醒消息服务 **/
    private void awakeAlarmManager(Intent intent){
        if(null != intent){
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            pendingIntent = PendingIntent.getService(this, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);
            if(null != am){
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 90 * 1000, pendingIntent);
            }
        }
    }

    /** 取消已有的pendingIntent **/
    private void cancelAlarmManager(){
        if (null != pendingIntent) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.cancel(pendingIntent);
            pendingIntent = null;
        }
    }

}

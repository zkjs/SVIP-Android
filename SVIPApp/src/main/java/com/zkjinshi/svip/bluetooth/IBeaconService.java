package com.zkjinshi.svip.bluetooth;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.support.annotation.Nullable;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
;

/**
 * IBeaconService服务
 * 开发者：JimmyZhang
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconService extends Service{

    public static String TAG = IBeaconService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getInstance().info(LogLevel.DEBUG, "IBeaconService.onCreate()");
        IBeaconManager.getInstance().init(this).initScanDevices().scanLeDevice();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.getInstance().info(LogLevel.DEBUG, "IBeaconService.onStartCommand()");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IBeaconManager.getInstance().cancelScheduleScan();
        LogUtil.getInstance().info(LogLevel.DEBUG, "IBeaconService.onDestroy()");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

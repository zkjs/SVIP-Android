package com.zkjinshi.pyxis.bluetooth;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;


;

/**
 * IBeaconService服务
 * 开发者：dujiande
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconService extends Service{

    public static String TAG = IBeaconService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"IBeaconService.onCreate()");
        IBeaconManager.getInstance().init(this).initScanDevices().scanLeDevice();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"IBeaconService.onStartCommand()");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IBeaconManager.getInstance().stopScan();
        IBeaconManager.getInstance().cancelScheduleScan();
        IBeaconContext.getInstance().clearIBeaconMap();
        Log.d(TAG,"IBeaconService.onDestroy()");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

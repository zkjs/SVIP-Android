package com.zkjinshi.pyxis.bluetooth;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * IBeaconService服务
 * 开发者：dujiande
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconService extends Service{

    public static String TAG = IBeaconService.class.getSimpleName();
    public static boolean killMyself = false;
    public Intent intent;
    public HashMap<String, NetBeaconVo> netBeaconMap = null;

    public static IBeaconObserver mIBeaconObserver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"IBeaconService.onCreate()");
        IBeaconManager.getInstance().init(this).initScanDevices().scanLeDevice();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"IBeaconService.onStartCommand()");
        this.intent = intent;
        if(intent == null){
            Log.d(TAG,"intent == null");
        }else if(netBeaconMap == null){
            netBeaconMap = (HashMap<String, NetBeaconVo>)intent.getSerializableExtra("netBeaconMap");
            IBeaconContext.getInstance().setNetBeanconMap(netBeaconMap);
             Log.d(TAG,"netBeaconMap:"+netBeaconMap.toString());
            if(IBeaconService.mIBeaconObserver != null){
                IBeaconSubject.getInstance().addObserver(IBeaconService.mIBeaconObserver);
            }else{
                Log.d(TAG,"IBeaconService.mIBeaconObserver == null");
            }

        }
        if(killMyself){
            return START_NOT_STICKY;
        }else{
            return  START_REDELIVER_INTENT;
        }
    }

    @Override
    public void onDestroy() {

        if(killMyself){
            IBeaconManager.getInstance().stopScan();
            IBeaconManager.getInstance().cancelScheduleScan();
            IBeaconContext.getInstance().clearIBeaconMap();
        }else if(intent != null){
            getApplicationContext().startService(intent);
        }

        Log.d(TAG,"IBeaconService.onDestroy()");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

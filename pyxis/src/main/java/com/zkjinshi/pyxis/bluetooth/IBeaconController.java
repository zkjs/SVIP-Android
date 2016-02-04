package com.zkjinshi.pyxis.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/8/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconController {

    public static final String TAG = IBeaconController.class.getSimpleName();

    public static long scanPeriodTime = 3000L;
    public static int failScanTime = 2;
    private boolean isRuning = false;

    private Context context;

    private IBeaconController(){}

    private static IBeaconController instance;

    public synchronized static IBeaconController getInstance(){
        if(null == instance){
            instance = new IBeaconController();
        }
        return  instance;
    }

    public void init(Context context){
        this.context = context;
    }

    public void init(Context context,long periodTime,int failTime){
        this.context = context;
        scanPeriodTime = periodTime;
        failScanTime = failTime;
    }

    /**
     * 设置需要监听的IBeacon
     */
    public void setListenBeancons(ArrayList<NetBeaconVo> beaconList){
        if (null != beaconList && !beaconList.isEmpty()) {
            for (NetBeaconVo beancon : beaconList) {
                IBeaconContext.getInstance().getNetBeaconMap().put(beancon.getBeaconKey(), beancon);
            }
        }

    }

    /**
     * 启动IBeacon蓝牙扫描服务
     */
    public void startBeaconService(){
        if(context == null){
            Log.e(TAG,"还没正确初始化");
            return;
        }
        Log.d(TAG,"启动IBeacon蓝牙扫描服务");
        IBeaconService.killMyself = false;
        Intent intent = new Intent(context, IBeaconService.class);
        intent.putExtra("netBeaconMap",IBeaconContext.getInstance().getNetBeaconMap());
        intent.setAction("com.zkjinshi.pyxis.Beacon_SERVICE");
        context.startService(intent);
        isRuning = true;
    }

    /**
     * 停止IBeacon蓝牙扫描服务
     */
    public void stopBeaconService(){
        if(context == null){
            Log.e(TAG,"还没正确初始化");
            return;
        }
        isRuning = false;
        Log.d(TAG,"停止IBeacon蓝牙扫描服务");
        IBeaconService.killMyself = true;
        Intent intent = new Intent(context, IBeaconService.class);
        intent.setAction("com.zkjinshi.pyxis.Beacon_SERVICE");
        context.stopService(intent);
    }

    public boolean isRuning() {
        return isRuning;
    }

    public void setRuning(boolean runing) {
        isRuning = runing;
    }
}

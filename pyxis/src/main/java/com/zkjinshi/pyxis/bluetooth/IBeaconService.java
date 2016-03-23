package com.zkjinshi.pyxis.bluetooth;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
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
public class IBeaconService extends Service implements BeaconConsumer {

    public static String TAG = IBeaconService.class.getSimpleName();
    public static boolean killMyself = false;
    public Intent intent;
    public HashMap<String, NetBeaconVo> netBeaconMap = null;
    public static IBeaconObserver mIBeaconObserver = null;

    private static final float CHECK_DISTANCE = 5F;
    private BeaconManager beaconManager;
    /**
     * 重新调整格式
     */
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    public static final String[] filterUUID= {"fda50693-a4e2-4fb1-afcf-c6eb0764783","931ddf8e-10e4-11e5-9493-1697f925ec7b"};

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"IBeaconService.onCreate()");
        //IBeaconManager.getInstance().init(this).initScanDevices().scanLeDevice();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
        beaconManager.bind(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"IBeaconService.onStartCommand()");
        this.intent = intent;
        if(intent == null){
            Log.d(TAG,"intent == null");
        }else if(netBeaconMap == null){
            //netBeaconMap = (HashMap<String, NetBeaconVo>)intent.getSerializableExtra("netBeaconMap");
            //IBeaconContext.getInstance().setNetBeanconMap(netBeaconMap);
            // Log.d(TAG,"netBeaconMap:"+netBeaconMap.toString());
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
//            IBeaconManager.getInstance().stopScan();
//            IBeaconManager.getInstance().cancelScheduleScan();
//            IBeaconContext.getInstance().clearIBeaconMap();
            beaconManager.unbind(this);

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

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for(Beacon beacon : beacons){
                        IBeaconVo ibeaconVo = new IBeaconVo();
                        ibeaconVo.setDistance(beacon.getDistance());
                        ibeaconVo.setBluetoothAddress(beacon.getBluetoothAddress());
                        ibeaconVo.setName(beacon.getBluetoothName());
                        ibeaconVo.setRssi(beacon.getRssi());
                        ibeaconVo.setTxPower(beacon.getTxPower());
                        if(  beacon.getId1() != null){
                            String uuid = beacon.getId1().toString();
                            ibeaconVo.setUuid(uuid);
                            ibeaconVo.setProximityUuid(uuid);
                        }
                        if(  beacon.getId2() != null){
                            String major = beacon.getId2().toString();
                            ibeaconVo.setMajor(Integer.parseInt(major));
                        }
                        if(  beacon.getId3() != null){
                            String minor = beacon.getId3().toString();
                            ibeaconVo.setMinor(Integer.parseInt(minor));
                        }
                        intoArea(ibeaconVo);
                    }
                }
            }
        });

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                IBeaconVo ibeaconVo = new IBeaconVo();
                ibeaconVo.setBluetoothAddress(region.getBluetoothAddress());
                if(  region.getId1() != null){
                    String uuid = region.getId1().toString();
                    ibeaconVo.setUuid(uuid);
                    ibeaconVo.setProximityUuid(uuid);
                }
                if(  region.getId2() != null){
                    String major = region.getId2().toString();
                    ibeaconVo.setMajor(Integer.parseInt(major));
                }
                if(  region.getId3() != null){
                    String minor = region.getId3().toString();
                    ibeaconVo.setMinor(Integer.parseInt(minor));
                }
                leaveArea(ibeaconVo);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        try {
            for(String uuid : filterUUID){
                beaconManager.startMonitoringBeaconsInRegion(new Region(uuid, null, null, null));
                beaconManager.startRangingBeaconsInRegion(new Region(uuid, null, null, null));
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void intoArea(IBeaconVo ibeacon){
        IBeaconSubject.getInstance().notifyObserversScan(ibeacon);
        //第一次扫描到
        if(!IBeaconContext.getInstance().getiBeaconMap().containsKey(ibeacon.getBeaconKey())){
            if(ibeacon.getDistance() <= CHECK_DISTANCE){
                IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
                IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
            }else{
                IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
            }
        }else{//重复扫描到
            IBeaconVo preBeaconVo = IBeaconContext.getInstance().getiBeaconMap().get(ibeacon.getBeaconKey());
            if(preBeaconVo.getDistance() > CHECK_DISTANCE && ibeacon.getDistance() <= CHECK_DISTANCE){
                IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
                IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
            }else{
                IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
            }
        }


    }

    private void leaveArea(IBeaconVo ibeacon){
        IBeaconSubject.getInstance().notifyObserversOut(ibeacon);
        IBeaconContext.getInstance().getiBeaconMap().remove(ibeacon.getBeaconKey());
    }
}

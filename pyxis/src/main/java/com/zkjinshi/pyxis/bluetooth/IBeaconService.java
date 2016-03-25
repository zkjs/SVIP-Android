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
import java.util.Iterator;
import java.util.Map;


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
    private static final float OVER_TIME = 10 * 1000;
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
            IBeaconContext.getInstance().getiBeaconMap().clear();

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
                        ibeaconVo.setTimestamp(System.currentTimeMillis());
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
                boolean hasRemove = false;
                IBeaconVo minBeaconVo = null;
                if( !IBeaconContext.getInstance().getiBeaconMap().isEmpty()){
                    //把很久都没有扫描到的beancon移除
                    Iterator<Map.Entry<String, IBeaconVo>> iterator2 = IBeaconContext.getInstance().getiBeaconMap().entrySet().iterator();
                    while (iterator2.hasNext()) {
                        Map.Entry<String, IBeaconVo> entry = iterator2.next();
                        IBeaconVo iBeaconVo = entry.getValue();
                        long currentTime = System.currentTimeMillis();
                        long offset = currentTime - iBeaconVo.getTimestamp();
                        if(offset > OVER_TIME){
                            hasRemove = true;
                            leaveArea(iBeaconVo);
                        }
                    }
//                    if(!hasRemove){
//                        //把消失最长时间的beancon移除
//                        Iterator<Map.Entry<String, IBeaconVo>> iterator = IBeaconContext.getInstance().getiBeaconMap().entrySet().iterator();
//                        while (iterator.hasNext()) {
//                            Map.Entry<String, IBeaconVo> entry = iterator.next();
//                            IBeaconVo iBeaconVo = entry.getValue();
//                            if(minBeaconVo == null){
//                                minBeaconVo = iBeaconVo;
//                            }else{
//                                if(iBeaconVo.getTimestamp() < minBeaconVo.getTimestamp()){
//                                    minBeaconVo = iBeaconVo;
//                                }
//                            }
//                        }
//                        if( System.currentTimeMillis() - minBeaconVo.getTimestamp() > 5000){
//                            leaveArea(minBeaconVo);
//                        }
//                    }

                }

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
        if( IBeaconService.killMyself){
            return;
        }
        if(!IBeaconContext.getInstance().getiBeaconMap().containsKey(ibeacon.getBeaconKey())){
            IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
            IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
        }
        else{
            IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
        }


    }

    private void leaveArea(IBeaconVo ibeacon){
        if( IBeaconService.killMyself){
            return;
        }
        IBeaconSubject.getInstance().notifyObserversOut(ibeacon);
        IBeaconContext.getInstance().getiBeaconMap().remove(ibeacon.getBeaconKey());
    }
}

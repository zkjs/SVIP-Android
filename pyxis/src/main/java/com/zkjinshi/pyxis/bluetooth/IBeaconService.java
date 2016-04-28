package com.zkjinshi.pyxis.bluetooth;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.os.Message;
import android.os.RemoteException;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


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
    public static int SEND_MIN_PEROID_TIEM = 5*1000;//两次发送至少的间隔时间
    public static int COLLET_BEACON_INF_PEROID_TIME = 10*60*1000;//收集beacon信息的间隔时间
    public static int COLLET_BEACON_INF_DELAY_TIME = 10*1000;//收集beacon信息的延时时间
    private Timer timer;
    private BeaconManager beaconManager;
    private long id = 0;
    public final static String PRE_STRING = "com.zkjinshi.pyxis";

    /**
     * 重新调整格式
     */
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    public static final String[] filterUUID= {"931ddf8e-10e4-11e5-9493-1697f925ec7b","fda50693-a4e2-4fb1-afcf-c6eb07647835"};




    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"IBeaconService.onCreate()");
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
        beaconManager.setRegionExitPeriod(90*1000);
        beaconManager.setBackgroundBetweenScanPeriod(10*1000);
        beaconManager.setBackgroundScanPeriod(3*1000);
        beaconManager.bind(this);
        timerCollectBeaconInfo();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"IBeaconService.onStartCommand()");
        this.intent = intent;
        if(intent == null){
            Log.d(TAG,"intent == null");
        }else if(netBeaconMap == null){
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
            beaconManager.unbind(this);
            IBeaconContext.getInstance().getiBeaconMap().clear();
            IBeaconContext.getInstance().getExtInfoMap().clear();
            if(this.timer != null){
                this.timer.cancel();
                this.timer = null;
            }

        }else if(intent != null){
            getApplicationContext().startService(intent);
        }

        Log.d(TAG,"IBeaconService.onDestroy()");
        super.onDestroy();
    }

    //定时器收集Beacon信息
    private void timerCollectBeaconInfo() {
        if(this.timer != null){
            this.timer.cancel();
            this.timer = null;
        }
        TimerTask task = new TimerTask(){
            public void run() {
                if( !IBeaconContext.getInstance().getiBeaconMap().isEmpty()){
                    IBeaconSubject.getInstance().notifyObserversPostCollectBeacons();
                }
            }
        };
        this.timer = new Timer(true);
        this.timer.schedule(task, COLLET_BEACON_INF_DELAY_TIME, COLLET_BEACON_INF_PEROID_TIME);
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
        try {
            for(String uuid : filterUUID){
                id++;
                String uniqueId = PRE_STRING+id;
                Identifier id1 = Identifier.fromUuid(UUID.fromString(uuid));
                beaconManager.startRangingBeaconsInRegion(new Region(uniqueId, id1, null, null));
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void intoArea(IBeaconVo ibeacon){
        if( IBeaconService.killMyself){
            return;
        }
        IBeaconSubject.getInstance().notifyObserversScan(ibeacon);
        //Log.i(TAG, "I scan "+ibeacon.toString());
        if(!IBeaconContext.getInstance().getiBeaconMap().containsKey(ibeacon.getBeaconKey())){

            if(IBeaconContext.getInstance().getExtInfoMap().containsKey(ibeacon.getBeaconKey())){
                BeaconExtInfo beaconExtInfo =  IBeaconContext.getInstance().getExtInfoMap().get(ibeacon.getBeaconKey());
                long curentTime = System.currentTimeMillis();
                long timeOffset = curentTime - beaconExtInfo.getSendTimestamp();

                if(beaconExtInfo.getSendTimestamp() == -1){//上次发送失败，重发
                    beaconExtInfo.setSendTimestamp(curentTime);
                    // 更新beacon信息
                    IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
                    // 更新beacon 额外信息
                    IBeaconContext.getInstance().getExtInfoMap().put(ibeacon.getBeaconKey(),beaconExtInfo);
                    //通知观察者
                    IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
                }else if(timeOffset >= SEND_MIN_PEROID_TIEM){ //至少要间隔的发送时间
                    beaconExtInfo.setSendTimestamp(curentTime);
                    // 更新beacon信息
                    IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
                    // 更新beacon 额外信息
                    IBeaconContext.getInstance().getExtInfoMap().put(ibeacon.getBeaconKey(),beaconExtInfo);
                    IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
                    monitoringBeacon(ibeacon);
                }

            }else{
                //添加发送时间等信息
                BeaconExtInfo beaconExtInfo = new BeaconExtInfo();
                beaconExtInfo.setKey(ibeacon.getBeaconKey());
                beaconExtInfo.setSendTimestamp(System.currentTimeMillis());
                // 更新beacon信息
                IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
                // 更新beacon 额外信息
                IBeaconContext.getInstance().getExtInfoMap().put(ibeacon.getBeaconKey(),beaconExtInfo);
                IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
                monitoringBeacon(ibeacon);
            }


        }else{
            // 更新beacon信息
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

    //框架自带离开监听
    private void monitoringBeacon(final IBeaconVo ibeacon){
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw "+region.getId2());

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see "+region.getId2());

                //IBeaconSubject.getInstance().notifyObserversExitRegion(region);

                if(region != null && region.getId1() != null && region.getId2() != null){
                    String uuid = region.getId1().toString();
                    String major = region.getId2().toString();
                    String key = uuid+major;
                    IBeaconVo iBeaconVo = IBeaconContext.getInstance().getiBeaconMap().get(key);
                    leaveArea(iBeaconVo);
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                //Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });
        try {
            String uuid = ibeacon.getUuid();
            int major = ibeacon.getMajor();
            Identifier id1 = Identifier.parse(uuid);
            Identifier id2 = Identifier.fromInt(major);
            String uniqueId = ibeacon.getBeaconKey();
            beaconManager.startMonitoringBeaconsInRegion(new Region(uniqueId, id1, id2, null));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

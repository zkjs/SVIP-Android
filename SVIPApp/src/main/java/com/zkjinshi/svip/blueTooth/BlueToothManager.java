package com.zkjinshi.svip.blueTooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.pyxis.bluetooth.BeaconExtInfo;
import com.zkjinshi.pyxis.bluetooth.IBeaconContext;
import com.zkjinshi.pyxis.bluetooth.IBeaconController;
import com.zkjinshi.pyxis.bluetooth.IBeaconObserver;
import com.zkjinshi.pyxis.bluetooth.IBeaconService;
import com.zkjinshi.pyxis.bluetooth.IBeaconSubject;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.manager.BleLogManager;
import com.zkjinshi.svip.manager.BleStatManager;
import com.zkjinshi.svip.manager.SSOManager;

import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.sqlite.BleStatDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.PayloadVo;

import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.NetUtils;

/**
 * 蓝牙管理器
 * 开发者：杜健德
 * 日期：2016/2/4
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BlueToothManager {

    public static final String TAG = BlueToothManager.class.getSimpleName();
    public static final int BEACON_NOTICE = 1;
    public static final int COLLECT_NOTICE = 2;
    private BlueToothManager(){}
    private static BlueToothManager instance;
    private Context context;

    public final static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * 打开蓝牙
     */
    public void openBluetooth(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                    .getDefaultAdapter();
            if(null != bluetoothAdapter && !bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }
        }
    }

    public  final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BEACON_NOTICE:
                {
                    Bundle bundle = msg.getData();
                    IBeaconVo iBeaconVo = (IBeaconVo)bundle.getSerializable("iBeaconVo");
                    lbsLocBeaconRequest(iBeaconVo);
                }
                break;
                case COLLECT_NOTICE:
                {
                    locBeaconsCollect();
                }
                break;
            }
        }
    };



    private IBeaconObserver mIBeaconObserver = new IBeaconObserver() {
        @Override
        public void intoRegion(IBeaconVo iBeaconVo) {
            LogUtil.getInstance().info(LogLevel.DEBUG,"进入："+iBeaconVo.getMajor());
            Bundle bundle = new Bundle();
            bundle.putSerializable("iBeaconVo",iBeaconVo);
            Message msg = new Message();
            msg.what = BEACON_NOTICE;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void outRegin(IBeaconVo iBeaconVo) {
            LogUtil.getInstance().info(LogLevel.DEBUG,"离开："+iBeaconVo.getMajor());
        }

        public void postCollectBeacons(){
            handler.sendEmptyMessage(COLLECT_NOTICE);
        }

        public void sacnBeacon(IBeaconVo iBeaconVo){
            //Log.d(TAG,"扫描到："+iBeaconVo.getName()+" 距离："+iBeaconVo.getDistance());
            //LogUtil.getInstance().info(LogLevel.RECORD,"扫描到："+iBeaconVo.getName()+" 距离："+iBeaconVo.getDistance());
//            if(iBeaconVo.getMajor() == 1000){
//                LogUtil.getInstance().info(LogLevel.DEBUG,"距离："+iBeaconVo.getDistance());
//            }
        }

        public void exitRegion(Region region){
//            if(region == null){
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region == null");
//            }else{
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region.getBluetoothAddress="+region.getBluetoothAddress());
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region.getId1="+region.getId1());
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region.getId2="+region.getId2());
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region.getId3="+region.getId3());
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region.getUniqueId="+region.getUniqueId());
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region.getIdentifier(0)="+region.getIdentifier(0));
//                LogUtil.getInstance().info(LogLevel.DEBUG,"region="+region.toString());
//            }
        }
    };

    public void lbsLocBeaconRequest(final IBeaconVo iBeaconVo){
        try {
            if(!NetWorkUtil.isNetworkConnected(context)){
                return;
            }
            if(!CacheUtil.getInstance().isLogin()){
                return;
            }
            if(TextUtils.isEmpty(CacheUtil.getInstance().getExtToken())){
                return;
            }
            PayloadVo payloadVo = SSOManager.getInstance().decodeToken(CacheUtil.getInstance().getExtToken());
            if(TextUtils.isEmpty(payloadVo.getSub())){
                return;
            }
            //记录Gps位置信息
            BeaconExtInfo beaconExtInfo = IBeaconContext.getInstance().getExtInfoMap().get(iBeaconVo.getBeaconKey());
            if(beaconExtInfo != null){
               AMapLocation aMapLocation = LocationManager.getInstance().getLastAMapLocation();
                if(aMapLocation != null){
                    beaconExtInfo.setLatitude(aMapLocation.getLatitude());
                    beaconExtInfo.setLongitude(aMapLocation.getLongitude());
                    beaconExtInfo.setAltitude(aMapLocation.getAltitude());
                    IBeaconContext.getInstance().getExtInfoMap().put(iBeaconVo.getBeaconKey(),beaconExtInfo);
                }
            }

            String url = ProtocolUtil.lbsLocBeacon();
            client.setMaxRetriesAndTimeout(3,1000*3);
            client.setConnectTimeout(1000*10);
            client.setResponseTimeout(1000*10);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("locid",iBeaconVo.getMajor()+"");
            jsonObject.put("major", iBeaconVo.getMajor()+"");
            jsonObject.put("minor", iBeaconVo.getMinor()+"");
            jsonObject.put("uuid", iBeaconVo.getProximityUuid());
            jsonObject.put("sensorid", iBeaconVo.getBluetoothAddress());
            jsonObject.put("token", CacheUtil.getInstance().getExtToken());
            jsonObject.put("timestamp", iBeaconVo.getTimestamp());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            client.put(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    //Log.d(TAG,response.toString());
                    LogUtil.getInstance().info(LogLevel.DEBUG,"距离："+iBeaconVo.getDistance()+"蓝牙推送成功"+iBeaconVo.getMajor());
                    BleStatDBUtil.getInstance().updateTotalCount();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e){
                    LogUtil.getInstance().info(LogLevel.DEBUG,"蓝牙推送失败"+iBeaconVo.getMajor());
                    BleLogManager.getInstance().collectBleLog(context,e.getMessage());
                    BeaconExtInfo beaconExtInfo = IBeaconContext.getInstance().getExtInfoMap().get(iBeaconVo.getBeaconKey());
                    if(beaconExtInfo != null && beaconExtInfo.getFailCount() < 3){
                        int failCount = beaconExtInfo.getFailCount();
                        beaconExtInfo.setSendTimestamp(-1);
                        beaconExtInfo.setFailCount(failCount+1);
                        IBeaconContext.getInstance().getExtInfoMap().put(iBeaconVo.getBeaconKey(),beaconExtInfo);
                        IBeaconContext.getInstance().getiBeaconMap().remove(iBeaconVo.getBeaconKey());

                        LogUtil.getInstance().info(LogLevel.DEBUG,iBeaconVo.getMajor()+"蓝牙推送重连"+ beaconExtInfo.getFailCount()+"次" );
                        BleStatManager.getInstance().updateRetryCount();
                        BleStatDBUtil.getInstance().updateTotalCount();

                    }

                }

                @Override
                public void onRetry(int retryNo) {
                    Log.d(TAG,"retryNo:"+retryNo);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void locBeaconsCollect() {
        try {
            if(!NetWorkUtil.isNetworkConnected(context)){
                return;
            }
            if(!CacheUtil.getInstance().isLogin()){
                return;
            }
            if(TextUtils.isEmpty(CacheUtil.getInstance().getExtToken())){
                return;
            }
            PayloadVo payloadVo = SSOManager.getInstance().decodeToken(CacheUtil.getInstance().getExtToken());
            if(TextUtils.isEmpty(payloadVo.getSub())){
                return;
            }

            String url = ProtocolUtil.lbsLocBeacons();
            client.setMaxRetriesAndTimeout(3,1000*3);
            client.setConnectTimeout(1000*10);
            client.setResponseTimeout(1000*10);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            final JSONArray jsonArray = new JSONArray();
            if( !IBeaconContext.getInstance().getiBeaconMap().isEmpty()){
                Iterator<Map.Entry<String, IBeaconVo>> iterator = IBeaconContext.getInstance().getiBeaconMap().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, IBeaconVo> entry = iterator.next();
                    IBeaconVo iBeaconVo = entry.getValue();
                    long currentTime = System.currentTimeMillis();
                    BeaconExtInfo beaconExtInfo = IBeaconContext.getInstance().getExtInfoMap().get(iBeaconVo.getBeaconKey());
                    JSONObject jsonBeacon = new JSONObject();
                    jsonBeacon.put("major",iBeaconVo.getMajor());
                    jsonBeacon.put("minor",iBeaconVo.getMinor());
                    jsonBeacon.put("uuid",iBeaconVo.getProximityUuid());
                    jsonBeacon.put("signal",iBeaconVo.getRssi());
                    jsonBeacon.put("accuracy",iBeaconVo.getDistance());
                    jsonBeacon.put("latitude",beaconExtInfo.getLatitude());
                    jsonBeacon.put("longitude",beaconExtInfo.getLongitude());
                    jsonBeacon.put("altitude",beaconExtInfo.getAltitude());
                    jsonBeacon.put("timestamp",currentTime);

                    jsonArray.put(jsonBeacon);
                }
            }else{
                return;
            }

            StringEntity stringEntity = new StringEntity(jsonArray.toString());
            client.post(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    LogUtil.getInstance().info(LogLevel.DEBUG,"beacons收集成功"+jsonArray.length());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e){
                    LogUtil.getInstance().info(LogLevel.DEBUG,"beacons收集失败");

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized BlueToothManager getInstance(){
        if(null == instance){
            instance = new BlueToothManager();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 大于等于android 4.4
            IBeaconController.getInstance().init(context,3000L,3);
            IBeaconService.mIBeaconObserver = mIBeaconObserver;
        }
    }

    /**
     * 启动IBeacon服务
     * @return
     */
    public void startIBeaconService(ArrayList<NetBeaconVo> beaconList){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IBeaconController.getInstance().setListenBeancons(beaconList);
            IBeaconController.getInstance().startBeaconService();
        }
    }

    /**
     * 停止IBeacon服务
     * @return
     */
    public void stopIBeaconService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IBeaconController.getInstance().stopBeaconService();
        }
    }

    /**
     * 判读IBeacon服务是否工作
     * @return
     */
    public boolean isIBeaconServiceRunning(){
        return IBeaconController.getInstance().isRuning();
    }

    /**
     * 添加观察者
     * @param observer
     */
    public void addObserver(IBeaconObserver observer){
        IBeaconSubject.getInstance().addObserver(observer);
    }

    /**
     * 移除观察者
     * @param observer
     */
    public void removeObserver(IBeaconObserver observer){
        IBeaconSubject.getInstance().removeObserver(observer);
    }

    /**
     * 返回最近检测到的IBeacon信息
     * @return
     */
    public IBeaconVo getLastIBeaconVo(){
        return IBeaconContext.getInstance().getLastIBeaconVo();
    }

}

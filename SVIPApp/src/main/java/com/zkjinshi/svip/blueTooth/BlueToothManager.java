package com.zkjinshi.svip.blueTooth;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.pyxis.bluetooth.IBeaconContext;
import com.zkjinshi.pyxis.bluetooth.IBeaconController;
import com.zkjinshi.pyxis.bluetooth.IBeaconObserver;
import com.zkjinshi.pyxis.bluetooth.IBeaconService;
import com.zkjinshi.pyxis.bluetooth.IBeaconSubject;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.common.MainController;
import com.zkjinshi.svip.bean.LocPushBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.yunba.android.manager.YunBaManager;

/**
 * 蓝牙管理器
 * 开发者：杜健德
 * 日期：2016/2/4
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BlueToothManager {

    public static final String TAG = BlueToothManager.class.getSimpleName();

    private BlueToothManager(){}
    private static BlueToothManager instance;
    private Context context;

    private IBeaconObserver mIBeaconObserver = new IBeaconObserver() {
        @Override
        public void intoRegion(IBeaconVo iBeaconVo) {
            LogUtil.getInstance().info(LogLevel.DEBUG,"进入："+iBeaconVo);
            //pushIbeacon(iBeaconVo);
            lbsLocBeaconRequest(iBeaconVo);
        }

        @Override
        public void outRegin(IBeaconVo iBeaconVo) {
            LogUtil.getInstance().info(LogLevel.DEBUG,"离开："+iBeaconVo);
        }
    };


    public void pushIbeacon(IBeaconVo iBeaconVo){
        try {
            String url = ProtocolUtil.lbsLocBeacon();
            NetRequest netRequest = new NetRequest(url);
            HashMap<String,Object> bizMap = new HashMap<String,Object>();
            bizMap.put("locid",iBeaconVo.getMajor()+"");
            bizMap.put("major", iBeaconVo.getMajor()+"");
            bizMap.put("minor", iBeaconVo.getMinor()+"");
            bizMap.put("uuid", iBeaconVo.getProximityUuid());
            bizMap.put("sensorid", iBeaconVo.getBluetoothAddress());
            bizMap.put("token", CacheUtil.getInstance().getExtToken());
            bizMap.put("timestamp", iBeaconVo.getTimestamp());

            /*bizMap.put("locid","1");
            bizMap.put("major","2");
            bizMap.put("minior", "3");
            bizMap.put("uuid", "uuid-uuid-uuid-uuid");
            bizMap.put("sensorid","sensorid");
            bizMap.put("token", "head.payload.sign");
            bizMap.put("timestamp", 1455870706863L);*/

            netRequest.setObjectParamMap(bizMap);
            NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
            netRequestTask.methodType = MethodType.PUT;
            LogUtil.getInstance().info(LogLevel.DEBUG,"调用API："+url);
            LogUtil.getInstance().info(LogLevel.DEBUG,"API推送参数:"+bizMap.toString());
            netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
                @Override
                public void onNetworkRequestError(int errorCode, String errorMessage) {
                    Log.i(TAG, "errorCode:" + errorCode);
                    Log.i(TAG, "errorMessage:" + errorMessage);
                }

                @Override
                public void onNetworkRequestCancelled() {

                }

                @Override
                public void onNetworkResponseSucceed(NetResponse result) {
                    super.onNetworkResponseSucceed(result);
                    //Log.i(TAG, "result.rawResult:" + result.rawResult);
                    LogUtil.getInstance().info(LogLevel.DEBUG,"API推送成功");
                }

                @Override
                public void beforeNetworkRequestStart() {

                }
            });
            netRequestTask.isShowLoadingDialog = false;
            netRequestTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void lbsLocBeaconRequest(IBeaconVo iBeaconVo){
        try {
            String url = ProtocolUtil.lbsLocBeacon();
            //url = "http://192.168.199.112:8080/lbs/v1/loc/beacon";
            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(3,500);
            client.setTimeout(3000);

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
            client.put(context,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG,response.toString());
                    LogUtil.getInstance().info(LogLevel.DEBUG,"蓝牙推送成功");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){

                    Log.d(TAG,errorResponse.toString());
                    LogUtil.getInstance().info(LogLevel.DEBUG,"蓝牙推送失败");
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                    Log.d(TAG,"retryNo:"+retryNo);
                    LogUtil.getInstance().info(LogLevel.DEBUG,"蓝牙推送重连"+ retryNo );

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public String getResponseString(byte[] responseBody){
        String result = "";
        try {
           result = new String(responseBody,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
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

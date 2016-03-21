package com.zkjinshi.svip.blueTooth;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
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

import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.sqlite.BleStatDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.PayloadVo;

import org.json.JSONObject;

import java.util.ArrayList;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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

    private BlueToothManager(){}
    private static BlueToothManager instance;
    private Context context;

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
            }
        }
    };

    private IBeaconObserver mIBeaconObserver = new IBeaconObserver() {
        @Override
        public void intoRegion(IBeaconVo iBeaconVo) {
            LogUtil.getInstance().info(LogLevel.DEBUG,"进入："+iBeaconVo);
            Bundle bundle = new Bundle();
            bundle.putSerializable("iBeaconVo",iBeaconVo);
            Message msg = new Message();
            msg.what = BEACON_NOTICE;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void outRegin(IBeaconVo iBeaconVo) {
            LogUtil.getInstance().info(LogLevel.DEBUG,"离开："+iBeaconVo);
        }
    };

    public void lbsLocBeaconRequest(final IBeaconVo iBeaconVo){
        try {
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
            String url = ProtocolUtil.lbsLocBeacon();
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
                    LogUtil.getInstance().info(LogLevel.DEBUG,"距离："+iBeaconVo.getDistance()+"蓝牙推送成功");
                    BleStatDBUtil.getInstance().updateTotalCount();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    LogUtil.getInstance().info(LogLevel.DEBUG,"蓝牙推送失败");
                    BleLogManager.getInstance().collectBleLog(context,throwable.getMessage());
                    //RequestUtil.onFailure(context,statusCode);
                }


                @Override
                public void onRetry(int retryNo) {
                    Log.d(TAG,"retryNo:"+retryNo);
                    LogUtil.getInstance().info(LogLevel.DEBUG,"蓝牙推送重连"+ retryNo );
                    BleStatManager.getInstance().updateRetryCount();
                    BleStatDBUtil.getInstance().updateTotalCount();

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

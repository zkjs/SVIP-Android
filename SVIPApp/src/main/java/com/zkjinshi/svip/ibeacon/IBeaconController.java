package com.zkjinshi.svip.ibeacon;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconController {

    public static final String TAG = IBeaconController.class.getSimpleName();


    private Context context;

    private IBeaconController(){}

    private static IBeaconController instance;

    private ArrayList<IBeaconEntity> beaconList;

    public synchronized static IBeaconController getInstance(){
        if(null == instance){
            instance = new IBeaconController();
        }
        return  instance;
    }

    public void init(Context context){
        this.context = context;
        requestIBeanconList();
    }

    /**
     * 请求蓝牙区域信息
     */
    private void requestIBeanconList(){
        String url = ProtocolUtil.getLocationListUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                String listStr =  CacheUtil.getInstance().getListStrCache(IBeaconController.class.getSimpleName());
                if(!TextUtils.isEmpty(listStr)){
                    Type listType = new TypeToken<ArrayList<IBeaconEntity>>(){}.getType();
                    Gson gson = new Gson();
                    beaconList = gson.fromJson(listStr, listType);
                    if (null != beaconList && !beaconList.isEmpty()) {
                        for (IBeaconEntity beancon : beaconList) {
                            IBeaconContext.getInstance().getBeanconMap().put(beancon.getBeaconKey(), beancon);
                        }
                        startBeaconService();
                    }
                }
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<ArrayList<IBeaconEntity>>(){}.getType();
                    Gson gson = new Gson();
                    beaconList = gson.fromJson( result.rawResult, listType);
                    if(null != beaconList && !beaconList.isEmpty()) {
                        //将List转为成Map
                        for (IBeaconEntity beancon : beaconList) {
                            IBeaconContext.getInstance().getBeanconMap().put(beancon.getBeaconKey(), beancon);
                        }
                        startBeaconService();
                        CacheUtil.getInstance().saveListCache(IBeaconController.class.getSimpleName(), beaconList);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();

    }

    /**
     * 启动IBeacon蓝牙扫描服务
     */
    public void startBeaconService(){
        Intent intent = new Intent(VIPContext.getInstance().getContext(), IBeaconService.class);
        intent.setAction("com.zkjinshi.svip.im.Beacon_SERVICE");
        VIPContext.getInstance().getContext().startService(intent);
    }

    /**
     * 停止IBeacon蓝牙扫描服务
     */
    public void stopBeaconService(){
        Intent intent = new Intent(VIPContext.getInstance().getContext(), IBeaconService.class);
        intent.setAction("com.zkjinshi.svip.im.Beacon_SERVICE");
        VIPContext.getInstance().getContext().stopService(intent);
    }
}

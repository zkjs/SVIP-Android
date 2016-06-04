package com.zkjinshi.ritz.manager;

import android.content.Context;

import android.os.Environment;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;

import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;

import com.zkjinshi.ritz.net.ExtNetRequestListener;
import com.zkjinshi.ritz.net.MethodType;
import com.zkjinshi.ritz.net.NetRequest;
import com.zkjinshi.ritz.net.NetRequestTask;
import com.zkjinshi.ritz.net.NetResponse;

import com.zkjinshi.ritz.sqlite.BleLogDBUtil;
import com.zkjinshi.ritz.utils.CacheUtil;
import com.zkjinshi.ritz.utils.FileUtil;

import com.zkjinshi.ritz.utils.ProtocolUtil;

import com.zkjinshi.ritz.vo.BaseResponseVo;
import com.zkjinshi.ritz.vo.BleLogVo;
import com.zkjinshi.ritz.vo.BleStatVo;


import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 蓝牙定义日志管理器
 * 开发者：JimmyZhang
 * 日期：2016/3/10
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BleLogManager {

    public static final String TAG = BleLogManager.class.getSimpleName();

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    private BleLogManager(){}

    private static BleLogManager instance;

    public synchronized static BleLogManager getInstance(){
        if(null == instance){
            instance = new BleLogManager();
            instance.init();
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init(){

    }

    /**
     * 收集网络请求错误信息
     * @param context
     * @param errorLog
     */
    public void collectBleLog(Context context,String errorLog){
        BleLogVo bleLogVo = new BleLogVo();
        bleLogVo.setErrorMessage(errorLog);
        bleLogVo.setBrand(DeviceUtils.getBrand());
        bleLogVo.setConnectedType(NetWorkUtil.getConnectedType(context));
        bleLogVo.setIMEI(DeviceUtils.getIMEI());
        bleLogVo.setPhoneNum(CacheUtil.getInstance().getUserPhone());
        bleLogVo.setDeviceType("android");
        BleLogDBUtil.getInstance().insertBleLog(bleLogVo);
    }

    /**
     * 清空蓝牙定位错误信息
     */
    public void clearBleLog(){
        BleLogDBUtil.getInstance().clearBleLog();
    }

    public ArrayList<BleLogVo> getBleLog(){
        return BleLogDBUtil.getInstance().queryBleLogL();
    }

    public void uploadBleStatLog(final Context context){
        boolean isUpdateStat = BleStatManager.getInstance().isUpdateStat();
        if(isUpdateStat){
            try {
                String logPath = getBlePath(context);
                File dirFile = new File(logPath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                long timestamp = System.currentTimeMillis();
                String time = formatter.format(new Date());
                String fileName = CacheUtil.getInstance().getUserId()+ "-" +time + "-" + timestamp + ".txt";
                File logFile = new File(logPath + fileName);
                FileWriter fw = new FileWriter(logFile, true);
                BleStatVo bleStatVo = BleStatManager.getInstance().getStatLog();
                String title = "网络请求总次数："+bleStatVo.getTotalCount()+"网络请求失败次数:"+bleStatVo.getRetryCount() + "\n";
                fw.write(title);
                ArrayList<BleLogVo> bleLogList = getBleLog();
                if(null != bleLogList && !bleLogList.isEmpty()){
                    for(BleLogVo bleLogVo : bleLogList){
                        fw.write("*************************************************");
                        fw.write("手机号码:"+bleLogVo.getPhoneNum()+"\n");
                        fw.write("设备类型:android\n");
                        fw.write("手机品牌:"+bleLogVo.getBrand()+"\n");
                        fw.write("IMEI:"+bleLogVo.getIMEI()+"\n");
                        int connectType = bleLogVo.getConnectedType();
                        if(0 == connectType){
                            fw.write("网络类型:2G/3G\n");
                        }else if(1 == connectType){
                            fw.write("网络类型:WIFI\n");
                        }else if(2 == connectType){
                            fw.write("网络类型:wap\n");
                        }else if(3 == connectType){
                            fw.write("网络类型:net\n");
                        }
                        fw.write("失败原因:"+bleLogVo.getErrorMessage()+"\n");
                    }
                }
                fw.close();
                uploadBleStatLog(context,logFile.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadBleStatLog(final Context context,String filePath){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getUserLogUrl());
        HashMap<String,String> bizMap = new HashMap<String, String>();
        HashMap<String,String> fileMap = new HashMap<String, String>();
        bizMap.put("filename", FileUtil.getInstance().getFileName(filePath));
        bizMap.put("category","android");
        fileMap.put("file",filePath);
        netRequest.setBizParamMap(bizMap);
        netRequest.setFileParamMap(fileMap);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.POST;
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
                try{
                    BaseResponseVo basePavoResponse = new Gson().fromJson(result.rawResult,BaseResponseVo.class);
                    if(basePavoResponse != null){
                        if(basePavoResponse.getRes() == 0){
                            //DialogUtil.getInstance().showCustomToast(context,"上传日志成功", Gravity.CENTER);
                            BleStatManager.getInstance().updateStatTime();
                            clearBleLog();
                        }else{
                            //DialogUtil.getInstance().showCustomToast(context,basePavoResponse.getResDesc(), Gravity.CENTER);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    private String getBlePath( Context context){
        return Environment.getExternalStorageDirectory()+"/"+context.getPackageName()+"/ble/";
    }

}

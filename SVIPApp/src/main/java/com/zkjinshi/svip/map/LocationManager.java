package com.zkjinshi.svip.map;

import android.content.Context;
import android.util.Log;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 高德定位管理器
 * 开发者：JimmyZhang
 * 日期：2015/8/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocationManager{

    public static final String TAG = LocationManager.class.getSimpleName();

    private static final long LOCATION_PERIOD_TIME = 1000 * 60 * 5;  //单位毫秒

    private LocationManager (){}
    private static LocationManager instance;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;

    private AMapLocationClientOption mLocationOption = null;
    private AMapLocation mAMapLocation = null;

    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    mAMapLocation = aMapLocation;
                    //aMapLocation.getLatitude();//获取纬度
                    //aMapLocation.getLongitude();//获取经度
                    Log.i("LocationManager",aMapLocation.toString());

                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    LogUtil.getInstance().info(LogLevel.ERROR,"location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());

                }
            }
        }
    };


    public static synchronized LocationManager getInstance(){
        if(null == instance){
            instance = new LocationManager();
        }
        return instance;
    }

    public void init(Context context){
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
    }

    //启动定位
    public void startLocation(){
        if(mLocationClient == null){
            LogUtil.getInstance().info(LogLevel.ERROR,"高德地图还没初始化");
            return;
        }
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图定位服务开启。。。");
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(false);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(LOCATION_PERIOD_TIME);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //停止定位
    public void stopLocation(){

        if(mLocationClient == null){
            LogUtil.getInstance().info(LogLevel.ERROR,"高德地图还没初始化");
            return;
        }
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图定位服务停止。。。");
        mLocationClient.stopLocation();//停止定位
    }

    //销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
    public void destroyLocation(){
        if(mLocationClient == null){
            LogUtil.getInstance().info(LogLevel.ERROR,"高德地图还没初始化");
            return;
        }
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图定位服务销毁。。。");
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    //获取最近的位置信息
    public AMapLocation getmAMapLocation() {
        return mAMapLocation;
    }

    //获取当前城市
    public String getCurrentCity(){
        String city = "";
        AMapLocation aMapLocation = getmAMapLocation();
        if(aMapLocation == null){
            city = "长沙";
        }else{
            city = aMapLocation.getCity();
            city = city.replace("市","");
        }
        return  city;
    }
}

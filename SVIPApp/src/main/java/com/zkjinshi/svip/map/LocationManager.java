package com.zkjinshi.svip.map;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.utils.CacheUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 高德定位管理器
 * 开发者：JimmyZhang
 * 日期：2015/8/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocationManager{

    public static final String TAG = LocationManager.class.getSimpleName();

    private LocationManager (){}
    private static LocationManager instance;
    private LocationManagerProxy mLocationManagerProxy;
    private AMapLocationListener aMapLocationListener;

    private LocationChangeListener locationChangeListener = null;

    public interface LocationChangeListener{
        public void onLocationChanged(AMapLocation aMapLocation);
    }

    public void setLocationChangeListener(LocationChangeListener locationChangeListener) {
        this.locationChangeListener = locationChangeListener;
    }

    public static synchronized LocationManager getInstance(){
        if(null == instance){
            instance = new LocationManager();
        }
        return instance;
    }

    public void registerLocation(Context context){
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        aMapLocationListener =  new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                if(aMapLocation != null ){
                    Log.i(TAG,"aMapLocation.getAMapException().getErrorCode()="+aMapLocation.getAMapException().getErrorCode());
                }

                if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                    //获取位置信息
                    Double geoLat = aMapLocation.getLatitude();//经度
                    Double geoLng = aMapLocation.getLongitude();//纬度
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String desc = "";
                    Bundle locBundle = aMapLocation.getExtras();
                    if (locBundle != null) {
                        desc = locBundle.getString("desc");
                    }
                    LogUtil.getInstance().info(LogLevel.DEBUG,"高德定位信息");
                    LogUtil.getInstance().info(LogLevel.DEBUG,"经度:"+geoLat);
                    LogUtil.getInstance().info(LogLevel.DEBUG,"纬度:"+geoLng);
                    LogUtil.getInstance().info(LogLevel.DEBUG, "街道:" + desc);
                    Log.i(TAG,"高德定位信息");
                    Log.i(TAG,"经度"+geoLat);
                    Log.i(TAG,"纬度"+geoLng);
                    Log.i(TAG, "街道" + desc);
                    HashMap<String,String> requestMap = new HashMap<String,String>();
                    requestMap.put("userid", CacheUtil.getInstance().getUserId());
                    requestMap.put("token",CacheUtil.getInstance().getToken());
                    requestMap.put("map_latitude",geoLat+"");
                    requestMap.put("map_longitude",geoLng+"");
                    requestMap.put("trace_time",sdf.format(new Date()));
                    LocationController.getInstance().requestAddGpsInfoTask(requestMap);
                    if(locationChangeListener != null){
                        locationChangeListener.onLocationChanged(aMapLocation);
                    }
                }
            }

            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法     
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60 * 1000, 15,aMapLocationListener);
        mLocationManagerProxy.setGpsEnable(false);
    }

    public void removeLocation(){
        if (mLocationManagerProxy != null) {
            if(null != aMapLocationListener){
                mLocationManagerProxy.removeUpdates(aMapLocationListener);
            }
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }

}

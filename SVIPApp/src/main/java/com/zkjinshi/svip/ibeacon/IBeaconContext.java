package com.zkjinshi.svip.ibeacon;

import android.util.Log;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IBeaconQueue队列
 * 开发者：JimmyZhang
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconContext {

    public static final String TAG = "IBeaconContext";

    private IBeaconContext(){}

    private static IBeaconContext instance;

    public synchronized static IBeaconContext getInstance(){
        if(null ==  instance){
            instance = new IBeaconContext();
        }
        return instance;
    }

    private ConcurrentHashMap<String, Long> checkCycleMap = new ConcurrentHashMap<String,Long>();

    private ConcurrentHashMap<String,RegionVo> regionCycleyMap = new ConcurrentHashMap<String,RegionVo>();

    private Map<String,IBeaconEntity> beanconMap = new HashMap<String,IBeaconEntity>();

    public Map<String,Long> getCheckCycleMap(){
        if(null == checkCycleMap){
            checkCycleMap = new ConcurrentHashMap<String,Long>();
        }
        return checkCycleMap;
    }

    public Map<String,IBeaconEntity> getBeanconMap(){
        if(null ==  beanconMap){
            beanconMap = new HashMap<String,IBeaconEntity>();
        }
        return beanconMap;
    }

    public Map<String,RegionVo> getRegionCycleyMapp(){
        if(null == regionCycleyMap){
            regionCycleyMap = new ConcurrentHashMap<String,RegionVo>();
        }
        return regionCycleyMap;
    }

    public void clearCheckCycle(){
        if(null != checkCycleMap){
            checkCycleMap.clear();
        }
    }

    public void clearReginCycle(){
        if(null != regionCycleyMap){
            regionCycleyMap.clear();
        }
    }

    public long removeCheck(RegionVo regionVo){
        if(checkCycleMap.containsKey(regionVo.getiBeacon().getBeaconKey())){
           return checkCycleMap.remove(regionVo.getiBeacon().getBeaconKey());
        }
        return 0;
    }

    public void removeRegion(RegionVo regionVo){
        if(regionCycleyMap.containsKey(regionVo.getiBeacon().getBeaconKey())){
            regionCycleyMap.remove(regionVo.getiBeacon().getBeaconKey());
        }
    }


    public void putCheck(RegionVo regionVo,long activityTime){
        checkCycleMap.put(regionVo.getiBeacon().getBeaconKey(), activityTime);
    }

    public void putRegion(RegionVo regionVo,long activityTime){
        if(!regionCycleyMap.containsKey(regionVo.getiBeacon().getBeaconKey())){
            regionVo.setInTime(activityTime);
            IBeaconSubject.getInstance().notifyObserversInto(regionVo);
            Log.i(TAG, "欢迎进入:" + regionVo.getiBeacon().toString());
            LogUtil.getInstance().info(LogLevel.ERROR, "用户进入以下beacon区域");
            LogUtil.getInstance().info(LogLevel.ERROR,"name:"+regionVo.getiBeacon().getName());
            LogUtil.getInstance().info(LogLevel.ERROR,"major:"+regionVo.getiBeacon().getMajor());
            LogUtil.getInstance().info(LogLevel.ERROR,"minor:"+regionVo.getiBeacon().getMinor());
            LogUtil.getInstance().info(LogLevel.ERROR, "-----------------------------------");
        }
        regionCycleyMap.put(regionVo.getiBeacon().getBeaconKey(), regionVo);
    }

}

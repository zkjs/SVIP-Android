package com.zkjinshi.pyxis.bluetooth;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IBeaconContext
 * 开发者：杜健德
 * 日期：2016/01/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconContext {

    public static final String TAG = IBeaconContext.class.getSimpleName();

    private IBeaconContext(){}

    private static IBeaconContext instance;

    public synchronized static IBeaconContext getInstance(){
        if(null ==  instance){
            instance = new IBeaconContext();
        }
        return instance;
    }
    private ConcurrentHashMap<String,NetBeaconVo> netBeanconMap = new ConcurrentHashMap<String,NetBeaconVo>();
    private ConcurrentHashMap<String,IBeaconVo> iBeanconMap = new ConcurrentHashMap<String,IBeaconVo>();
    private ConcurrentHashMap<String,BeaconExtInfo> extInfoMap = new ConcurrentHashMap<String,BeaconExtInfo>();

    public ConcurrentHashMap<String, NetBeaconVo> getNetBeaconMap() {
        if(netBeanconMap == null){
            netBeanconMap = new ConcurrentHashMap<String,NetBeaconVo>();
        }
        return netBeanconMap;
    }

    public ConcurrentHashMap<String, IBeaconVo> getiBeaconMap() {
        if(iBeanconMap == null){
            iBeanconMap = new ConcurrentHashMap<String,IBeaconVo>();
        }
        return iBeanconMap;
    }

    public ConcurrentHashMap<String, BeaconExtInfo> getExtInfoMap() {
        if(extInfoMap == null){
            extInfoMap = new ConcurrentHashMap<String,BeaconExtInfo>();
        }
        return extInfoMap;
    }

    public void clearIBeaconMap(){
        Log.d(TAG,"clearIBeaconMap");
        if(iBeanconMap != null){
            iBeanconMap.clear();
        }
    }

    public void setNetBeanconMap(ConcurrentHashMap<String, NetBeaconVo> netBeanconMap) {
        this.netBeanconMap = netBeanconMap;
    }

    public void setNetBeanconMap(HashMap<String, NetBeaconVo> hashMap) {
        if(hashMap == null || hashMap.isEmpty()){
            return;
        }
        netBeanconMap = new ConcurrentHashMap<String,NetBeaconVo>();
        Iterator<Map.Entry<String, NetBeaconVo>> iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, NetBeaconVo> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            NetBeaconVo val = entry.getValue();
            netBeanconMap.put(key,val);
        }
    }

    /**
     * 返回是否检测到IBeacon
     * @return
     */
    public boolean isIBeaconEmpty(){
        if(iBeanconMap == null){
            return true;
        }
        return iBeanconMap.isEmpty();
    }

    /**
     * 返回最近检测到的IBeacon信息
     * @return
     */
    public IBeaconVo getLastIBeaconVo(){
        if(isIBeaconEmpty()){
            return null;
        }

        IBeaconVo lastIBeaconVo = null;
        Iterator<Map.Entry<String, IBeaconVo>> iter = iBeanconMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, IBeaconVo> entry = (Map.Entry) iter.next();
            IBeaconVo val = entry.getValue();
            if(lastIBeaconVo == null){
                lastIBeaconVo = val;
            }else if(val.getTimestamp() > lastIBeaconVo.getTimestamp()){
                lastIBeaconVo = val;
            }
        }
        return lastIBeaconVo;
    }


}

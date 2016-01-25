package com.zkjinshi.svip.bluetooth;

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

    public void clearIBeaconMap(){
        if(iBeanconMap == null){
            iBeanconMap.clear();
        }
    }

}

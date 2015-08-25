package com.zkjinshi.svip.ibeacon;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconSubject {

    private IBeaconSubject(){}

    private static IBeaconSubject instance;

    private Vector<IBeaconObserver> obsVector;

    private Iterator<Map.Entry<RegionVo, Long>> iterator;
    private Map.Entry<RegionVo, Long> entry;
    private RegionVo regionVo;
    private Map<RegionVo, Long> beaconsFoundInScanCycle;
    private long intoTime,outTime,standTime;

    public synchronized static IBeaconSubject getInstance(){
        if(null == instance){
            instance = new IBeaconSubject();
            instance.init();
        }
        return instance;
    }

    public void init(){
        obsVector = new  Vector<IBeaconObserver>();
    }

    /**
     * 添加观察着
     */
    public void addObserver(IBeaconObserver observer){
        if(null != obsVector && !obsVector.contains(observer)){
            obsVector.add(observer);
        }
    }

    /**
     * 移除观察着
     */
    public void removeObserver(IBeaconObserver observer){
        if(null != obsVector && obsVector.contains(observer)){
            obsVector.remove(observer);
        }
    }

    /**
     * 进入区域通知所有观察着
     */
    public void notifyObserversInto(RegionVo regionVo){
        if(null != obsVector && !obsVector.isEmpty()){
            for (IBeaconObserver observer : obsVector){
                if(null != regionVo){
                    observer.intoRegion(regionVo);
                }
            }
        }
    }

    /**
     * 离开区域通知所有观察着
     */
    public void notifyObserversOut(RegionVo regionVo){
        if(null != obsVector && !obsVector.isEmpty()){
            regionVo.setOutTime(System.currentTimeMillis());
            intoTime = regionVo.getInTime();
            outTime = regionVo.getOutTime();
            standTime = outTime - intoTime;
            regionVo.setStandTime(standTime);
            for (IBeaconObserver observer : obsVector) {
                observer.outRegin(regionVo);
            }
            IBeaconContext.getInstance().removeRegion(regionVo);
        }
    }
}

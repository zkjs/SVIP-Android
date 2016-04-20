package com.zkjinshi.pyxis.bluetooth;

import org.altbeacon.beacon.Region;

import java.util.Vector;

/**
 * 开发者：杜健德
 * 日期：2016/01/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconSubject {

    private IBeaconSubject(){}

    private static IBeaconSubject instance;

    private Vector<IBeaconObserver> obsVector;



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
    public void notifyObserversInto(IBeaconVo iBeaconVo){
        if(null != obsVector && !obsVector.isEmpty()){
            for (IBeaconObserver observer : obsVector){
                if(null != iBeaconVo){
                    observer.intoRegion(iBeaconVo);
                }
            }
        }
    }

    /**
     * 离开区域通知所有观察着
     */
    public void notifyObserversOut(IBeaconVo iBeaconVo){
        if(null != obsVector && !obsVector.isEmpty()){
            for (IBeaconObserver observer : obsVector) {
                if(null != iBeaconVo) {
                    observer.outRegin(iBeaconVo);
                }
            }
        }
    }

    /**
     * 离开区域通知所有观察着
     */
    public void notifyObserversExitRegion(Region region){
        if(null != obsVector && !obsVector.isEmpty()){
            for (IBeaconObserver observer : obsVector) {
                observer.exitRegion(region);
            }
        }
    }

    /**
     * 离开区域通知所有观察着
     */
    public void notifyObserversScan(IBeaconVo iBeaconVo){
        if(null != obsVector && !obsVector.isEmpty()){
            for (IBeaconObserver observer : obsVector) {
                if(null != iBeaconVo) {
                    observer.sacnBeacon(iBeaconVo);
                }
            }
        }
    }

    /**
     * 通知观察者上传收集到的beacon信息
     */
    public void notifyObserversPostCollectBeacons(){
        if(null != obsVector && !obsVector.isEmpty()){
            for (IBeaconObserver observer : obsVector) {
                observer.postCollectBeacons();
            }
        }
    }
}

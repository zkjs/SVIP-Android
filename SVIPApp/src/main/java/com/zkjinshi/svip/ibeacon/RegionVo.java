package com.zkjinshi.svip.ibeacon;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RegionVo implements Serializable{

    private IBeaconVo iBeacon;
    private long inTime;
    private long outTime;
    private long standTime;

    public RegionVo(IBeaconVo iBeacon,long inTime,long outTime,long standTime){
        this.iBeacon = iBeacon;
        this.inTime = inTime;
        this.outTime = outTime;
        this.standTime = standTime;
    }

    public long getInTime() {
        return inTime;
    }

    public void setInTime(long inTime) {
        this.inTime = inTime;
    }

    public IBeaconVo getiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(IBeaconVo iBeacon) {
        this.iBeacon = iBeacon;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public long getStandTime() {
        return standTime;
    }

    public void setStandTime(long standTime) {
        this.standTime = standTime;
    }

    static class Builder{

        private IBeaconVo iBeacon;

        private long inTime;

        private long outTime;

        private long standTime;

        public Builder setiBeacon(IBeaconVo iBeacon) {
            this.iBeacon = iBeacon;
            return this;
        }

        public Builder setInTime(long inTime) {
            this.inTime = inTime;
            return this;
        }

        public Builder setOutTime(long outTime) {
            this.outTime = outTime;
            return this;
        }

        public Builder setStandTime(long standTime) {
            this.standTime = standTime;
            return this;
        }

        public RegionVo build(){
          return  new RegionVo(iBeacon,inTime,outTime,standTime);
        }
    }

    @Override
    public String toString() {
        return "RegionVo{" +
                "iBeacon=" + iBeacon +
                ", inTime=" + inTime +
                ", outTime=" + outTime +
                ", standTime=" + standTime +
                '}';
    }
}

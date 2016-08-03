package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * 保存收银柜台Beacon信息
 * 开发者：jimmyzhang
 * 日期：16/6/16
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BeaconPushVo implements Serializable {

    private String major;
    private String minor;
    private String uuid;
    private ArrayList<BeaconRssiVo> rssis = new ArrayList<>();
    private BeaconRssiVo lastRssi;
    private long timestamp;

    public BeaconPushVo() {
        timestamp = System.currentTimeMillis();
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ArrayList<BeaconRssiVo> getRssis() {
        return rssis;
    }

    public void setRssis(ArrayList<BeaconRssiVo> rssis) {
        this.rssis = rssis;
    }

    public BeaconRssiVo getLastRssi() {return lastRssi;}

    public void setLastRssi(BeaconRssiVo rssi) { this.lastRssi = rssi;}

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

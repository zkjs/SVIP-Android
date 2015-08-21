package com.zkjinshi.svip.ibeacon;

import java.io.Serializable;

/**
 * IBeacon信息Entity类
 * 开发者：JimmyZhang
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconVo implements Serializable{

    private String name;
    private int major;
    private int minor;
    private String proximityUuid;
    private String bluetoothAddress;
    private int txPower;
    private int rssi;

    public String getBeaconKey(){
        return getProximityUuid()+getMajor()+getMinor();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getProximityUuid() {
        return proximityUuid;
    }

    public void setProximityUuid(String proximityUuid) {
        this.proximityUuid = proximityUuid;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "IBeaconVo{" +
                "name='" + name + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", proximityUuid='" + proximityUuid + '\'' +
                ", bluetoothAddress='" + bluetoothAddress + '\'' +
                ", txPower=" + txPower +
                ", rssi=" + rssi +
                '}';
    }
}

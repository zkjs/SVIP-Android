package com.zkjinshi.pyxis.bluetooth;

import java.io.Serializable;

/**
 * IBeacon信息Entity类
 * 开发者：dujiande
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconVo implements Serializable{

    //本地扫描到的Beacon属性
    private String name;
    private int major;
    private int minor;
    private String proximityUuid;
    private String bluetoothAddress;
    private int txPower;
    private int rssi;
    //从API得到的Beacon属性
    private String locid;
    private String shopid;
    private String sensorid;
    private String uuid;
    private String minior;
    private String locdesc;
    private String status;
    private String remark;
    //计算得到的属性
    private int disappearTime; //统计消失的次数
    private double distance;   //离蓝牙设备距离，单位米
    private long timestamp;   //扫描到的时间戳

    public String getBeaconKey(){
        return getProximityUuid()+getMajor();
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

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMinior() {
        return minior;
    }

    public void setMinior(String minior) {
        this.minior = minior;
    }

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getDisappearTime() {
        return disappearTime;
    }

    public void setDisappearTime(int disappearTime) {
        this.disappearTime = disappearTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
                ", timestamp=" + timestamp +
                ", distance=" + distance +
                '}';
    }
}

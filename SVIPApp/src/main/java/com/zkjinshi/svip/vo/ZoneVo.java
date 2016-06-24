package com.zkjinshi.svip.vo;



import java.util.ArrayList;

/**
 * 服务员获取商家整个区域列表 返回实体
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneVo {

    private String area;
    private String locid;
    private int payment_support;//0-不支持支付, 1-支持支付
    private String major;
    private String minor;
    private String uuid;
    private String sensorid;
    private int subscribed;
    private ArrayList<BeaconVo> beacons;

    public ArrayList<BeaconVo> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<BeaconVo> beacons) {
        this.beacons = beacons;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
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

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public int getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(int subscribed) {
        this.subscribed = subscribed;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getPayment_support() {
        return payment_support;
    }

    public void setPayment_support(int payment_support) {
        this.payment_support = payment_support;
    }
}

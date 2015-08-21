package com.zkjinshi.svip.ibeacon;

import com.zkjinshi.svip.utils.PayResult;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconEntity {

    private String ID;
    private String code;
    private String UUID;
    private String mac_address;
    private String major;
    private String minior;
    private String name;
    private String type;
    private String battery_level;
    private String deviceNO;
    private String manufacture;
    private String status;
    private String remark;

    public String getBeaconKey(){
        return UUID+major+minior;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinior() {
        return minior;
    }

    public void setMinior(String minior) {
        this.minior = minior;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(String battery_level) {
        this.battery_level = battery_level;
    }

    public String getDeviceNO() {
        return deviceNO;
    }

    public void setDeviceNO(String deviceNO) {
        this.deviceNO = deviceNO;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
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

    @Override
    public String toString() {
        return "IBeaconEntity{" +
                "ID='" + ID + '\'' +
                ", code='" + code + '\'' +
                ", UUID='" + UUID + '\'' +
                ", mac_address='" + mac_address + '\'' +
                ", major='" + major + '\'' +
                ", minior='" + minior + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", battery_level='" + battery_level + '\'' +
                ", deviceNO='" + deviceNO + '\'' +
                ", manufacture='" + manufacture + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

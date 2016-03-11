package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 蓝牙定位日志实体
 * 开发者：JimmyZhang
 * 日期：2016/3/10
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BleLogVo implements Serializable {

    private String phoneNum;//手机号码
    private String deviceType;// android/ios
    private String brand;//手机品牌(华为)
    private String IMEI;//手机唯一码
    private int connectedType;//-1没有网络 0：2G/3G网络 1：WIFI网络 2：wap网络 3：net网络
    private String errorMessage;//失败原因

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public int getConnectedType() {
        return connectedType;
    }

    public void setConnectedType(int connectedType) {
        this.connectedType = connectedType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}

package com.zkjinshi.ritz.vo;

import java.io.Serializable;

/**
 * 蓝牙定位统计实体
 * 开发者：JimmyZhang
 * 日期：2016/3/3
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BleStatVo implements Serializable {

    private long retryCount;//重发次数
    private long totalCount;//网络请求总次数
    private long timestamp;//上传时间戳
    private String IMEI;//手机唯一码

    public long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(long retryCount) {
        this.retryCount = retryCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }
}

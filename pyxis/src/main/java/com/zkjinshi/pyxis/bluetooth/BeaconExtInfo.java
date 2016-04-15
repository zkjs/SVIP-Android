package com.zkjinshi.pyxis.bluetooth;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/8/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BeaconExtInfo implements Serializable {


    private String key;

    private int failCount = 0;
    private long sendTimestamp;   //发送的时间戳

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public long getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(long sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }
}

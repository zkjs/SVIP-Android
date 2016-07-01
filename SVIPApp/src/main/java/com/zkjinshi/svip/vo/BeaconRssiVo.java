package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 保存收银柜台Beacon信息
 * 开发者：jimmyzhang
 * 日期：16/6/16
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BeaconRssiVo implements Serializable {

    private int rssi;
    private long timestamp;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.zkjinshi.svip.bean.jsonbean;

import java.util.ArrayList;

/**
 * 区域广告推送请求实体
 * 开发者：JimmyZhang
 * 日期：2015/9/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgPushLocA2MRSP {

    private int type;//协议消息类型
    private long timestamp;//时间戳
    private int protover;//消息协议版本
    private int result;

    private String shopid;
    private String locid;//区域id
    private ArrayList<MsgPushLocAd> arrmsg;


    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getProtover() {
        return protover;
    }

    public void setProtover(int protover) {
        this.protover = protover;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public ArrayList<MsgPushLocAd> getArrmsg() {
        return arrmsg;
    }

    public void setArrmsg(ArrayList<MsgPushLocAd> arrmsg) {
        this.arrmsg = arrmsg;
    }
}

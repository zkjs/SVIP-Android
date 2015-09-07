package com.zkjinshi.svip.bean.jsonbean;

/**
 * 区域广告推送请求实体
 * 开发者：JimmyZhang
 * 日期：2015/9/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgPushLocA2M {

    private int         type;//协议消息类型
    private long        timestamp;//时间戳
    private int         protover;//消息协议版本

    private String userid;
    private String username;
    private String shopid;
    private String locid;//区域id
    private String locdesc;//区域描述
    private String loctype;//区域类型

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

    public String getLoctype() {
        return loctype;
    }

    public void setLoctype(String loctype) {
        this.loctype = loctype;
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

    @Override
    public String toString() {
        return "MsgPushLocA2M{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", protover=" + protover +
                ", userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", shopid='" + shopid + '\'' +
                ", locid='" + locid + '\'' +
                ", locdesc='" + locdesc + '\'' +
                ", loctype='" + loctype + '\'' +
                '}';
    }
}

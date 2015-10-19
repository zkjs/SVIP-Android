package com.zkjinshi.svip.bean.jsonbean;

/**
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgRequestWaiterC2S {
    private int     type;//not null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private String  tempid;//app端临时ID
    private long    srvmsgid;
    private int     protover;//消息协议版本

    private String  shopid;
    private String  ruletype;
    private String  clientid;
    private String  clientname;
    private String  desc;
    private String  locid;
    private String  other;

    private String  sessionid;
    private String  seqid;
    private String  isreadack;
    private String salesid;

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

    public String getTempid() {
        return tempid;
    }

    public void setTempid(String tempid) {
        this.tempid = tempid;
    }

    public long getSrvmsgid() {
        return srvmsgid;
    }

    public void setSrvmsgid(long srvmsgid) {
        this.srvmsgid = srvmsgid;
    }

    public int getProtover() {
        return protover;
    }

    public void setProtover(int protover) {
        this.protover = protover;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getRuletype() {
        return ruletype;
    }

    public void setRuletype(String ruletype) {
        this.ruletype = ruletype;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSeqid() {
        return seqid;
    }

    public void setSeqid(String seqid) {
        this.seqid = seqid;
    }

    public String getIsreadack() {
        return isreadack;
    }

    public void setIsreadack(String isreadack) {
        this.isreadack = isreadack;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    @Override
    public String toString() {
        return "MsgRequestWaiterC2S{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", tempid='" + tempid + '\'' +
                ", srvmsgid=" + srvmsgid +
                ", protover=" + protover +
                ", shopid='" + shopid + '\'' +
                ", ruletype='" + ruletype + '\'' +
                ", clientid='" + clientid + '\'' +
                ", clientname='" + clientname + '\'' +
                ", desc='" + desc + '\'' +
                ", locid='" + locid + '\'' +
                ", other='" + other + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", seqid='" + seqid + '\'' +
                ", isreadack='" + isreadack + '\'' +
                '}';
    }
}

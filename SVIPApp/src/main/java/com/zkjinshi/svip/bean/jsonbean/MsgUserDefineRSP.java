package com.zkjinshi.svip.bean.jsonbean;

/**
 * 开发者：vincent
 * 日期：2015/7/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgUserDefineRSP {

    public final static int PUSH_SUCCESS = 0;// 0:在线推送成功
    public final static int PUSH_FAILED = 1;// 1:在线推送失败
    public final static int PUSH_OFFLINE_SUCCESS = 2;// 2:离线apns推送成功
    public final static int PUSH_OFFLINE_FAILED = 3;// 3:离线apns推送失败
    public final static int PUSH_AS_OFFLINE_MSG = 4;// 4:存为离线消息

    private int type;//not  null //协议消息类型
    private long timestamp;//not  null //当前时间
    private String tempid;//app端临时ID
    private long srvmsgid;
    private int protover;//消息协议版本

    private String toid;
    private int result;// 0:在线推送成功 1:在线推送失败 2:离线apns推送成功 3:离线apns推送失败 4:存为离线消息
    private String desc;// 备注

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

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package com.zkjinshi.svip.bean.jsonbean;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by vincent on 2015/6/19.
 * 文本消息回复包体
 */
public class MsgCustomerServiceTextChatRSP {

    private int          type;//not  null //协议消息类型
    private long         timestamp;//not  null //当前时间
    private String       tempid;//app端临时ID
    private long         srvmsgid;
    private int          protover;//消息协议版本

    private String       shopid;//商家管理端ID
    private String       sessionid;
    private int          result;
    private JSONArray    failedusers;  //发送失败用户列表

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

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public JSONArray getFailedusers() {
        return failedusers;
    }

    public void setFailedusers(JSONArray failedusers) {
        this.failedusers = failedusers;
    }

}

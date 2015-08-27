package com.zkjinshi.svip.bean.jsonbean;

/**
 * 自定义协议消息对象
 * 开发者：vincent
 * 日期：2015/7/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgUserDefine {

    private int type;//not  null //协议消息类型
    private long timestamp;//not  null //当前时间
    private String tempid;//app端临时ID
    private long srvmsgid;
    private int protover;//消息协议版本

    private String fromid;
    private String toid;
    private int pushofflinemsg;// 用户不在线的处理 0:不处理离线 1:ios发送apns推送,android保存为离线消息
    private String pushalert;
    private int childtype;//0:订单预定失败 1:订单预定成功
    private String content;  // Content消息内容

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
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

    public int getPushofflinemsg() {
        return pushofflinemsg;
    }

    public void setPushofflinemsg(int pushofflinemsg) {
        this.pushofflinemsg = pushofflinemsg;
    }

    public String getPushalert() {
        return pushalert;
    }

    public void setPushalert(String pushalert) {
        this.pushalert = pushalert;
    }

    public int getChildtype() {
        return childtype;
    }

    public void setChildtype(int childtype) {
        this.childtype = childtype;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.zkjinshi.svip.bean.jsonbean;

import org.json.JSONArray;

/**
 * Created by vincent on 2015/6/19.
 * 文本消息发送包体
 */
public class MsgCustomerServiceTextChat {

    private int          type;//not  null //协议消息类型
    private long         timestamp;//not  null //当前时间
    private String       tempid;//app端临时ID
    private long         srvmsgid;
    private int          protover;//消息协议版本

    private String       fromid;
    private String       fromname;
    private String       clientid;
    private String       clientname;
    private String       shopid;
    private String       ruletype = "DefaultChatRuleType";// "INNERSESSION",则表明是商家员工内部聊天 "DefaultChatRuleType"，客人与商家聊天
    private String       adminid;//发送消息的商家管理端用户ID

    private String       sessionid;//会话ID
    private String       seqid;
    private int          isreadack;//是否要求消息已读回执 0:不需要 1:需要
    private String       textmsg;//文本消息体
    private int childtype;//0:普通Text 1:自定义json格式
    private String       salesid;

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

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
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

    public String getAdminid() {
        return adminid;
    }

    public void setAdminid(String adminid) {
        this.adminid = adminid;
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

    public int getIsreadack() {
        return isreadack;
    }

    public void setIsreadack(int isreadack) {
        this.isreadack = isreadack;
    }

    public String getTextmsg() {
        return textmsg;
    }

    public void setTextmsg(String textmsg) {
        this.textmsg = textmsg;
    }

    public int getChildtype() {
        return childtype;
    }

    public void setChildtype(int childtype) {
        this.childtype = childtype;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    @Override
    public String toString() {
        return "MsgCustomerServiceTextChat{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", tempid='" + tempid + '\'' +
                ", srvmsgid=" + srvmsgid +
                ", protover=" + protover +
                ", fromid='" + fromid + '\'' +
                ", fromname='" + fromname + '\'' +
                ", clientid='" + clientid + '\'' +
                ", clientname='" + clientname + '\'' +
                ", shopid='" + shopid + '\'' +
                ", ruletype='" + ruletype + '\'' +
                ", adminid='" + adminid + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", seqid='" + seqid + '\'' +
                ", isreadack=" + isreadack +
                ", textmsg='" + textmsg + '\'' +
                ", childtype=" + childtype +
                ", salesid='" + salesid + '\'' +
                '}';
    }
}

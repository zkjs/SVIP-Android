package com.zkjinshi.svip.bean.jsonbean;

/**
 * Created by vincent on 2015/6/19.
 * 图片消息发送包体
 */
public class MsgCustomerServiceImgChat {

    //MsgHeader
    private int         type;//not  null //协议消息类型
    private long        timestamp;//not  null //当前时间
    private String      tempid;//app端临时ID
    private String attachId;
    private String filePath;
    private long        srvmsgid;
    private int         protover;//消息协议版本

    private String      fromid;
    private String      fromname;
    private String      clientid;
    private String      clientname;
    private String      shopid;
    private String      ruletype =  "DefaultChatRuleType";// "INNERSESSION",则表明是商家员工内部聊天 "DefaultChatRuleType"，客人与商家聊天;
    private String      adminid;

    //MsgSessionID
    private String      sessionid;//会话ID
    private String      seqid;
    private int         isreadack;//是否要求消息已读回执 0:不需要 1:需要

    //MsgImg
    private int         width;//原图片宽度
    private int         height;//原图片高度
    private String      format;//图片格式(.png,.jpg...)
    private String      filename;//原文件名
    private int         fsize;//文件大小(字节)
    private String      url;//大图链接
    private String      scaleurl;//缩略图链接
    private String      crc;//效验值
    private String      body;//文件内容(Base64编码)
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFsize() {
        return fsize;
    }

    public void setFsize(int fsize) {
        this.fsize = fsize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScaleurl() {
        return scaleurl;
    }

    public void setScaleurl(String scaleurl) {
        this.scaleurl = scaleurl;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachId() {
        return attachId;
    }

    public void setAttachId(String attachId) {
        this.attachId = attachId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    @Override
    public String toString() {
        return "MsgCustomerServiceImgChat{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", tempid='" + tempid + '\'' +
                ", attachId='" + attachId + '\'' +
                ", filePath='" + filePath + '\'' +
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
                ", width=" + width +
                ", height=" + height +
                ", format='" + format + '\'' +
                ", filename='" + filename + '\'' +
                ", fsize=" + fsize +
                ", url='" + url + '\'' +
                ", scaleurl='" + scaleurl + '\'' +
                ", crc='" + crc + '\'' +
                ", body='" + body + '\'' +
                ", salesid='" + salesid + '\'' +
                '}';
    }
}

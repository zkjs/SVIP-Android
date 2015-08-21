package com.zkjinshi.svip.bean.jsonbean;

import org.json.JSONArray;

/**
 * Created by vincent on 2015/6/1.
 */
public class MsgClientLogin {

    private int         type;//not  null //协议消息类型
    private long        timestamp;//not  null //当前时间
    private String      tempid;//app端临时ID
    private long        srvmsgid;//消息唯一ID
    private int         protover;//消息协议版本

    private String      id;         //即APP登录时返回的用户ID
    private String      name;
    private String      devtoken;
    private int         logintype;  // 0:app用户  1:商家员工 默认为:0
    private String      version;    // 通讯协议版本
    private String      platform;   // A: android,I:ios,W:web
    private String      appid;      // HOTELVIP/OTHERS
    private String      shopid;     //商家id
    private String      empid;      //员工id
    private int         roleid;     //商家角色ID
    private JSONArray   loc;        //员工区域数组
    private String      beaconid;   //beaconid
    private int         workstatus; //0:上班 1:下班

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevtoken() {
        return devtoken;
    }

    public void setDevtoken(String devtoken) {
        this.devtoken = devtoken;
    }

    public int getLogintype() {
        return logintype;
    }

    public void setLogintype(int logintype) {
        this.logintype = logintype;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public JSONArray getLoc() {
        return loc;
    }

    public void setLoc(JSONArray loc) {
        this.loc = loc;
    }

    public String getBeaconid() {
        return beaconid;
    }

    public void setBeaconid(String beaconid) {
        this.beaconid = beaconid;
    }

    public int getWorkstatus() {
        return workstatus;
    }

    public void setWorkstatus(int workstatus) {
        this.workstatus = workstatus;
    }



}

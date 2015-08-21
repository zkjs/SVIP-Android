package com.zkjinshi.svip.entity;

/**
 * 聊天室数据库操作实体
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomEntity {

    private int id;//自动生成ID

    private String sessionid;//

    private String remark;//

    private long created;//

    private long endtime;//

    private String enduserid;//

    private String clientid;//

    private String clientname;//

    private int isVisible;//

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getEnduserid() {
        return enduserid;
    }

    public void setEnduserid(String enduserid) {
        this.enduserid = enduserid;
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

    public int getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(int isVisible) {
        this.isVisible = isVisible;
    }
}

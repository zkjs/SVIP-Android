package com.zkjinshi.svip.entity;

/**
 * 聊天室成员数据库操作实体
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatMemberEntity {

    private String sessionid;

    private String userid;

    private String username;

    private String logintype;

    private String empRoleID;

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

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

    public String getLogintype() {
        return logintype;
    }

    public void setLogintype(String logintype) {
        this.logintype = logintype;
    }

    public String getEmpRoleID() {
        return empRoleID;
    }

    public void setEmpRoleID(String empRoleID) {
        this.empRoleID = empRoleID;
    }
}

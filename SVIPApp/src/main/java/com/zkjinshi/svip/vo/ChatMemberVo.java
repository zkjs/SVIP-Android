package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 聊天室成员ui操作操作相关实体
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatMemberVo implements Serializable{

    private String sessionid;//会话ID
    private String userid;//用户ID
    private int    logintype;//用户类型 0:app用户  1:商家员工 默认为:0
    private String shopid;//商家ID
    private String empid;//员工ID
    private String roleid;//角色ID
    private long   created;//创建时间

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

    public int getLogintype() {
        return logintype;
    }

    public void setLogintype(int logintype) {
        this.logintype = logintype;
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

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}

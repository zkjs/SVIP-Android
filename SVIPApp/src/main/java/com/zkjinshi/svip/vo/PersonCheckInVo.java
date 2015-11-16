package com.zkjinshi.svip.vo;

/**
 * 表语句操作帮助类
 * 开发者：WinkyQin
 * 日期： 2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PersonCheckInVo {

    private int    id;          //  联系人   id
    private String realname;    //  真实姓名 realname
    private String idcard;      //  证件号   realname
    private String phone;       //  联系电话 phone

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

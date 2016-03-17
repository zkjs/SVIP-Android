package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomerServiceBean implements Serializable {

    private String email;
    private String phone;
    private int sex;
    private String shopid;
    private String userid;
    private String userimage;
    private String username;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

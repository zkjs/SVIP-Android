package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserCallVo implements Serializable {

    private boolean set;
    private String username;
    private String userid;
    private String user_avatar;
    private String sex;
    private String userstatus;
    private String phone;
    private String shopid;
    private ShopCallVo shop;

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public ShopCallVo getShop() {
        return shop;
    }

    public void setShop(ShopCallVo shop) {
        this.shop = shop;
    }
}

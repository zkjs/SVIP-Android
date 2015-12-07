package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 销售服务员对象
 * 开发者：WinkyQin
 * 日期：2015/12/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SalerInfoBean implements Serializable{

    private boolean set;
    private String   err;

    private String   userid;
    private String   username;
    private int      sex;
    private String   phone;
    private String   city;
    private String   real_name;
    private String   shopid;
    private String   shop_name;

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
}

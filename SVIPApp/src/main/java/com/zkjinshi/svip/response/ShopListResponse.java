package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopListResponse implements Serializable{

    private String shopid;
    private String logo;
    private String fullname;
    private String phone;
    private double map_longitude;
    private double map_latitude;
    private String shop_title;
    private int wifi;//1 有,0 没有
    private int park;//1 有,0 没有
    private int star;//1 有,0 没有


    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getMap_longitude() {
        return map_longitude;
    }

    public void setMap_longitude(double map_longitude) {
        this.map_longitude = map_longitude;
    }

    public double getMap_latitude() {
        return map_latitude;
    }

    public void setMap_latitude(double map_latitude) {
        this.map_latitude = map_latitude;
    }

    public String getShop_title() {
        return shop_title;
    }

    public void setShop_title(String shop_title) {
        this.shop_title = shop_title;
    }

    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }

    public int getPark() {
        return park;
    }

    public void setPark(int park) {
        this.park = park;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}

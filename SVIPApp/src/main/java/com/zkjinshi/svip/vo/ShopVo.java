package com.zkjinshi.svip.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家商家详情页
 * 开发者：JimmyZhang
 * 日期：2015/12/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopVo {

    private String shopId;
    private String shopName;
    private String address;
    private String telephone;
    private String evaluation;
    private int score;
    private String shopdescUrl;
    private String category;
    private ArrayList<String> images;
    private int shopStatus;//1代表上线，0代表不上线

    public int getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(int shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getShopdescUrl() {
        return shopdescUrl;
    }

    public void setShopdescUrl(String shopdescUrl) {
        this.shopdescUrl = shopdescUrl;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

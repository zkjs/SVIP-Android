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

    /**
     "shopid": "2009",   //商户编码
     "shopname": "豪庭",   //商户名称
     "shopaddress": "湘江中路二段138号",    //商户地址
     "shopindustry": "酒店行业", //所属行业
     "shopdesc": "<p class=\"nice\">酒店描述</p>",   //详情描述
     "shopstatus": 0,    //商户状态
     "telephone": "0731-85996399",   //商户电话
     "evaluation": 232,  //评价累计次数
     "score": 3.4,    //评价平均分
     "images": ["uploads/1.jpg", "uploads/2.jpg"]    //商家图片
     */

    private String shopid;
    private String shopname;
    private String shopaddress;
    private String shopindustry;
    private int industrycode;
    private String shopdesc;
    private int shopstatus;//1代表上线，0代表不上线
    private String telephone;
    private int evaluation;
    private float score;
    private ArrayList<String> images;

    public int getIndustrycode() {
        return industrycode;
    }

    public void setIndustrycode(int industrycode) {
        this.industrycode = industrycode;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public void setShopaddress(String shopaddress) {
        this.shopaddress = shopaddress;
    }

    public String getShopindustry() {
        return shopindustry;
    }

    public void setShopindustry(String shopindustry) {
        this.shopindustry = shopindustry;
    }

    public String getShopdesc() {
        return shopdesc;
    }

    public void setShopdesc(String shopdesc) {
        this.shopdesc = shopdesc;
    }

    public int getShopstatus() {
        return shopstatus;
    }

    public void setShopstatus(int shopstatus) {
        this.shopstatus = shopstatus;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}

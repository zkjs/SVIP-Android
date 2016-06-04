package com.zkjinshi.ritz.vo;

import com.blueware.agent.android.harvest.P;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 商家详情实体
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopVo implements Serializable{

    /**
     {
     "res": 0,
     "resDesc": "success",
     "data": [{
     "shopid": "2009",   //商户编码
     "shopname": "豪庭",   //商户名称
     "shoplogo": "/uploads/logo.png",    //商户logo
     "shopaddress": "湘江中路二段138号",    //商户地址
     "shopindustry": "酒店行业", //所属行业
     "industrycode": 60, //行业编码
     "shopdesc": "<p class=\"nice\">酒店描述</p>",   //详情描述
     "shopstatus": 0,    //商户状态
     "telephone": "0731-85996399",   //商户电话
     "evaluation": 232,  //评价累计次数
     "score": 3.4,    //评价平均分
     "shopmods": [
     {
     "title":"xxx",
     "body":"xxx",
     "shopid":"8888",
     "modid":"1",
     "sort":"1",
     "photos":[
     "url://ddd",
     "url://sss"
     ]
     }
     ]
     }]
     }
     */

    private String shopid;
    private String shopname;
    private String shoplogo;
    private String shopbg;
    private String shopaddress;
    private String shopindustry;
    private int industrycode;
    private String shopdesc;
    private int shopstatus;
    private String telephone;
    private int evaluation;
    private double score;
    private ArrayList<ShopModeVo> shopmods;

    public String getShopbg() {
        return shopbg;
    }

    public void setShopbg(String shopbg) {
        this.shopbg = shopbg;
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

    public String getShoplogo() {
        return shoplogo;
    }

    public void setShoplogo(String shoplogo) {
        this.shoplogo = shoplogo;
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

    public int getIndustrycode() {
        return industrycode;
    }

    public void setIndustrycode(int industrycode) {
        this.industrycode = industrycode;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ArrayList<ShopModeVo> getShopmods() {
        return shopmods;
    }

    public void setShopmods(ArrayList<ShopModeVo> shopmods) {
        this.shopmods = shopmods;
    }
}

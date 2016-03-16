package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 商品信息实体类
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodInfoVo implements Serializable {

    /**
     "id": "3212",   //商品编号
     "shopid": "2009",   //商户编号
     "name": "王府鸳鸯锅",    //商品名称
     "brief": "王府鸳鸯锅",   //商品简介
     "imgurl": "/uploads/goods/2123.jpg",    //商品图片
     "price": 2381,  //商品价格
     "keywords": "火锅"    //商品关键字
     */

    private String id;
    private String shopid;
    private String name;
    private String brief;
    private String imgurl;
    private float price;
    private String keywords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}

package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopBean extends BaseShopBean implements Serializable {

    private String salesid;
    private String shopbusiness;
    private String shopdesc;
    private String shoplogo;
    private String shopname;
    private String bgImgUrl;

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getShopbusiness() {
        return shopbusiness;
    }

    public void setShopbusiness(String shopbusiness) {
        this.shopbusiness = shopbusiness;
    }

    public String getShopdesc() {
        return shopdesc;
    }

    public void setShopdesc(String shopdesc) {
        this.shopdesc = shopdesc;
    }

    public String getShoplogo() {
        return shoplogo;
    }

    public void setShoplogo(String shoplogo) {
        this.shoplogo = shoplogo;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }
}

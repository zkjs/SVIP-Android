package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopBean implements Serializable {

    private String salesid;
    private String shopbusiness;
    private String shopdesc;
    private String shopid;
    private String shoplogo;
    private String shopname;

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getShopBusiness() {
        return shopbusiness;
    }

    public void setShopBusiness(String shopbusiness) {
        this.shopbusiness = shopbusiness;
    }

    public String getShopDesc() {
        return shopdesc;
    }

    public void setShopDesc(String shopdesc) {
        this.shopdesc = shopdesc;
    }

    public String getShopId() {
        return shopid;
    }

    public void setShopDd(String shopid) {
        this.shopid = shopid;
    }

    public String getShopLogo() {
        return shoplogo;
    }

    public void setShopLogo(String shoplogo) {
        this.shoplogo = shoplogo;
    }

    public String getShopName() {
        return shopname;
    }

    public void setShopName(String shopname) {
        this.shopname = shopname;
    }
}

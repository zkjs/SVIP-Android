package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopBean extends BaseShopBean implements Serializable {

    private String shoplogo;
    private String shopname;
    private String shoptitle;
    private String shopindustry;
    private String shopaddress;
    private String bgimgurl;
    private String recomm;
    private String salesid;
    private String link;

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

    public String getShoptitle() {
        return shoptitle;
    }

    public void setShoptitle(String shoptitle) {
        this.shoptitle = shoptitle;
    }

    public String getShopindustry() {
        return shopindustry;
    }

    public void setShopindustry(String shopindustry) {
        this.shopindustry = shopindustry;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public void setShopaddress(String shopaddress) {
        this.shopaddress = shopaddress;
    }

    public String getBgimgurl() {
        return bgimgurl;
    }

    public void setBgimgurl(String bgimgurl) {
        this.bgimgurl = bgimgurl;
    }

    public String getRecomm() {
        return recomm;
    }

    public void setRecomm(String recomm) {
        this.recomm = recomm;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 其他商家实体
 * 开发者：JimmyZhang
 * 日期：2016/4/6
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class OtherShopVo implements Serializable {

    /**
     "locid": "1000", //区域id
     "shopid": "8888", //商户id
     "shopname": "豪廷酒店", //商户名称
     "logo": "uploads/logo/ht.png", //商户logo
     "changetime": "2016-04-01 00:00:00" //变更时间
     */

    private String locid;
    private String shopid;
    private String shopname;
    private String logo;
    private String changetime;

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getChangetime() {
        return changetime;
    }

    public void setChangetime(String changetime) {
        this.changetime = changetime;
    }
}

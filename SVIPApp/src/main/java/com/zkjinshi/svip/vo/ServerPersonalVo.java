package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/10/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServerPersonalVo implements Serializable {

    private String shopid;
    private String salesid;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }
}

package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 订单确认信息
 * 开发者：JimmyZhang
 * 日期：2015/9/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderVo implements Serializable {

    private String shopId;
    private String orderId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

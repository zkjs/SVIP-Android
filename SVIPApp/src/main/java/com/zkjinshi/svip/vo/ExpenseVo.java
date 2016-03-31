package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 消费记录实体
 * 开发者：JimmyZhang
 * 日期：2016/3/29
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ExpenseVo implements Serializable {

    private String shopName;
    private String price;
    private long payTime;
    private String payNo;
    private String payee;//收款单位

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getPayTime() {
        return payTime;
    }

    public void setPayTime(long payTime) {
        this.payTime = payTime;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }
}

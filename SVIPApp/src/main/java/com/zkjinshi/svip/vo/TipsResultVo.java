package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 小费实体
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TipsResultVo implements Serializable {

    private WaiterVo waiterVo;
    private double price;

    public WaiterVo getWaiterVo() {
        return waiterVo;
    }

    public void setWaiterVo(WaiterVo waiterVo) {
        this.waiterVo = waiterVo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.EvaluateBean;
import com.zkjinshi.svip.bean.OrderDetailBean;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderEvaluateResponse extends BaseExtResponse {

    private OrderDetailBean data;

    public OrderDetailBean getData() {
        return data;
    }

    public void setData(OrderDetailBean data) {
        this.data = data;
    }
}

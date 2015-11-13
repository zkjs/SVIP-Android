package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.CustomerServiceBean;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomerServiceListResponse extends BaseExtResponse {

    private ArrayList<CustomerServiceBean> data;

    public ArrayList<CustomerServiceBean> getData() {
        return data;
    }

    public void setData(ArrayList<CustomerServiceBean> data) {
        this.data = data;
    }
}

package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.CustomerServiceBean;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/17
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomerServiceResponse extends BaseFornaxResponse {

    private CustomerServiceBean data;

    public CustomerServiceBean getData() {
        return data;
    }

    public void setData(CustomerServiceBean data) {
        this.data = data;
    }
}

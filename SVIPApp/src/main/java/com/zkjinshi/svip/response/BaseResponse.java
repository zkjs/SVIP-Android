package com.zkjinshi.svip.response;

import com.zkjinshi.svip.net.NetResponse;

/**
 * http响应基类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseResponse extends NetResponse {

    private boolean set = true;

    public boolean isSet() {
        return set;
    }
    public void setSet(boolean set) {
        this.set = set;
    }

}

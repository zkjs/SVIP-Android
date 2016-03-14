package com.zkjinshi.svip.response;

import com.zkjinshi.svip.vo.SalesVo;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/12
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class SaleByCodeResponse extends BaseFornaxResponse{

    private SalesVo data;

    public SalesVo getData() {
        return data;
    }

    public void setData(SalesVo data) {
        this.data = data;
    }
}

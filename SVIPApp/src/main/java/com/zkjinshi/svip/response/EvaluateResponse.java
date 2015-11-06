package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.EvaluateBean;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EvaluateResponse extends BaseExtResponse {

    private EvaluateBean data;

    public EvaluateBean getData() {
        return data;
    }

    public void setData(EvaluateBean data) {
        this.data = data;
    }
}

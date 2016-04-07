package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseResponse implements Serializable {

    private int res;
    private String resDesc;

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }
}

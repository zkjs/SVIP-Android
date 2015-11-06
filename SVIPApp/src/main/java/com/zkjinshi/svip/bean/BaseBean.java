package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseBean implements Serializable {
    private boolean set = true;
    private String err = "";

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}

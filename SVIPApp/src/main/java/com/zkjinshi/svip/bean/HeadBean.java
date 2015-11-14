package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 头部实体类
 * 开发者：JimmyZhang
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HeadBean implements Serializable {

    private boolean set = true;
    private String err;
    private String exclusive_salesid;

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

    public String getExclusive_salesid() {
        return exclusive_salesid;
    }

    public void setExclusive_salesid(String exclusive_salesid) {
        this.exclusive_salesid = exclusive_salesid;
    }
}

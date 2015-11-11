package com.zkjinshi.svip.bean.jsonbean;

import java.io.Serializable;

/**
 * 开发者：vincent
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientTag implements Serializable {

    public int    tagid;
    public String tag;

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }
}
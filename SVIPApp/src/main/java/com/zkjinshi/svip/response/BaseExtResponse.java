package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.HeadBean;

import java.io.Serializable;

/**
 * http请求响应过渡类
 * 开发者：JimmyZhang
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseExtResponse implements Serializable {

    private HeadBean head;

    public HeadBean getHead() {
        return head;
    }

    public void setHead(HeadBean head) {
        this.head = head;
    }
}

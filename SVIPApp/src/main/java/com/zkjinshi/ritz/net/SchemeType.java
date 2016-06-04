package com.zkjinshi.ritz.net;

/**
 * 网络协议类型
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum SchemeType {

    HTTP(0),
    HTTPS(1),
    TCP(2);

    private SchemeType(int value){
        this.value = value;
    }
    private final int value;

    public int getVlaue(){
        return value;
    }
}

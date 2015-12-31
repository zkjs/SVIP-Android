package com.zkjinshi.svip.net;

/**
 * 网络请求类型
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum MethodType {

    GET(0),
    POST(1),
    PUSH(2),
    JSON(3),
    JSONPOST(4);

    private MethodType(int value){
        this.value = value;
    }
    private final int value;
    public int getVlaue(){
        return value;
    }

}

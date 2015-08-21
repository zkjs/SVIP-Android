package com.zkjinshi.svip.vo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum SendStatus {
    SEND_FAIL(0),//发送失败
    SENDING(2),//发送中
    SEND_SUCCESS(1);//发送成功
    private SendStatus(int value){
        this.value = value;
    }
    private int value;
    public int getVlaue(){
        return value;
    }
}

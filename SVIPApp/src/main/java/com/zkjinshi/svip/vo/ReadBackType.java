package com.zkjinshi.svip.vo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum ReadBackType {

    NEED_READBACK(0),   //需要返回
    NO_NEED_READBACK(1);//不需要消息发送成功的返回

    private ReadBackType(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }
}

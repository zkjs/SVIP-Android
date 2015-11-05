package com.zkjinshi.svip.vo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum PushOfflineMsg {

    NOT_PUSH_MSG(0),   //需要返回
    PUSH_MSG(1);//不需要消息发送成功的返回

    private PushOfflineMsg(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }
}
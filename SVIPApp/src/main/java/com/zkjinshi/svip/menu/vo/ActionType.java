package com.zkjinshi.svip.menu.vo;

/**
 * 定义指令类别
 * 开发者：JimmyZhang
 * 日期：2015/8/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum ActionType {

    CHAT(0), //聊天咨询
    PUSH(1), //推送预定订单
    WEB(2), //打开网页链接
    BOOK(3); //预定酒店

    private final int value;

    ActionType(int value){
        this.value = value;
    }

    public int getValue(){
        return  value;
    }
}

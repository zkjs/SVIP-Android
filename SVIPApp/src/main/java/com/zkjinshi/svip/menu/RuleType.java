package com.zkjinshi.svip.menu;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum RuleType {
    RESERVATION(0),//订房部
    CALL_CENTER(1),//呼叫中心
    RESERVATION_RECEPTIONIST(2),//订房部和前台
    SALE(3),//销售部
    CALL_CENTER_CATERING(4),//呼叫中心和餐饮部
    HOUSE_KEEPING(5);//客户部
    private final int value;
    RuleType(int value){
        this.value = value;
    }
}

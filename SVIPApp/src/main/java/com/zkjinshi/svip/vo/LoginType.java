package com.zkjinshi.svip.vo;

/**
 * 登录类型枚举类
 * 开发者：vincent
 * 日期：2015/7/31
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum LoginType{

    appUser(0), shopUser(1);

    private LoginType(int type){}

    private int value;

    public  int getVlaue(){
        return value;
    }
}
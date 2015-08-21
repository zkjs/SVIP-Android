package com.zkjinshi.svip.menu;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum MenuType {
    SINGLE(0),//单菜单
    MULTI(1);//复选菜单
    private final int value;
    MenuType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}

package com.zkjinshi.svip.menu.view;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum ChatMenuType {
    SINGLE(1), GROUP(2);
    private int value;
    private ChatMenuType(int value) {
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}

package com.zkjinshi.svip.menu.view;

/**
 * 菜单位置类别
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum PostionType {
    LEFT(1), RIGHT(2), CENTER(3);
    private int value = 3;
    private PostionType(int value) {
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}

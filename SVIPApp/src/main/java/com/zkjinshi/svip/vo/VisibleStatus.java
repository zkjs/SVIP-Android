package com.zkjinshi.svip.vo;

/**
 * 是否UI界面上显示
 * 开发者：vincent
 * 日期：2015/8/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum VisibleStatus {

    INVISIBLE(0),//不显示
    VISIBLE(1);//显示

    private VisibleStatus(int value){
        this.value = value;
    }
    private int value;
    public int getVlaue(){
        return value;
    }
}
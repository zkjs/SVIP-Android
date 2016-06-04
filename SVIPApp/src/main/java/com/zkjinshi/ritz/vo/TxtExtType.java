package com.zkjinshi.ritz.vo;

/**
 * 文本消息扩展类型
 * 开发者：JimmyZhang
 * 日期：2015/11/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum TxtExtType {
    DEFAULT(0),//普通文本类型
    CARD(1);//卡片类型
    private TxtExtType(int value){
        this.value = value;
    }
    private int value;
    public int getVlaue(){
        return value;
    }
}

package com.zkjinshi.svip.vo;

/**
 * 评价等级
 * 开发者：JimmyZhang
 * 日期：2015/11/4
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum EvaluateLevel {

    POOR(1),//差
    COMMON(2),//一般
    GRATIFY(3),//满意
    GREAT_GRATIFY(4),//非常满意
    HIGHLY_RECOMMEND(5);//强烈推荐

    private EvaluateLevel(int value){
        this.value = value;
    }
    private final int value;
    public int getVlaue(){
        return value;
    }
}

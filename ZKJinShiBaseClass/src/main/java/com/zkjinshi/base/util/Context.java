package com.zkjinshi.base.util;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Context {

    private android.content.Context context;

    private Context(){}

    private static Context instance;

    public static synchronized Context getInstance(){
        if(null == instance){
            instance = new Context();
        }
        return instance;
    }

    public void init(android.content.Context context){
        this.context = context;
    }

    public android.content.Context getContext() {
        return context;
    }
}

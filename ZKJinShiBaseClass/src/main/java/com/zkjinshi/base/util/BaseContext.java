package com.zkjinshi.base.util;

import android.content.Context;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseContext {

    private Context context;

    private BaseContext(){}

    private static BaseContext instance;

    public static synchronized BaseContext getInstance(){
        if(null == instance){
            instance = new BaseContext();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}

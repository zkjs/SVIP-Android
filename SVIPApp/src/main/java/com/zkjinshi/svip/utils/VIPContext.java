package com.zkjinshi.svip.utils;

import android.content.Context;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class VIPContext {

    private Context context;

    private VIPContext(){}

    private static VIPContext instance;

    public static synchronized VIPContext getInstance(){
        if(null == instance){
            instance = new VIPContext();
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

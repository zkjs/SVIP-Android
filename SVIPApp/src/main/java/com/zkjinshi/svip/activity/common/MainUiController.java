package com.zkjinshi.svip.activity.common;

import android.content.Context;

import com.zkjinshi.svip.activity.base.BaseUiController;

/**
 * 主页面控制逻辑
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainUiController extends BaseUiController{
    private MainUiController(){}
    private static MainUiController instance;
    private Context context;
    public static synchronized MainUiController getInstance(){
        if(null ==  instance){
            instance = new MainUiController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
    }
}

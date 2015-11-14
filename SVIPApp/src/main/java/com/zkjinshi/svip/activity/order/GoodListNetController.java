package com.zkjinshi.svip.activity.order;

import android.content.Context;

import com.zkjinshi.base.util.DialogUtil;

/**
 * 商品列表网络请求控制层
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListNetController {
    private GoodListNetController(){}
    private static GoodListNetController instance;
    private Context context;

    public static synchronized GoodListNetController getInstance(){
        if(null ==  instance){
            instance = new GoodListNetController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;

    }

}

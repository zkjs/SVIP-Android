package com.zkjinshi.svip.activity.order;

import android.content.Context;


import com.zkjinshi.base.util.DialogUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopListNetController {
    private ShopListNetController(){}
    private static ShopListNetController instance;
    private Context context;

    public static synchronized ShopListNetController getInstance(){
        if(null ==  instance){
            instance = new ShopListNetController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;

    }

}

package com.zkjinshi.svip.activity.order;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    private RequestQueue requestQueue;
    public static synchronized GoodListNetController getInstance(){
        if(null ==  instance){
            instance = new GoodListNetController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void requestGetGoodListTask(StringRequest stringRequest){
        DialogUtil.getInstance().showProgressDialog(context);
        requestQueue.add(stringRequest);
    }
}
